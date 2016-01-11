package ru.ruranobe.misc;

import org.apache.wicket.util.string.Strings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Encodes a string using MD5 hashing
 *
 * @author Rafael Steil
 * @version $Id: MD5.java,v 1.7 2006/08/23 02:13:44 rafaelsteil Exp $
 *          <p/>
 *          Copied from jforum sources.
 */
public class MD5
{

    public static String crypt(String str)
    {
        if (Strings.isEmpty(str))
        {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }

        StringBuilder hexString = new StringBuilder();

        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] hash = md.digest();

            for (byte hashByte : hash)
            {
                if ((0xff & hashByte) < 0x10)
                {
                    hexString.append("0").append(Integer.toHexString((0xFF & hashByte)));
                }
                else
                {
                    hexString.append(Integer.toHexString(0xFF & hashByte));
                }
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("" + e);
        }

        return hexString.toString();
    }
}
