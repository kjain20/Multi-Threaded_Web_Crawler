package edu.upenn.cis.cis455.storage;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

import java.io.Serializable;

@Persistent
public class UserPK implements Serializable {

    @KeyField(1)
    private String uuid; //this must be count of values + 1..shouldnt be uuid

    public UserPK() {
    }

    public UserPK(String uuid) {
        this.uuid = uuid; // this must not be randome UUID. It must be count of database + 1
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
