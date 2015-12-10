package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.ExternalResource;

public interface ExternalResourcesMapper
{
    ExternalResource getExternalResourceById(Integer externalResourceId);

    int insertExternalResource(ExternalResource externalResource);
}
