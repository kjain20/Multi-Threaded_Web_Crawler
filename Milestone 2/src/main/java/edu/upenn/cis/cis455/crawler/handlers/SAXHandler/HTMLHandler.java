package edu.upenn.cis.cis455.crawler.handlers.SAXHandler;

import edu.upenn.cis.cis455.Util.URLResourceParser;
import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.EventNode;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Job;
import edu.upenn.cis.cis455.model.OccurrenceEvent;
import edu.upenn.cis.stormlite.bolt.OutputCollector;
import edu.upenn.cis.stormlite.tuple.Values;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HTMLHandler {
    private OutputCollector outputCollector;
    private String data;
    private String docId;
    private String url;
    private static List<EventNode> eventList = new ArrayList<>();


    public HTMLHandler(OutputCollector outputCollector, String data, String docId, String url) {
        this.outputCollector = outputCollector;
        this.data = data;
        this.docId = docId;
        this.url = url;
    }

    public List<EventNode> getEventNodeList() {
        return eventList;
    }

    public void parse() {
        Document parsedDocument = Jsoup.parse(data, url);
        NodeVisitorImplementation visitor = new NodeVisitorImplementation();
        visitor.setCrawlerInstance(Crawler.getMap_reduce_crawler());
        Node element = parsedDocument.select("html").first();
        NodeTraversor traversor = new NodeTraversor(visitor);
        traversor.traverse(element);
    }

    public void setEventNodeList(List<EventNode> eventNodeList) {
        this.eventList = eventNodeList;
    }

    public class NodeVisitorImplementation implements NodeVisitor {
        private List<EventNode> eventNodeList = new ArrayList<>();
        private Stack<EventNode> xmlPathFromRoot = new Stack<>();
        private List<OccurrenceEvent> testOccurenceEvents = new ArrayList<>();
        private Crawler crawlerInstance;

        public void setCrawlerInstance(Crawler crawlerInstance) {
            this.crawlerInstance = crawlerInstance;
        }

        @Override
        public void head(Node node, int i) {
            if(node instanceof Element) {
                int level = 1;
                Element element = (Element) node;
                List<TextNode> textNodes = element.textNodes();
                if (this.xmlPathFromRoot.isEmpty()) {
                    EventNode eventNode = new EventNode(element.nodeName(), EventNode.NodeType.OPEN_TAG, level);
                    for (int textIndex = 0; textIndex < textNodes.size(); textIndex++) {
                        eventNode.addTextNode(textNodes.get(textIndex).text());
                    }
                    xmlPathFromRoot.push(eventNode);
                    OccurrenceEvent occurrenceEvent = new OccurrenceEvent(eventNode);
                    testOccurenceEvents.add(occurrenceEvent);
                    Job newJob = new Job(docId, occurrenceEvent);
                    outputCollector.emit(new Values<Object>(docId, newJob));
                    eventList.add(eventNode);
                    System.out.println("Emitted from DOMParser Bolt:" + occurrenceEvent.getEventNode().getNodeName());
                    synchronized (crawlerInstance) {
                        crawlerInstance.getCrawlerDataStructure().addJob(newJob);
                    }

                } else {
                    EventNode recentSeenXpathNode = xmlPathFromRoot.peek();
                    level = recentSeenXpathNode.getLevel() + 1;
                    EventNode eventNode = new EventNode(element.nodeName(), EventNode.NodeType.OPEN_TAG, level);
                    for (int textIndex = 0; textIndex < textNodes.size(); textIndex++) {
                        eventNode.addTextNode(textNodes.get(textIndex).text());
                    }
                    eventNode.setParentNode(recentSeenXpathNode);
                    xmlPathFromRoot.push(eventNode);
                    OccurrenceEvent occurrenceEvent = new OccurrenceEvent(eventNode);
                    testOccurenceEvents.add(occurrenceEvent);
                    Job newJob = new Job(docId, occurrenceEvent);
                    outputCollector.emit(new Values<Object>(docId, newJob));
                    eventList.add(eventNode);
                    System.out.println("Emitted from DOMParser Bolt:" + occurrenceEvent.getEventNode().getNodeName());
                    synchronized (crawlerInstance) {
                        crawlerInstance.getCrawlerDataStructure().addJob(newJob);
                    }

                }
            }
        }

        @Override
        public void tail(Node node, int i) {
            if (node instanceof Element) {
                EventNode xpathStartEventNode = xmlPathFromRoot.pop();
                EventNode closexPathEventNode = new EventNode(xpathStartEventNode.getNodeName(), EventNode.NodeType.CLOSE_TAG, xpathStartEventNode.getLevel());
                eventList.add(closexPathEventNode);
                OccurrenceEvent occurrenceEvent = new OccurrenceEvent(closexPathEventNode);
                Job job = new Job(occurrenceEvent);
                outputCollector.emit(new Values<Object>(docId, job));
                System.out.println("Emitted from DOMParser Bolt:" + occurrenceEvent.getEventNode().getNodeName());
                testOccurenceEvents.add(occurrenceEvent);
            }
        }

    }

    public void setOutputCollector(OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public OutputCollector getOutputCollector() {
        return outputCollector;
    }

    public String getData() {
        return data;
    }

    public String getDocId() {
        return docId;
    }

    public String getUrl() {
        return url;
    }


}
