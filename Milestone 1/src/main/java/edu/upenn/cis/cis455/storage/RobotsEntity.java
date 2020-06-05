package edu.upenn.cis.cis455.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Entity
public class RobotsEntity implements Serializable {
    @PrimaryKey
    private KeyClass robotFileKey;
    private String robotStructure;
    private Date lastUpdateDate;
    private Date creationDate;
    private String robotFilePath;
    private Map<String, String> additionalInfo = new HashMap<>();

    public RobotsEntity()
    {

    }

    public RobotsEntity(String host,int port)
    {
        robotFileKey = new KeyClass(host,port);
    }

    public RobotsEntity(String host,int port, Map<String, String> additionalInfo)
    {
        this.robotFileKey = new KeyClass(host,port);
        this.additionalInfo = additionalInfo;
    }





    public RobotsEntity(KeyClass robotFileKey, String robotStructure, Date lastUpdateDate, Date creationDate, String robotFilePath, Map<String, String> additionalInfo) {
        this.robotFileKey = robotFileKey;
        this.robotStructure = robotStructure;
        this.lastUpdateDate = lastUpdateDate;
        this.creationDate = creationDate;
        this.robotFilePath = robotFilePath;
        this.additionalInfo = additionalInfo;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
