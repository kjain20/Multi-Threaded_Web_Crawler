package edu.upenn.cis.cis455.crawler.Workers;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.QueuePackage.CrawlerDataStructure;
import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.QueuePackage.WaitQueueInstance;
import edu.upenn.cis.cis455.crawler.ResponseForReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.crawler.microservice.RequestMicroService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


//CrawlerThread
public class CrawlWorker extends Thread {

    private Crawler crawlerInstance;
    private CrawlerThreadState crawlerThreadState;
    private RequestMicroService requestService;


    //how to make use of the RequestMicroService by every worker?? -- use a service for every worker
    public CrawlWorker(Crawler crawler_object)
    {
        super();
        this.crawlerThreadState = CrawlerThreadState.IDLE;
        this.crawlerInstance = crawler_object;
        this.requestService = crawlerInstance.getRequestMicroService(); //this must be shared across workers
    }


    public CrawlerThreadState getCrawlerThreadState() {
        return crawlerThreadState;
    }

    public void endCrawl()
    {
        synchronized (crawlerInstance)
        {
            this.crawlerThreadState = CrawlerThreadState.TERMINATE;
        }
        crawlerInstance.exitThread(this);
    }

    public CrawlerThreadState getCrawlerThreadStatus() {
        return this.crawlerThreadState;
    }





    //this must poll for en-queue into waitQ ,(de-queue + process url---so talk to storage Factory)
    @Override
    public void run() {
        //for working till termination
        while (true) {
            ReadyQueueInstance readyQueueInstance = null;
            while (true) {
                //polling for readyQ instance
                synchronized (crawlerInstance) {
                    //to check if thread can pop an instance
                    if (crawlerInstance.shouldCrawlerTerminate()) {
                        crawlerInstance.notifyAll();
                        break;
                    }

                    if (!crawlerInstance.getCrawlerDataStructure().canIIndexNewDocument()) {
                        crawlerInstance.getCrawlerDataStructure().setWaitQueue(new LinkedList<>());
                        crawlerInstance.getCrawlerDataStructure().setReadyQueue(new LinkedList<>());
                        crawlerInstance.notifyAll();
                        break;
                    }

                    readyQueueInstance = crawlerInstance.getCrawlerDataStructure().getReadyQueueInstance();

                    while (readyQueueInstance != null &&
                            crawlerInstance.getCrawlerDataStructure().getSeenReadyQueueInstances()
                                    .contains(readyQueueInstance)) {
                        //System.out.println("Already Seen This Task");
                        readyQueueInstance = crawlerInstance.getCrawlerDataStructure().getReadyQueueInstance();
                    }
                    try {
                        if (readyQueueInstance == null) {
                            crawlerInstance.wait();
                        } else {
                            //valid readyQInstance
                            crawlerInstance.getCrawlerDataStructure().getSeenReadyQueueInstances().add(readyQueueInstance);
                            this.crawlerThreadState = CrawlerThreadState.RUNNING;
                            break;
                        }
                    } catch (Exception e) {
                        //System.out.println(e);
                    }
                }
            }

            if(readyQueueInstance == null) {
                endCrawl();
                return;
            }

            ResponseForReadyQueueInstance responseForReadyQueueInstance = null;
            List<ReadyQueueInstance> readyTasks = new ArrayList<>();
            List<WaitQueueInstance> waitTasks = new LinkedList<>();
            //process the request
            try {
                responseForReadyQueueInstance = requestService.processURL(readyQueueInstance);

                if(responseForReadyQueueInstance != null && responseForReadyQueueInstance.isSuccess()) {
                    readyTasks = responseForReadyQueueInstance.getReadyQueueInstances();
                    waitTasks = responseForReadyQueueInstance.getWaitQueueInstances();
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            synchronized (crawlerInstance)
            {
                this.crawlerThreadState = CrawlerThreadState.IDLE;

                for(WaitQueueInstance waitQueueInstance : waitTasks) {
                    crawlerInstance.getCrawlerDataStructure().getSeenReadyQueueInstances().remove(waitQueueInstance.getReadyQueueInstanceofThis());
                    crawlerInstance.getCrawlerDataStructure().getWaitQueue().add(waitQueueInstance);
                }

                for(ReadyQueueInstance readyQueueInstanceTemp : readyTasks) {
                    if(!crawlerInstance.getCrawlerDataStructure().getSeenReadyQueueInstances().contains(readyQueueInstanceTemp)) {
                        crawlerInstance.getCrawlerDataStructure().getReadyQueue().add(readyQueueInstanceTemp);
                        crawlerInstance.notify();

                    }
                }
            }
        }
    }



    public enum CrawlerThreadState
    {
        RUNNING,
        TERMINATE,
        IDLE;
    }

}

