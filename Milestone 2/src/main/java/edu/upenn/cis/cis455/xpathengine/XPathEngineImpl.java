package edu.upenn.cis.cis455.xpathengine;

import edu.upenn.cis.cis455.crawler.handlers.SAXHandler.XMLHandler;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.DocumentTree;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Query;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.EventNode;
import edu.upenn.cis.cis455.crawler.mapreduce.Util.DOMParserUtil;
import edu.upenn.cis.cis455.crawler.mapreduce.Util.XPathUtil;
import edu.upenn.cis.cis455.model.OccurrenceEvent;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XPathEngineImpl implements XPathEngine {

    private DocumentTree documentTree = new DocumentTree();

    private List<Query> xPathQueries = new ArrayList<>();
    List<Boolean> result = new ArrayList<>();

    @Override
    public void setXPaths(String[] expressions) {
        int xpathIndex = 0;
        for (String expression : expressions) {
            Query query = XPathUtil.getXPathQuery(expression);
            xPathQueries.add(query);
            result.add(false);
            xpathIndex = xpathIndex + 1;
        }
    }

    public void setDocumentTree(DocumentTree documentTree) {
        this.documentTree = documentTree;
    }



    public DocumentTree getDocumentTree() {
        return documentTree;
    }

    @Override
    public boolean isValid(int i) {
        return false;
    }

    @Override
    public boolean[] evaluateEvent(OccurrenceEvent event) {
        //for every event check for DOM match by building tree and match across paths
        EventNode eventNode = event.getEventNode();
        documentTree.build(eventNode);
        int xpathIndex = 0;
        for (Query query : xPathQueries) {
            if(query == null) {
                xpathIndex = xpathIndex + 1;
                continue;
            }
            boolean matchResult = DOMParserUtil.match(documentTree, query);
            result.set(xpathIndex, matchResult);
            xpathIndex = xpathIndex + 1;
        }
        boolean[] matchResultForXpaths = new boolean[result.size()];
        for (int i = 0; i < result.size(); i++) {
            matchResultForXpaths[i] = result.get(i);
        }
        return matchResultForXpaths;
    }

    public List<Query> getxPathQueries() {
        return xPathQueries;
    }

    public static void main(String[] args) throws Exception {
        String[] expressions = new String[1];
        expressions[0] = "/db[text() = \"Alice\"]/record/name[contains(text(),\"someSubstring\")]";
        XPathEngineImpl xPathEngine = new XPathEngineImpl();
        xPathEngine.setXPaths(expressions);
        String xmlData = "<db>Alice<record><name>someSubstring is here</name></record></db>";
        List<OccurrenceEvent> occurrenceEvents = new ArrayList<>();
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLHandler xmlHandler = new XMLHandler();
        xmlHandler.setDocId("1");
        saxParser.parse(new InputSource(new StringReader(xmlData)), xmlHandler);
        for (OccurrenceEvent occurrenceEvent : xmlHandler.getTestOccurenceEvents()) {
            occurrenceEvents.add(occurrenceEvent);
            xPathEngine.evaluateEvent(occurrenceEvent);
        }
        System.out.println(xPathEngine.getxPathQueries());

    }
}
