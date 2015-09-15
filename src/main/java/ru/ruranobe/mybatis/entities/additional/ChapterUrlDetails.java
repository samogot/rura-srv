package ru.ruranobe.mybatis.entities.additional;

import java.io.Serializable;

public class ChapterUrlDetails implements Serializable
{
    public ChapterUrlDetails()
    {

    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getVolumeUrl() {
        return volumeUrl;
    }

    public void setVolumeUrl(String volumeUrl) {
        this.volumeUrl = volumeUrl;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getVolumeTitle() {
        return volumeTitle;
    }

    public void setVolumeTitle(String volumeTitle) {
        this.volumeTitle = volumeTitle;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    private String chapterUrl;
    private String volumeUrl;
    private String projectUrl;
    private String chapterTitle;
    private String volumeTitle;
    private String projectTitle;

    private static final long serialVersionUID = 1L;
}
