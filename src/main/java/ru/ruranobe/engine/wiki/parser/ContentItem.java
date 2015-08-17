package ru.ruranobe.engine.wiki.parser;

public class ContentItem
{
    public ContentItem(String tagName, long tagId, String title)
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

    public long getTagId() {
        return tagId;
    }

    private final String tagName;
    private final long tagId;
    private final String title;
}
