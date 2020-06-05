package edu.upenn.cis.cis455.crawler.mapreduce.Topology;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.QueuePackage.WaitQueueInstance;
import edu.upenn.cis.cis455.crawler.ResponseForReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.Workers.CrawlWorker;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Job;
import edu.upenn.cis.cis455.crawler.microservice.RequestMicroService;
import edu.upenn.cis.stormlite.OutputFieldsDeclarer;
import edu.upenn.cis.stormlite.TopologyContext;
import edu.upenn.cis.stormlite.bolt.IRichBolt;
import edu.upenn.cis.stormlite.bolt.OutputCollector;
import edu.upenn.cis.stormlite.routers.IStreamRouter;
import edu.upenn.cis.stormlite.tuple.Fields;
import edu.upenn.cis.stormlite.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class URLExtractorBolt implements IRichBolt {
    private Crawler crawlerInstance;
    private RequestMicroService requestMicroService;
    private String executorId = UUID.randomUUID().toString();


    public URLExtractorBolt() {
        this.crawlerInstance = Crawler.getMap_reduce_crawler();
        this.requestMicroService = this.crawlerInstance.getRequestMicroService();
    }

    private Fields schema = new Fields();

    @Override
    public void cleanup() {

    }

    @Override
    public void execute(Tuple input) {

        Object obj = input.getObjectByField("responseForReadyQueueInstance");
        ResponseForReadyQueueInstance responseForReadyQueueInstance = (ResponseForReadyQueueInstance) ((Job) obj).getObject();
        try
        {
            ResponseForReadyQueueInstance boutProcessedResponse = null;
            boutProcessedResponse = responseForReadyQueueInstance;
            if(boutProcessedResponse == null)
            {
                return;
            }
            List<ReadyQueueInstance> generatedReadyQueueInstances = new ArrayList<>();
            List<WaitQueueInstance> generatedWaitQueueInstances = new ArrayList<>();
            if(!boutProcessedResponse.isSuccess())
            {

            }
            else
            {
                //generatedReadyQueueInstances = boutProcessedResponse.getReadyQueueInstances();
                //generatedWaitQueueInstances = boutProcessedResponse.getWaitQueueInstances();
                synchronized (crawlerInstance)
                {

                    for(WaitQueueInstance waitQueueInstance : generatedWaitQueueInstances) {
                        crawlerInstance.getCrawlerDataStructure().getSeenReadyQueueInstances().remove(waitQueueInstance.getReadyQueueInstanceofThis());
                        crawlerInstance.getCrawlerDataStructure().getWaitQueue().add(waitQueueInstance);
                    }

                    for(ReadyQueueInstance readyQueueInstanceTemp : generatedReadyQueueInstances) {
                        if(!crawlerInstance.getCrawlerDataStructure().getSeenReadyQueueInstances().contains(readyQueueInstanceTemp)) {
                            crawlerInstance.getCrawlerDataStructure().getReadyQueue().add(readyQueueInstanceTemp);
                            crawlerInstance.notify();

                        }
                    }
                }
            }

        }
        finally {
            synchronized (crawlerInstance) {
                this.crawlerInstance.getCrawlerDataStructure().removeJob(((Job) obj));
            }
        }

    }

    @Override
    public void prepare(Map<String, String> stormConf, TopologyContext context, OutputCollector collector) {

    }

    @Override
    public void setRouter(IStreamRouter router) {

    }

    @Override
    public Fields getSchema() {
        return null;
    }

    @Override
    public String getExecutorId() {
        return null;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
