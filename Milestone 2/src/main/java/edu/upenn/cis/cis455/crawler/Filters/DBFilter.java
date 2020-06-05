package edu.upenn.cis.cis455.crawler.Filters;

import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.crawler.microservice.WebURLInfoFetchMicroService;
import edu.upenn.cis.cis455.model.responseModels.URLMetaData;

public class DBFilter implements CrawlFilter {

    private WebURLInfoFetchMicroService webURLInfoFetchMicroService;

    public DBFilter(WebURLInfoFetchMicroService webURLInfoFetchMicroService) {
        this.webURLInfoFetchMicroService = webURLInfoFetchMicroService;
    }


    @Override
    public void renderRequest(RequestContext requestContext) {
        if(requestContext.isValid() && requestContext.getCrawlDelayManagerOutput() != null &&
                requestContext.getCrawlDelayManagerOutput().isOkayToCrawl()) {
            URLMetaData urlMetaInformation = webURLInfoFetchMicroService.fetchGetInformationFromDb(
                    requestContext.getUrlInfo(), requestContext.getTxn());
            requestContext.setUrlMetaDataFromDB(urlMetaInformation);
        }
        else {
            requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
            requestContext.setDebugMessage("DB Check validation failed");
            return;
        }
    }

}
