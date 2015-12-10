package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.VolumeActivity;

import java.util.List;

public interface VolumeActivitiesMapper
{
    VolumeActivity getVolumeActivityById(int activityId);

    List<VolumeActivity> getAllVolumeActivities();

    void insertVolumeActivity(VolumeActivity volumeActivity);

    void deleteVolumeActivity(int activityId);

    void updateVolumeActivity(VolumeActivity volumeActivity);
}
