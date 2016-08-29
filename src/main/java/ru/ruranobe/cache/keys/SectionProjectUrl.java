package ru.ruranobe.cache.keys;

import java.io.Serializable;

import javax.annotation.Nonnull;

public class SectionProjectUrl implements Serializable {

	private static final long serialVersionUID = 3L;

	@Nonnull
	private Integer sectionId;
	@Nonnull
	private String url;

	public SectionProjectUrl() {
	}

	public SectionProjectUrl(@Nonnull Integer sectionId, @Nonnull String url) {
		this.sectionId = sectionId;
		this.url = url;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
