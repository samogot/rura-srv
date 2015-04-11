package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

public class VolumeActivity implements Serializable
{

    public VolumeActivity()
    {
    }

    public VolumeActivity(String activityName)
    {
        this.activityName = activityName;
    }

    public Integer getActivityId()
    {
        return activityId;
    }

    public void setActivityId(Integer activityId)
    {
        this.activityId = activityId;
    }

    public String getActivityName()
    {
        return activityName;
    }

    public void setActivityName(String activityName)
    {
        this.activityName = activityName;
    }
    
    private Integer activityId;
    private String activityName;
    private static final long serialVersionUID = 1L;
}
