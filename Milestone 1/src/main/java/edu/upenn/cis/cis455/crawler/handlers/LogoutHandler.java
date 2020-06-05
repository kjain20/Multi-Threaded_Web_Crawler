package edu.upenn.cis.cis455.crawler.handlers;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

public class LogoutHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        Session session = request.session();//this will create a new session if does not exist...therefore if uname and password are not
        //valid then delete this session by calling invalidate()
        String uname = session.attribute("user");
        String fname = session.attribute("firstName");
        String lastname = session.attribute("lastName");
        if(uname == null || fname == null || lastname == null)
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
            response.redirect("/login-form.html");
        }
        return response.body(); //this must go as the response to /index request
    }
}
