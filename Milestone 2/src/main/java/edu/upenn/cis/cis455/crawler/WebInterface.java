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
            System.exit(1);
        }

        if (!Files.exists(Paths.get(args[0]))) {
            try {
                Files.createDirectory(Paths.get(args[0]));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        port(8888); //change port to 8888
        StorageInterface database = StorageFactory.getDatabaseInstance(args[0]);
        Spark.ipAddress("127.0.0.1");
        LoginFilter testIfLoggedIn = new LoginFilter(database);
        if (args.length == 2) {
            staticFiles.externalLocation(args[1]);
            staticFileLocation(args[1]);
        }

        before("/*", "POST", testIfLoggedIn);
        before("/*","GET",testIfLoggedIn);

        get("/lookup","GET",new LookupHandler((StorageImpl)database));
        get("/index",new IndexHandler((StorageImpl)database)); // this must show the user credentials, this route is only called by internal valid requests
        get("/",new IndexHandler((StorageImpl)database)); //this also calls the index.html handler
        get("/logout",new LogoutHandler());
        get("/create/:name",new SubscriptionHandler((StorageImpl)database));
        get("/show",new ShowHandler((StorageImpl)database));

        post("/index",new IndexHandler((StorageImpl) database));
        post("/register",new RegisterHandler(database)); //redirect after register
        post("/login", new LoginHandler(database)); //this will create a session
        post("/logout",new LogoutHandler());
        //post("/create",new SubscriptionHandler((StorageImpl)database));

        awaitInitialization();
    }
}
