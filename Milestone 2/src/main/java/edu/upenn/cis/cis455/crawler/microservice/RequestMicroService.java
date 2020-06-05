package edu.upenn.cis.cis455.crawler.microservice;


import com.sleepycat.je.Transaction;
import edu.upenn.cis.cis455.Util.PathMatcher;
import edu.upenn.cis.cis455.Util.URLResourceParser;
import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.DocumentTypes;
import edu.upenn.cis.cis455.crawler.Filters.*;
import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.QueuePackage.WaitQueueInstance;
import edu.upenn.cis.cis455.crawler.ResponseForReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfoKey;
import edu.upenn.cis.cis455.storage.DBManagers.DomainCrawlDelayDBManager;
import edu.upenn.cis.cis455.storage.DBManagers.DomainRobotDBManager;
import edu.upenn.cis.cis455.storage.DBManagers.URLDBManager;
import edu.upenn.cis.cis455.storage.StorageImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMicroService {
    private StorageImpl db; //should check if we can call methods of this class
    private Crawler crawlerInstance;
    private URLResourceParser urlResourceParser; //document url parser


    private RobotsFilter robotsFilter; // checks robot structure and enrich
    private CrawlDelayFilter crawlDelayFilter; // checks for crawl delays
    private DBFilter dbFilter;
    private HEADFilter headFilter;
    private GETFilter getFilter;
    private DocumentIndexingFilter documentIndexingFilter;


    private List<ReadyQueueInstance> readyQueueInstances;
    private List<WaitQueueInstance> waitQueueInstances;
     //this is required for validation check of request Instance
     Validator validator;


    //this microservice must be shared by workers....it cannot be different
    public RequestMicroService(Crawler crawlerInstance) {

        this.db = (StorageImpl)crawlerInstance.getDb();
        this.crawlerInstance = crawlerInstance;
        urlResourceParser = new URLResourceParser();

        DomainRobotDBManager domainRobotDBManager = db.getDomainRobotDBManager();
        WebURLDataFetchMicroService webURLDataFetchMicroService = new WebURLDataFetchMicroService();
        RobotsMicroService robotsMicroService = new RobotsMicroService(domainRobotDBManager,webURLDataFetchMicroService);
        DomainCrawlDelayDBManager domainCrawlDelayInfoManager = db.getDomainCrawlDelayDBManager();
        PathMatcher pathMatcher = new PathMatcher();
        URLDBManager urldbManager = db.getUrldbManager(); //URLDataManager
        WebURLInfoFetchMicroService webURLInfoFetchMicroService = new WebURLInfoFetchMicroService(webURLDataFetchMicroService,urldbManager);


        validator = new Validator();

        robotsFilter = new RobotsFilter(robotsMicroService,pathMatcher);
        validator.addValidator(robotsFilter);


        crawlDelayFilter = new CrawlDelayFilter(domainCrawlDelayInfoManager);
        validator.addValidator(crawlDelayFilter);


        dbFilter = new DBFilter(webURLInfoFetchMicroService);
        validator.addValidator(dbFilter);

        headFilter = new HEADFilter(webURLInfoFetchMicroService);
        validator.addValidator(headFilter);


        getFilter = new GETFilter(webURLInfoFetchMicroService);
        validator.addValidator(getFilter);

        documentIndexingFilter = new DocumentIndexingFilter(crawlerInstance,urldbManager);
        validator.addValidator(documentIndexingFilter);

    }



    //this must do filtering and render the links
    public ResponseForReadyQueueInstance processURL(ReadyQueueInstance readyQueueInstance)
    {
        if (!readyQueueInstance.isValid()) {
            //return new ResponseForReadyQueueInstance(false, "invalid instance");
            RequestContext requestContext = null;
            return new ResponseForReadyQueueInstance(false, requestContext);
        }
        Transaction txn = this.db.getEnvironment().beginTransaction(null,
                null);

        RequestContext requestContext = new RequestContext(readyQueueInstance.getRequestURLObject(), txn, crawlerInstance.getMaxDocSize());
        try {
            validator.processRequest(requestContext);
        } catch (Exception e) {
            requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
            requestContext.setDebugMessage(e.getMessage());
        }

        if (requestContext.didFail()) {
            txn.abort();
            return new ResponseForReadyQueueInstance(false, requestContext);
        } else {
            txn.commit();
        }

        //System.out.println(requestContext.getDebugMessage());

        if (requestContext.isWaiting()) {
            ResponseForReadyQueueInstance responseForReadyQueueInstance = new
                    ResponseForReadyQueueInstance(new ArrayList<>(), Arrays.asList(requestContext.getWaitQueueInstance()));
            responseForReadyQueueInstance.setRequestContext(requestContext);
            return responseForReadyQueueInstance;
        }
        if (!requestContext.isValid()) {
            ResponseForReadyQueueInstance responseForReadyQueueInstance = new  ResponseForReadyQueueInstance(false, requestContext.getDebugMessage());
            responseForReadyQueueInstance.setRequestContext(requestContext);
            return responseForReadyQueueInstance;
        }

        return processDocument(readyQueueInstance, requestContext);
    }

    private boolean shouldIProcessDocument(RequestContext processContext) {

        if(processContext.getHashMd5() == null) {
            return false;
        }

        synchronized (this.crawlerInstance) {
            if(crawlerInstance.getCrawlerDataStructure().getVisitedContent().contains(processContext.getHashMd5())){
                return false;
            }
            //logger.info("Content not processed already");
            crawlerInstance.getCrawlerDataStructure().getVisitedContent().add(processContext.getHashMd5());
            crawlerInstance.getCrawlerDataStructure().getVisitedDocumentUrlMap().put(processContext.getHashMd5(),
                    processContext.getUrlInfo().getURL().toString());
            return true;
        }
    }

    public ResponseForReadyQueueInstance processDocument(ReadyQueueInstance readyQueueInstance, RequestContext processContext) {

        if(!shouldIProcessDocument(processContext)) {
            ResponseForReadyQueueInstance responseForReadyQueueInstance = new ResponseForReadyQueueInstance(new ArrayList<>(), new ArrayList<>());
            responseForReadyQueueInstance.setRequestContext(processContext);
            return responseForReadyQueueInstance;
        }

        DomainInfo domainInfo = crawlerInstance.getDb().getUrldbManager().getURLDataInfo(new DomainInfoKey(processContext.getUrlInfo().getURL(), "GET", processContext.getUrlInfo().getPortNo()), null);
        processContext.setxPathData(domainInfo);

        DocumentTypes docType = DocumentTypes.getTypeFromContentType(processContext.getGetMetaData().getContentType());

        if (docType == DocumentTypes.HTML) {
            String protocol = readyQueueInstance.getRequestURLObject().isSecure() ? "https" : "http";
            try {
                String baseUrl = new URL(protocol, readyQueueInstance.getRequestURLObject().getHostName(),
                        readyQueueInstance.getRequestURLObject().getPortNo(), readyQueueInstance.getRequestURLObject().getFilePath()).toString();
                List<URLInfo> urlInfoList = urlResourceParser.getUrls(baseUrl, processContext.getGetMetaData().getData());
                List<ReadyQueueInstance> readyQueueInstances = urlInfoList.stream().map(urlInfo
                        -> new ReadyQueueInstance(urlInfo)).collect(Collectors.toList());
                ResponseForReadyQueueInstance responseForReadyQueueInstance = new ResponseForReadyQueueInstance(readyQueueInstances, new ArrayList<>());
                responseForReadyQueueInstance.setRequestContext(processContext);
                return responseForReadyQueueInstance;
            } catch (MalformedURLException e) {
                ResponseForReadyQueueInstance responseForReadyQueueInstance = new ResponseForReadyQueueInstance(false, e.getMessage());
                responseForReadyQueueInstance.setRequestContext(processContext);
                return responseForReadyQueueInstance;
            }
        } else if (docType == DocumentTypes.XML) {
            ResponseForReadyQueueInstance responseForReadyQueueInstance = new ResponseForReadyQueueInstance(new ArrayList<>(), new ArrayList<>());
            responseForReadyQueueInstance.setRequestContext(processContext);
            return responseForReadyQueueInstance;
        }
        ResponseForReadyQueueInstance responseForReadyQueueInstance = new ResponseForReadyQueueInstance(false, "UNKNOWN Error, Bad Document");
        responseForReadyQueueInstance.setRequestContext(processContext);
        return responseForReadyQueueInstance;
    }




}
