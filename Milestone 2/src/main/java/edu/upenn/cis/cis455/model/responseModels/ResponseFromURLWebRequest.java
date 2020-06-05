package edu.upenn.cis.cis455.model.responseModels;

import java.util.Map;

public class ResponseFromURLWebRequest {
    private int statusCode;
    private String data;
    private String contentType;
    private int contentLength;
    private long lastModifiedTime;
    private Map<String, String> responseHeaders;

    public ResponseFromURLWebRequest(int statusCode, String data, String contentType, int contentLength, long lastModifiedTime, Map<String, String> responseHeaders) {
        this.statusCode = statusCode;
        this.data = data;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.lastModifiedTime = lastModifiedTime;
        this.responseHeaders = responseHeaders;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
}
