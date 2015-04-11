package ru.ruranobe.mybatis.mappers;

import java.util.Collection;
import ru.ruranobe.mybatis.tables.VolumeReleaseActivity;

public interface VolumeReleaseActivitiesMapper
{
    public Collection<VolumeReleaseActivity> getVolumeReleaseActivitiesByVolumeId(int volumeId);
    public void insertVolumeReleaseActivity(VolumeReleaseActivity volumeReleaseActivity);
    public void deleteVolumeReleaseActivity(int releaseActivityId);
    public void updateVolumeReleaseActivity(VolumeReleaseActivity volumeReleaseActivity);
}
