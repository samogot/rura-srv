package ru.ruranobe.engine.wiki.parser;

/**
 * TODO: this is not a correct implementation. e.g. text "<sup>" will not be escaped
 * while it should. The text "<sup></sup>" should not be escaped. Also it will
 * think that <---> is correct tag combination. But nah... Good enough.
 */
public class SimpleHtmlSanitizer
{
    public static String apply(String text)
    {
        StringBuilder result = new StringBuilder();

        String beginAndEnd = "abracadabraabracadabraabracadabraabracadabraabracadabraabracadabraabracadabraabracadabra";
        text = beginAndEnd + text + beginAndEnd;
        boolean ignoreInComment = false;
        boolean commentInNewLine = false;

        for (int i = beginAndEnd.length(); i < text.length() - beginAndEnd.length(); ++i)
        {
            char c = text.charAt(i);
            if (c == '<' && !ignoreInComment)
            {
                String temp = text.substring(i, i + 5);
                switch (temp)
                {
                    case "<sub>":
                        result.append(temp);
                        i += temp.length() - 1;
                        continue;
                    case "<sup>":
                        result.append(temp);
                        i += temp.length() - 1;
                        continue;
                }

                temp = text.substring(i, i + 6);
                switch (temp)
                {
                    case "</sub>":
                        result.append(temp);
                        i += temp.length() - 1;
                        continue;
                    case "</sup>":
                        result.append(temp);
                        i += temp.length() - 1;
                        continue;
                }

                temp = text.substring(i, i + 4);
                if (temp.equals("<!--"))
                {
                    ignoreInComment = true;
                    commentInNewLine = text.codePointBefore(i) == '\n' || text.codePointBefore(i) == '\r';
                    i += temp.length() - 1;
                    continue;
                }

                result.append("&lt;");
            }
            else if (c == '>')
            {
                String temp = text.substring(i - 2, i + 1);
                if (temp.equals("-->"))
                {
                    if (commentInNewLine && (text.codePointAt(i + 1) == '\n' || text.codePointAt(i + 1) == '\r'))
                    {
                        ++i;
                    }
                    if (commentInNewLine && (text.codePointAt(i + 1) == '\n' || text.codePointAt(i + 1) == '\r'))
                    {
                        ++i;
                    }
                    ignoreInComment = false;
                    continue;
                }
                if (!ignoreInComment)
                {
                    result.append("&gt;");
                }
            }
            else if (c == '&' && !ignoreInComment)
            {
                StringBuilder temp = new StringBuilder("&");
                for (int j = i + 1; j < i + beginAndEnd.length(); ++j)
                {
                    if (text.charAt(j) == ';')
                    {
                        temp.append(text.charAt(j));
                        break;
                    }
                    else if (text.charAt(j) == '&')
                    {
                        break;
                    }
                    else
                    {
                        temp.append(text.charAt(j));
                    }
                }

                if (temp.toString().matches("&[^ ]*;"))
                {
                    result.append('&');
                    continue;
                }

                result.append("&amp;");
            }
            else if (!ignoreInComment && c != '\r')
            {
                result.append(c);
            }
        }

        return result.toString();
    }
}
