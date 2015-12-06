package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;

public class ChapterImage implements Serializable
{

    private static final long serialVersionUID = 2L;
    private Integer chapterImageId;
    private Integer chapterId;
    private Integer volumeId;
    private ExternalResource coloredImage;
    private ExternalResource nonColoredImage;
    private Integer orderNumber;
    private boolean adult = false;

    public ChapterImage()
    {
    }

    // Step away from common approach. Colored Image and Non Colored Image in this class
    // presented as a reference to an entity. When we load ChapterImage we always load
    // images also. That's why I choose reference over id's. (Yet we don't have to alwasy
    // load chapter entity or volume entity...)
    public ChapterImage(Integer chapterImageId, Integer chapterId, Integer volumeId, ExternalResource coloredImage,
                        ExternalResource nonColoredImage, Integer orderNumber)
    {
        this.chapterImageId = chapterImageId;
        this.chapterId = chapterId;
        this.volumeId = volumeId;
        this.coloredImage = coloredImage;
        this.nonColoredImage = nonColoredImage;
        this.orderNumber = orderNumber;
    }

    public Integer getChapterId()
    {
        return chapterId;
    }

    public void setChapterId(Integer chapterId)
    {
        this.chapterId = chapterId;
    }

    public Integer getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    public Integer getVolumeId()
    {
        return volumeId;
    }

    public void setVolumeId(Integer volumeId)
    {
        this.volumeId = volumeId;
    }

    public ExternalResource getColoredImage()
    {
        return coloredImage;
    }

    public void setColoredImage(ExternalResource coloredImage)
    {
        this.coloredImage = coloredImage;
    }

    public ExternalResource getNonColoredImage()
    {
        return nonColoredImage;
    }

    public void setNonColoredImage(ExternalResource nonColoredImage)
    {
        this.nonColoredImage = nonColoredImage;
    }

    public boolean getAdult()
    {
        return adult;
    }

    public void setAdult(boolean adult)
    {
        this.adult = adult;
    }

    public Integer getChapterImageId()
    {
        return chapterImageId;
    }

    public void setChapterImageId(Integer chapterImageId)
    {
        this.chapterImageId = chapterImageId;
    }
}
