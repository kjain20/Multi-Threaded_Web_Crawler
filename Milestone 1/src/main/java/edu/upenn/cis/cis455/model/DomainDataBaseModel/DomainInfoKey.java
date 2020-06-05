package edu.upenn.cis.cis455.model.DomainDataBaseModel;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

import java.io.Serializable;

//we know the url attribs required to form the key --> essentially the domain name along with path

@Persistent
public class DomainInfoKey implements Serializable {

    @KeyField(1) private String protocol;
    @KeyField(2) private String host;
    @KeyField(3) private int port;
    @KeyField(4) private String resourcePath;
    @KeyField(5) private String requestType;

    public DomainInfoKey()
    {

    }



    public DomainInfoKey(String protocol, String host, int port, String resourcePath, String requestType) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.resourcePath = resourcePath;
        this.requestType = requestType;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
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

    public String getResourcePath() {
        return resourcePath;
    }

    public String getCompletePath()
    {
        return this.protocol+"://"+this.host;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
