package edu.upenn.cis.cis455.crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static spark.Spark.*;

import edu.upenn.cis.cis455.crawler.handlers.*;
import edu.upenn.cis.cis455.storage.StorageFactory;
import edu.upenn.cis.cis455.storage.StorageImpl;
import edu.upenn.cis.cis455.storage.StorageInterface;
import spark.Spark;

public class WebInterface {
    public static void main(String args[]) {

        if (args.length < 1 || args.length > 2) {
            //System.out.println("Syntax: WebInterface {path} {root}");
            System.exit(1);
        }

        //System.out.println("Arg[0],Arg[1]:"+args[0]+","+args[1]);
        //args[0] - database path
        // Creating directory for database??
        if (!Files.exists(Paths.get(args[0]))) {
            try {
                Files.createDirectory(Paths.get(args[0]));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        port(8888); //change port to 8888

        //get database for login check
        //StorageInterface database = StorageFactory.getDatabaseInstance(args[0]);
        StorageInterface database = StorageFactory.getDatabaseInstance(args[0]);
        //use this database object for open() , funciton(), close()
        Spark.ipAddress("0.0.0.0");


        //Route must be registered for
        //implement handle for logged in = session present
        LoginFilter testIfLoggedIn = new LoginFilter(database);
        
        if (args.length == 2) {
            staticFiles.externalLocation(args[1]);
            //did server start here??
            staticFileLocation(args[1]);
        }

        before("/*", "POST", testIfLoggedIn); // every route except login is again re-routed to login form
        before("/*","GET",testIfLoggedIn);
        get("/lookup","GET",new LookupHandler((StorageImpl)database));
        post("/index",new IndexHandler());
        get("/index",new IndexHandler()); // this must show the user credentials, this route is only called by internal valid requests
        get("/",new IndexHandler()); //this also calls the index.html handler
        post("/register",new RegisterHandler(database)); //redirect after register
        post("/login", new LoginHandler(database)); //this will create a session
        get("/logout",new LogoutHandler());
        post("/logout",new LogoutHandler());
        awaitInitialization();
    }
}
