package edu.upenn.cis.cis455.crawler.handlers;

import com.sleepycat.persist.EntityCursor;
import edu.upenn.cis.cis455.model.DomainDataBaseModel.URLIdInfo;
import edu.upenn.cis.cis455.storage.DocumentIndexValue;
import edu.upenn.cis.cis455.storage.StorageImpl;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.ArrayList;
import java.util.List;

public class ShowHandler implements Route {

    private StorageImpl db;

    public ShowHandler(StorageImpl db) {
        this.db = db;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Session session = request.session();//this will create a new session if does not exist...therefore if uname and password are not
        String uname = session.attribute("user");
        String fname = session.attribute("firstName");
        if(uname == null || fname == null)
        {
            session.invalidate();
            response.status(404);
            response.body("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Title</title>\n" +
                    "</head>\n" +
                    "<body>\n"+
                    "Resource cannot be accessed\n"+
                    "</body>\n" +
                    "</html>");
        }

        else
        {
            StringBuilder stringBuilder = new StringBuilder();
            String channelName = request.queryParams("channel");
            List<Integer>docIds = new ArrayList<>();
            List<DocumentIndexValue>documentIndexValueList = db.getDocumentIndexManager().getDocumentsForChannel(channelName);
            for(DocumentIndexValue document : documentIndexValueList) {
                int docId = document.getDocId();
                docIds.add(docId);
            }
            stringBuilder.append("<div class=\"channelheader\"><br>");
            stringBuilder.append("created by:"+db.getChannelDBManager().getUserNameForChannel(channelName) + "<br>");
            for(int i = 0;i<docIds.size();i++)
            {
                String content = db.getUrldbManager().getDocInfoPrimaryIndex().get(docIds.get(i)).getData();
                EntityCursor<URLIdInfo> entityCursor = db.getUrldbManager().getUrlIdInfoSecondaryIndex().subIndex(docIds.get(i)).entities();
                try {
                    for(URLIdInfo urlIdInfo : entityCursor)
                    {
                        stringBuilder.append("Crawled on:"+urlIdInfo.getLastModifiedTime()+"<br>");
                        stringBuilder.append("Location:"+urlIdInfo.getDomainInfoKey().getCompletePath() + "<br>");
                    }
                    stringBuilder.append("<div class=\"document\"><br>");
                    stringBuilder.append(content + "</div>");

                } finally {
                    entityCursor.close();
                }

            }
            stringBuilder.append("</div>");
            String html = stringBuilder.toString();
            response.body(html); // write html body inside response
        return response.body();
        }

        return null;
    }
}
