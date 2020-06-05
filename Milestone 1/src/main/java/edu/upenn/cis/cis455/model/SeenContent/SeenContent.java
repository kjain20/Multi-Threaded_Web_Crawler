package edu.upenn.cis.cis455.model.SeenContent;

public class SeenContent {

    String hash;
    String data;

    public SeenContent(String hash, String data) {

        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
