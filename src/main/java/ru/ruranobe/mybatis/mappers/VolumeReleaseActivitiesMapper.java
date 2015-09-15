package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.VolumeReleaseActivity;

import java.util.Collection;

public interface VolumeReleaseActivitiesMapper
{
    public Collection<VolumeReleaseActivity> getVolumeReleaseActivitiesByVolumeId(int volumeId);

    public void insertVolumeReleaseActivity(VolumeReleaseActivity volumeReleaseActivity);

    public void deleteVolumeReleaseActivity(int releaseActivityId);

    public void updateVolumeReleaseActivity(VolumeReleaseActivity volumeReleaseActivity);
}
