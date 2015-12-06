package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;
import java.util.Date;

public class ExternalResourceHistory implements Serializable
{

    public Integer getHistoryId()
    {
        return historyId;
    }

    public void setHistoryId(Integer historyId)
    {
        this.historyId = historyId;
    }

    public String getColoredType()
    {
        return coloredType;
    }

    public void setColoredType(String coloredType)
    {
        this.coloredType = coloredType;
    }

    public Integer getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Integer projectId)
    {
        this.projectId = projectId;
    }

    public Integer getVolumeId()
    {
        return volumeId;
    }

    public void setVolumeId(Integer volumeId)
    {
        this.volumeId = volumeId;
    }

    public Integer getChapterImageId()
    {
        return chapterImage == null ? null : chapterImage.getChapterImageId();
    }

    public ChapterImage getChapterImage()
    {
        return chapterImage;
    }

    public void setChapterImage(ChapterImage chapterImage)
    {
        this.chapterImage = chapterImage;
    }

    public Date getUploadedWhen()
    {
        return uploadedWhen;
    }

    public void setUploadedWhen(Date uploadedWhen)
    {
        this.uploadedWhen = uploadedWhen;
    }

    public ExternalResourceHistory()
    {
    }

    static final public String COLORED_TYPE_MAIN = "main";
    static final public String COLORED_TYPE_COLOR = "color";
    private static final long serialVersionUID = 1L;
    private Integer historyId;
    private String coloredType = COLORED_TYPE_MAIN;
    private Integer projectId;
    private Integer volumeId;
    private ChapterImage chapterImage;
    private Date uploadedWhen = new Date();
}
