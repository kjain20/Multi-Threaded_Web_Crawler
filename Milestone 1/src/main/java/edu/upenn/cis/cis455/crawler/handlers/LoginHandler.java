package edu.upenn.cis.cis455.crawler.handlers;

import edu.upenn.cis.cis455.Util.EncryptPassword;
import spark.Request;
import spark.Route;
import spark.Response;
import spark.HaltException;
import spark.Session;
import edu.upenn.cis.cis455.storage.StorageInterface;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginHandler implements Route {
    StorageInterface db;
    
    public LoginHandler(StorageInterface db) {
        this.db = db;
    }

    @Override
    public String handle(Request req, Response resp) throws HaltException {
        String user = req.queryParams("username");
        String pass = req.queryParams("password");
        System.err.println("Login request for " + user + " and " + pass);
        //convert pass into sha256 password
        String encrypted_password = EncryptPassword.getSHA(pass);

        //getSession boolean user is valid

        if (db.getSessionForUser(user, encrypted_password)) {
            System.err.println("Logged in!");
            Session session = req.session();
            //this creates new session...so set maxinactive interval
            session.maxInactiveInterval(300);
            session.attribute("user", user);
            session.attribute("password", pass);
            String firstName = db.getFirstName(user);
            String lastName = db.getLastName(user);
            session.attribute("firstName",firstName);
            session.attribute("lastName",lastName);
            resp.redirect("/index");
        } else {
            System.err.println("Invalid credentials");
            resp.redirect("/login-form.html");
        }

            
        return resp.body();
    }


}
