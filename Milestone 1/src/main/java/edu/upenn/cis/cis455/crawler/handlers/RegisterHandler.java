package edu.upenn.cis.cis455.crawler.handlers;

import edu.upenn.cis.cis455.Util.EncryptPassword;
import edu.upenn.cis.cis455.storage.StorageInterface;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    StorageInterface db;

    public RegisterHandler(StorageInterface db) {
        this.db = db;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String  user = request.queryParams("username");
        String password = request.queryParams("password");
        String firstName = request.queryParams("firstName");
        String secondName = request.queryParams("secondName");
        String encrypted_password = EncryptPassword.getSHA(password);
        //open connection to db
        db.addUser(user,encrypted_password,firstName,secondName);
        //insert into db
        //close connection into db
        response.redirect("/login-form.html"); //redirect to login is not calling the handler
        return "";
    }
}
