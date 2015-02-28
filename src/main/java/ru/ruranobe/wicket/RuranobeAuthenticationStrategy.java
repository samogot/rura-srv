package ru.ruranobe.wicket;

import javax.servlet.http.Cookie;
import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.util.cookies.CookieUtils;

public class RuranobeAuthenticationStrategy extends DefaultAuthenticationStrategy
{
    
    public RuranobeAuthenticationStrategy(String cookieKey)
    {
        super(cookieKey);
    }
    
    @Override
    protected CookieUtils getCookieUtils()
    {
        if (cookieUtils == null)
        {
            cookieUtils = new RuranobeCookieUtils();
        }
        return cookieUtils;
    }
    
    public class RuranobeCookieUtils extends CookieUtils
    {
        @Override
	protected void initializeCookie(final Cookie cookie)
	{
            super.initializeCookie(cookie);
            cookie.setPath("/");
	}
    }
    
    private CookieUtils cookieUtils;
}
