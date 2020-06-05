package edu.upenn.cis.cis455.crawler.QueuePackage;

import edu.upenn.cis.cis455.crawler.info.URLInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ReadyQueueInstance {

    private URLInfo requestURLObject;

    public ReadyQueueInstance(URLInfo requestURLObject) {
        this.requestURLObject = requestURLObject;
    }

    public URLInfo getRequestURLObject() {
        return requestURLObject;
    }

    public void setRequestURLObject(URLInfo requestURLObject) {
        this.requestURLObject = requestURLObject;
    }


    public ReadyQueueInstance(String docURL)
    {

        this.requestURLObject = new URLInfo(docURL);
    }
    public String getSite() {
        return this.requestURLObject.getHostName();
    }

    public int getPort() {
        return this.requestURLObject.getPortNo();
    }

    public boolean isSecure() {
        return this.requestURLObject.isSecure();
    }

    //setters are only inside URLInfoClass
    public boolean equals(Object obj)
    {
        if(obj instanceof ReadyQueueInstance)
        {
            ReadyQueueInstance newReadyQueueInstance = (ReadyQueueInstance) obj;
            if(this.getRequestURLObject().equals(newReadyQueueInstance.getRequestURLObject()))
            {
                return true;
            }
        }
        return false;
    }

    public int hashCode()
    {
        return this.getRequestURLObject().hashCode();
    }


    public boolean isValid() {
        return requestURLObject.isValid();
    }

    public static void main(String[] args) {
        Set<ReadyQueueInstance> readyQueueInstances = new HashSet<>();
        readyQueueInstances.add(new ReadyQueueInstance(new URLInfo("http://www.google.com")));
        readyQueueInstances.add(new ReadyQueueInstance(new URLInfo("http://www.google.com")));
        //System.out.println(readyQueueInstances.size());

    }


}


