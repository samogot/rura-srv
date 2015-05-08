package ru.ruranobe.engine.wiki.parser;

import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.WikiModel;
import info.bliki.wiki.namespaces.INamespace;

import java.util.Locale;
import java.util.ResourceBundle;

public class RuranobeWikiModel extends WikiModel
{
    static
    {
        /* Associates {{image}} with ImageParserFunction */
        Configuration.DEFAULT_CONFIGURATION.addTemplateFunction("image", ImageParserFunction.CONST);
    }

    public RuranobeWikiModel(String imageBaseURL, String linkBaseURL)
    {
        this(Configuration.DEFAULT_CONFIGURATION, imageBaseURL, linkBaseURL);
    }

    public RuranobeWikiModel(Configuration configuration, String imageBaseURL, String linkBaseURL)
    {
        super(configuration, imageBaseURL, linkBaseURL);
    }

    public RuranobeWikiModel(Configuration configuration, Locale locale, String imageBaseURL, String linkBaseURL)
    {
        super(configuration, locale, imageBaseURL, linkBaseURL);
    }

    public RuranobeWikiModel(Configuration configuration, ResourceBundle resourceBundle, INamespace namespace, String imageBaseURL,
                             String linkBaseURL)
    {
        super(configuration, resourceBundle, namespace, imageBaseURL, linkBaseURL);
    }
}
