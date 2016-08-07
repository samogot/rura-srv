package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;
import java.util.Date;

public class ExternalResource implements Serializable
{

    public Integer getHistoryId()
    {
        return historyId;
    }

    public void setHistoryId(Integer historyId)
    {
        this.historyId = historyId;
    }

    public String getThumbnail()
    {
        return thumbnail;
    }

    public String getThumbnail(int width)
    {
        return String.format(thumbnail, Math.min(width, getWidth()));
    }

    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public Integer getWidth()
    {
        return width;
    }

    public void setWidth(Integer width)
    {
        this.width = width;
    }

    public Integer getHeight()
    {
        return height;
    }

    public void setHeight(Integer height)
    {
        this.height = height;
    }

    public ExternalResource getNonColored()
    {
        return nonColored;
    }

    public void setNonColored(ExternalResource nonColored)
    {
        this.nonColored = nonColored;
    }

    private static final long serialVersionUID = 1L;
    private Integer resourceId;
    private Integer userId;
    private Integer historyId;
    private Integer width;
    private Integer height;
    private String mimeType;
    private String url;
    private String thumbnail;
    private String title;
    private Date uploadedWhen;
    private ExternalResource nonColored;


    public ExternalResource()
    {
    }

    public ExternalResource(String url, String thumbnail, Integer width, Integer height)
    {
        this.resourceId = -1;
        this.url = url;
        this.thumbnail = thumbnail;
        this.width = width;
        this.height = height;
    }

    public ExternalResource(Integer resourceId, String url, String thumbnail)
    {
        this.resourceId = resourceId;
        this.url = url;
        this.thumbnail = thumbnail;
    }

    public ExternalResource(Integer userId, String mimeType, String url, String title, Date uploadedWhen)
    {
        this.userId = userId;
        this.mimeType = mimeType;
        this.url = url;
        this.title = title;
        this.uploadedWhen = uploadedWhen;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public Integer getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(Integer resourceId)
    {
        this.resourceId = resourceId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getUploadedWhen()
    {
        return uploadedWhen;
    }

    public void setUploadedWhen(Date uploadedWhen)
    {
        this.uploadedWhen = uploadedWhen;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }
}
