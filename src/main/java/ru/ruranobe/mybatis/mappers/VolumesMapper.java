package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.ProjectInfo;
import ru.ruranobe.mybatis.tables.Volume;

import java.util.Collection;

public interface VolumesMapper
{
    public Volume getVolumeByUrl(String url);

    public Volume getVolumeNextPrevByUrl(String url);

    public ProjectInfo getInfoByProjectId(int projectId);

    public Collection<Volume> getVolumesByProjectId(int projectId);
}
