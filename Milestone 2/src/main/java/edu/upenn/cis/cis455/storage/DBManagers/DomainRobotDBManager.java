package edu.upenn.cis.cis455.storage.DBManagers;


import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainRobotInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainRobotInfoKey;


import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//this must open connection to DB, check for domain
//crawldelays
public class DomainRobotDBManager {

    Map<DomainRobotInfoKey, DomainRobotInfo> inMemoryDatabase = new ConcurrentHashMap<>();


    public DomainRobotDBManager() {
    }


    public DomainRobotInfo getDomainInfo(DomainRobotInfoKey domainRobotInfoKey) {
        return inMemoryDatabase.get(domainRobotInfoKey);
    }

    public DomainRobotInfo insertDomainIntoDB(DomainRobotInfo domainRobotInfo) {
        DomainRobotInfo[] output = new DomainRobotInfo[1];
        inMemoryDatabase.compute(domainRobotInfo.getDomainRobotInfoKey(), (key, value) -> {
            if (value == null) {
                domainRobotInfo.setCreationTime(new Date());
                domainRobotInfo.setLastUpdatedTime(new Date());
                output[0] = domainRobotInfo;
                return domainRobotInfo;
            } else {
                value.setLastUpdatedTime(new Date());
                output[0] = value;
                return value;
            }
        });
        return output[0];
    }

    //this deletes the domain
    //required when robots.txt file is modified
    public void deleteDomainRobot(DomainRobotInfo domainRobotInfo) {
        inMemoryDatabase.remove(domainRobotInfo.getDomainRobotInfoKey());
    }

    public static void main(String[] args) {
//        DomainRobotDBManager domainRobotDBManager = new DomainRobotDBManager();
//        DomainCrawlDelayInfoKey domainCrawlDelayInfoKey = new DomainCrawlDelayInfoKey("www.google.com", 80);

    }

}
