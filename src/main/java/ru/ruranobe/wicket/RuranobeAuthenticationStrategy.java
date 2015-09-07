package ru.ruranobe.wicket;

import org.apache.wicket.Application;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.util.cookies.CookieUtils;
import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;

import javax.servlet.http.Cookie;

/* 
 * 6.18.0 version of class DefaultAuthenticationStrategy 
 * is poor. Here I copied source code from later release.
 */
public class RuranobeAuthenticationStrategy implements IAuthenticationStrategy
{

    protected final String cookieKey;
    protected final String VALUE_SEPARATOR = "-keiuekk-";
    private CookieUtils cookieUtils;
    private ICrypt crypt;

    public RuranobeAuthenticationStrategy(final String cookieKey)
    {
        this.cookieKey = Args.notEmpty(cookieKey, "cookieKey");
    }

    protected CookieUtils getCookieUtils()
    {
        if (cookieUtils == null)
        {
            cookieUtils = new RuranobeCookieUtils();
        }
        return cookieUtils;
    }

    protected ICrypt getCrypt()
    {
        if (crypt == null)
        {
            crypt = Application.get().getSecuritySettings().getCryptFactory().newCrypt();
        }
        return crypt;
    }

    @Override
    public String[] load()
    {
        String value = getCookieUtils().load(cookieKey);
        if (!Strings.isEmpty(value))
        {
            try
            {
                value = getCrypt().decryptUrlSafe(value);
            }
            catch (RuntimeException e)
            {
                getCookieUtils().remove(cookieKey);
                value = null;
            }
            return decode(value);
        }

        return null;
    }

    protected String[] decode(String value)
    {
        if (!Strings.isEmpty(value))
        {
            String username = null;
            String password = null;

            String[] values = value.split(VALUE_SEPARATOR);
            if ((values.length > 0) && (!Strings.isEmpty(values[0])))
            {
                username = values[0];
            }
            if ((values.length > 1) && (!Strings.isEmpty(values[1])))
            {
                password = values[1];
            }

            return new String[]{username, password};
        }
        return null;
    }

    @Override
    public void save(String username, String password)
    {
        String encryptedValue = getCrypt().encryptUrlSafe(encode(username, password));
        getCookieUtils().save(cookieKey, encryptedValue);
    }

    protected String encode(final String credential, final String... extraCredentials)
    {
        StringBuilder value = new StringBuilder(credential);
        if (extraCredentials != null)
        {
            for (String extraCredential : extraCredentials)
            {
                value.append(VALUE_SEPARATOR).append(extraCredential);
            }
        }
        return value.toString();
    }

    @Override
    public void remove()
    {
        getCookieUtils().remove(cookieKey);
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
}