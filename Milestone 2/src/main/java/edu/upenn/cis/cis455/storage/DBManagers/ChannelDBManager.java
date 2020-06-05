package edu.upenn.cis.cis455.storage.DBManagers;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.*;
import edu.upenn.cis.cis455.model.ChannelsDatabaseModel.ChannelsInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChannelDBManager {

    private PrimaryIndex<String, ChannelsInfo> pIdx;
    private SecondaryIndex<String, String, ChannelsInfo> sIdx;
    private EntityStore channel_store_object;

    public ChannelDBManager(Environment environment) {


        StoreConfig config = new StoreConfig();
        config.setAllowCreate(true);
        config.setTransactional(true);

        this.channel_store_object = new EntityStore(environment, "ChannelsDB", config);
        //StoreName -> change to same name
        this.pIdx = this.channel_store_object.getPrimaryIndex(String.class, ChannelsInfo.class);
        this.sIdx = this.channel_store_object.getSecondaryIndex(this.pIdx, String.class, "userName");
    }


    public ChannelsInfo insertRecord(ChannelsInfo channelsInfo, Transaction txn) {
        ChannelsInfo checkIfPresent = pIdx.get(txn, channelsInfo.getChannelName(), LockMode.READ_COMMITTED);
        if (checkIfPresent != null) {
            return null;
        }
        pIdx.put(txn, channelsInfo);
        return channelsInfo;
    }


    public PrimaryIndex<String, ChannelsInfo> getpIdx() {
        return pIdx;
    }

    public SecondaryIndex<String, String, ChannelsInfo> getsIdx() {
        return sIdx;
    }

    public String getUserNameForChannel(String channelName)
    {
        ChannelsInfo checkIfPresent = pIdx.get(null, channelName, LockMode.READ_COMMITTED);
        if(checkIfPresent != null)
        {
            return checkIfPresent.getUserName();
        }
        return "";
    }

    public EntityStore getChannel_store_object() {
        return channel_store_object;
    }

    public List<String> getSubscribedChannelsList(String userName) {
        List<String> channels = new ArrayList<>();
        EntityCursor<ChannelsInfo> cursorObject = sIdx.subIndex(userName).entities();
        try {
            for (ChannelsInfo channelsInfo : cursorObject) {
                channels.add(channelsInfo.getChannelName());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            cursorObject.close();
        }
        return channels;
    }

    public List<ChannelsInfo> getAllChannels() {
        List<ChannelsInfo> channels = new ArrayList<>();
        EntityCursor<ChannelsInfo> cursorObject = pIdx.entities();
        try {
            for (ChannelsInfo channelsInfo : cursorObject) {
                channels.add(channelsInfo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            cursorObject.close();
        }
        return channels;
    }

    public static void main(String[] args) {

        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);
        Environment environment = new Environment(new File("/Users/karishma/Desktop/GitWorkspace/IWSProject2/555-hw2/www"), environmentConfig);
        ChannelDBManager db = new ChannelDBManager(environment);
        //ChannelsInfo channelsInfo = new ChannelsInfo("");
        System.out.println(db.getpIdx().get("sports"));
    }

}
