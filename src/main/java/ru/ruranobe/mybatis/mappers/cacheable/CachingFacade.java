package ru.ruranobe.mybatis.mappers.cacheable;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import ru.ruranobe.mybatis.mappers.*;

public class CachingFacade 
{
    public static <T> T getCacheableMapper(SqlSession session, Class<T> mapperClass)
    {
        Class<?> mapperCacheableClass = mapperToMapperCacheable.get(mapperClass);   
        if (mapperCacheableClass == null)
        {
            throw new IllegalArgumentException("Unable to receive cacheable version of class " + mapperClass.toString());
        }
        
        T mapper = session.getMapper(mapperClass);
        try
        {
            Constructor<?> constructor = mapperCacheableClass.getConstructor(mapperClass);
            return (T) constructor.newInstance(mapper);
        }
        catch (NoSuchMethodException ex)
        {
            throw new IllegalArgumentException("There is no valid constructor with input parameter of type " + mapperClass.toString() + " in class " + mapperCacheableClass.toString(), ex);
        }
        catch (Exception ex)
        {
            throw new IllegalStateException(ex);
        }
    }
    
    private static final Map<Class<?>, Class<?>> mapperToMapperCacheable =
            new ImmutableMap.Builder<Class<?>, Class<?>>()
            .put(ProjectsMapper.class, ProjectsMapperCacheable.class)
            .put(ExternalResourcesMapper.class, ExternalResourcesCacheableMapper.class)
            .put(UpdatesMapper.class, UpdatesMapperCacheable.class)
            .put(VolumesMapper.class, VolumesMapperCacheable.class)
            .put(VolumeActivitiesMapper.class, VolumeActivitiesMapperCacheable.class)
            .put(VolumeReleaseActivitiesMapper.class, VolumeReleaseActivitiesMapperCacheable.class)
            .put(TeamsMapper.class, TeamsMapperCacheable.class)
            .put(ChaptersMapper.class, ChaptersMapperCacheable.class)
            .build();
}
