package edu.upenn.cis.cis455.crawler.Filters;

import edu.upenn.cis.cis455.Util.GenerateMd5Hash;
import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfoKey;
import edu.upenn.cis.cis455.model.SeenContent.SeenContent;
import edu.upenn.cis.cis455.model.responseModels.URLMetaData;
import edu.upenn.cis.cis455.storage.DBManagers.URLDBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DocumentIndexingFilter implements CrawlFilter {

    Logger logger = LogManager.getLogger(DocumentIndexingFilter.class);

    private Crawler crawlerInstance;
    private URLDBManager urldbManager;

    public DocumentIndexingFilter(Crawler crawlerInstance, URLDBManager urldbManager) {
        this.crawlerInstance = crawlerInstance;
        this.urldbManager = urldbManager;
    }


    @Override
    public void renderRequest(RequestContext requestContext) {

        if (requestContext.getGetMetaData() != null && requestContext.isValid()) {

            String md5Hash = GenerateMd5Hash.hash(requestContext.getGetMetaData().getData());
            requestContext.setHashMd5(md5Hash);

            if (requestContext.getHeadMetaData().getStatusCode() == 304) {


                return;
            }

            if (requestContext.getGetMetaData().getData() == null || requestContext.getGetMetaData().getData().equals("")) {
                requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
                requestContext.setDebugMessage("IndexDocumentProcessor data is null");
                return;
            }

            SeenContent contentSeenInfo = urldbManager.getContentSeenInfo(md5Hash,
                    requestContext.getTxn());

            if (contentSeenInfo != null) {
            } else {
                synchronized (crawlerInstance) {
                    try {
                        if (crawlerInstance.getCrawlerDataStructure().canIIndexNewDocument()) {
                            crawlerInstance.getCrawlerDataStructure().incrementIndexedDocumentCount(1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            }

            try {

                URLMetaData urlMetaData = requestContext.getGetMetaData();
                DomainInfoKey domainInfoKey = new DomainInfoKey(requestContext.getUrlInfo().getURL().getProtocol(),
                        requestContext.getUrlInfo().getHostName(),
                        requestContext.getUrlInfo().getPortNo(), requestContext.getUrlInfo().getFilePath(),
                        urlMetaData.getMethodType());

                DomainInfo domainInfo = new DomainInfo(domainInfoKey);
                domainInfo.setContentLength(urlMetaData.getContentLength());
                domainInfo.setContentType(urlMetaData.getContentType());
                domainInfo.setData(urlMetaData.getData());
                domainInfo.setStatusCode(urlMetaData.getStatusCode());
                urldbManager.insertURLDataInfo(domainInfo, requestContext.getTxn());

            } catch (Exception e) {

                synchronized (crawlerInstance) {
                    crawlerInstance.getCrawlerDataStructure().incrementIndexedDocumentCount(-1);
                }

                throw e;
            }

        } else {
            requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
            requestContext.setDebugMessage("IndexDocumentProcessor validation failed");
            return;
        }

    }

}
