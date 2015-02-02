package ru.ruranobe.engine.wiki.parser;

import info.bliki.wiki.model.WikiModel;


public class WikiParser 
{
    public static String parseText(String text)
    {
        return WikiModel.toHtml(text);
    }
}
