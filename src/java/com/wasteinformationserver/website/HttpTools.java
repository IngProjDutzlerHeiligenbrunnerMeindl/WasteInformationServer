package com.wasteinformationserver.website;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * basic http tools
 *
 * @author Lukas Heiligenbrunner
 */
public class HttpTools {
    /**
     * create md5 hash of string
     *
     * @param value input string
     * @return md5 hash
     */
    public static String StringToMD5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(value.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            return no.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
