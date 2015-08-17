package ru.ruranobe.engine.wiki.parser;

public class WikiTag
{
    public WikiTag(WikiTagType wikiTagType, int startPosition, long uniqueId)
    {
        this.wikiTagType = wikiTagType;
        this.startPosition = startPosition;
        this.uniqueId = uniqueId;
    }

    public WikiTagType getWikiTagType()
    {
        return wikiTagType;
    }

    public int getStartPosition()
    {
        return startPosition;
    }

    public int getWikiTagLength()
    {
        return wikiTagType.getWikiTagSize();
    }

    public long getUniqueId()
    {
        return uniqueId;
    }

    public int getListOrderNumber()
    {
        return listOrderNumber;
    }

    public void setListOrderNumber(int listOrderNumber) {
        this.listOrderNumber = listOrderNumber;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WikiTag wikiTag = (WikiTag) o;

        if (startPosition != wikiTag.startPosition) return false;
        return wikiTagType.equals(wikiTag.wikiTagType);

    }

    @Override
    public int hashCode()
    {
        int result = wikiTagType.hashCode();
        result = 31 * result + startPosition;
        return result;
    }

    private final WikiTagType wikiTagType;
    private final int startPosition;
    private final long uniqueId;
    private int listOrderNumber;
    private String imageUrl;
}