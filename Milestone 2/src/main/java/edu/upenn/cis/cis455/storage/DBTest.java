package edu.upenn.cis.cis455.storage;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

import java.io.File;

public class DBTest {

    public static void main(String ar[])
    {
        PrimaryIndex<KeyClass,RobotsEntity>cursor; // this is the object which has the reference...have getter
        //Environment
        Environment myDbEnvironment;
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);

        //The Environment will be given as an argument to WebInterface
        myDbEnvironment = new Environment(new File("/Users/karishma/Desktop/GitWorkspace/IWSProject2/555-hw2/src/main/java/edu/upenn/cis/cis455/storage"), envConfig);

        //configs
        StoreConfig config = new StoreConfig();
        config.setAllowCreate(true);
        EntityStore storeObject = new EntityStore(myDbEnvironment,"SampleStore",config);
        cursor = storeObject.getPrimaryIndex(KeyClass.class,RobotsEntity.class);
        RobotsEntity entity = new RobotsEntity("www.google.com",80);
        entity.getAdditionalInfo().put("testkey","testvalue");
        cursor.putNoReturn(entity);
        //create a new instance to get the object
        RobotsEntity getEntity = cursor.get(new KeyClass("www.google.com",80));
        //System.out.println(getEntity.getAdditionalInfo());

        //Use the same logic to store users
    }


}
