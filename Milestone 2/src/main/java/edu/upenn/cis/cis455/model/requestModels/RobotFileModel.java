package edu.upenn.cis.cis455.model.requestModels;

import edu.upenn.cis.cis455.Util.RobotsParser;

import java.util.*;

//In Memory Robots File Cache
public class RobotFileModel {

    private HashMap<String,ArrayList<String>> disallowedLinks;
    private HashMap<String,ArrayList<String>> allowedLinks;

    private HashMap<String,Integer> crawlDelays;
    private ArrayList<String> sitemapLinks;
    private ArrayList<String> userAgents;

    //constructor must be called by whom?? and only once -- give it to crawler??

    public void addDisallowedLink(String key, String value){
        if(!disallowedLinks.containsKey(key)){
            ArrayList<String> temp = new ArrayList<String>();
            temp.add(value);
            disallowedLinks.put(key, temp);
        }
        else{
            ArrayList<String> temp = disallowedLinks.get(key);
            if(temp == null)
                temp = new ArrayList<String>();
            temp.add(value);
            disallowedLinks.put(key, temp);
        }
    }

    public void addAllowedLink(String key, String value){
        if(!allowedLinks.containsKey(key)){
            ArrayList<String> temp = new ArrayList<String>();
            temp.add(value);
            allowedLinks.put(key, temp);
        }
        else{
            ArrayList<String> temp = allowedLinks.get(key);
            if(temp == null)
                temp = new ArrayList<String>();
            temp.add(value);
            allowedLinks.put(key, temp);
        }
    }

    public void addCrawlDelay(String key, Integer value){
        crawlDelays.put(key, value);
    }

    public void addSitemapLink(String val){
        sitemapLinks.add(val);
    }

    public void addUserAgent(String key){
        userAgents.add(key);
    }

    public boolean containsUserAgent(String key){
        return userAgents.contains(key);
    }

    public ArrayList<String> getDisallowedLinks(String key){
        return disallowedLinks.get(key);
    }

    public ArrayList<String> getAllowedLinks(String key){
        return allowedLinks.get(key);
    }

    public int getCrawlDelay(String key){
        return crawlDelays.get(key);
    }


    public boolean crawlContainAgent(String key){
        return crawlDelays.containsKey(key);
    }



    public RobotFileModel(String input)
    {
        disallowedLinks = new HashMap<>();
        allowedLinks = new HashMap<>();
        crawlDelays = new HashMap<>();
        sitemapLinks = new ArrayList<>();
        userAgents = new ArrayList<>();
        RobotsParser.model(this,input); //this will enrich
    }


    public HashMap<String, ArrayList<String>> getDisallowedLinks() {
        return disallowedLinks;
    }

    public HashMap<String, ArrayList<String>> getAllowedLinks() {
        return allowedLinks;
    }

    public HashMap<String, Integer> getCrawlDelays() {
        return crawlDelays;
    }
}
