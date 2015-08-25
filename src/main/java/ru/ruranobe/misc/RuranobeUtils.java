package ru.ruranobe.misc;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.INamedParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.encoding.UrlEncoder;
import ru.ruranobe.config.ApplicationContext;
import ru.ruranobe.config.ConfigurationManager;
import ru.ruranobe.wicket.RuraConstants;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
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

    public static ApplicationContext getApplicationContext()
    {
        return ConfigurationManager.getApplicationContext(RuraConstants.PATH_TO_CONFIGURATION_FILE,
                RuraConstants.PATH_TO_CONFIGURATION_FILE_SCHEMA);
    }

    public static String paragraphIdOf(Integer chapterId, Integer textId, int lineNo)
    {
        String r1 = "-" + (chapterId == null ? "" : chapterId.toString());
        String r2 = "-" + (textId == null ? "" : textId.toString());
        String r3 = "-" + lineNo;
        return "p" + r1 + r2 + r3;
    }

    public static Url mergeParameters(final Url url, final PageParameters params)
    {

        if (params == null)
        {
            return url;
        }

        Charset charset = url.getCharset();

        Url mergedUrl = Url.parse(url.toString(), charset);

        UrlEncoder urlEncoder = UrlEncoder.QUERY_INSTANCE;

        Set<String> setParameters = new HashSet<String>();

        for (INamedParameters.NamedPair pair : params.getAllNamed())
        {
            String key = urlEncoder.encode(pair.getKey(), charset);
            String value = urlEncoder.encode(pair.getValue(), charset);

            if (setParameters.contains(key))
            {
                mergedUrl.addQueryParameter(key, value);
            }
            else
            {
                mergedUrl.setQueryParameter(key, value);
                setParameters.add(key);
            }
        }

        return mergedUrl;
    }

    private RuranobeUtils()
    {

    }
}
