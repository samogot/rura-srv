package ru.ruranobe.mybatis.entities.tables;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.wicket.webpages.common.ProjectPage;
import ru.ruranobe.wicket.webpages.common.VolumePage;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

public class Project extends PageRepresentable implements Serializable
{

    public Integer getRequisiteId()
    {
        return requisiteId;
    }

    public void setRequisiteId(Integer requisiteId)
    {
        this.requisiteId = requisiteId;
    }

    public ExternalResource getImage()
    {
        return image;
    }

    public void setImage(ExternalResource image)
    {
        this.image = image;
        this.imageId = image == null ? null : image.getResourceId();
    }

    public String getOriginalDesign()
    {
        return originalDesign;
    }

    public void setOriginalDesign(String originalDesign)
    {
        this.originalDesign = originalDesign;
    }

    public String getOriginalStory()
    {
        return originalStory;
    }

    public void setOriginalStory(String originalStory)
    {
        this.originalStory = originalStory;
    }

    public String getIssueStatus()
    {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus)
    {
        this.issueStatus = issueStatus;
    }

    public String getTranslationStatus()
    {
        return translationStatus;
    }

    public void setTranslationStatus(String translationStatus)
    {
        this.translationStatus = translationStatus;
    }

    public Boolean getWorks()
    {
        return works;
    }

    public void setWorks(Boolean works)
    {
        this.works = works;
    }

    public Date getLastUpdateDate()
    {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate)
    {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Date getLastEditDate()
    {
        return lastEditDate;
    }

    public void setLastEditDate(Date lastEditDate)
    {
        this.lastEditDate = lastEditDate;
    }

    public Requisite getRequisite()
    {
        return requisite;
    }

    public void setRequisite(Requisite requisite)
    {
        this.requisite = requisite;
        if (requisite != null)
        {
            requisiteId = requisite.getRequisiteId();
        }
    }

    private static final long serialVersionUID = 3L;
    private Integer projectId;
    private Integer parentId;
    private Integer imageId;
    private String url;
    private String title;
    private String nameJp;
    private String nameEn;
    private String nameRu;
    private String nameRomaji;
    private String author;
    private String illustrator;
    private String originalDesign;
    private String originalStory;
    private Integer orderNumber;
    private Boolean bannerHidden;
    private Boolean projectHidden;
    private Boolean onevolume;
    private Boolean works;
    private String franchise;
    private String annotation;
    private Integer forumId;
    private String status;
    private String issueStatus;
    private String translationStatus;
    private Integer requisiteId;
    //optional
    private ExternalResource image;
    private Date lastUpdateDate;
    private Date lastEditDate;
    private Requisite requisite;

    public Project(Integer parentId, Integer imageId, String url, String title, Integer orderNumber, boolean bannerHidden, boolean projectHidden, String annotation)
    {
        this.parentId = parentId;
        this.imageId = imageId;
        this.url = url;
        this.title = title;
        this.orderNumber = orderNumber;
        this.bannerHidden = bannerHidden;
        this.projectHidden = projectHidden;
        this.annotation = annotation;
    }

    public Project()
    {
    }

    public static Project subProjectOf(Project project, Integer orderNumber, String projectTitle)
    {
        Project subProject = new Project();
        subProject.setParentId(project.getProjectId());
        subProject.setUrl(null);
        subProject.setOrderNumber(orderNumber);
        subProject.setTitle(projectTitle);
        subProject.setBannerHidden(true);
        subProject.setProjectHidden(false);
        subProject.setAuthor(project.getAuthor());
        subProject.setIllustrator(project.getIllustrator());
        subProject.setAnnotation("");
        subProject.setFranchise("");
        subProject.setImageId(null);
        subProject.setNameEn("");
        subProject.setNameJp("");
        subProject.setNameRomaji("");
        subProject.setNameRu("");
        subProject.setOnevolume(false);
        subProject.setWorks(false);
        return subProject;
    }

    public static PageParameters makeUrlParameters(String url)
    {
        return new PageParameters().set("project", url);
    }

    public PageParameters getUrlParameters()
    {
        return Boolean.TRUE.equals(onevolume) ? makeUrlParameters(url).set("volume", "v1") : makeUrlParameters(url);
    }

    public Class getLinkClass()
    {
        return onevolume ? VolumePage.class : ProjectPage.class;
    }

    public String getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(String annotation)
    {
        this.annotation = annotation;
    }

    public Boolean getBannerHidden()
    {
        return bannerHidden;
    }

    public void setBannerHidden(Boolean bannerHidden)
    {
        this.bannerHidden = bannerHidden;
    }

    public Integer getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    public Integer getParentId()
    {
        return parentId;
    }

    public void setParentId(Integer parentId)
    {
        this.parentId = parentId;
    }

    public Boolean getProjectHidden()
    {
        return projectHidden;
    }

    public void setProjectHidden(Boolean projectHidden)
    {
        this.projectHidden = projectHidden;
    }

    public Integer getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Integer projectId)
    {
        this.projectId = projectId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Integer getImageId()
    {
        return imageId;
    }

    public void setImageId(Integer imageId)
    {
        this.imageId = imageId;
    }

    public String getFranchise()
    {
        return franchise;
    }

    public void setFranchise(String franchise)
    {
        this.franchise = franchise;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getIllustrator()
    {
        return illustrator;
    }

    public void setIllustrator(String illustrator)
    {
        this.illustrator = illustrator;
    }

    public String getNameJp()
    {
        return nameJp;
    }

    public void setNameJp(String nameJp)
    {
        this.nameJp = nameJp;
    }

    public String getNameEn()
    {
        return nameEn;
    }

    public void setNameEn(String nameEn)
    {
        this.nameEn = nameEn;
    }

    public String getNameRu()
    {
        return nameRu;
    }

    public void setNameRu(String nameRu)
    {
        this.nameRu = nameRu;
    }

    public String getNameRomaji()
    {
        return nameRomaji;
    }

    public void setNameRomaji(String nameRomaji)
    {
        this.nameRomaji = nameRomaji;
    }

    public Boolean getOnevolume()
    {
        return onevolume;
    }

    public void setOnevolume(Boolean onevolume)
    {
        this.onevolume = onevolume;
    }

    public Integer getForumId()
    {
        return forumId;
    }

    public void setForumId(Integer forumId)
    {
        this.forumId = forumId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "Project{" + "projectId=" + projectId + ", parentId=" + parentId + ", imageId=" + imageId + ", url=" + url + ", title=" + title + ", orderNumber=" + orderNumber + ", bannerHidden=" + bannerHidden + ", projectHidden=" + projectHidden + ", annotation=" + annotation + '}';
    }

    public String getAnnotationParsed()
    {
        WikiParser wikiParser = new WikiParser(null, null, annotation, false);
        return annotation == null ? null : wikiParser.parseWikiText(Collections.<ExternalResource>emptyList(), false);
    }

    public String getFranchiseParsed()
    {
        WikiParser wikiParser = new WikiParser(null, null, franchise, false);
        return franchise == null ? null : wikiParser.parseWikiText(Collections.<ExternalResource>emptyList(), false);
    }

    /*@Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Project project = (Project) o;

        return projectId.equals(project.projectId);

    }

    @Override
    public int hashCode()
    {
        return projectId.hashCode();
    }*/

}
