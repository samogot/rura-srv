package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

public class Chapter implements Serializable
{

    public Chapter()
    {
        
    }

    public Chapter(Integer volumeId, Integer textId, String url, String title, Integer orderNumber, boolean published, boolean nested)
    {
        this.volumeId = volumeId;
        this.textId = textId;
        this.url = url;
        this.title = title;
        this.orderNumber = orderNumber;
        this.published = published;
        this.nested = nested;
    }

    public Integer getChapterId()
    {
        return chapterId;
    }

    public void setChapterId(Integer chapterId)
    {
        this.chapterId = chapterId;
    }

    public boolean isNested()
    {
        return nested;
    }

    public void setNested(boolean nested)
    {
        this.nested = nested;
    }

    public Integer getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    public boolean isPublished()
    {
        return published;
    }

    public void setPublished(boolean published)
    {
        this.published = published;
    }

    public Integer getTextId()
    {
        return textId;
    }

    public void setTextId(Integer textId)
    {
        this.textId = textId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Integer getVolumeId()
    {
        return volumeId;
    }

    public void setVolumeId(Integer volumeId)
    {
        this.volumeId = volumeId;
    }
    
    private Integer chapterId;
    private Integer volumeId;
    private Integer textId;
    private String url;
    private String title;
    private Integer orderNumber;
    private boolean published;
    private boolean nested;
    private static final long serialVersionUID = 1L;
}
