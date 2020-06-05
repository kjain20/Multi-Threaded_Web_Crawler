package edu.upenn.cis.cis455.crawler.mapreduce.Models;

import java.util.*;

public class DocumentElement {
    private String id;
    private String nodeName;
    private Set<String> textNodes = new HashSet<>();

    Map<String, DocumentElement> childNodes = new HashMap<>();

    public DocumentElement(String id, String nodeName) {
        this.id = id;
        this.nodeName = nodeName;
    }

    public String getId() {
        return id;
    }

    public void addChild(DocumentElement childNode) {
        childNodes.put(childNode.getId(), childNode);
    }

    public Map<String, DocumentElement> getChildNodes() {
        return childNodes;
    }

    public void addTextNode(String text) {
        textNodes.add(text);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Set<String> getTextNodes() {
        return textNodes;
    }

    public void setTextNodes(Set<String> textNodes) {
        this.textNodes = textNodes;
    }

    public void setChildNodes(Map<String, DocumentElement> childNodes) {
        this.childNodes = childNodes;
    }


}
