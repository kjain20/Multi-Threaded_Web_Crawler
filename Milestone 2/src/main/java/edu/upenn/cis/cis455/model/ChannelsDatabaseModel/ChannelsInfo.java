package edu.upenn.cis.cis455.model.ChannelsDatabaseModel;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import java.io.Serializable;

@Entity
public class ChannelsInfo implements Serializable {

    @PrimaryKey
    private String channelName;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String userName;

    private String xpath;

    public ChannelsInfo() {
    }

    public ChannelsInfo(String channelName, String userName, String xpath) {
        this.channelName = channelName;
        this.userName = userName;
        this.xpath = xpath;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getUserId() {
        return userName;
    }

    public void setUserId(String userId) {
        this.userName = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }
}
