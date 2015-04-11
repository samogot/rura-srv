package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.VolumeActivity;

public interface VolumeActivitiesMapper
{
    public VolumeActivity getVolumeActivityById(int activityId);
    public void insertVolumeActivity(VolumeActivity volumeActivity);
    public void deleteVolumeActivity(int activityId);
    public void updateVolumeActivity(VolumeActivity volumeActivity);
}
