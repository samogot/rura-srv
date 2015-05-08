package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

public class VolumeActivity implements Serializable
{

    private static final long serialVersionUID = 2L;
    private Integer activityId;
    private String activityName;
    private String activityType;

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

    public String getActivityType()
    {
        return activityType;
    }

    public void setActivityType(String activityType)
    {
        this.activityType = activityType;
    }
}
