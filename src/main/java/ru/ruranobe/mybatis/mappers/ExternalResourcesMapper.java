package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.ExternalResource;

public interface ExternalResourcesMapper 
{
    public ExternalResource getExternalResourceById(Integer externalResourceId);
    public int insertExternalResource(ExternalResource externalResource);
}
