package edu.upenn.cis.cis455.crawler.Workers;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.QueuePackage.WaitQueueInstance;

import java.util.*;

public class WaitQueueWorker extends Thread {
    private Crawler crawlerInstance;
    private CrawlWorker.CrawlerThreadState crawlerThreadState;
    private int pollDelay;


    public WaitQueueWorker(Crawler crawlerInstance,int pollDelay) {
        super();
        this.crawlerInstance = crawlerInstance;
        this.pollDelay = pollDelay;
        crawlerThreadState = CrawlWorker.CrawlerThreadState.RUNNING;
    }


    public void endCrawl()
    {
        this.crawlerThreadState = CrawlWorker.CrawlerThreadState.TERMINATE;
    }


    public void run() {
        while (true) {
            //dequeue synchronously, what to store in queue??

            if(crawlerInstance.getStatus() == Crawler.CrawlerStatus.WAITING_FOR_TERMINATE || crawlerInstance.getStatus() == Crawler.CrawlerStatus.TERMINATE)
            {
                endCrawl();
                return;
            }

            synchronized (crawlerInstance) {
                this.crawlerThreadState = CrawlWorker.CrawlerThreadState.RUNNING;
                Queue<WaitQueueInstance> waitQueue = new LinkedList<>();
                List<ReadyQueueInstance> readyQueueInstanceList = new ArrayList<>();
                for (WaitQueueInstance waitQueueInstance : crawlerInstance.getCrawlerDataStructure().getWaitQueue()) {
                    if (crawlDelayNotPassed(waitQueueInstance)) {
                        waitQueue.add(waitQueueInstance);
                    }
                    else {
                        readyQueueInstanceList.add(waitQueueInstance.getReadyQueueInstanceofThis());
                    }
                }
                crawlerInstance.getCrawlerDataStructure().getReadyQueue().addAll(readyQueueInstanceList);
                crawlerInstance.getCrawlerDataStructure().setWaitQueue(waitQueue);
                for(int i = 0;i<readyQueueInstanceList.size();i++)
                {
                    crawlerInstance.notify();
                }
                this.crawlerThreadState = CrawlWorker.CrawlerThreadState.IDLE;

            }

            try {
                sleep(this.pollDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean crawlDelayNotPassed(WaitQueueInstance waitQueueInstance)
    {
        Date current_date = new Date();
        Date insertion_time_of_url = waitQueueInstance.getCurrentInsertionTime();
        if(current_date.getTime() - insertion_time_of_url.getTime() > waitQueueInstance.getDeltaWaitTime())
            return false;

        return true;
    }


    public CrawlWorker.CrawlerThreadState getCrawlerThreadState() {
        return crawlerThreadState;
    }
}
