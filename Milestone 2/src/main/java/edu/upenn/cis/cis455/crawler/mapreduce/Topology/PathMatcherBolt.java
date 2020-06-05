package edu.upenn.cis.cis455.crawler.mapreduce.Topology;

import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.DocumentTree;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Job;
import edu.upenn.cis.cis455.model.ChannelsDatabaseModel.ChannelsInfo;
import edu.upenn.cis.cis455.model.OccurrenceEvent;
import edu.upenn.cis.cis455.storage.DBManagers.ChannelDBManager;
import edu.upenn.cis.cis455.storage.DocumentIndexKey;
import edu.upenn.cis.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis.cis455.xpathengine.XPathEngineImpl;
import edu.upenn.cis.stormlite.OutputFieldsDeclarer;
import edu.upenn.cis.stormlite.TopologyContext;
import edu.upenn.cis.stormlite.bolt.IRichBolt;
import edu.upenn.cis.stormlite.bolt.OutputCollector;
import edu.upenn.cis.stormlite.routers.IStreamRouter;
import edu.upenn.cis.stormlite.tuple.Fields;
import edu.upenn.cis.stormlite.tuple.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class PathMatcherBolt implements IRichBolt {

    private Crawler crawlerInstance;
    private String executorId = UUID.randomUUID().toString();
    private Map<String, XPathEngineImpl> documentXpathEngine = new HashMap<>();
    private Set<String> matchedDocumentId = new HashSet<>();
    private List<String> xpathStrings;
    private List<ChannelsInfo> channelsInfoList;
    public PathMatcherBolt()
    {
        this.crawlerInstance = Crawler.getMap_reduce_crawler();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void execute(Tuple input) {
        Object obj = input.getObjectByField("OccurenceEvent");
        try {
            String docId = input.getStringByField("docId");
            OccurrenceEvent occurrenceEvent = (OccurrenceEvent) ((Job) obj).getObject();
            if (occurrenceEvent.isNewRecord()) {
                XPathEngineImpl xPathEngine = (XPathEngineImpl) XPathEngineFactory.getXPathEngine();
                xPathEngine.setXPaths(xpathStrings.toArray(new String[0]));
                documentXpathEngine.put(docId, xPathEngine);
                crawlerInstance.getDb().getDocumentIndexManager().deleteIndexForDocument(Integer.valueOf(docId));
                return;
            }
            if (occurrenceEvent.isRemoveEvent()) {
                documentXpathEngine.remove(docId);
                return;
            }

            try {
                XPathEngineImpl xPathEngine = documentXpathEngine.get(docId);
                boolean[] matchingXpaths = xPathEngine.evaluateEvent(occurrenceEvent);
                for (int i = 0; i < matchingXpaths.length; i++) {
                    if (matchingXpaths[i]) {
                        String channelName = channelsInfoList.get(i).getChannelName();
                        Integer documentId = Integer.valueOf(docId);
                        crawlerInstance.getDb().getDocumentIndexManager().insert(new DocumentIndexKey(channelName, documentId));
                    }
                }
                //store document into db even if one xpath matches
                //CSE455/CIS555 HW2 Sample Data
            } catch (Exception e) {

            }
        } finally {
            synchronized (crawlerInstance) {
                crawlerInstance.getCrawlerDataStructure().removeJob((Job)obj);
            }

        }
    }

    @Override
    public void prepare(Map<String, String> stormConf, TopologyContext context, OutputCollector collector) {
        // set channels from db
        crawlerInstance = Crawler.getMap_reduce_crawler();
        channelsInfoList = crawlerInstance.getDb().getChannelDBManager().getAllChannels();
        xpathStrings = channelsInfoList.stream().map(entry -> entry.getXpath()).collect(Collectors.toList());

    }

    @Override
    public void setRouter(IStreamRouter router) {

    }

    @Override
    public Fields getSchema() {
        return null;
    }

    @Override
    public String getExecutorId() {
        return null;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
