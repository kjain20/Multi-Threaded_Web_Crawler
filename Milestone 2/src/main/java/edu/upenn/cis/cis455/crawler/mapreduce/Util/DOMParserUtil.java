package edu.upenn.cis.cis455.crawler.mapreduce.Util;


import edu.upenn.cis.cis455.crawler.mapreduce.Models.*;

import java.util.HashSet;
import java.util.Set;

public class DOMParserUtil {

    public static boolean match(DocumentTree documentTree, Query query)
    {
        Set<DocumentElement> currentDOMNode = new HashSet<>();
        currentDOMNode.add(documentTree.getRootNode());
        int queryNodeIndex = 0;
        for(QueryNode queryNode : query.getQueryNodeList()) {
            //currentDOMNode =
            Set<DocumentElement> matchingNodes = getMatchingNodes(queryNode, currentDOMNode);
            if(queryNodeIndex == query.getQueryNodeList().size() - 1 && matchingNodes.size() > 0) {
                return true;
            }
            currentDOMNode.clear();
            for(DocumentElement documentElement : matchingNodes) {
                currentDOMNode.addAll(documentElement.getChildNodes().values());
            }
            queryNodeIndex += 1;
        }
        return false;
    }


    public static Set<DocumentElement> getMatchingNodes(QueryNode queryNode, Set<DocumentElement> currentDOMs)
    {
        Set<DocumentElement> matchingNodes = new HashSet<>();
        for(DocumentElement documentElement : currentDOMs)
        {
            int validFilterCount = 0;
            boolean foundNodeMatch = false;
            if(documentElement.getNodeName().equals(queryNode.getNodeName()))
            {
                foundNodeMatch = true;

                for(TextMatchFilter textMatchFilter : queryNode.getTextMatchFilterList())
                {
                    for(String text : documentElement.getTextNodes())
                    {
                        if(textMatchFilter.getFilterType() == Test.TextType.EXACT && textMatchFilter.getTextString().equalsIgnoreCase(text))
                        {
                            validFilterCount+= 1;
                            break;
                        }
                        else if(textMatchFilter.getFilterType() == Test.TextType.CONTAINS && text.contains(textMatchFilter.getTextString()))
                        {
                            validFilterCount += 1;
                            break;
                        }
                    }

                }

            }
            if(validFilterCount == queryNode.getTextMatchFilterList().size() && foundNodeMatch) {
                matchingNodes.add(documentElement);
            }
        }
        return matchingNodes;
    }


    public static void main(String[] args) {


    }

}
