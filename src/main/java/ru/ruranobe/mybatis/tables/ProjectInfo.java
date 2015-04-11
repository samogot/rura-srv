package ru.ruranobe.mybatis.tables;

public class ProjectInfo 
{
    public ProjectInfo()
    {
    }

    public ProjectInfo(String author, Integer volumesCount, String volumeStatus, String illustrator)
    {
        this.volumeStatus = volumeStatus;
        this.author = author;
        this.volumesCount = volumesCount;
        this.illustrator = illustrator;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public Integer getVolumesCount()
    {
        return volumesCount;
    }

    public void setVolumesCount(Integer volumesCount)
    {
        this.volumesCount = volumesCount;
    }

    public String getVolumeStatus()
    {
        return volumeStatus;
    }

    public void setVolumeStatus(String volumeStatus)
    {
        this.volumeStatus = volumeStatus;
    }

    public String getIllustrator()
    {
        return illustrator;
    }

    public void setIllustrator(String illustrator)
    {
        this.illustrator = illustrator;
    }
    
    private String illustrator;
    private String author;
    private Integer volumesCount;
    private String volumeStatus;
}
