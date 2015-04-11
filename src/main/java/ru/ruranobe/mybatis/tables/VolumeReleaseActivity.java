package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

public class VolumeReleaseActivity implements Serializable
{

    public VolumeReleaseActivity()
    {
    }

    public VolumeReleaseActivity(Integer volumeId, Integer teamId, Integer activityId, String assigneeTeamMember)
    {
        this.volumeId = volumeId;
        this.teamId = teamId;
        this.activityId = activityId;
        this.assigneeTeamMember = assigneeTeamMember;
    }

    public Integer getActivityId()
    {
        return activityId;
    }

    public void setActivityId(Integer activityId)
    {
        this.activityId = activityId;
    }

    public String getAssigneeTeamMember()
    {
        return assigneeTeamMember;
    }

    public void setAssigneeTeamMember(String assigneeTeamMember)
    {
        this.assigneeTeamMember = assigneeTeamMember;
    }

    public Integer getTeamId()
    {
        return teamId;
    }

    public void setTeamId(Integer teamId)
    {
        this.teamId = teamId;
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
    
    private Integer releaseActivityId;
    private Integer volumeId;
    private Integer teamId;
    private Integer activityId;
    private String assigneeTeamMember;
    private static final long serialVersionUID = 1L;
}
