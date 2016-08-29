package ru.ruranobe.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.SectionProject;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;

public class SectionProjectsLoader implements CacheLoader<Integer, List<SectionProject>> {

	@Override
	public List<SectionProject> load(@Nonnull Integer sectionId) throws Exception {
		List<SectionProject> result;
		SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
		try (SqlSession session = sessionFactory.openSession()) {
			result = session.getMapper(ProjectsMapper.class).getAllSectionProjects(sectionId);
		}
		return result != null ? result : Collections.emptyList();
	}
}
