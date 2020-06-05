package edu.upenn.cis.cis455.crawler.handlers.SAXHandler;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.DocumentTree;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Job;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.EventNode;
import edu.upenn.cis.cis455.model.OccurrenceEvent;
import edu.upenn.cis.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis.cis455.xpathengine.XPathEngineImpl;
import edu.upenn.cis.stormlite.bolt.OutputCollector;
import edu.upenn.cis.stormlite.tuple.Values;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class XMLHandler extends DefaultHandler {

    private String docId;
    private OutputCollector outputCollector = new OutputCollector(null);
    private List<EventNode> eventNodeList = new ArrayList<>();
    private Stack<EventNode> xmlPathFromRoot = new Stack<>();
    private List<OccurrenceEvent> testOccurenceEvents = new ArrayList<>();
    private Crawler crawlerInstance;

    public void setCrawlerInstance(Crawler crawlerInstance) {
        this.crawlerInstance = crawlerInstance;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setOutputCollector(OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    public void setEventNodeList(List<EventNode> eventNodeList) {
        this.eventNodeList = eventNodeList;
    }

    public List<OccurrenceEvent> getTestOccurenceEvents() {
        return testOccurenceEvents;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        int level = 1;
        if(this.xmlPathFromRoot.isEmpty())
        {
            EventNode eventNode = new EventNode(qName, EventNode.NodeType.OPEN_TAG,level);
            xmlPathFromRoot.push(eventNode);
            eventNodeList.add(eventNode);
            OccurrenceEvent occurrenceEvent = new OccurrenceEvent(eventNode);
            testOccurenceEvents.add(occurrenceEvent);
            Job newJob = new Job(docId,occurrenceEvent);
            outputCollector.emit(new Values<Object>(docId,newJob));
            System.out.println("Emitted from DOMParser Bolt:"+occurrenceEvent.getEventNode().getNodeName());
            synchronized (crawlerInstance) {
                crawlerInstance.getCrawlerDataStructure().addJob(newJob);
            }

        }
        else {
            EventNode recentSeenXpathNode = xmlPathFromRoot.peek();
            level = recentSeenXpathNode.getLevel() + 1;
            EventNode eventNode = new EventNode(qName, EventNode.NodeType.OPEN_TAG, level);
            eventNode.setParentNode(recentSeenXpathNode);
            xmlPathFromRoot.push(eventNode);
            eventNodeList.add(eventNode);
            OccurrenceEvent occurrenceEvent = new OccurrenceEvent(eventNode);
            testOccurenceEvents.add(occurrenceEvent);
            Job newJob = new Job(docId,occurrenceEvent);
            outputCollector.emit(new Values<Object>(docId,newJob));
            System.out.println("Emitted from DOMParser Bolt:"+occurrenceEvent.getEventNode().getNodeName());
            synchronized (crawlerInstance) {
                crawlerInstance.getCrawlerDataStructure().addJob(newJob);
            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        EventNode xpathStartEventNode = xmlPathFromRoot.pop();
        EventNode closexPathEventNode = new EventNode(xpathStartEventNode.getNodeName(), EventNode.NodeType.CLOSE_TAG,xpathStartEventNode.getLevel());

        eventNodeList.add(closexPathEventNode);
        OccurrenceEvent occurrenceEvent = new OccurrenceEvent(closexPathEventNode);
        Job job = new Job(occurrenceEvent);
        outputCollector.emit(new Values<Object>(docId, job));
        System.out.println("Emitted from DOMParser Bolt:"+occurrenceEvent.getEventNode().getNodeName());
        testOccurenceEvents.add(occurrenceEvent);
    }

    public List<EventNode> getEventNodeList() {
        return eventNodeList;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        EventNode nodeWithText = this.xmlPathFromRoot.peek();
        String s = new String(ch, start, length);
        nodeWithText.addTextNode(s);
        eventNodeList.add(nodeWithText);
        OccurrenceEvent occurrenceEvent = new OccurrenceEvent(nodeWithText);
        testOccurenceEvents.add(occurrenceEvent);
        Job newJob = new Job(docId,occurrenceEvent);
        outputCollector.emit(new Values<Object>(docId,newJob));
    }

    public static void main(String[] args) {
        String xmlData = "<a>Vedire<b>Raghav</b></a>";
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLHandler xmlHandler = new XMLHandler();
            xmlHandler.setDocId("1");
            saxParser.parse(new InputSource(new StringReader(xmlData)), xmlHandler);
            List<EventNode> eventNodeList = xmlHandler.getEventNodeList();
            DocumentTree documentTree = new DocumentTree();
            for(EventNode eventNode : eventNodeList)
            {
                documentTree.build(eventNode);
            }
            System.out.println("");
            XPathEngineImpl xPathEngine = (XPathEngineImpl) XPathEngineFactory.getXPathEngine();




        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
