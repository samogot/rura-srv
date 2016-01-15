package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.VolumeReleaseActivity;

import java.util.Collection;

public interface VolumeReleaseActivitiesMapper
{
    Collection<VolumeReleaseActivity> getVolumeReleaseActivitiesByVolumeId(int volumeId);

    Collection<VolumeReleaseActivity> getGroupedVolumeReleaseActivitiesByVolumeId(int volumeId);

    void deleteVolumeReleaseActivitysByVolumeId(int volumeId);

    void insertVolumeReleaseActivitysByVolumeId(@Param("volumeId") int volumeId,
                                                @Param("releaseActivities") Collection<VolumeReleaseActivity> releaseActivities);
}
