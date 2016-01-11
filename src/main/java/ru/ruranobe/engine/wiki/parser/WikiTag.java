package ru.ruranobe.engine.wiki.parser;

import java.util.Map;

public class WikiTag
{
    public WikiTag(WikiTagType wikiTagType, int startPosition, String uniqueId, Map<String, String> attributeNameToValue)
    {
        this.wikiTagType = wikiTagType;
        this.startPosition = startPosition;
        this.uniqueId = uniqueId;
        this.attributeNameToValue = attributeNameToValue;
        this.wikiTagLength = wikiTagType.getWikiTagSize();
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
        return wikiTagLength;
    }

    public void setWikiTagLength(int wikiTagLength)
    {
        this.wikiTagLength = wikiTagLength;
    }

    public String getUniqueId()
    {
        return uniqueId;
    }

    public int getListOrderNumber()
    {
        return listOrderNumber;
    }

    public Integer getExternalResourceId() {
        return externalResourceId;
    }

    public void setExternalResourceId(Integer externalResourceId) {
        this.externalResourceId = externalResourceId;
    }

    public Map<String, String> getAttributeNameToValue()
    {
        return attributeNameToValue;
    }

    public void setListOrderNumber(int listOrderNumber)
    {
        this.listOrderNumber = listOrderNumber;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        WikiTag wikiTag = (WikiTag) o;

        return startPosition == wikiTag.startPosition && wikiTagType.equals(wikiTag.wikiTagType);

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
    private final String uniqueId;
    private final Map<String, String> attributeNameToValue;
    private int listOrderNumber;
    private String imageUrl;
    private int wikiTagLength;
    private Integer externalResourceId;

}