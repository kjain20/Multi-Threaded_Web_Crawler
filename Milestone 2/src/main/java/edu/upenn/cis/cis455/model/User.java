package edu.upenn.cis.cis455.model;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import java.io.Serializable;

//This is the user class
//convert into dpl aligned storage
@Entity
public class User implements Serializable {
    //userId mentioned??

    @PrimaryKey(sequence = "userId")
    Integer userId;

    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    String userName; //this is the secondary -- inorder to retrieve for validation

    String firstName;
    String lastName;
    String password;

    public User()
    {

    }

    public User(String userName, String password, String firstName, String lastName) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
