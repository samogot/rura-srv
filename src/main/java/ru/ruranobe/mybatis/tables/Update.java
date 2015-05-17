package ru.ruranobe.mybatis.tables;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.wicket.webpages.VolumePage;
import ru.ruranobe.wicket.webpages.VolumeTextPage;

import java.io.Serializable;
import java.util.Date;

public class Update implements Serializable, Comparable<Update>, PageRepresentable
{

    private static final long serialVersionUID = 2L;
    private static final String SHORT_TITLE_REGEX = "[-\\.,—–:].*$";
    private Integer updateId;
    private Integer projectId;
    private Integer volumeId;
    private Integer chapterId;
    private String updateType;
    private Date showTime;
    private String description;
    /* Optional. Doesn't exist in table, used only in mybatis selects and corresponding code. */
    private String volumeTitle;
    private String volumeTitleShort;
    private String chapterTitle;
    private String volumeUrl;
    private String chapterUrl;

    public Update()
    {
    }

    public Update(Integer volumeId, Integer chapterId, Date showTime, String description)
    {
        this.volumeId = volumeId;
        this.chapterId = chapterId;
        this.showTime = showTime;
        this.description = description;
    }

    public PageParameters getUrlParameters()
    {
        return chapterUrl != null ? Chapter.makeUrlParameters(chapterUrl.split("/")) : Volume.makeUrlParameters(volumeUrl.split("/"));
    }

    public Class getLinkClass()
    {
        return chapterId != null ? VolumeTextPage.class : VolumePage.class;
    }

    public Integer getChapterId()
    {
        return chapterId;
    }

    public void setChapterId(Integer chapterId)
    {
        this.chapterId = chapterId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getShowTime()
    {
        return showTime;
    }

    public void setShowTime(Date showTime)
    {
        this.showTime = showTime;
    }

    public Integer getUpdateId()
    {
        return updateId;
    }

    public void setUpdateId(Integer updateId)
    {
        this.updateId = updateId;
    }

    public Integer getVolumeId()
    {
        return volumeId;
    }

    public void setVolumeId(Integer volumeId)
    {
        this.volumeId = volumeId;
    }

    public String getUpdateType()
    {
        return updateType;
    }

    public void setUpdateType(String updateType)
    {
        this.updateType = updateType;
    }

    public String getChapterTitle()
    {
        return chapterTitle;
    }

    public String getVolumeTitle()
    {
        return volumeTitle;
    }

    public String getChapterUrl()
    {
        return chapterUrl;
    }

    public String getVolumeUrl()
    {
        return volumeUrl;
    }

    public String getVolumeTitleShort()
    {
        return volumeTitleShort;
    }

    @Override
    public int compareTo(Update update)
    {
        return showTime.compareTo(update.showTime);
    }


    public String getShortTitle()
    {
        String shortTitle;
        if (volumeTitleShort != null)
        {
            shortTitle = volumeTitleShort;
        }
        else
        {
            shortTitle = volumeTitle.replaceFirst(SHORT_TITLE_REGEX, "");
        }
        if (chapterTitle != null)
        {
            shortTitle += " - " + getChapterShortTitle();
        }
        return shortTitle;
    }

    public String getChapterShortTitle()
    {
        //todo supchapter
        return chapterTitle != null ? chapterTitle.replaceFirst(SHORT_TITLE_REGEX, "") : null;
    }

    public Integer getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Integer projectId)
    {
        this.projectId = projectId;
    }
}
