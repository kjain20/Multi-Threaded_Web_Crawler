package edu.upenn.cis.cis455.crawler.mapreduce.Util;

import edu.upenn.cis.cis455.crawler.mapreduce.Models.*;

import java.util.ArrayList;
import java.util.List;

public class XPathUtil {


    public static Query getXPathQuery(String queryString)
    {
        XPathRecursiveDecentParser xPathRecursiveDecentParser = new XPathRecursiveDecentParser(queryString);
        try
        {
            List<Step> stepsOfXpathQuery = xPathRecursiveDecentParser.getSteps();
            List<QueryNode> queryNodeList = getXpathChildNodes(stepsOfXpathQuery);
            Query query = new Query(queryString, queryNodeList);
            return query;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<Step> getSteps(String queryString)
    {
        return null;
    }

    public static List<QueryNode> getXpathChildNodes(List<Step> stepNodes) {
        List<QueryNode> queryNodeList = new ArrayList<>();
        for (Step step : stepNodes) {
            QueryNode queryNode = new QueryNode(step.getNameOfNode());
            for(Test test : step.getTest()) {
                TextMatchFilter xPathFilter = new TextMatchFilter(test.getTextType(),test.getText());
                queryNode.getTextMatchFilterList().add(xPathFilter);
            }
            queryNodeList.add(queryNode);
        }
        return queryNodeList;
    }

}