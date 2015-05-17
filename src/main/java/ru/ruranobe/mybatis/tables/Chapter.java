package ru.ruranobe.mybatis.tables;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.wicket.webpages.VolumeTextPage;

import java.io.Serializable;

public class Chapter implements Serializable, PageRepresentable
{

    private static final long serialVersionUID = 1L;
    private Integer chapterId;
    private Integer volumeId;
    private Integer textId;
    private String url;
    private String title;
    private Integer orderNumber;
    private boolean published;
    private boolean nested;

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

    public static PageParameters makeUrlParameters(String[] urlParts)
    {
        return new PageParameters().set("project", urlParts[0]).set("volume", urlParts[1]).set("chapter", urlParts[2]);
    }

    public Class getLinkClass()
    {
        return VolumeTextPage.class;
    }

    public PageParameters getUrlParameters()
    {
        if (url == null)
        {
            return null;
        }
        return makeUrlParameters(url.split("/"));
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
}
