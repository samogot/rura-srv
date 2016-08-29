package ru.ruranobe.cache;

import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.RemovalCause;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.ruranobe.cache.keys.SectionProjectUrl;
import ru.ruranobe.mybatis.entities.tables.SectionProject;

public class SectionProjectsWriter implements CacheWriter<Integer, List<SectionProject>> {

	@Override
	public void write(@Nonnull Integer sectionId, @Nonnull List<SectionProject> sectionProjects) {
		sectionProjects.forEach(
				sectionProject -> Cache.SECTION_PROJECTS_BY_URL.put(
						new SectionProjectUrl(sectionId, sectionProject.getUrl()), sectionProject));
	}

	@Override
	public void delete(@Nonnull Integer integer, @Nullable List<SectionProject> sectionProjects,
	                   @Nonnull RemovalCause removalCause) {

	}
}
