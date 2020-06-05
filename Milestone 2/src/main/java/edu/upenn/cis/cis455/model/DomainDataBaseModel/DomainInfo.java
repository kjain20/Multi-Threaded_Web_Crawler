package edu.upenn.cis.cis455.model.DomainDataBaseModel;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import edu.upenn.cis.cis455.Util.GenerateMd5Hash;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

//This class will have the row of URL Table(s)
@Entity
public class DomainInfo implements Serializable {
    @PrimaryKey(sequence = "urlId")
    private Integer urlId;

    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    private DomainInfoKey domainInfoKey;

    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    private String hash_md5;

    private Date lastModifiedTime;
    private Date creationTime;
    private int contentLength;
    private String contentType;
    private String data;
    private Map<String, String> headers;
    private int statusCode;


    public DomainInfo()
    {

    }

    public DomainInfo(DomainInfoKey domainInfoKey) {
        this.domainInfoKey = domainInfoKey;
    }

    public DomainInfo(DocInfo docInfo, URLIdInfo urlWithoutDataInfo)
    {
        domainInfoKey = urlWithoutDataInfo.getDomainInfoKey();

        //id urls or docs?
        urlId = docInfo.getDocId();
        lastModifiedTime = urlWithoutDataInfo.getLastModifiedTime();
        creationTime = urlWithoutDataInfo.getCreatedTime();
        contentLength = docInfo.getContentLength();
        contentType = urlWithoutDataInfo.getContentType();
        data = docInfo.getData();
        headers = urlWithoutDataInfo.getHeaders();
        statusCode = urlWithoutDataInfo.getStatusCode();
        hash_md5 = docInfo.getHash_md5();

    }

    public DocInfo getDocInfo() {

        if(this.urlId == null) {
            return new DocInfo(data, contentLength);
        }

        return new DocInfo(urlId, data, contentLength);
    }

    public URLIdInfo getURLIdInfo() {

        URLIdInfo urlOnlyInfo = new URLIdInfo(domainInfoKey, urlId);
        urlOnlyInfo.setContentType(contentType);
        urlOnlyInfo.setCreatedTime(creationTime);
        urlOnlyInfo.setDocId(urlId);
        urlOnlyInfo.setHeaders(headers);
        urlOnlyInfo.setLastModifiedTime(lastModifiedTime);
        urlOnlyInfo.setStatusCode(statusCode);
        return urlOnlyInfo;
    }



    public Integer getUrlId() {
        return urlId;
    }

    public void setUrlId(Integer urlId) {
        this.urlId = urlId;
    }

    public DomainInfoKey getDomainInfoKey() {
        return domainInfoKey;
    }

    public void setDomainInfoKey(DomainInfoKey domainInfoKey) {
        this.domainInfoKey = domainInfoKey;
    }

    public String getHash_md5() {
        return hash_md5;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        this.hash_md5 = GenerateMd5Hash.hash(this.data);
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
