package ru.ruranobe.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.annotation.Nonnull;

import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;

public class ProjectsLoader implements CacheLoader<Integer, Project> {

	@Override
	public Project load(@Nonnull Integer projectId) throws Exception {
		Project result;
		SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
		try (SqlSession session = sessionFactory.openSession()) {
			result = session.getMapper(ProjectsMapper.class).getProjectById(projectId);
		}
		return result;
	}
}
