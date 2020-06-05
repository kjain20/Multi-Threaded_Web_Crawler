package edu.upenn.cis.cis455.crawler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    private static Constants constantClass;

    private final Map<String, String> HEAD_REQUEST_HEADERS = new HashMap<String,String>(){
        {put("User-Agent","cis455crawler");}
    };
    private final Map<String, String> GET_HEADERS = new HashMap<String,String>(){
        {put("User-Agent","cis455crawler");}
    };
    private final List<String> allowedUserAgents = Arrays.asList("cis455crawler", "*");


    public static Constants getThis() {
        if(constantClass != null)
        {
            return constantClass;
        }
        constantClass = new Constants();
        return constantClass;
    }


    public int getHTTPS_CONNECTION_TIME_OUT() {
        return 3000;
    }

    public int getHTTP_CONNECTION_TIME_OUT() {
        return 3000;
    }


    public String getROBOT_FILE_PATH() {
        return "/robots.txt";
    }


    public List<String> getAllowedUserAgents() {
        return allowedUserAgents;
    }


    public Map<String, String> getHEAD_HEADERS() {
        return HEAD_REQUEST_HEADERS;
    }

}
