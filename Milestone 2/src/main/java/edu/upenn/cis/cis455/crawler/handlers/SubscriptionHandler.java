package edu.upenn.cis.cis455.crawler.handlers;

import com.sleepycat.je.DatabaseException;
import edu.upenn.cis.cis455.storage.StorageImpl;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

public class SubscriptionHandler implements Route {
    private StorageImpl db;

    public SubscriptionHandler(StorageImpl db) {
        this.db = db;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            Session session = request.session();
            String userName = session.attribute("user");
            String channelName = request.params("name");
            String xpath = request.queryParams("xpath");
            //there will be an xpath matcher for valid xpaths
            db.addChannelForUser(channelName,userName,xpath);

        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }
        return "Added Channel";
    }
}
