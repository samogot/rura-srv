package ru.ruranobe.mybatis.tables;

public class Series
{
    
    public Series() 
    {
         
    }
     
    public Series(String nameUrl, String title, Integer parentId) 
    {
        this.nameUrl = nameUrl;
        this.title = title;
        this.parentId = parentId;
    }

    public String getNameUrl()
    {
        return nameUrl;
    }

    public void setNameUrl(String nameUrl)
    {
        this.nameUrl = nameUrl;
    }

    public Integer getParentId()
    {
        return parentId;
    }

    public void setParentId(Integer parentId)
    {
        this.parentId = parentId;
    }

    public Integer getSeriesId()
    {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId)
    {
        this.seriesId = seriesId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    
    private Integer seriesId;    
    private String nameUrl;    
    private String title;    
    private Integer parentId;
}
