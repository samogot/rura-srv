package ru.ruranobe.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.annotation.Nonnull;

import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.base.SimpleEntry;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;

public class DomainCacheLoader implements CacheLoader<String, Integer> {

	@Override
	public Integer load(@Nonnull String domain) throws Exception {
		SimpleEntry<String, Integer> result = null;
		SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
		try (SqlSession session = sessionFactory.openSession()) {
			 result = session.getMapper(ProjectsMapper.class).getDomain(domain);
		}
		return result != null ? result.getValue() : null;
	}
}
