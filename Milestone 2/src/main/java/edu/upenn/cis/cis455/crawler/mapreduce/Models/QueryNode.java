package edu.upenn.cis.cis455.crawler.mapreduce.Models;

import java.util.ArrayList;
import java.util.List;

public class QueryNode {
    private String queryId;
    private String nodeName;
    private List<TextMatchFilter> textMatchFilterList = new ArrayList<>();

    public QueryNode() {
    }

    public QueryNode(String queryId, String nodeName) {
        this.queryId = queryId;
        this.nodeName = nodeName;
    }

    public QueryNode(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }


    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }


    public List<TextMatchFilter> getTextMatchFilterList() {
        return textMatchFilterList;
    }

    public void setTextMatchFilterList(List<TextMatchFilter> textMatchFilterList) {
        this.textMatchFilterList = textMatchFilterList;
    }

}
