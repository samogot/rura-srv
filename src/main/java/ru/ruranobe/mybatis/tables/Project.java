package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

public class Project implements Serializable
{

    public Project(Integer parentId, String url, String title, Integer orderNumber, boolean bannerHidden, boolean projectHidden, String annotation)
    {
        this.parentId = parentId;
        this.url = url;
        this.title = title;
        this.orderNumber = orderNumber;
        this.bannerHidden = bannerHidden;
        this.projectHidden = projectHidden;
        this.annotation = annotation;
    }

    public Project()
    {
    }

    public String getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(String annotation)
    {
        this.annotation = annotation;
    }

    public boolean isBannerHidden()
    {
        return bannerHidden;
    }

    public void setBannerHidden(boolean bannerHidden)
    {
        this.bannerHidden = bannerHidden;
    }

    public Integer getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    public Integer getParentId()
    {
        return parentId;
    }

    public void setParentId(Integer parentId)
    {
        this.parentId = parentId;
    }

    public boolean isProjectHidden()
    {
        return projectHidden;
    }

    public void setProjectHidden(boolean projectHidden)
    {
        this.projectHidden = projectHidden;
    }

    public Integer getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Integer projectId)
    {
        this.projectId = projectId;
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
    
    private Integer projectId;
    private Integer parentId;
    private String url;
    private String title;
    private Integer orderNumber;
    private boolean bannerHidden;
    private boolean projectHidden;
    private String annotation;
    private static final long serialVersionUID = 1L;
}
