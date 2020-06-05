package edu.upenn.cis.cis455.model.DomainDataBaseModel;

import java.util.HashSet;
import java.util.Set;

public class DomainCrawlDelayInfoKey {

    private String host;
    private int port;
    private String protocol;

    public DomainCrawlDelayInfoKey(String host, int port) {
        this.host = host;
        this.port = port;
        this.protocol = "http";
    }

    public DomainCrawlDelayInfoKey(String host, int port, String protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
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

    public boolean equals(Object obj) {
        if (obj instanceof DomainCrawlDelayInfoKey) {
            DomainCrawlDelayInfoKey keyInstance = (DomainCrawlDelayInfoKey) obj;
            if (this.host.equals(keyInstance.getHost()) && this.port == keyInstance.getPort() && this.protocol.equals(keyInstance.protocol)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        String key = protocol + "://" + host + ":" + String.valueOf(port);
        return key.hashCode();
    }

    public static void main(String[] args) {
        Set<DomainCrawlDelayInfoKey> domainCrawlDelayInfoKeySet = new HashSet<>();
        domainCrawlDelayInfoKeySet.add(new DomainCrawlDelayInfoKey("www.google.com", 80));
        domainCrawlDelayInfoKeySet.add(new DomainCrawlDelayInfoKey("www.google.com", 80));
        //System.out.println(domainCrawlDelayInfoKeySet.size());
    }
}
