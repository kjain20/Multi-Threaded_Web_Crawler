package edu.upenn.cis.cis455.crawler.mapreduce.Topology;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.ResponseForReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Job;
import edu.upenn.cis.cis455.crawler.microservice.RequestMicroService;
import edu.upenn.cis.stormlite.OutputFieldsDeclarer;
import edu.upenn.cis.stormlite.TopologyContext;
import edu.upenn.cis.stormlite.bolt.IRichBolt;
import edu.upenn.cis.stormlite.bolt.OutputCollector;
import edu.upenn.cis.stormlite.routers.IStreamRouter;
import edu.upenn.cis.stormlite.tuple.Fields;
import edu.upenn.cis.stormlite.tuple.Tuple;
import edu.upenn.cis.stormlite.tuple.Values;

import java.util.Map;
import java.util.UUID;

public class DocFetcherBolt implements IRichBolt {

    private Crawler crawlerInstance;
    private RequestMicroService requestMicroService;
    private OutputCollector outputCollector;
    String executorId = UUID.randomUUID().toString();

    private Fields schema = new Fields("responseForReadyQueueInstance");

    public DocFetcherBolt(Crawler crawlerInstance) {
        this.crawlerInstance = crawlerInstance;
        this.requestMicroService = this.crawlerInstance.getRequestMicroService();
    }

    public DocFetcherBolt() {
        crawlerInstance = Crawler.getMap_reduce_crawler();
        this.requestMicroService = crawlerInstance.getRequestMicroService();
    }




    @Override
    public void cleanup() {

    }

    @Override
    public void execute(Tuple input) {

        Object readyQueueWrapperObject = input.getObjectByField("readyQueueJob");
        ReadyQueueInstance readyQueueInstance = (ReadyQueueInstance)((Job)readyQueueWrapperObject).getObject();
        try {
            if (readyQueueInstance != null) {
                ResponseForReadyQueueInstance responseForReadyQueueInstance = null;

                try {
                    responseForReadyQueueInstance = this.requestMicroService.processURL(readyQueueInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Job newJob = new Job(responseForReadyQueueInstance);
                synchronized (crawlerInstance) {
                    crawlerInstance.getCrawlerDataStructure().addJob(newJob);
                    crawlerInstance.getCrawlerDataStructure().addJob(newJob);
                }
                outputCollector.emit(new Values<Object>(newJob));
            } else {
                return;
            }
        }
        finally {
            synchronized (crawlerInstance) {
                crawlerInstance.getCrawlerDataStructure().removeJob(((Job) readyQueueWrapperObject));
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
        declarer.declare(new Fields("responseForReadyQueueInstance"));

    }
}
