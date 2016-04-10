package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;
import java.util.Date;

public class ExternalResource implements Serializable
{

    public int getHistoryId()
    {
        return historyId;
    }

    public void setHistoryId(int historyId)
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

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    private static final long serialVersionUID = 1L;
    private int resourceId;
    private int userId;
    private int historyId;
    private int width;
    private int height;
    private String mimeType;
    private String url;
    private String thumbnail;
    private String title;
    private Date uploadedWhen;

    public ExternalResource()
    {
    }

    public ExternalResource(String url, String thumbnail, int width, int height)
    {
        this.resourceId = -1;
        this.url = url;
        this.thumbnail = thumbnail;
        this.width = width;
        this.height = height;
    }

    public ExternalResource(int resourceId, String url, String thumbnail)
    {
        this.resourceId = resourceId;
        this.url = url;
        this.thumbnail = thumbnail;
    }

    public ExternalResource(int userId, String mimeType, String url, String title, Date uploadedWhen)
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

    public int getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(int resourceId)
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

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }
}
