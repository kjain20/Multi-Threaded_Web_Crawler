package edu.upenn.cis.cis455.crawler.info;

import java.util.ArrayList;
import java.util.HashMap;

//use this class to store robots file from a domains(sites)?? Assuming one place for all sites??
public class RobotsTxtInfo {
	
	private HashMap<String,ArrayList<String>> disallowedLinks;
	private HashMap<String,ArrayList<String>> allowedLinks;
	
	private HashMap<String,Integer> crawlDelays;
	private ArrayList<String> sitemapLinks;
	private ArrayList<String> userAgents;

	//constructor must be called by whom?? and only once -- give it to crawler??
	public RobotsTxtInfo(){
		disallowedLinks = new HashMap<String,ArrayList<String>>();
		allowedLinks = new HashMap<String,ArrayList<String>>();
		crawlDelays = new HashMap<String,Integer>();
		sitemapLinks = new ArrayList<String>();
		userAgents = new ArrayList<String>();
	}
	
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
	
	public void print(){
		for(String userAgent:userAgents){
			ArrayList<String> dlinks = disallowedLinks.get(userAgent);
			ArrayList<String> alinks = allowedLinks.get(userAgent);
		}
		if(sitemapLinks.size() > 0){
		}
	}
	
	public boolean crawlContainAgent(String key){
		return crawlDelays.containsKey(key);
	}
}
