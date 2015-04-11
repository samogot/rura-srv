package ru.ruranobe.wicket;

import java.util.Iterator;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.apache.wicket.util.string.Strings;

public class PageParameterEncoder extends UrlPathPageParametersEncoder 
{
    @Override
    public PageParameters decodePageParameters(Url url)
    {
        PageParameters params = new PageParameters();

        for (Iterator<String> segment = url.getSegments().iterator(); segment.hasNext();)
        {
            String key = segment.next();
            if (Strings.isEmpty(key))
            {
                continue;
            }
            
            params.add(key, segment.hasNext() ? segment.next() : "");
        }

        return params.isEmpty() ? null : params;
    }
}
