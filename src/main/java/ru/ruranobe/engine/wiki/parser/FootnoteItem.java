package ru.ruranobe.engine.wiki.parser;

public class FootnoteItem
{
    public FootnoteItem(String footnoteId, String footnoteText)
    {
        this.footnoteId = footnoteId;
        this.footnoteText = footnoteText;
    }

    public String getFootnoteId()
    {
        return footnoteId;
    }

    public String getFootnoteText()
    {
        return footnoteText;
    }

    private final String footnoteId;
    private final String footnoteText;
}
