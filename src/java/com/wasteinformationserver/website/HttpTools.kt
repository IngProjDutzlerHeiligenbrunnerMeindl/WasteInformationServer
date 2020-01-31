package com.wasteinformationserver.website

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * basic http tools
 *
 * @author Lukas Heiligenbrunner
 */
class HttpTools {
    companion object{
        /**
         * create md5 hash of string
         *
         * @param value input string
         * @return md5 hash
         */
        fun StringToMD5(value: String): String {
            return try {
                val md = MessageDigest.getInstance("MD5")
                val messageDigest = md.digest(value.toByteArray())
                val no = BigInteger(1, messageDigest)
                no.toString(16)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                ""
            }
        }
    }
}