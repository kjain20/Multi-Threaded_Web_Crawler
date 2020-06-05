package edu.upenn.cis.cis455.crawler.mapreduce.Models;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.handlers.SAXHandler.HTMLHandler;
import edu.upenn.cis.cis455.crawler.mapreduce.Util.DOMParserUtil;
import edu.upenn.cis.cis455.xpathengine.XPathEngineImpl;
import edu.upenn.cis.stormlite.bolt.OutputCollector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeTraversor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentTree {
    private DocumentElement rootNode;

    Map<String, DocumentElement> allNodes = new HashMap<>();

    public void build(EventNode newNode) {

        if(newNode.getNodeType() == EventNode.NodeType.CLOSE_TAG) {
            return;
        }
        if(rootNode == null) {

                rootNode = new DocumentElement(newNode.getId(), newNode.getNodeName());
                allNodes.put(newNode.getId(), rootNode);
                for(String textNode: newNode.getTextNodesList()) {
                    rootNode.addTextNode(textNode);
                }
                return;
        }

        if(allNodes.containsKey(newNode.getId())) {
            DocumentElement documentElement = allNodes.get(newNode.getId());
            for(String textNode: newNode.getTextNodesList()) {
                documentElement.addTextNode(textNode);
            }
        } else {
            DocumentElement documentElement = new DocumentElement(newNode.getId(), newNode.getNodeName());
            if(newNode.getTextNodesList().size() != 0)
            {
               for(String textString : newNode.getTextNodesList())
               {
                   documentElement.addTextNode(textString);
               }
            }
            DocumentElement parentElement = allNodes.get(newNode.getParentNode().getId());
            parentElement.getChildNodes().put(documentElement.getId(), documentElement);
            allNodes.put(documentElement.getId(), documentElement);

        }

    }

    public DocumentElement getRootNode() {
        return rootNode;
    }

    public Map<String, DocumentElement> getNonRootNodes() {
        return allNodes;
    }

    public static void main(String[] args) {
        String data = "<html><head><title>This is text</title></head></html>";
        HTMLHandler htmlHandler = new HTMLHandler(new OutputCollector(null),data,"1","www.ggo.com");
        htmlHandler.parse();
        List<EventNode> eventNodeList = htmlHandler.getEventNodeList();
        DocumentTree documentTree = new DocumentTree();
        for(EventNode eventNode : eventNodeList)
        {
            documentTree.build(eventNode);
        }

       // boolean match = DOMParserUtil.match(documentTree,)
        XPathEngineImpl xPathEngine = new XPathEngineImpl();
        xPathEngine.setDocumentTree(documentTree);
        String[] expressions = new String[1];
        expressions[0] = "/html/head/title[text()=\"This is text\"]";
        xPathEngine.setXPaths(expressions);
        List<Query> queries = xPathEngine.getxPathQueries();
        for(Query query : queries)
        {
            boolean match = DOMParserUtil.match(documentTree,query);
            System.out.println("..");
        }

    }
}
