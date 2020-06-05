package edu.upenn.cis.cis455.crawler.mapreduce.Models;

import java.util.List;

public class Query {
    private String query;
    private List<QueryNode> queryNodeList;

    public Query(String query, List<QueryNode> queryNodeList) {
        this.query = query;
        this.queryNodeList = queryNodeList;
    }

    public String getQuery() {
        return query;
    }

    public List<QueryNode> getQueryNodeList() {
        return queryNodeList;
    }
}
