package edu.upenn.cis.cis455.crawler.Filters;

import edu.upenn.cis.cis455.crawler.Constants;
import edu.upenn.cis.cis455.crawler.DocumentTypes;
import edu.upenn.cis.cis455.crawler.QueuePackage.ReadyQueueInstance;
import edu.upenn.cis.cis455.crawler.QueuePackage.WaitQueueInstance;
import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.crawler.microservice.WebURLInfoFetchMicroService;
import edu.upenn.cis.cis455.model.responseModels.URLMetaData;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HEADFilter implements CrawlFilter {

    private WebURLInfoFetchMicroService webURLInfoFetchMicroService;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    public HEADFilter(WebURLInfoFetchMicroService webURLInfoFetchMicroService) {
        this.webURLInfoFetchMicroService = webURLInfoFetchMicroService;
    }



    @Override
    public void renderRequest(RequestContext requestContext) {
        if(requestContext.isValid() && requestContext.getCrawlDelayManagerOutput() != null &&
                requestContext.getCrawlDelayManagerOutput().isOkayToCrawl()) {

            Map<String, String> headers = new HashMap<>(Constants.getThis().getHEAD_HEADERS());
            if (requestContext.getUrlMetaDataFromDB() != null) {
                Date lastModifiedTime = requestContext.getUrlMetaDataFromDB().getLastModifiedDate();
                headers.put("If-Modified-Since", dateFormatter.format(lastModifiedTime));
            }
            URLMetaData urlMetaInformation = webURLInfoFetchMicroService.fetchHeadInformationLive(
                    requestContext.getUrlInfo(), headers);

            if (urlMetaInformation == null) {
                requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
                requestContext.setDebugMessage("HeadDataEnricher Head Fetch Failed");
                return;
            }

            if (urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_OK
                    || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_CREATED
                    || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_ACCEPTED
                    || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_NOT_AUTHORITATIVE
                    || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_NO_CONTENT
                    || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_RESET
                    || urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_PARTIAL ||
                    urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP||
                    urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_SEE_OTHER||
                    urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_MOVED_PERM) {

                int contentLength = urlMetaInformation.getContentLength();
                if(contentLength > requestContext.getMaxDocumentRetrievable_size()) {
                    requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
                    requestContext.setDebugMessage("Document Size Exceeded ");
                    return;
                }


                DocumentTypes docType = DocumentTypes.getTypeFromContentType(urlMetaInformation.getContentType());
                if (docType == null) {
                    requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
                    requestContext.setDebugMessage("HeadDataEnricher Document Type Failed ");
                    return;
                }

                if(urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP||
                        urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_SEE_OTHER||
                        urlMetaInformation.getStatusCode() == HttpURLConnection.HTTP_MOVED_PERM)
                {
                     if(urlMetaInformation.getHeaders().containsKey("Location"))
                     {
                        String redirectionUrl = urlMetaInformation.getHeaders().get("Location");
                        if(redirectionUrl.startsWith("/")) {
                            redirectionUrl = requestContext.getUrlInfo().getURL().getProtocol() + "://" +
                                    requestContext.getUrlInfo().getURL().getHost() +
                                    ":" + requestContext.getUrlInfo().getURL().getPort() +
                                    requestContext.getUrlInfo().getURL() + redirectionUrl;
                        }

                            URLInfo urlInfo =  new URLInfo(redirectionUrl);
                            WaitQueueInstance newWaitQueuInstance = new WaitQueueInstance(new ReadyQueueInstance(urlInfo),new Date(),0);
                            requestContext.setWaitQueueInstance(newWaitQueuInstance);
                            requestContext.setRequestContextStatus(RequestContext.RequestContextStatus.WAIT);
                            return;

                     }
                     else
                     {
                         requestContext.setRequestContextStatus(RequestContext.RequestContextStatus.FAILURE);
                         return;
                     }
                }

            }
            requestContext.setHeadMetaData(urlMetaInformation);
        }
        else {
            requestContext.setStatus(RequestContext.RequestContextStatus.FAILURE);
            requestContext.setDebugMessage("HeadRequestChecker validation failed");
            return;
        }
    }

}
