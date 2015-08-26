package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

public class VolumeReleaseActivity implements Serializable
{

    private static final long serialVersionUID = 2L;
    private Integer releaseActivityId;
    private Integer volumeId;
    private Integer activityId;
    private Integer memberId;
    private boolean teamHidden;
    /* Optional. Doesn't exist in table, used only in mybatis selects and corresponding code. */
    private String activityName;
    private String memberName;

    public VolumeReleaseActivity()
    {
    }

    public VolumeReleaseActivity(Integer volumeId, Integer activityId)
    {
        this.volumeId = volumeId;
        this.activityId = activityId;
    }

    public Integer getActivityId()
    {
        return activityId;
    }

    public void setActivityId(Integer activityId)
    {
        this.activityId = activityId;
    }

    public Integer getVolumeId()
    {
        return volumeId;
    }

    public void setVolumeId(Integer volumeId)
    {
        this.volumeId = volumeId;
    }

    public Integer getReleaseActivityId()
    {
        return releaseActivityId;
    }

    public void setReleaseActivityId(Integer releaseActivityId)
    {
        this.releaseActivityId = releaseActivityId;
    }

    public Integer getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Integer memberId)
    {
        this.memberId = memberId;
    }

    public boolean isTeamHidden()
    {
        return teamHidden;
    }

    public void setTeamHidden(boolean teamHidden)
    {
        this.teamHidden = teamHidden;
    }

    public String getActivityName()
    {
        return activityName;
    }

    public String getMemberName()
    {
        return memberName;
    }

    public void setActivityName(String activityName)
    {
        this.activityName = activityName;
    }

    public void setMemberName(String memberName)
    {
        this.memberName = memberName;
    }
}
