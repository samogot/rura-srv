package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.VolumeActivitiesMapper;
import ru.ruranobe.mybatis.tables.VolumeActivity;

import java.util.List;

public class VolumeActivitiesMapperCacheable implements VolumeActivitiesMapper
{
    private VolumeActivitiesMapper mapper;

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
    public List<VolumeActivity> getAllVolumeActivities()
    {
        return mapper.getAllVolumeActivities();
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
}
