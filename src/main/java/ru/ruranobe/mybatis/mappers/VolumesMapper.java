package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Volume;

import java.util.Date;
import java.util.List;

public interface VolumesMapper
{
    Volume getVolumeByUrl(String url);

    Volume getVolumeById(Integer volumeId);

    Volume getVolumeNextPrevByUrl(String url);

    int getVolumesCountByProjectId(int projectId);

    List<Volume> getVolumesByProjectId(int projectId);

    void updateVolume(Volume volume);

    void updateVolumeCovers(Volume volume);

    void updateChaptersUrl(Volume volume);

    int insertVolume(Volume volumre);

    void deleteVolume(Integer volumeId);

    void resetVolumeTextCache(Integer volumeId);

    Date getProjectUpdateDate(Integer volumeId);

    Date getProjectEditDate(Integer volumeId);
}
