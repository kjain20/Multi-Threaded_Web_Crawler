package edu.upenn.cis.cis455.crawler.Filters;

import com.sleepycat.je.Transaction;
import edu.upenn.cis.cis455.Util.PathMatcher;
import edu.upenn.cis.cis455.crawler.QueuePackage.WaitQueueInstance;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.model.requestModels.RobotFileModel;
import edu.upenn.cis.cis455.model.responseModels.URLMetaData;
import edu.upenn.cis.cis455.storage.DBManagers.DomainCrawlDelayDBManager;


//ProcessContext is required to send all the required request parameters which are
//set on rolling basis
public class RequestContext {
    private URLInfo urlInfo;
    private RobotFileModel robotFileModel;
    private RequestContextStatus requestContextStatus;
    private String debugMessage;
    private Transaction txn;
    String hashMd5;
    int maxDocumentRetrievable_size;
    private PathMatcherOutput pathMatcherOutput;
    private DomainCrawlDelayDBManager.CrawlDelayManagerOutput crawlDelayManagerOutput;
    private WaitQueueInstance waitQueueInstance;
    private URLMetaData urlMetaDataFromDB;
    private URLMetaData headMetaData;
    private URLMetaData getMetaData;


    public RequestContext(URLInfo urlInfo, Transaction txn, int maxDocumentRetrievable_size) {
        this.urlInfo = urlInfo;
        this.txn = txn;
        this.maxDocumentRetrievable_size = maxDocumentRetrievable_size;
        this.requestContextStatus = RequestContextStatus.SUCCESS;
    }

    public boolean isWaiting() {
        return requestContextStatus == RequestContextStatus.WAIT;
    }

    public enum RequestContextStatus {
        SUCCESS,
        FAILURE,
        WAIT;
    }

    public URLInfo getUrlInfo() {
        return urlInfo;
    }

    public RequestContextStatus getStatus() {
        return requestContextStatus;
    }

    public RobotFileModel getRobotFileModel() {
        return robotFileModel;
    }

    public void setWaitQueueInstance(WaitQueueInstance waitQueueInstance) {
        this.waitQueueInstance = waitQueueInstance;
    }

    public void setUrlInfo(URLInfo urlInfo) {
        this.urlInfo = urlInfo;
    }

    public void setStatus(RequestContextStatus status) {
        this.requestContextStatus = status;
    }

    public void setRobotFileModel(RobotFileModel robotFileModel) {
        this.robotFileModel = robotFileModel;
    }

    public void setCrawlDelayManagerOutput(DomainCrawlDelayDBManager.CrawlDelayManagerOutput crawlDelayManagerOutput) {
        this.crawlDelayManagerOutput = crawlDelayManagerOutput;
    }

    public boolean isValid() {
        return requestContextStatus == RequestContextStatus.SUCCESS;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public RequestContextStatus getRequestContextStatus() {
        return requestContextStatus;
    }

    public Transaction getTxn() {
        return txn;
    }

    public int getMaxDocumentRetrievable_size() {
        return maxDocumentRetrievable_size;
    }

    public PathMatcherOutput getPathMatcherOutput() {
        return pathMatcherOutput;
    }

    public WaitQueueInstance getWaitQueueInstance() {
        return waitQueueInstance;
    }

    public void setRequestContextStatus(RequestContextStatus requestContextStatus) {
        this.requestContextStatus = requestContextStatus;
    }

    public void setMaxDocumentRetrievable_size(int maxDocumentRetrievable_size) {
        this.maxDocumentRetrievable_size = maxDocumentRetrievable_size;
    }

    public DomainCrawlDelayDBManager.CrawlDelayManagerOutput getCrawlDelayManagerOutput() {
        return crawlDelayManagerOutput;
    }

    public void setPathMatcherOutput(PathMatcherOutput pathMatcherOutput) {
        this.pathMatcherOutput = pathMatcherOutput;
    }

    public URLMetaData getUrlMetaDataFromDB() {
        return urlMetaDataFromDB;
    }

    public void setUrlMetaDataFromDB(URLMetaData urlMetaData) {
        this.urlMetaDataFromDB = urlMetaData;
    }

    public void setHeadMetaData(URLMetaData headMetaData) {
        this.headMetaData = headMetaData;
    }

    public void setGetMetaData(URLMetaData getMetaData) {
        this.getMetaData = getMetaData;
    }

    public URLMetaData getHeadMetaData() {
        return headMetaData;
    }

    public URLMetaData getGetMetaData() {
        return getMetaData;
    }

    public void setHashMd5(String hashMd5) {
        this.hashMd5 = hashMd5;
    }

    public String getHashMd5() {
        return hashMd5;
    }

    public boolean didFail() {
        return requestContextStatus == RequestContextStatus.FAILURE;
    }
}


