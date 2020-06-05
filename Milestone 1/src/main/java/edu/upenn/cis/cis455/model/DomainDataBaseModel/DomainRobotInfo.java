package edu.upenn.cis.cis455.model.DomainDataBaseModel;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//@Entity
public class DomainRobotInfo implements Serializable {

//    @PrimaryKey(sequence = "domainRobotInfoKey")
//    private Long domain_Id;
    //@SecondaryKey(relate = Relationship.ONE_TO_ONE)
    private DomainRobotInfoKey domainRobotInfoKey;
    private String robotFileString;
    private Date lastUpdatedTime;
    private Date creationTime;
    private String robotFilePath;
    private Map<String, String> additionalAttributes = new HashMap<>();
    private String protocol;
    private String host;
    private int port;

    public DomainRobotInfo()
    {

    }

    public DomainRobotInfo(String protocol, String host, int port) {
        this.domainRobotInfoKey = new DomainRobotInfoKey(protocol,host,port);
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

//    public Long getDomain_Id() {
//        return domain_Id;
//    }
//
//    public void setDomain_Id(Long domain_Id) {
//        this.domain_Id = domain_Id;
//    }

    public DomainRobotInfoKey getDomainRobotInfoKey() {
        return domainRobotInfoKey;
    }

    public void setDomainRobotInfoKey(DomainRobotInfoKey domainRobotInfoKey) {
        this.domainRobotInfoKey = domainRobotInfoKey;
    }

    public String getRobotFileString() {
        return robotFileString;
    }

    public void setRobotFileString(String robotFileString) {
        this.robotFileString = robotFileString;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getRobotFilePath() {
        return robotFilePath;
    }

    public void setRobotFilePath(String robotFilePath) {
        this.robotFilePath = robotFilePath;
    }

    public Map<String, String> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Map<String, String> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
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


}
