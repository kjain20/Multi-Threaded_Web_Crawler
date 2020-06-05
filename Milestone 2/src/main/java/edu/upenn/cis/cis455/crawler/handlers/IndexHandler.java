package edu.upenn.cis.cis455.crawler.handlers;

import edu.upenn.cis.cis455.model.ChannelsDatabaseModel.ChannelsInfo;
import edu.upenn.cis.cis455.storage.StorageImpl;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.ArrayList;
import java.util.List;

public class IndexHandler implements Route {

    private StorageImpl db;

    public IndexHandler(StorageImpl db) {
        this.db = db;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Session session = request.session();//this will create a new session if does not exist...therefore if uname and password are not
        String uname = session.attribute("user");
        String fname = session.attribute("firstName");
        String lastname = session.attribute("lastName");
        if (uname == null || fname == null) {
            session.invalidate();
            response.status(404);
            response.body("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Title</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "Resource cannot be accessed\n" +
                    "</body>\n" +
                    "</html>");
        } else {
            List<ChannelsInfo> channlesList = new ArrayList<>();
            try {
                channlesList.addAll(db.getChannelDBManager().getAllChannels());

                String htmlChannelString = "";
                for (ChannelsInfo channnel : channlesList) {
                    htmlChannelString = htmlChannelString + channnel.getChannelName() + "<br>";
                }
                response.body("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Title</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "This is your User Name:\t" + uname + "<br><br>" +
                        "\nThis is your FName:\t" + fname + "<br>" +
                        "\nThis is your LName:\t" + lastname + "<br>" +
                        "\n Subscribed Channels:<br>" +
                        "\n" + htmlChannelString + "<br>" +
                        "</body>\n" +
                        "</html>"); // write html body inside response
            } finally {
                //...
            }
            //this must go as the response to /index request
        }
        return response.body();
    }
}
