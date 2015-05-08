package ru.ruranobe.misc;

import org.apache.wicket.request.flow.RedirectToUrlException;

import java.util.regex.Pattern;

public class RuranobeUtils
{
    public static final RedirectToUrlException REDIRECT_TO_404 = new RedirectToUrlException("http://404");
    private static final String PASSWORD_REGEXP = "^[A-Za-z0-9]+";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEXP);

    public static boolean isPasswordSyntaxValid(String password)
    {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
