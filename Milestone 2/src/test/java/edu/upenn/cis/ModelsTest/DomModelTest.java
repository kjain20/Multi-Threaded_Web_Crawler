package edu.upenn.cis.ModelsTest;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.QueuePackage.CrawlerDataStructure;
import edu.upenn.cis.cis455.crawler.handlers.SAXHandler.HTMLHandler;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.DocumentTree;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.EventNode;
import edu.upenn.cis.stormlite.bolt.OutputCollector;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

public class DomModelTest {


//    DocumentTree documentTree = new DocumentTree();
//        for(EventNode eventNode : eventNodeList)
//    {
//        documentTree.build(eventNode);
//    }
//
//    // boolean match = DOMParserUtil.match(documentTree,)
//    XPathEngineImpl xPathEngine = new XPathEngineImpl();
//        xPathEngine.setDocumentTree(documentTree);
//    String[] expressions = new String[1];
//    expressions[0] = "/html/head/title[text()=\"This is text\"]";
//        xPathEngine.setXPaths(expressions);
//    List<Query> queries = xPathEngine.getxPathQueries();
//        for(Query query : queries)
//    {
//        boolean match = DOMParserUtil.match(documentTree,query);
//        System.out.println("..");
//    }

        @Test
        public void testForXMLParserValidity()
        {
            String data = "<html><head><title>This is text</title></head></html>";
            HTMLHandler htmlHandler = new HTMLHandler(new OutputCollector(null),data,"1","www.ggo.com");
            Crawler crawler = Mockito.mock(Crawler.class);
            CrawlerDataStructure crawlerDataStructure = Mockito.mock(CrawlerDataStructure.class);
            Mockito.when(crawler.getCrawlerDataStructure()).thenReturn(crawlerDataStructure);
            Crawler.map_reduce_crawler = crawler;
            htmlHandler.parse();
            List<EventNode> eventNodeList = htmlHandler.getEventNodeList();
            Assert.assertNotEquals(0,eventNodeList.size());
        }

        @Test
        public void testDOMMatch()
        {
            String data = "<html><head><title>This is text</title></head></html>";
            HTMLHandler htmlHandler = new HTMLHandler(new OutputCollector(null),data,"1","www.ggo.com");
            Crawler crawler = Mockito.mock(Crawler.class);
            CrawlerDataStructure crawlerDataStructure = Mockito.mock(CrawlerDataStructure.class);
            Mockito.when(crawler.getCrawlerDataStructure()).thenReturn(crawlerDataStructure);
            Crawler.map_reduce_crawler = crawler;
            htmlHandler.parse();
            List<EventNode> eventNodeList = htmlHandler.getEventNodeList();
            Assert.assertEquals(8,eventNodeList.size());
            DocumentTree documentTree = new DocumentTree();
        for(EventNode eventNode : eventNodeList)
    {
        documentTree.build(eventNode);
    }
        Assert.assertEquals("html",documentTree.getRootNode().getNodeName());

    // boolean match = DOMParserUtil.match(documentTree,)
//    XPathEngineImpl xPathEngine = new XPathEngineImpl();
//        xPathEngine.setDocumentTree(documentTree);
//    String[] expressions = new String[1];
//    expressions[0] = "/html/head/title[text()=\"This is text\"]";
//        xPathEngine.setXPaths(expressions);
//    List<Query> queries = xPathEngine.getxPathQueries();

        }
}
