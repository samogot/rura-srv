package ru.ruranobe.engine.wiki.parser;

import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.template.AbstractTemplateFunction;
import info.bliki.wiki.template.ITemplateFunction;
import java.io.IOException;
import java.util.List;

public class ImageParserFunction extends AbstractTemplateFunction 
{

    public ImageParserFunction() 
    {
    }

    @Override
    public String parseFunction(List<String> list, IWikiModel model, 
            char[] src, int beginIndex, int endIndex, boolean b) throws IOException
    {
        if (1==1)
        {
            throw new RuntimeException("Found you " + src);
        }
        if (list.size() > 0) 
        {
            String result = parse(list.get(0), model);
            return result;
        }
        return null;
    }

    public final static ITemplateFunction CONST = new ImageParserFunction();
} 