package edu.upenn.cis.cis455.crawler.Filters;

import java.util.ArrayList;
import java.util.List;

public class Validator {
    private List<CrawlFilter> processList;

    public void addValidator(CrawlFilter crawlFilter) {
        processList.add(crawlFilter);
    }

    public Validator() {
        processList = new ArrayList<>();
    }

    public void processRequest(RequestContext requestContext) {

        for(CrawlFilter process: processList) {
            if(requestContext.isValid()) {
                process.renderRequest(requestContext);
            }
        }
    }
}
