package ru.ruranobe.mybatis.mappers.cacheable;

import com.google.common.collect.ImmutableMap;
import org.apache.ibatis.session.SqlSession;
import ru.ruranobe.mybatis.mappers.*;

import java.lang.reflect.Constructor;
import java.util.Map;

public class CachingFacade
{
    private static final Map<Class<?>, Class<?>> mapperToMapperCacheable =
            new ImmutableMap.Builder<Class<?>, Class<?>>()
                    .put(ProjectsMapper.class, ProjectsMapperCacheable.class)
                    .put(ExternalResourcesMapper.class, ExternalResourcesCacheableMapper.class)
                    .put(ExternalResourcesHistoryMapper.class, ExternalResourcesHistoryCacheableMapper.class)
                    .put(UpdatesMapper.class, UpdatesMapperCacheable.class)
                    .put(VolumesMapper.class, VolumesMapperCacheable.class)
                    .put(VolumeActivitiesMapper.class, VolumeActivitiesMapperCacheable.class)
                    .put(VolumeReleaseActivitiesMapper.class, VolumeReleaseActivitiesMapperCacheable.class)
                    .put(TeamsMapper.class, TeamsMapperCacheable.class)
                    .put(ChaptersMapper.class, ChaptersMapperCacheable.class)
                    .put(TeamMembersMapper.class, TeamMembersMapperCacheable.class)
                    .put(ChapterImagesMapper.class, ChapterImagesMapperCacheable.class)
                    .put(TextsMapper.class, TextsMapperCacheable.class)
                    .put(TextsHistoryMapper.class, TextsHistoryMapperCacheable.class)
                    .put(BookmarksMapper.class, BookmarksMapperCacheable.class)
                    .put(ChapterUrlDetailsMapper.class, ChapterUrlDetailsMapperCacheable.class)
                    .put(UsersMapper.class, UsersMapperCacheable.class)
                    .put(OrphusCommentsMapper.class, OrphusCommentsMapperCacheable.class)
                    .put(ParagraphsMapper.class, ParagraphsMapperCacheable.class)
                    .put(RolesMapper.class, RolesMapperCacheable.class)
                    .build();

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
}
