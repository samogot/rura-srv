package ru.ruranobe.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.annotation.Nonnull;

import ru.ruranobe.cache.keys.SectionProjectUrl;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.SectionProject;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;

public class SectionProjectsByUrlLoader implements CacheLoader<SectionProjectUrl, SectionProject> {

	@Override
	public SectionProject load(@Nonnull SectionProjectUrl sectionUrl) throws Exception {
		SectionProject result;
		SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
		try (SqlSession session = sessionFactory.openSession()) {
			result =
					session.getMapper(ProjectsMapper.class)
							.getSectionProjectByUrl(sectionUrl.getSectionId(), sectionUrl.getUrl());
		}
		return result;
	}
}
