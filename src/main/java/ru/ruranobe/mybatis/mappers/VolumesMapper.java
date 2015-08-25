package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.ProjectInfo;
import ru.ruranobe.mybatis.tables.Volume;

import java.util.List;

public interface VolumesMapper
{
    public Volume getVolumeByUrl(String url);

    public Volume getVolumeNextPrevByUrl(String url);

    public ProjectInfo getInfoByProjectId(int projectId);

    public List<Volume> getVolumesByProjectId(int projectId);

    public void updateVolume(Volume volume);

    public int insertVolume(Volume volumre);

    public void deleteVolume(Integer volumeId);
}
