package edu.upenn.cis.cis455.storage;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.*;
import edu.upenn.cis.cis455.model.User;
import edu.upenn.cis.cis455.storage.DBManagers.DomainCrawlDelayDBManager;
import edu.upenn.cis.cis455.storage.DBManagers.DomainRobotDBManager;
import edu.upenn.cis.cis455.storage.DBManagers.URLDBManager;

import java.io.File;

//This class must talk to different oracle db instances and get records as per query
public class StorageImpl implements StorageInterface {

    private PrimaryIndex<Integer, User> pIdx; // this is the primary index
    private SecondaryIndex<String,Integer,User> sIdx; //this must be compatible
    private EntityStore user_store_object;
    private DomainRobotDBManager domainRobotDBManager;
    private DomainCrawlDelayDBManager domainCrawlDelayDBManager;
    private URLDBManager urldbManager;
    private Environment environment;
    private String database_storage_directory;


    public SecondaryIndex<String, Integer, User> getsIdx() {
        return sIdx;
    }

    public void setsIdx(SecondaryIndex<String, Integer, User> sIdx) {
        this.sIdx = sIdx;
    }


    public PrimaryIndex<Integer, User> getpIdx() {
        return pIdx;
    }

    public void setpIdx(PrimaryIndex<Integer, User> pIdx) {
        this.pIdx = pIdx;
    }

    @Override
    public int addUser(String username, String password) {
        return 0;
    }



    public StorageImpl(String database_storage_directory) {
        this.database_storage_directory = database_storage_directory;
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);
        environment = new Environment(new File(database_storage_directory), environmentConfig);
        this.domainRobotDBManager = new DomainRobotDBManager();
        this.urldbManager = new URLDBManager(environment);
        domainCrawlDelayDBManager = new DomainCrawlDelayDBManager();
    }

    //function to start connection

    public void openConnectionForUserDB()
    {
        //Environment
        try {
        Environment myDbEnvironment;
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(true);
        //The Environment will be given as an argument to WebInterface
        myDbEnvironment = new Environment(new File(database_storage_directory), envConfig);
        //configs
        StoreConfig config = new StoreConfig();
        config.setAllowCreate(true);
        this.user_store_object = new EntityStore(myDbEnvironment,"UserDB",config);
        this.pIdx = this.user_store_object.getPrimaryIndex(Integer.class, User.class);
        this.sIdx = this.user_store_object.getSecondaryIndex(this.pIdx, String.class, "userName");
        }
        catch (Exception e)
        {
            //to see if exception occurred here
            //System.out.println("entered");
        }
    }


    @Override
    public int getCorpusSize() {
        return 0;
    }

    @Override
    public int addDocument(String url, String documentContents) {
        return 0;
    }

    @Override
    public int getLexiconSize() {
        return 0;
    }

    @Override
    public int addOrGetKeywordId(String keyword) {
        return 0;
    }

    @Override
    public int addUser(String username, String password, String firstName, String lastName) {
        User new_user = new User(username,password,firstName,lastName);
        if(this.user_store_object != null)
        {
            this.pIdx.putNoReturn(new_user);

        }
        else
        {
            openConnectionForUserDB();
            this.pIdx.putNoReturn(new_user);
        }
        return 0;
    }

    @Override
    public boolean getSessionForUser(String username, String password) {

        openConnectionForUserDB(); //creates store object
        //get the value corresponding to this username and password
        try {
            User isValidUser = this.sIdx.get(username); //user is valid ..check for valid password
            //how to retrieve other
            if (password.equalsIgnoreCase(isValidUser.getPassword())) {
                return true;
            } else {
                return false;
            }
        }
        catch (DatabaseException e)
        {
            return false;
        }

    }

    public String getFirstName(String username)
    {
        try
        {
            User isValidUser = this.sIdx.get(username);
            String firstName = isValidUser.getFirstName();
            return firstName;
        }
        catch (DatabaseException e)
        {
            return "";
        }
    }

    public String getLastName(String username)
    {
        try
        {
            User isValidUser = this.sIdx.get(username);
            String lastName = isValidUser.getLastName();
            return lastName;
        }
        catch (DatabaseException e)
        {
            return "";
        }
    }


    @Override
    public String getDocument(String url) {
        return null;
    }

    //this function must clear all open store objects etc....
    @Override
    public void close() {
        this.urldbManager.close();
    }

    public DomainRobotDBManager getDomainRobotDBManager() {
        return this.domainRobotDBManager;
    }

    public URLDBManager getUrldbManager() {
        return urldbManager;
    }

    public EntityStore getUser_store_object() {
        return user_store_object;
    }

    public String getDatabase_storage_directory() {
        return database_storage_directory;
    }

    public DomainCrawlDelayDBManager getDomainCrawlDelayDBManager() {
        return domainCrawlDelayDBManager;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
