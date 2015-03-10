package ru.ruranobe.engine.wiki.parser;

public class WikiParser 
{
    public static String parseText(String text)
    {
        return RuranobeWikiModel.toHtml(text);
    }

}
