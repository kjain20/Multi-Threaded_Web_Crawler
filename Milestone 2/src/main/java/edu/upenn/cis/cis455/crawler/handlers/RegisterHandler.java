package edu.upenn.cis.cis455.crawler.handlers;

import com.sleepycat.je.UniqueConstraintException;
import edu.upenn.cis.cis455.Util.EncryptPassword;
import edu.upenn.cis.cis455.storage.StorageInterface;
import org.eclipse.jetty.client.HttpRequest;
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
        try {
            String user = request.queryParams("username");
            String password = request.queryParams("password");
            String firstName = request.queryParams("firstName");
            String secondName = request.queryParams("secondName");
            String encrypted_password = EncryptPassword.getSHA(password);
            db.addUser(user, encrypted_password, firstName, secondName);
            response.redirect("/login-form.html"); //redirect to login is not calling the handler
        }
        catch (UniqueConstraintException e)
        {

            response.body("User Already Present");
            return "User Already Present";
        }
        return "";


    }
}
