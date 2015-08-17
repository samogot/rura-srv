package ru.ruranobe.mybatis.tables;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.webpages.VolumePage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Volume implements Serializable, PageRepresentable
{

    private static final long serialVersionUID = 2L;
    private Integer volumeId;
    private Integer projectId;
    private Integer imageOne;
    private Integer imageTwo;
    private Integer imageThree;
    private Integer imageFour;
    private String url;
    private String nameFile;
    private String nameTitle;
    private String nameJp;
    private String nameEn;
    private String nameRu;
    private String nameRomaji;
    private String nameShort;
    private Integer sequenceNumber;
    private String author;
    private String illustrator;
    private String originalDesign;
    private Date releaseDate;
    private String isbn;
    private String externalUrl;
    private String volumeType;
    private String volumeStatus;
    private String volumeStatusHint;
    private boolean adult;
    private String annotation;
    /* Optional */
    private String prevNameShort;
    private String prevUrl;
    private String nextNameShort;
    private String nextUrl;
    private String subProjectName;

    public Volume()
    {
    }



    public Volume(Volume toClone, Integer sequenceNumber)
    {
        this.projectId = toClone.projectId;
        this.imageOne = toClone.imageOne;
        this.imageTwo = toClone.imageTwo;
        this.imageThree = toClone.imageThree;
        this.imageFour = toClone.imageFour;
        this.url = toClone.url;
        this.nameFile = toClone.nameFile;
        this.nameTitle = toClone.nameTitle;
        this.nameJp = toClone.nameJp;
        this.nameEn = toClone.nameEn;
        this.nameRu = toClone.nameRu;
        this.nameRomaji = toClone.nameRomaji;
        this.nameShort = toClone.nameShort;
        this.sequenceNumber = sequenceNumber;
        this.author = toClone.author;
        this.illustrator = toClone.illustrator;
        this.releaseDate = toClone.releaseDate;
        this.isbn = toClone.isbn;
        this.externalUrl = toClone.externalUrl;
        this.annotation = toClone.annotation;
        this.volumeStatus = toClone.volumeStatus;
        this.volumeType = toClone.volumeType;
        this.volumeStatusHint = toClone.volumeStatusHint;
        this.adult = toClone.adult;
    }

    public Volume(Integer projectId, String url, String nameFile, String nameTitle,
                  String nameJp, String nameEn, String nameRu, String nameShort,
                  Integer sequenceNumber, String author, String illustrator, Date releaseDate,
                  String isbn, String externalUrl, String annotation)
    {
        this.projectId = projectId;
        this.url = url;
        this.nameFile = nameFile;
        this.nameTitle = nameTitle;
        this.nameJp = nameJp;
        this.nameEn = nameEn;
        this.nameRu = nameRu;
        this.nameShort = nameShort;
        this.sequenceNumber = sequenceNumber;
        this.author = author;
        this.illustrator = illustrator;
        this.releaseDate = releaseDate;
        this.isbn = isbn;
        this.externalUrl = externalUrl;
        this.annotation = annotation;
    }

    public static PageParameters makeUrlParameters(String[] urlParts)
    {
        return new PageParameters().set("project", urlParts[0]).set("volume", urlParts[1]);
    }

    public PageParameters getFullTextUrlParameters()
    {
        return makeUrlParameters(url.split("/")).set("chapter", "text");
    }

    public PageParameters getUrlParameters()
    {
        return makeUrlParameters(url.split("/"));
    }

    public PageParameters getPrevUrlParameters()
    {
        return prevUrl == null ? null : makeUrlParameters(prevUrl.split("/"));
    }

    public PageParameters getNextUrlParameters()
    {
        return nextUrl == null ? null : makeUrlParameters(nextUrl.split("/"));
    }

    public Class getLinkClass()
    {
        return VolumePage.class;
    }

    public String getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(String annotation)
    {
        this.annotation = annotation;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getExternalUrl()
    {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl)
    {
        this.externalUrl = externalUrl;
    }

    public String getIllustrator()
    {
        return illustrator;
    }

    public void setIllustrator(String illustrator)
    {
        this.illustrator = illustrator;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    public String getNameEn()
    {
        return nameEn;
    }

    public void setNameEn(String nameEn)
    {
        this.nameEn = nameEn;
    }

    public String getNameFile()
    {
        return nameFile;
    }

    public void setNameFile(String nameFile)
    {
        this.nameFile = nameFile;
    }

    public String getNameJp()
    {
        return nameJp;
    }

    public void setNameJp(String nameJp)
    {
        this.nameJp = nameJp;
    }

    public String getNameRu()
    {
        return nameRu;
    }

    public void setNameRu(String nameRu)
    {
        this.nameRu = nameRu;
    }

    public String getNameShort()
    {
        return nameShort;
    }

    public void setNameShort(String nameShort)
    {
        this.nameShort = nameShort;
    }

    public String getNameTitle()
    {
        return nameTitle;
    }

    public void setNameTitle(String nameTitle)
    {
        this.nameTitle = nameTitle;
    }

    public Integer getSequenceNumber()
    {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber)
    {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Integer projectId)
    {
        this.projectId = projectId;
    }

    public Date getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Integer getVolumeId()
    {
        return volumeId;
    }

    public void setVolumeId(Integer volumeId)
    {
        this.volumeId = volumeId;
    }

    public boolean isAdult()
    {
        return adult;
    }

    public void setAdult(boolean adult)
    {
        this.adult = adult;
    }

    public String getVolumeStatus()
    {
        return volumeStatus;
    }

    public void setVolumeStatus(String volumeStatus)
    {
        this.volumeStatus = volumeStatus;
    }

    public String getVolumeType()
    {
        return volumeType;
    }

    public void setVolumeType(String volumeType)
    {
        this.volumeType = volumeType;
    }

    public Integer getImageFour()
    {
        return imageFour;
    }

    public void setImageFour(Integer imageFour)
    {
        this.imageFour = imageFour;
    }

    public Integer getImageOne()
    {
        return imageOne;
    }

    public void setImageOne(Integer imageOne)
    {
        this.imageOne = imageOne;
    }

    public Integer getImageThree()
    {
        return imageThree;
    }

    public void setImageThree(Integer imageThree)
    {
        this.imageThree = imageThree;
    }

    public Integer getImageTwo()
    {
        return imageTwo;
    }

    public void setImageTwo(Integer imageTwo)
    {
        this.imageTwo = imageTwo;
    }

    public String getNameRomaji()
    {
        return nameRomaji;
    }

    public void setNameRomaji(String nameRomaji)
    {
        this.nameRomaji = nameRomaji;
    }

    public String getOriginalDesign()
    {
        return originalDesign;
    }

    public void setOriginalDesign(String originalDesign)
    {
        this.originalDesign = originalDesign;
    }

    public String getVolumeStatusHint()
    {
        return volumeStatusHint;
    }

    public void setVolumeStatusHint(String volumeStatusHint)
    {
        this.volumeStatusHint = volumeStatusHint;
    }

    public String getNextNameShort()
    {
        return nextNameShort;
    }

    public String getNextUrl()
    {
        return nextUrl;
    }

    public String getPrevNameShort()
    {
        return prevNameShort;
    }

    public String getPrevUrl()
    {
        return prevUrl;
    }


    @Override
    public String toString()
    {
        return "Volume{" + "volumeId=" + volumeId + ", projectId=" + projectId + ", url=" + url + ", nameFile=" + nameFile + ", nameTitle=" + nameTitle + ", nameJp=" + nameJp + ", nameEn=" + nameEn + ", nameRu=" + nameRu + ", nameShort=" + nameShort + ", sequenceNumber=" + sequenceNumber + ", author=" + author + ", illustrator=" + illustrator + ", releaseDate=" + releaseDate + ", isbn=" + isbn + ", externalUrl=" + externalUrl + ", annotation=" + annotation + ", volumeStatus=" + volumeStatus + ", volumeType=" + volumeType + ", adult=" + adult + '}';
    }

    public String getSubProjectName()
    {
        return subProjectName;
    }

    public boolean isStatusExternal()
    {
        return volumeStatus.equals(RuraConstants.VOLUME_STATUS_EXTERNAL_ACTIVE)
               || volumeStatus.equals(RuraConstants.VOLUME_STATUS_EXTERNAL_DONE)
               || volumeStatus.equals(RuraConstants.VOLUME_STATUS_EXTERNAL_DROPPED);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Volume volume = (Volume) o;

        return volumeId != null && volumeId.equals(volume.volumeId);
    }

    @Override
    public int hashCode()
    {
        return volumeId != null ? volumeId.hashCode() : 0;
    }

    public String getFullStatus()
    {
        return RuraConstants.VOLUME_STATUS_TO_FULL_TEXT.get(volumeStatus);
    }

    public String getAnnotationParsed()
    {
        WikiParser wikiParser = new WikiParser(0,annotation);
        return annotation == null ? null : wikiParser.parseWikiText(new ArrayList<String>(), false);
    }
}
