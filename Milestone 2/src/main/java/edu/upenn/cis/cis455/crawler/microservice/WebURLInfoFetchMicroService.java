package edu.upenn.cis.cis455.crawler.microservice;

import com.sleepycat.je.Transaction;
import edu.upenn.cis.cis455.crawler.Constants;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfoKey;
import edu.upenn.cis.cis455.model.responseModels.ResponseFromURLWebRequest;
import edu.upenn.cis.cis455.model.responseModels.URLMetaData;
import edu.upenn.cis.cis455.storage.DBManagers.URLDBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class WebURLInfoFetchMicroService {

    private WebURLDataFetchMicroService webURLDataFetchMicroService;
    private URLDBManager urldbManager;

    private final String GET_METHOD = "GET";
    private final String HEAD_METHOD = "HEAD";


    public WebURLInfoFetchMicroService(WebURLDataFetchMicroService webURLDataFetchMicroService, URLDBManager urldbManager) {
        this.webURLDataFetchMicroService = webURLDataFetchMicroService;
        this.urldbManager = urldbManager;
    }

    public URLMetaData fetchHeadInformationLive(URLInfo urlInfo, Map<String, String> headers) {
        ResponseFromURLWebRequest urlResponse = webURLDataFetchMicroService.getContentFromUrlInfo(urlInfo,
                "HEAD", headers);
        if(urlResponse == null) {
            return null;
        }
        return new URLMetaData(urlInfo, urlResponse, HEAD_METHOD, headers);
    }

    public URLMetaData fetchGetInformationLive(URLInfo urlInfo, Map<String, String> headers) {
        ResponseFromURLWebRequest urlResponse = webURLDataFetchMicroService.getContentFromUrlInfo(urlInfo,
                "GET", Constants.getThis().getHEAD_HEADERS());
        if(urlResponse == null || urlResponse.getStatusCode() < 200 || urlResponse.getStatusCode() >= 300) {
            return null;
        }
        return new URLMetaData(urlInfo, urlResponse, GET_METHOD, headers);
    }


    public URLMetaData fetchGetInformationFromDb(URLInfo urlInfo, Transaction txn) {

        DomainInfoKey urlDataInfoKey = new DomainInfoKey(getProtocol(urlInfo), urlInfo.getHostName(),
                urlInfo.getPortNo(), urlInfo.getFilePath(), GET_METHOD);
        DomainInfo urlDataInfo = urldbManager.getURLDataInfo(urlDataInfoKey, txn);
        if(urlDataInfo == null) {
            return null;
        }
        return new URLMetaData(urlInfo, urlDataInfo);
    }


    private String getProtocol(URLInfo urlInfo) {
        return urlInfo.isSecure()?"https":"http";
    }

}
