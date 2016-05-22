package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.VolumesMapper;

import java.util.Date;
import java.util.List;

public class VolumesMapperCacheable implements VolumesMapper
{
    private final VolumesMapper mapper;

    public VolumesMapperCacheable(VolumesMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public Volume getVolumeByUrl(String url)
    {
        return mapper.getVolumeByUrl(url);
    }

    @Override
    public int getVolumesCountByProjectId(int projectId)
    {
        return mapper.getVolumesCountByProjectId(projectId);
    }

    @Override
    public List<Volume> getVolumesByProjectId(int projectId)
    {
        return mapper.getVolumesByProjectId(projectId);
    }

    @Override
    public Volume getVolumeNextPrevByUrl(String url)
    {
        return mapper.getVolumeNextPrevByUrl(url);
    }

    @Override
    public void updateVolume(Volume volume)
    {
        mapper.updateVolume(volume);
    }

    @Override
    public void updateVolumeCovers(Volume volume)
    {
        mapper.updateVolumeCovers(volume);
    }

    @Override
    public int insertVolume(Volume volume)
    {
        return mapper.insertVolume(volume);
    }

    @Override
    public void deleteVolume(Integer volumeId)
    {
        mapper.deleteVolume(volumeId);
    }

    @Override
    public void resetVolumeTextCache(Integer volumeId)
    {
        mapper.resetVolumeTextCache(volumeId);
    }

    @Override
    public Date getProjectUpdateDate(Integer volumeId)
    {
        return mapper.getProjectUpdateDate(volumeId);
    }

    @Override
    public Date getProjectEditDate(Integer volumeId)
    {
        return mapper.getProjectEditDate(volumeId);
    }
}
