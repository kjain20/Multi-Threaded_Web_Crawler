package edu.upenn.cis.cis455.crawler;

import edu.upenn.cis.cis455.crawler.Filters.RequestContext;
import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.QueuePackage.WaitQueueInstance;

import java.util.ArrayList;
import java.util.List;

//this class will go as the response to the url request
public class ResponseForReadyQueueInstance {
    List<ReadyQueueInstance> readyQueueInstances = new ArrayList<>();
    List<WaitQueueInstance> waitQueueInstances = new ArrayList<>();
    private boolean isGood = true;
    private boolean processed = false;
    private String body = "";
    private RequestContext requestContext;

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public List<ReadyQueueInstance> getReadyQueueInstances() {
        return readyQueueInstances;
    }



    public void setReadyQueueInstances(List<ReadyQueueInstance> readyQueueInstances) {
        this.readyQueueInstances = readyQueueInstances;
    }

    public List<WaitQueueInstance> getWaitQueueInstances() {
        return waitQueueInstances;
    }

    public void setWaitQueueInstances(List<WaitQueueInstance> waitQueueInstances) {
        this.waitQueueInstances = waitQueueInstances;
    }

    public ResponseForReadyQueueInstance(boolean isGood) {
        this.isGood = isGood;
    }

    public ResponseForReadyQueueInstance(boolean isGood, String body) {
        this.isGood = isGood;
        this.body = body;
    }

    public ResponseForReadyQueueInstance(List<ReadyQueueInstance> readyQueueInstances, List<WaitQueueInstance> waitQueueInstances) {
        this.readyQueueInstances = readyQueueInstances;
        this.waitQueueInstances = waitQueueInstances;
    }

    public ResponseForReadyQueueInstance(boolean isGood, RequestContext requestContext) {
        this.isGood = isGood;
        this.requestContext = requestContext;
    }

    public ResponseForReadyQueueInstance(List<ReadyQueueInstance> readyQueueInstances, List<WaitQueueInstance> waitQueueInstances, boolean isGood, boolean processed, RequestContext requestContext) {
        this.readyQueueInstances = readyQueueInstances;
        this.waitQueueInstances = waitQueueInstances;
        this.isGood = isGood;
        this.processed = processed;
        this.requestContext = requestContext;
    }

    public boolean isSuccess() {
        return isGood;
    }

    public void setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
}
