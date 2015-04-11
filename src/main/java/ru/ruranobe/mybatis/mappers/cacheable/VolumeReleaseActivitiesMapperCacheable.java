package ru.ruranobe.mybatis.mappers.cacheable;

import java.util.Collection;
import ru.ruranobe.mybatis.mappers.VolumeReleaseActivitiesMapper;
import ru.ruranobe.mybatis.tables.VolumeReleaseActivity;

public class VolumeReleaseActivitiesMapperCacheable implements VolumeReleaseActivitiesMapper
{
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
    
    private VolumeReleaseActivitiesMapper mapper;
}
