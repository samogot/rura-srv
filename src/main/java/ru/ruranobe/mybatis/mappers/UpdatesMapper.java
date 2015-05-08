package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.tables.Update;

import java.util.List;

public interface UpdatesMapper
{
    public List<Update> getLastUpdatesBy(@Param("projectId") Integer projectId,
                                         @Param("volumeId") Integer volumeId,
                                         @Param("updateType") String updateType,
                                         @Param("limitFrom") Integer limitFrom,
                                         @Param("limitTo") Integer limitTo);

    public int getUpdatesCountBy(@Param("projectId") Integer projectId,
                                 @Param("volumeId") Integer volumeId,
                                 @Param("updateType") String updateType);
}
