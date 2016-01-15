package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.tables.VolumeReleaseActivity;
import ru.ruranobe.mybatis.mappers.VolumeReleaseActivitiesMapper;

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
    public void deleteVolumeReleaseActivitysByVolumeId(int volumeId)
    {
        mapper.deleteVolumeReleaseActivitysByVolumeId(volumeId);
    }

    @Override
    public void insertVolumeReleaseActivitysByVolumeId(int volumeId, Collection<VolumeReleaseActivity> releaseActivities)
    {
        mapper.insertVolumeReleaseActivitysByVolumeId(volumeId, releaseActivities);
    }
}
