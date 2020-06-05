package edu.upenn.cis.cis455.crawler.microservice;

import edu.upenn.cis.cis455.crawler.Constants;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.model.responseModels.ResponseFromURLWebRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebURLDataFetchMicroService {


    private final String HTTP = "http";
    private final String HTTPS = "https";


    Logger logger = LogManager.getLogger(WebURLDataFetchMicroService.class);


    public ResponseFromURLWebRequest getContentFromURL(URL url, String method, Map<String, String> headers) {

        if (url == null) {
            return null;
        }

        if (url.getProtocol().equals(HTTP)) {
            return getHttpUrlContent(url, method, headers);
        } else if (url.getProtocol().equals(HTTPS)) {
            return getHttpsUrlContent(url, method, headers);
        }
        return null;
    }


    //if DomainInfo object is given as the argument, get the Content from URL...This will be called by worker via ReadyQueueInstance

    public ResponseFromURLWebRequest getContentFromUrlInfo(URLInfo urlInfo, String method, Map<String, String> headers) {

        URL url = null;
        try {
            if (urlInfo.isSecure()) {
                url = new URL(HTTPS, urlInfo.getHostName(), urlInfo.getPortNo(), urlInfo.getFilePath());
            } else {
                url = new URL(HTTP, urlInfo.getHostName(), urlInfo.getPortNo(), urlInfo.getFilePath());
            }
        } catch (MalformedURLException e) {
            //logger.info("BAD URL");
        }

        return getContentFromURL(url, method, headers);
    }


    private ResponseFromURLWebRequest getHttpUrlContent(URL url, String method, Map<String, String> headers) {

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(method);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            httpURLConnection.setReadTimeout(Constants.getThis().getHTTP_CONNECTION_TIME_OUT());
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.connect();
            String data = readDataFromWebConnectionInputStream(httpURLConnection.getInputStream());
            int statusCode = httpURLConnection.getResponseCode();
            String contentType = httpURLConnection.getContentType();
            int contentLength = httpURLConnection.getContentLength();
            long lastModifiedTime = httpURLConnection.getLastModified();
            return new ResponseFromURLWebRequest(statusCode, data, contentType, contentLength, lastModifiedTime, getHeaders(httpURLConnection.getHeaderFields()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private ResponseFromURLWebRequest getHttpsUrlContent(URL url,String method,Map<String,String> headers)
    {
        try
        {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod(method);
            for(Map.Entry<String,String> entry: headers.entrySet()) {
                httpsURLConnection.setRequestProperty(entry.getKey(),entry.getValue());
            }
            httpsURLConnection.setConnectTimeout(Constants.getThis().getHTTPS_CONNECTION_TIME_OUT());
            httpsURLConnection.setInstanceFollowRedirects(false);
            httpsURLConnection.connect();
            String data = readDataFromWebConnectionInputStream(httpsURLConnection.getInputStream());
            int statusCode = httpsURLConnection.getResponseCode();
            String contentType = httpsURLConnection.getContentType();
            int contentLength = httpsURLConnection.getContentLength();
            long lastModifiedTime = httpsURLConnection.getLastModified();
            return new ResponseFromURLWebRequest(statusCode,data,contentType,contentLength,lastModifiedTime, getHeaders(httpsURLConnection.getHeaderFields()));

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;

    }

    private Map<String, String> getHeaders(Map<String, List<String>> given) {

        Map<String, String> result = new HashMap<>();
        for(Map.Entry<String, List<String>> entry: given.entrySet()) {
            if(entry.getValue()!=null && entry.getValue().size() > 0) {
                result.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        return result;
    }


    private String readDataFromWebConnectionInputStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder inputStreamDataBuilder = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            inputStreamDataBuilder.append(line);
            inputStreamDataBuilder.append("\n");
        }
        return inputStreamDataBuilder.toString();
    }
}
