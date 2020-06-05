package edu.upenn.cis.cis455.crawler.Filters;

import edu.upenn.cis.cis455.crawler.Constants;
import edu.upenn.cis.cis455.crawler.DocumentTypes;
import edu.upenn.cis.cis455.crawler.handlers.LoginFilter;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.crawler.microservice.WebURLInfoFetchMicroService;
import edu.upenn.cis.cis455.model.responseModels.URLMetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.print.Doc;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class GETFilter implements CrawlFilter {

    private WebURLInfoFetchMicroService webURLInfoFetchMicroService;
    Logger logger = LogManager.getLogger(WebURLInfoFetchMicroService.class);

    public GETFilter(WebURLInfoFetchMicroService webURLInfoFetchMicroService) {
        this.webURLInfoFetchMicroService = webURLInfoFetchMicroService;
    }


    @Override
    public void renderRequest(RequestContext requestContext) {

        if (requestContext.getHeadMetaData() != null && (requestContext.getHeadMetaData().getStatusCode() == HttpURLConnection.HTTP_OK
                || requestContext.getHeadMetaData().getStatusCode() == HttpURLConnection.HTTP_NOT_MODIFIED)) {

            if (requestContext.getHeadMetaData().getStatusCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                requestContext.setGetMetaData(requestContext.getUrlMetaDataFromDB());
            }
            else {
                Map<String, String> headers = new HashMap<>(Constants.getThis().getHEAD_HEADERS());
                URLMetaData urlMetaInformation = webURLInfoFetchMicroService.fetchGetInformationLive(
                        requestContext.getUrlInfo(), headers);

                if (urlMetaInformation == null || !(urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_OK
                        || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_CREATED
                        || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_ACCEPTED
                        || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_NOT_AUTHORITATIVE
                        || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_NO_CONTENT
                        || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_RESET
                        || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_PARTIAL)) {
                    requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);

                    return;
                }

                int contentLength = urlMetaInformation.getContentLength();
                if (contentLength > requestContext.getMaxDocumentRetrievable_size()) {
                    requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
                    return;
                }

                DocumentTypes docType = DocumentTypes.getTypeFromContentType(urlMetaInformation.getContentType());
                if (docType == null) {
                    requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
                    return;
                }
                logger.info(requestContext.getUrlInfo().getURL()+":Downloading");
                requestContext.setGetMetaData(urlMetaInformation);
            }
        } else {
            requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
            requestContext.setDebugMessage("Get validation failed");
            return;
        }

    }
}
