package edu.upenn.cis.cis455.crawler.mapreduce.Topology;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Job;
import edu.upenn.cis.stormlite.OutputFieldsDeclarer;
import edu.upenn.cis.stormlite.TopologyContext;
import edu.upenn.cis.stormlite.routers.IStreamRouter;
import edu.upenn.cis.stormlite.spout.IRichSpout;
import edu.upenn.cis.stormlite.spout.SpoutOutputCollector;
import edu.upenn.cis.stormlite.tuple.Fields;
import edu.upenn.cis.stormlite.tuple.Values;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class QueueSpout implements IRichSpout {

    static Logger logger = LogManager.getLogger(QueueSpout.class);

    String executorId = UUID.randomUUID().toString();

    SpoutOutputCollector outputCollector;

    private Crawler crawlerInstance;

    public QueueSpout() {
        this.crawlerInstance = Crawler.getMap_reduce_crawler();
    }

    public QueueSpout(Crawler crawler) {
        this.crawlerInstance = crawler;
    }

    @Override
    public void open(Map<String, String> config, TopologyContext topo, SpoutOutputCollector collector) {
        this.outputCollector = collector;
    }

    @Override
    public void close() {

    }

    @Override
    public void nextTuple() {
            ReadyQueueInstance readyQueueInstance = null;
                //polling for readyQ instance
                synchronized (crawlerInstance) {

                    readyQueueInstance = crawlerInstance.getCrawlerDataStructure().getReadyQueueInstance();

                    while (readyQueueInstance != null &&
                            crawlerInstance.getCrawlerDataStructure().getSeenReadyQueueInstances()
                                    .contains(readyQueueInstance)) {
                        //System.out.println("Already Seen This Task");
                        readyQueueInstance = crawlerInstance.getCrawlerDataStructure().getReadyQueueInstance();
                    }
                    try {
                        if (readyQueueInstance == null) {
//                            crawlerInstance.wait();
                            //The spout should not wait...it will do task when it exists
                        } else {
                            //valid readyQInstance
                                crawlerInstance.getCrawlerDataStructure().getSeenReadyQueueInstances().add(readyQueueInstance);
                                //this.crawlerThreadState = CrawlWorker.CrawlerThreadState.RUNNING
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            if(readyQueueInstance == null) {
                Thread.yield();
            }
            else
            {
                Job job = new Job(readyQueueInstance);
                synchronized (crawlerInstance) {
                    crawlerInstance.getCrawlerDataStructure().addJob(job);
                }
                this.outputCollector.emit(new Values<>(job));
            }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("readyQueueJob"));
    }


    @Override
    public String getExecutorId() {

        return executorId;
    }


    @Override
    public void setRouter(IStreamRouter router) {
        this.outputCollector.setRouter(router);
    }

}
