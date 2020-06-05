package edu.upenn.cis.cis455.crawler.mapreduce.Topology;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.DocumentTypes;
import edu.upenn.cis.cis455.crawler.Filters.RequestContext;
import edu.upenn.cis.cis455.crawler.ResponseForReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.handlers.SAXHandler.HTMLHandler;
import edu.upenn.cis.cis455.crawler.handlers.SAXHandler.XMLHandler;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Job;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfo;
import edu.upenn.cis.cis455.model.OccurrenceEvent;
import edu.upenn.cis.cis455.model.responseModels.URLMetaData;
import edu.upenn.cis.stormlite.OutputFieldsDeclarer;
import edu.upenn.cis.stormlite.TopologyContext;
import edu.upenn.cis.stormlite.bolt.IRichBolt;
import edu.upenn.cis.stormlite.bolt.OutputCollector;
import edu.upenn.cis.stormlite.routers.IStreamRouter;
import edu.upenn.cis.stormlite.tuple.Fields;
import edu.upenn.cis.stormlite.tuple.Tuple;
import edu.upenn.cis.stormlite.tuple.Values;
import org.xml.sax.InputSource;

import javax.swing.text.html.HTML;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.Map;
import java.util.UUID;

public class DOMParserBolt implements IRichBolt {
    private Crawler crawlerInstance;
    private Fields schema = new Fields("docId","OccurenceEvent");
    private OutputCollector outputCollector;
    String executorId = UUID.randomUUID().toString();

    public DOMParserBolt() {
        this.crawlerInstance = Crawler.getMap_reduce_crawler();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void execute(Tuple input) {


        Object obj = input.getObjectByField("responseForReadyQueueInstance");
        try {

            ResponseForReadyQueueInstance responseForReadyQueueInstance = (ResponseForReadyQueueInstance) ((Job) obj).getObject();
            if (responseForReadyQueueInstance.isSuccess() && responseForReadyQueueInstance.getRequestContext()!=null
                    && responseForReadyQueueInstance.getRequestContext().getxPathData()!=null ) {

                RequestContext requestContext = responseForReadyQueueInstance.getRequestContext();
                DomainInfo urlMetaDataFromGet = requestContext.getxPathData();
                String contentType = urlMetaDataFromGet.getContentType();
                if (!(DocumentTypes.XML == DocumentTypes.getTypeFromContentType(contentType)) && !(DocumentTypes.HTML == DocumentTypes.getTypeFromContentType(contentType))) {
                    //no Op
                } else {
                    String docId = String.valueOf(responseForReadyQueueInstance.getRequestContext().getxPathData().getDocInfo().getDocId());
                    try {
                        String xmlData = urlMetaDataFromGet.getData();
                        if (DocumentTypes.XML == DocumentTypes.getTypeFromContentType(contentType) || DocumentTypes.HTML == DocumentTypes.getTypeFromContentType(contentType)) {
                            System.out.println("Start Of XML Event");
                            OccurrenceEvent startEvent = new OccurrenceEvent(null);
                            startEvent.setNewRecord(true);
                            startEvent.setRemoveEvent(false);
                            Job newJob = new Job(docId, startEvent);
                            synchronized (crawlerInstance) {
                                crawlerInstance.getCrawlerDataStructure().addJob(newJob);
                            }
                            outputCollector.emit(new Values<Object>(docId,newJob));
                        }

                        if (DocumentTypes.HTML == DocumentTypes.getTypeFromContentType(contentType)) {
                            HTMLHandler htmlHandler = new HTMLHandler(outputCollector,xmlData,docId,requestContext.getUrlInfo().getURL().toString());
                            htmlHandler.setDocId(docId);
                            htmlHandler.parse();

                        } else if (DocumentTypes.XML == DocumentTypes.getTypeFromContentType(contentType)) {
                            System.out.println("Parsing XML Document");
                            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                            SAXParser saxParser = saxParserFactory.newSAXParser();
                            XMLHandler xmlHandler = new XMLHandler();
                            xmlHandler.setCrawlerInstance(crawlerInstance);
                            xmlHandler.setOutputCollector(outputCollector);
                            xmlHandler.setDocId(docId);
                            saxParser.parse(new InputSource(new StringReader(xmlData)), xmlHandler);
                        }
                    } catch (Exception e) {
                        System.out.println(e);

                    } finally {
                        System.out.println("Ending XML Event");
                        OccurrenceEvent endEvent = new OccurrenceEvent(null);
                        endEvent.setNewRecord(false);
                        endEvent.setRemoveEvent(true);
                        Job newJob = new Job(docId, endEvent);
                        outputCollector.emit(new Values<Object>(docId,newJob));
                        synchronized (crawlerInstance) {
                            crawlerInstance.getCrawlerDataStructure().addJob(newJob);
                        }
                    }
                }
            } else {
                return;
            }
        } finally {
            synchronized (crawlerInstance) {
                crawlerInstance.getCrawlerDataStructure().removeJob((Job) obj);
            }
        }
    }



    @Override
    public void prepare(Map<String, String> stormConf, TopologyContext context, OutputCollector collector) {
        this.outputCollector = collector;
        this.crawlerInstance = Crawler.getMap_reduce_crawler();
    }

    @Override
    public void setRouter(IStreamRouter router) {
        this.outputCollector.setRouter(router);

    }

    @Override
    public Fields getSchema() {
        return this.schema;
    }

    @Override
    public String getExecutorId() {
        return executorId;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("docId","OccurenceEvent"));

    }
}
