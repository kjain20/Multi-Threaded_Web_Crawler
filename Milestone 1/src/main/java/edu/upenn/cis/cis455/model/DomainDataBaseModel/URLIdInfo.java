package edu.upenn.cis.cis455.model.DomainDataBaseModel;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import java.util.Date;
import java.util.Map;

@Entity
public class URLIdInfo {
    @PrimaryKey
    private DomainInfoKey domainInfoKey;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE, name = "docId")
    private Integer docId;
    private Date lastModifiedTime;
    private Date createdTime;
    private String contentType;
    private Map<String, String> headers;
    private int statusCode;

    public URLIdInfo() {
    }

    public URLIdInfo(DomainInfoKey domainInfoKey, Integer docId) {
        this.domainInfoKey = domainInfoKey;
        this.docId = docId;
    }

    public DomainInfoKey getDomainInfoKey() {
        return domainInfoKey;
    }

    public void setDomainInfoKey(DomainInfoKey domainInfoKey) {
        this.domainInfoKey = domainInfoKey;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
