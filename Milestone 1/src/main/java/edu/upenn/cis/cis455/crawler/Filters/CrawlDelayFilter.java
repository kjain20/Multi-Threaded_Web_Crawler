package edu.upenn.cis.cis455.crawler.Filters;

import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.QueuePackage.WaitQueueInstance;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainCrawlDelayInfoKey;
import edu.upenn.cis.cis455.storage.DBManagers.DomainCrawlDelayDBManager;

import java.util.Date;

public class CrawlDelayFilter implements CrawlFilter {

    private DomainCrawlDelayDBManager domainCrawlDelayDBManager;

    public CrawlDelayFilter(DomainCrawlDelayDBManager domainCrawlDelayDBManager) {
        this.domainCrawlDelayDBManager = domainCrawlDelayDBManager;
    }


    @Override
    public void renderRequest(RequestContext requestContext) {

        if(requestContext.isValid() && requestContext.getPathMatcherOutput()!=null && requestContext.getPathMatcherOutput().isMatch()) {

        }
        else {
            requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
            requestContext.setDebugMessage("Wrong Host information");
            return;
        }


        long crawlDelay = requestContext.getRobotFileModel().getCrawlDelay(requestContext.getPathMatcherOutput().getUserAgent()) * 1000;

        DomainCrawlDelayInfoKey domainCrawlDelayInfoKey = new DomainCrawlDelayInfoKey(requestContext.getUrlInfo().getHostName(),
                requestContext.getUrlInfo().getPortNo(), requestContext.getUrlInfo().getURL().getProtocol());

        Date currentTime = new Date();
        DomainCrawlDelayDBManager.CrawlDelayManagerOutput crawlDelayManagerOutput = this.domainCrawlDelayDBManager.getCrawlerInformation(
                domainCrawlDelayInfoKey,currentTime,crawlDelay
        );
        requestContext.setCrawlDelayManagerOutput(crawlDelayManagerOutput);
        if(crawlDelayManagerOutput.isOkayToCrawl())
        {
            requestContext.setStatus(RequestContext.RequestContextStatus.SUCCESS);
        }
        else {
            requestContext.setStatus(RequestContext.RequestContextStatus.WAIT);
            requestContext.setDebugMessage("Should be Moved into Waiting Queue");
            requestContext.setWaitQueueInstance(new WaitQueueInstance(new ReadyQueueInstance(requestContext.getUrlInfo()),
                    crawlDelayManagerOutput.getDomainCrawlDelayInfo().getLastAccessedTime(),crawlDelay));
        }
    }

}
