package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.VolumeReleaseActivitiesMapper;
import ru.ruranobe.mybatis.entities.tables.VolumeReleaseActivity;

import java.util.Collection;

public class VolumeReleaseActivitiesMapperCacheable implements VolumeReleaseActivitiesMapper
{
    private VolumeReleaseActivitiesMapper mapper;

    public VolumeReleaseActivitiesMapperCacheable(VolumeReleaseActivitiesMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public Collection<VolumeReleaseActivity> getVolumeReleaseActivitiesByVolumeId(int volumeId)
    {
        return mapper.getVolumeReleaseActivitiesByVolumeId(volumeId);
    }

    @Override
    public void insertVolumeReleaseActivity(VolumeReleaseActivity volumeReleaseActivity)
    {
        mapper.insertVolumeReleaseActivity(volumeReleaseActivity);
    }

    @Override
    public void deleteVolumeReleaseActivity(int releaseActivityId)
    {
        mapper.deleteVolumeReleaseActivity(releaseActivityId);
    }

    @Override
    public void updateVolumeReleaseActivity(VolumeReleaseActivity volumeReleaseActivity)
    {
        mapper.updateVolumeReleaseActivity(volumeReleaseActivity);
    }
}
