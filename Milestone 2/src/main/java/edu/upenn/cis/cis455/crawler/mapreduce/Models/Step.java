package edu.upenn.cis.cis455.crawler.mapreduce.Models;


import java.util.List;

public class Step {
    private String nameOfNode;
    private List<Test> test;

    public Step(String nameOfNode, List<Test> testNodes) {
        this.nameOfNode = nameOfNode;
        this.test = testNodes;
    }

    public Step(String nameOfNode) {
        this.nameOfNode = nameOfNode;
    }

    public void addTextNode(Test testNode)
    {
        this.test.add(testNode);
    }

    public String getNameOfNode() {
        return nameOfNode;
    }

    public List<Test> getTest() {
        return test;
    }
}
