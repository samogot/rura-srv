package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.VolumeActivity;

import java.util.List;

public interface VolumeActivitiesMapper
{
    public VolumeActivity getVolumeActivityById(int activityId);

    public List<VolumeActivity> getAllVolumeActivities();

    public void insertVolumeActivity(VolumeActivity volumeActivity);

    public void deleteVolumeActivity(int activityId);

    public void updateVolumeActivity(VolumeActivity volumeActivity);
}
