package edu.upenn.cis.cis455.crawler.handlers;

import edu.upenn.cis.cis455.crawler.info.URLInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DocInfo;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.DomainInfoKey;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.URLIdInfo;
import edu.upenn.cis.cis455.storage.StorageImpl;
import spark.Request;
import spark.Response;
import spark.Route;

public class LookupHandler implements Route {

    private StorageImpl db;

    public LookupHandler(StorageImpl db) {
        this.db = db;
    }



    @Override
    public Object handle(Request request, Response response) throws Exception {
        String url = request.queryString().split("url=")[1];
        URLInfo urlInfo = new URLInfo(url);
        String protocol = url.startsWith("https")?"https":"http";
        DomainInfoKey domainInfoKey = new DomainInfoKey(protocol,urlInfo.getHostName(),urlInfo.getPortNo(),urlInfo.getFilePath(),"GET");
        URLIdInfo urlIdInfo = db.getUrldbManager().getUrlIdInfoPrimaryIndex().get(domainInfoKey);
        int docId1 = urlIdInfo.getDocId();
        DocInfo docInfo = db.getUrldbManager().getDocInfoPrimaryIndex().get(docId1);
        String contentType = urlIdInfo.getContentType();
        response.type(contentType);
        return docInfo.getData();

    }


    public static void main(String[] args) {
//        //String url = "www.google.com/lookup?url=abc";
//
//        URLInfo urlInfo = new URLInfo("https://www.google.com/private/nytimes:80");
//        //System.out.println(urlInfo.getPortNo());

    }
}
