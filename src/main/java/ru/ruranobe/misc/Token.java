package ru.ruranobe.misc;

import java.security.SecureRandom;
import java.util.Date;

public class Token
{
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 64;
    private String tokenValue;
    private Date tokenExpirationDate;

    private Token(String tokenValue, Date tokenExpirationDate)
    {
        this.tokenValue = tokenValue;
        this.tokenExpirationDate = tokenExpirationDate;
    }

    public static Token valueOf(int uniqueId, long expirationTime)
    {
        StringBuilder tokenValue = new StringBuilder(Integer.toHexString(uniqueId));
        tokenValue.append(generateTokenValue(TOKEN_LENGTH - tokenValue.length()));
        return new Token(tokenValue.toString(), generateTokenExpirationDate(expirationTime));
    }

    private static String generateTokenValue(int length)
    {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i)
        {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private static Date generateTokenExpirationDate(long expirationTime)
    {
        return new Date(System.currentTimeMillis() + expirationTime);
    }

    public Date getTokenExpirationDate()
    {
        return tokenExpirationDate;
    }

    public String getTokenValue()
    {
        return tokenValue;
    }
}