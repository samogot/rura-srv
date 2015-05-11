package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.Text;

public interface TextsMapper
{
    Text getTextById(Integer textId);
}
