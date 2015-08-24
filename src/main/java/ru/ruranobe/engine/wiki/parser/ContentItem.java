package ru.ruranobe.engine.wiki.parser;

public class ContentItem
{
    public ContentItem(String tagName, String tagId, String title)
    {
        this.tagName = tagName;
        this.tagId = tagId;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagId() {
        return tagId;
    }

    private final String tagName;
    private final String tagId;
    private final String title;
}
