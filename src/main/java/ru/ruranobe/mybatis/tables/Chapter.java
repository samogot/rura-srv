package ru.ruranobe.mybatis.tables;

public class Chapter 
{

    public Chapter()
    {
        
    }
    
    public Chapter(Integer releaseId, Integer order, Integer level, String url, 
            String title, String titleShort) 
    {
        this.releaseId = releaseId;
        this.order = order;
        this.level = level;
        this.url = url;
        this.title = title;
        this.titleShort = titleShort;
    }

    public Integer getLevel()
    {
        return level;
    }

    public void setLevel(Integer level)
    {
        this.level = level;
    }

    public Integer getOrder()
    {
        return order;
    }

    public void setOrder(Integer order)
    {
        this.order = order;
    }

    public Integer getReleaseId()
    {
        return releaseId;
    }

    public void setReleaseId(Integer releaseId)
    {
        this.releaseId = releaseId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitleShort()
    {
        return titleShort;
    }

    public void setTitleShort(String titleShort)
    {
        this.titleShort = titleShort;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Integer getChapterId()
    {
        return chapterId;
    }

    public void setChapterId(Integer chapterId)
    {
        this.chapterId = chapterId;
    }
    
    private Integer chapterId;
    private Integer releaseId;
    private Integer order;
    private Integer level;
    private String url;
    private String title;
    private String titleShort;
}
