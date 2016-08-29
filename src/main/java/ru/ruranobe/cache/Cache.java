package ru.ruranobe.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.ruranobe.cache.keys.SectionProjectUrl;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.SectionProject;

public class Cache {

	public static final LoadingCache<String, Integer> DOMAINS;

	public static final LoadingCache<SectionProjectUrl, SectionProject> SECTION_PROJECTS_BY_URL;

	public static final LoadingCache<Integer, List<SectionProject>> SECTION_PROJECTS;

	public static final LoadingCache<Integer, Project> PROJECTS;

	static {
		DOMAINS = Caffeine.newBuilder().refreshAfterWrite(1, TimeUnit.DAYS).build(new DomainCacheLoader());
		SECTION_PROJECTS_BY_URL =
				Caffeine.newBuilder().refreshAfterWrite(15, TimeUnit.MINUTES).build(new SectionProjectsByUrlLoader());
		SECTION_PROJECTS =
				Caffeine.newBuilder().refreshAfterWrite(30, TimeUnit.MINUTES).writer(new SectionProjectsWriter())
						.build(new SectionProjectsLoader());
		PROJECTS =
				Caffeine.newBuilder().refreshAfterWrite(15, TimeUnit.MINUTES).build(new ProjectsLoader());
	}
}
