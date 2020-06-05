package edu.upenn.cis.cis455.model.responseModels;

import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfo;

import java.util.Date;
import java.util.Map;

public class URLMetaData {
    private URLInfo urlInfo;
    private Date lastModifiedDate;
    private int contentLength;
    private String contentType;
    private String data;
    private String methodType;
    private int statusCode;
    private Map<String, String> headers;

    public URLMetaData(URLInfo urlInfo, ResponseFromURLWebRequest urlResponse, String requestMethod,
                              Map<String, String> headers) {
        this.urlInfo = urlInfo;
        this.lastModifiedDate = new Date(urlResponse.getLastModifiedTime());
        this.contentLength = urlResponse.getContentLength();
        this.contentType = urlResponse.getContentType();
        this.data = urlResponse.getData();
        this.methodType = requestMethod;
        this.statusCode = urlResponse.getStatusCode();
        this.headers = urlResponse.getResponseHeaders();
    }

    public URLMetaData(URLInfo urlInfo, DomainInfo urlDataInfo) {
        this.urlInfo = urlInfo;
        this.lastModifiedDate = urlDataInfo.getLastModifiedTime();
        this.contentLength = urlDataInfo.getContentLength();
        this.contentType = urlDataInfo.getContentType();
        this.data = urlDataInfo.getData();
        this.methodType = urlDataInfo.getDomainInfoKey().getRequestType();
        this.statusCode = urlDataInfo.getStatusCode();
        this.headers = urlDataInfo.getHeaders();
    }


    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMethodType() {
        return methodType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static void main(String[] args) {

    }
}
