package edu.upenn.cis.cis455.crawler.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.upenn.cis.cis455.storage.StorageInterface;
import spark.Request;
import spark.Filter;
import spark.Response;


public class LoginFilter implements Filter {
    Logger logger = LogManager.getLogger(LoginFilter.class);
    
    public LoginFilter(StorageInterface db) {
        //DB instance not required here
    }

    //redirect to login-form if request is for login
    @Override
    public void handle(Request req, Response response) throws Exception {
       if (!req.pathInfo().equals("/login-form.html") &&
        !req.pathInfo().equals("/login") &&
        !req.pathInfo().equals("/register") &&
        !req.pathInfo().equals("/register.html")
        ) {
            logger.info("Request is NOT login/registration"); // since no condition is matched
            if (req.session(false) == null) {
                response.redirect("/login-form.html");
            } else {
                //req.attribute("user", req.session().attribute("user"));
                //response.redirect(req.url());
            }

        } else {
//            logger.info
            //leave un redircted

        }
        
    }
}
