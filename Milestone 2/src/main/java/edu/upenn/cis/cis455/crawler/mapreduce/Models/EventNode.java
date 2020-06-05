package edu.upenn.cis.cis455.crawler.mapreduce.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventNode {

    private String id = UUID.randomUUID().toString();

    private String nodeName;

    private NodeType nodeType;

    private List<String> textNodesList = new ArrayList<>();

    private EventNode parentNode;

    private int level;

    public EventNode(String nodeName, NodeType nodeType, int level) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.level = level;
    }

    public EventNode(String nodeName, NodeType nodeType) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void setParentNode(EventNode parentNode) {
        this.parentNode = parentNode;
    }

    public EventNode getParentNode() {
        return parentNode;
    }

    public void addTextNode(String text)
    {
        this.textNodesList.add(text);
    }

    public String getNodeName() {
        return nodeName;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public List<String> getTextNodesList() {
        return textNodesList;
    }

    public void setTextNodesList(List<String> textNodesList) {
        this.textNodesList = textNodesList;
    }

    public enum NodeType
    {
        OPEN_TAG,
        TEXT,
        CLOSE_TAG;

    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }
}
