package edu.upenn.cis.cis455.model.DomainDataBaseModel;

import java.util.Date;

public class DomainCrawlDelayInfo {
    private DomainCrawlDelayInfoKey domainCrawlDelayInfoKey;
    private Date lastAccessedTime;
    private String host;
    private int port;

    public DomainCrawlDelayInfo(DomainCrawlDelayInfoKey domainCrawlDelayInfoKey, Date lastAccessedTime) {
        this.domainCrawlDelayInfoKey = domainCrawlDelayInfoKey;
        this.lastAccessedTime = lastAccessedTime;
        this.host = domainCrawlDelayInfoKey.getHost();
        this.port = domainCrawlDelayInfoKey.getPort();
    }

    public DomainCrawlDelayInfoKey getDomainCrawlDelayInfoKey() {
        return domainCrawlDelayInfoKey;
    }

    public void setDomainCrawlDelayInfoKey(DomainCrawlDelayInfoKey domainCrawlDelayInfoKey) {
        this.domainCrawlDelayInfoKey = domainCrawlDelayInfoKey;
    }

    public Date getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(Date lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
