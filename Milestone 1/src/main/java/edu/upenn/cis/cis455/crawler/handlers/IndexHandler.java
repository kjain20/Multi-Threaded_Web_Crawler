package edu.upenn.cis.cis455.crawler.handlers;

import edu.upenn.cis.cis455.storage.StorageInterface;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

public class IndexHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Session session = request.session();//this will create a new session if does not exist...therefore if uname and password are not
        //valid then delete this session by calling invalidate()
        String uname = session.attribute("user");
        String fname = session.attribute("firstName");
        String lastname = session.attribute("lastName");
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
        else {
            response.body("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Title</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "This is your User Name:\t" + uname + "<br><br>" +
                    "This is your FName:\t" + fname + "\n" +
                    "This is your LName:\t" + lastname + "\n" +
                    "</body>\n" +
                    "</html>"); // write html body inside response
        }
        return response.body(); //this must go as the response to /index request
    }
}
