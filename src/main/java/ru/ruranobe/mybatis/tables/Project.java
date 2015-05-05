package ru.ruranobe.mybatis.tables;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;

public class Project implements Serializable
{

    private static final long serialVersionUID = 2L;
    private Integer projectId;
    private Integer parentId;
    private Integer imageId;
    private String url;
    private String title;
    private Integer orderNumber;
    private boolean bannerHidden;
    private boolean projectHidden;
    private String author;
    private String illustrator;
    private String franchise;
    private String annotation;

    public Project(Integer parentId, Integer imageId, String url, String title, Integer orderNumber, boolean bannerHidden, boolean projectHidden, String annotation)
    {
        this.parentId = parentId;
        this.imageId = imageId;
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

    public static PageParameters makeUrlParameters(String url)
    {
        return new PageParameters().set("project", url);
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

    public Integer getImageId()
    {
        return imageId;
    }

    public void setImageId(Integer imageId)
    {
        this.imageId = imageId;
    }

    public String getFranchise()
    {
        return franchise;
    }

    public void setFranchise(String franchise)
    {
        this.franchise = franchise;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getIllustrator()
    {
        return illustrator;
    }

    public void setIllustrator(String illustrator)
    {
        this.illustrator = illustrator;
    }

    @Override
    public String toString()
    {
        return "Project{" + "projectId=" + projectId + ", parentId=" + parentId + ", imageId=" + imageId + ", url=" + url + ", title=" + title + ", orderNumber=" + orderNumber + ", bannerHidden=" + bannerHidden + ", projectHidden=" + projectHidden + ", annotation=" + annotation + '}';
    }

    public PageParameters getUrlParameters()
    {
        return makeUrlParameters(url);
    }
}
