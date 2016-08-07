package ru.ruranobe.engine.wiki.parser;

import ru.ruranobe.mybatis.entities.tables.ExternalResource;

import java.util.Map;

public class WikiTag
{

    public ExternalResource getExternalResource()
    {
        return externalResource;
    }

    public void setExternalResource(ExternalResource externalResource)
    {
        this.externalResource = externalResource;
    }

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

    public Map<String, String> getAttributeNameToValue()
    {
        return attributeNameToValue;
    }

    public void setListOrderNumber(int listOrderNumber)
    {
        this.listOrderNumber = listOrderNumber;
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
    private int wikiTagLength;
    private ExternalResource externalResource;

}