package edu.upenn.cis.cis455.crawler.microservice;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.sleepycat.je.Transaction;
import edu.upenn.cis.cis455.crawler.Constants;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainRobotInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainRobotInfoKey;
import edu.upenn.cis.cis455.model.requestModels.RobotFileModel;
import edu.upenn.cis.cis455.model.responseModels.ResponseFromURLWebRequest;
import edu.upenn.cis.cis455.storage.DBManagers.DomainRobotDBManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.LogManager;

public class RobotsMicroService {

    private DomainRobotDBManager domainRobotDBManager;
    private WebURLDataFetchMicroService webURLDataFetchMicroService;


    private final Gson gson = new Gson();

    @Inject
    public RobotsMicroService(DomainRobotDBManager domainRobotDBManager, WebURLDataFetchMicroService webURLDataFetchMicroService) {
        this.domainRobotDBManager = domainRobotDBManager;
        this.webURLDataFetchMicroService = webURLDataFetchMicroService;
    }

    public DomainRobotInfo getDomainInfo(String protocol, String host, int port) {
        DomainRobotInfoKey domainRobotInfoKey = new DomainRobotInfoKey(protocol, host, port);
        DomainRobotInfo domainRobotInfoFromDB = domainRobotDBManager.getDomainInfo(domainRobotInfoKey);
        if (domainRobotInfoFromDB == null) {
            DomainRobotInfo newDomainRobotInfo = getNewRobot(protocol, host, port);
            if (newDomainRobotInfo == null) {
                return null;
            }
            domainRobotDBManager.insertDomainIntoDB(newDomainRobotInfo);
            return newDomainRobotInfo;
        }
        return domainRobotInfoFromDB;

    }


    public DomainRobotInfo getNewRobot(String protocol, String host, int port) {
        String robotPath = Constants.getThis().getROBOT_FILE_PATH();
        try {
            URL url = new URL(protocol, host, port, robotPath);
            ResponseFromURLWebRequest responseFromURLWebRequest = this.webURLDataFetchMicroService.getContentFromURL(url,
                    "GET", new HashMap<>());
            if (responseFromURLWebRequest.getData() == null) {
                return null;
            }

            DomainRobotInfo domainRobotInfo = new DomainRobotInfo(protocol, host, port);
            domainRobotInfo.setRobotFilePath(robotPath);
            domainRobotInfo.setRobotFileString(responseFromURLWebRequest.getData());
            return domainRobotInfo;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
