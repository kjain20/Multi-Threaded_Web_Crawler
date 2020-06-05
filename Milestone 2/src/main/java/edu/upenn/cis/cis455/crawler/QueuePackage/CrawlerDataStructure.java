package edu.upenn.cis.cis455.crawler.QueuePackage;


import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Job;

import java.util.*;


public class CrawlerDataStructure {
    Queue<ReadyQueueInstance> readyQueue = new LinkedList<>();
    Queue<WaitQueueInstance> waitQueue = new LinkedList<>();
    Set<ReadyQueueInstance> seenReadyQueueInstances = new HashSet<>();
    Set<String> visitedContent = new HashSet<>(); //this is to see if the crawled content matched
    Map<String, String> visitedDocumentUrlMap = new HashMap<>();

    List<String> jobsScheduled = new ArrayList<>();

    public List<String> getJobsScheduled() {
        return jobsScheduled;
    }

    public void addJob(Job job)
    {
        this.jobsScheduled.add(job.getJobId());
    }

    public void removeJob(Job job)
    {
        this.jobsScheduled.remove(job.getJobId());
    }

    int documentsInDB = 0;
    int maxDocumentsToBeStored = 100;

    public CrawlerDataStructure(String startURL,int documentsInDB, int maxDocumentsToBeStored) {
        this.documentsInDB = documentsInDB;
        this.maxDocumentsToBeStored = maxDocumentsToBeStored;
        this.readyQueue.add(new ReadyQueueInstance(new URLInfo(startURL)));
    }

    public Set<ReadyQueueInstance> getSeenReadyQueueInstances() {
        return seenReadyQueueInstances;
    }

    public void setSeenReadyQueueInstances(Set<ReadyQueueInstance> seenReadyQueueInstances) {
        this.seenReadyQueueInstances = seenReadyQueueInstances;
    }

    public Set<String> getVisitedContent() {
        return visitedContent;
    }

    public void setVisitedContent(Set<String> visitedContent) {
        this.visitedContent = visitedContent;
    }

    public CrawlerDataStructure() {
        this.readyQueue = new LinkedList<>();
        this.waitQueue = new LinkedList<>();
    }




    public synchronized Queue<WaitQueueInstance> getWaitQueue() {
        return waitQueue;
    }


    //this is required for waitQ to see if the Q is empty so that it terminates
    public synchronized boolean isWaitQueueEmpty() {
        if(this.waitQueue.isEmpty())
            return true;
        else
            return false;
    }

    public void setWaitQueue(Queue<WaitQueueInstance> waitQueue) {
        this.waitQueue = waitQueue;
    }

    public void setReadyQueue(Queue<ReadyQueueInstance> readyQueue) {
        this.readyQueue = readyQueue;
    }

    public Queue<ReadyQueueInstance> getReadyQueue() {
        return this.readyQueue;
    }

    public synchronized ReadyQueueInstance getReadyQueueInstance()
    {
            if(!readyQueue.isEmpty())
            {
                return readyQueue.remove();
            }
            else
            {
                return null;

            }
    }

    public int getQueuesSize() {
        return readyQueue.size() + waitQueue.size();
    }

    public boolean canIIndexNewDocument() {
        return this.documentsInDB < this.maxDocumentsToBeStored;
    }

    public void incrementIndexedDocumentCount(int value)
    {
        this.documentsInDB = this.documentsInDB + value;
    }

    public Map<String, String> getVisitedDocumentUrlMap() {
        return visitedDocumentUrlMap;
    }
}
