package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;

public class SectionProject implements Serializable {

	private static final long serialVersionUID = 3L;
	private Integer sectionId;
	private Integer projectId;
	private Mode mode;
	private String url;
	private String alias;
	private Boolean projectHidden;
	private Boolean bannerHidden;
	private Boolean main;
	private Integer order;

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Boolean getProjectHidden() {
		return projectHidden;
	}

	public void setProjectHidden(Boolean projectHidden) {
		this.projectHidden = projectHidden;
	}

	public Boolean getBannerHidden() {
		return bannerHidden;
	}

	public void setBannerHidden(Boolean bannerHidden) {
		this.bannerHidden = bannerHidden;
	}

	public Boolean getMain() {
		return main;
	}

	public void setMain(Boolean main) {
		this.main = main;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
