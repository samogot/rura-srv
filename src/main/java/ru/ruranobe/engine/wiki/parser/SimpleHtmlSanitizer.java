package ru.ruranobe.engine.wiki.parser;

/**
 * TODO: this is not a correct implementation. e.g. text "<sup>" will not be escaped
 *       while it should. The text "<sup></sup>" should not be escaped. Also it will
 *       think that <---> is correct tag combination. But nah... Good enough.
 */
public class SimpleHtmlSanitizer
{
    public static String apply(String text)
    {
        StringBuilder result = new StringBuilder();

        String beginAndEnd = "abracadabraabracadabraabracadabraabracadabraabracadabraabracadabraabracadabraabracadabra";
        text = beginAndEnd + text + beginAndEnd;

        for (int i = beginAndEnd.length(); i < text.length()-beginAndEnd.length(); ++i)
        {
            char c = text.charAt(i);
            if (c == '<')
            {
                String temp = text.substring(i, i+5);
                switch (temp)
                {
                    case "<sub>":
                        result.append(temp);
                        i+=temp.length()-1;
                        continue;
                    case "<sup>":
                        result.append(temp);
                        i+=temp.length()-1;
                        continue;
                }

                temp = text.substring(i, i+6);
                switch (temp)
                {
                    case "</sub>":
                        result.append(temp);
                        i+=temp.length()-1;
                        continue;
                    case "</sup>":
                        result.append(temp);
                        i+=temp.length()-1;
                        continue;
                }

                temp = text.substring(i, i+4);
                if (temp.equals("<!--"))
                {
                    result.append(temp);
                    i+=temp.length()-1;
                    continue;
                }

                result.append("&lt;");
            }
            else if (c == '>')
            {
                String temp = text.substring(i-2, i+1);
                if (temp.equals("-->"))
                {
                    result.append('>');
                    continue;
                }

                result.append("&gt;");
            }
            else if (c == '&')
            {
                StringBuilder temp = new StringBuilder("&");
                for (int j = i+1; j < i+beginAndEnd.length(); ++j)
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
            else
            {
                result.append(c);
            }
        }

        return result.toString();
    }
}
