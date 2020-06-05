package edu.upenn.cis.cis455.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import java.io.Serializable;
import java.util.UUID;

@Entity
public class UserClass implements Serializable {
    public UserPK getPrimary_key() {
        return primary_key;
    }

    public void setPrimary_key(UserPK primary_key) {
        this.primary_key = primary_key;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    @PrimaryKey
    private UserPK primary_key;
    private String uuid;//since UUID is String
    private String fname;
    private String lname;

    public UserClass() {
    }

    public UserClass(String fname, String lname) {
        //cant use random number ---can clash with multiple users,usually how is uuid generated?? - safe for pkeys
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
        this.primary_key = new UserPK(this.uuid);
        this.fname = fname;
        this.lname = lname;
    }
}
