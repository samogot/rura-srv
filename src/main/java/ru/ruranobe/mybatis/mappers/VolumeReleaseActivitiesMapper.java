package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.VolumeReleaseActivity;

import java.util.Collection;

public interface VolumeReleaseActivitiesMapper
{
    Collection<VolumeReleaseActivity> getVolumeReleaseActivitiesByVolumeId(int volumeId);

    void insertVolumeReleaseActivity(VolumeReleaseActivity volumeReleaseActivity);

    void deleteVolumeReleaseActivity(int releaseActivityId);

    void updateVolumeReleaseActivity(VolumeReleaseActivity volumeReleaseActivity);
}
