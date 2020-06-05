package edu.upenn.cis.cis455.crawler.mapreduce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upenn.cis.cis455.crawler.Crawler;
import edu.upenn.cis.cis455.crawler.mapreduce.Topology.*;
import edu.upenn.cis.stormlite.Config;
import edu.upenn.cis.stormlite.LocalCluster;
import edu.upenn.cis.stormlite.Topology;
import edu.upenn.cis.stormlite.TopologyBuilder;
import edu.upenn.cis.stormlite.tuple.Fields;

public class MapReduceTopology {
    private Crawler crawler;
    private LocalCluster localCluster;

    public MapReduceTopology(Crawler crawler) {
        this.crawler = crawler;
    }

    private static final String crawlerQueueSpoutIdentifier = "CRAWLER_QUEUE_SPOUT";
    private static final String docFetcherBoltIdentifier = "DOC_FETCHER_BOLT";
    private static final String domParserBoltIdentifier = "DOM_PARSER_BOLT";
    private static final String linkExtractorBoltIdentifier = "LINK_EXTRACTOR_BOLT";
    private static final String pathMatcherBoltIdentifier = "PATH_MATHCER_BOLT";


    private static String crawlerClusterIdentifier = "crawler";

    public void startEngine() {
        Config config = new Config();
        QueueSpout queueSpout = new QueueSpout();
        DocFetcherBolt docFetcherBolt = new DocFetcherBolt();
        URLExtractorBolt urlExtractorBolt = new URLExtractorBolt();
        DOMParserBolt domParserBolt = new DOMParserBolt();
        PathMatcherBolt pathMatcherBolt = new PathMatcherBolt();
        //PathMatcherBolt pathMatcherBolt = new PathMatcherBolt(this.crawler);
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout(crawlerQueueSpoutIdentifier, queueSpout, 1);
        topologyBuilder.setBolt(docFetcherBoltIdentifier, docFetcherBolt, 10).shuffleGrouping(crawlerQueueSpoutIdentifier);
        topologyBuilder.setBolt(linkExtractorBoltIdentifier, urlExtractorBolt, 10).shuffleGrouping(docFetcherBoltIdentifier);
        topologyBuilder.setBolt(domParserBoltIdentifier,domParserBolt,10).shuffleGrouping(docFetcherBoltIdentifier);
        topologyBuilder.setBolt(pathMatcherBoltIdentifier,pathMatcherBolt,10).fieldsGrouping(domParserBoltIdentifier, new Fields("docId"));
        localCluster = new LocalCluster();
        Topology topo = topologyBuilder.createTopology();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String str = mapper.writeValueAsString(topo);
            System.out.println("The StormLite topology is:\n" + str);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        localCluster.submitTopology(crawlerClusterIdentifier, config, topo);
    }

    public void closeTopology() {
        synchronized (this.crawler) {
            crawler.getCrawlerDataStructure().getVisitedContent().clear();
        }
        try {
            localCluster.killTopology(crawlerClusterIdentifier);
            localCluster.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
