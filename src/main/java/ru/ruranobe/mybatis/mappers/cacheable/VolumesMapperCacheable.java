package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.tables.ProjectInfo;
import ru.ruranobe.mybatis.tables.Volume;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class VolumesMapperCacheable implements VolumesMapper
{
    private static final ConcurrentHashMap<Integer, ProjectInfo> projectIdToProjectInfo =
            new ConcurrentHashMap<Integer, ProjectInfo>();
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
    public ProjectInfo getInfoByProjectId(int projectId)
    {
        if (projectIdToProjectInfo.containsKey(projectId))
        {
            return projectIdToProjectInfo.get(projectId);
        }

        ProjectInfo projectInfo = mapper.getInfoByProjectId(projectId);
        if (projectInfo != null)
        {
            projectIdToProjectInfo.put(projectId, projectInfo);
        }
        return projectInfo;
    }

    @Override
    public Collection<Volume> getVolumesByProjectId(int projectId)
    {
        return mapper.getVolumesByProjectId(projectId);
    }

    @Override
    public Volume getVolumeNextPrevByUrl(String url)
    {
        return mapper.getVolumeNextPrevByUrl(url);
    }
}