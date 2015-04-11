package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.VolumeActivitiesMapper;
import ru.ruranobe.mybatis.tables.VolumeActivity;

public class VolumeActivitiesMapperCacheable implements VolumeActivitiesMapper
{
    public VolumeActivitiesMapperCacheable(VolumeActivitiesMapper mapper)
    {
        this.mapper = mapper;
    }
    
    @Override
    public VolumeActivity getVolumeActivityById(int activityId)
    {
        return mapper.getVolumeActivityById(activityId);
    }

    @Override
    public void insertVolumeActivity(VolumeActivity volumeActivity)
    {
        mapper.insertVolumeActivity(volumeActivity);
    }

    @Override
    public void deleteVolumeActivity(int activityId)
    {
        mapper.deleteVolumeActivity(activityId);
    }

    @Override
    public void updateVolumeActivity(VolumeActivity volumeActivity)
    {
        mapper.updateVolumeActivity(volumeActivity);
    }
    
    private VolumeActivitiesMapper mapper;
}
