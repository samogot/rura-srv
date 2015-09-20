package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.OrphusComment;

import java.util.List;

public interface OrphusCommentsMapper
{
    public int insertOrphusComment(OrphusComment orphusComment);
    public List<OrphusComment> getLastOrphusCommentsBy(
            @Param("sortingOrder") String sortingOrder,
            @Param("limitFrom") long limitFrom,
            @Param("limitTo") long limitTo);
    public int getOrphusCommentsSize();
}
