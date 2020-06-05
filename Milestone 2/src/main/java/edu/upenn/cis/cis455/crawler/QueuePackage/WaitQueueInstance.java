package edu.upenn.cis.cis455.crawler.QueuePackage;


import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.storage.StorageImpl;

import java.util.Date;

//this is WaitQueue Instance --- this will be pushed into the Wait Queue of Crawler class by the cleaner thread
public class WaitQueueInstance {

    private Date currentInsertionTime;
    private long deltaWaitTime;
    private ReadyQueueInstance readyQueueInstanceofThis;// wrapper around readyQ


    //constructor must have place to take all three arguments before pushing

    public WaitQueueInstance(ReadyQueueInstance readyQueueInstanceofThis,Date currentInsertionTime,long deltaWaitTime)
    {
        this.readyQueueInstanceofThis = readyQueueInstanceofThis;
        this.currentInsertionTime = currentInsertionTime;
        this.deltaWaitTime = deltaWaitTime;
    }

    public ReadyQueueInstance getReadyQueueInstanceofThis() {
        return this.readyQueueInstanceofThis;
    }

    public Date getCurrentInsertionTime() {
        return currentInsertionTime;
    }

    public long getDeltaWaitTime() {
        return deltaWaitTime;
    }

}
