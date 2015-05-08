package ru.ruranobe.mybatis.mappers.cacheable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.tables.ExternalResource;

public class ExternalResourcesCacheableMapper implements ExternalResourcesMapper
{

    private static final Cache cache = CacheManager.newInstance().getCache("ExternalResourcesInMemoryCache");
    private final ExternalResourcesMapper mapper;

    public ExternalResourcesCacheableMapper(ExternalResourcesMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public ExternalResource getExternalResourceById(Integer externalResourceId)
    {
        if (cache.isKeyInCache(externalResourceId))
        {
            return (ExternalResource) cache.get(externalResourceId).getObjectValue();
        } else
        {
            ExternalResource externalResource = mapper.getExternalResourceById(externalResourceId);
            cache.put(new Element(externalResourceId, externalResource));
            return externalResource;
        }
    }

    @Override
    public int insertExternalResource(ExternalResource externalResource)
    {
        int resourceId = mapper.insertExternalResource(externalResource);
        cache.put(new Element(resourceId, externalResource));
        return resourceId;
    }
}
