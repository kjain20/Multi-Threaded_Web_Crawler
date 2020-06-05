package edu.upenn.cis.cis455.Util;

import org.omg.CORBA.Environment;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptPassword {

    //calculating sha 256 hashed password
    public static String getSHA(String input)
    {

        try {

            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {

            return null;
        }
    }

    public static void main(String[] args) {
        ////System.out.println(EncryptPassword.getSHA("abc"));
    }
}
