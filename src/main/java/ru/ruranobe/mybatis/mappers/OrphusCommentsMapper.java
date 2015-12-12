package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.OrphusComment;

import java.util.List;

public interface OrphusCommentsMapper
{
    int insertOrphusComment(OrphusComment orphusComment);

    List<OrphusComment> getLastOrphusCommentsBy(
            @Param("projectId") Integer projectId,
            @Param("volumeId") Integer volumeId,
            @Param("chapterId") Integer chapterId,
            @Param("sortingOrder") String sortingOrder,
            @Param("limitFrom") long limitFrom,
            @Param("limitTo") long limitTo);

    int getOrphusCommentsSize();
}
