package ru.ruranobe.wicket;

import java.security.SecureRandom;
import java.sql.Date;

public class Token 
{
 
    public static Token newInstance()
    {
        return new Token(generateTokenValue(), generateTokenExpirationDate());
    }
    
    private static String generateTokenValue()
    {
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
        for(int i = 0; i < TOKEN_LENGTH; ++i) 
        {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private static Date generateTokenExpirationDate()
    {
        return new Date(System.currentTimeMillis() + EXPIRATION_IN_MILLIS);
    }
    
    public Date getTokenExpirationDate()
    {
        return tokenExpirationDate;
    }

    public String getTokenValue()
    {
        return tokenValue;
    }
    
    private Token(String tokenValue, Date tokenExpirationDate)
    {
        this.tokenValue = tokenValue;
        this.tokenExpirationDate = tokenExpirationDate;
    }
    
    private String tokenValue;
    private Date tokenExpirationDate;
    private static final long EXPIRATION_IN_MILLIS = 21600000L; // 6 hours
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 128;
}