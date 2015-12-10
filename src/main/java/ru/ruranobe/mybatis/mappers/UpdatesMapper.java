package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.Update;

import java.util.List;

public interface UpdatesMapper
{
    List<Update> getLastUpdatesBy(@Param("projectId") Integer projectId,
                                  @Param("volumeId") Integer volumeId,
                                  @Param("updateType") String updateType,
                                  @Param("limitFrom") Integer limitFrom,
                                  @Param("limitTo") Integer limitTo);

    int getUpdatesCountBy(@Param("projectId") Integer projectId,
                          @Param("volumeId") Integer volumeId,
                          @Param("updateType") String updateType);

    List<Update> getUpdatesByVolumeId(@Param("volumeId") Integer volumeId);

    void updateUpdate(Update update);

    void deleteUpdate(int update);

    void insertUpdate(Update update);
}
