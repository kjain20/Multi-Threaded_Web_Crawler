package edu.upenn.cis.cis455.crawler.Filters;

public class PathMatcherOutput {
    private boolean match;
    private String userAgent;

    public PathMatcherOutput() {
        match = false;
        userAgent = null;
    }

    public PathMatcherOutput(boolean match, String userAgent) {
        this.match = match;
        this.userAgent = userAgent;
    }

    public boolean isMatch() {
        return match;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
