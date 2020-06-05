package edu.upenn.cis.cis455.storage;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

import java.io.Serializable;

@Persistent
public class KeyClass implements Serializable {

    @KeyField(1) private String host;
    @KeyField(2) private int port;

    public KeyClass()
    {

    }

    public KeyClass(String host,int port)
    {
        this.host = host;
        this.port = port;
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
