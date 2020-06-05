package edu.upenn.cis.cis455.storage.DBManagers;

import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainCrawlDelayInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainCrawlDelayInfoKey;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class DomainCrawlDelayDBManager {

    private ConcurrentHashMap<DomainCrawlDelayInfoKey,DomainCrawlDelayInfo> crawlDelaysMap = new ConcurrentHashMap<>();

    public CrawlDelayManagerOutput getCrawlerInformation(DomainCrawlDelayInfoKey domainCrawlDelayInfoKey, Date newDate, long crawlDelay)
    {
        CrawlDelayManagerOutput crawlDelayManagerOutput[] = new CrawlDelayManagerOutput[1];
        crawlDelayManagerOutput[0] = new CrawlDelayManagerOutput(false,null);

        crawlDelaysMap.compute(domainCrawlDelayInfoKey,(key,value)-> {
            if (value == null) {
                value = new DomainCrawlDelayInfo(domainCrawlDelayInfoKey,newDate);
                crawlDelayManagerOutput[0].setOkayToCrawl(true);
            }
            else
            {
                //System.out.println(newDate.getTime());
                //System.out.println(value.getLastAccessedTime());
                if(newDate.getTime() > value.getLastAccessedTime().getTime() +  crawlDelay)
                {
                    value.setLastAccessedTime(newDate);
                    crawlDelayManagerOutput[0].setOkayToCrawl(true);
                }
            }
            crawlDelayManagerOutput[0].setDomainCrawlDelayInfo(value);
            return value;
        });

        return crawlDelayManagerOutput[0];
    }

        public class CrawlDelayManagerOutput
    {
        private boolean isOkayToCrawl;
        private DomainCrawlDelayInfo domainCrawlDelayInfo;

        public CrawlDelayManagerOutput(boolean isOkayToCrawl, DomainCrawlDelayInfo domainCrawlDelayInfo) {
            this.isOkayToCrawl = isOkayToCrawl;
            this.domainCrawlDelayInfo = domainCrawlDelayInfo;
        }

        public boolean isOkayToCrawl() {
            return isOkayToCrawl;
        }

        public void setOkayToCrawl(boolean okayToCrawl) {
            isOkayToCrawl = okayToCrawl;
        }

        public DomainCrawlDelayInfo getDomainCrawlDelayInfo() {
            return domainCrawlDelayInfo;
        }

        public void setDomainCrawlDelayInfo(DomainCrawlDelayInfo domainCrawlDelayInfo) {
            this.domainCrawlDelayInfo = domainCrawlDelayInfo;
        }
    }

}
