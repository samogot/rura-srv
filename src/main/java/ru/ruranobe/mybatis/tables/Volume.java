package ru.ruranobe.mybatis.tables;

import java.io.Serializable;
import java.util.Date;

public class Volume implements Serializable
{

    public Volume()
    {
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

    @Override
    public String toString()
    {
        return "Volume{" + "volumeId=" + volumeId + ", projectId=" + projectId + ", url=" + url + ", nameFile=" + nameFile + ", nameTitle=" + nameTitle + ", nameJp=" + nameJp + ", nameEn=" + nameEn + ", nameRu=" + nameRu + ", nameShort=" + nameShort + ", sequenceNumber=" + sequenceNumber + ", author=" + author + ", illustrator=" + illustrator + ", releaseDate=" + releaseDate + ", isbn=" + isbn + ", externalUrl=" + externalUrl + ", annotation=" + annotation + ", volumeStatus=" + volumeStatus + ", volumeType=" + volumeType + ", adult=" + adult + '}';
    }
    
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
    private String nameShort;
    private Integer sequenceNumber;
    private String author;
    private String illustrator;
    private Date releaseDate;
    private String isbn;
    private String externalUrl;
    private String annotation;
    private String volumeStatus;
    private String volumeType;
    private boolean adult;
    private static final long serialVersionUID = 1L;
    
    /* Optional */
    private String prevNameShort;
    private String prevUrl;
    private String nextNameShort;
    private String nextUrl;

    public String getNextNameShort()
    {
        return nextNameShort;
    }

    public void setNextNameShort(String nextNameShort)
    {
        this.nextNameShort = nextNameShort;
    }

    public String getNextUrl()
    {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl)
    {
        this.nextUrl = nextUrl;
    }

    public String getPrevNameShort()
    {
        return prevNameShort;
    }

    public void setPrevNameShort(String prevNameShort)
    {
        this.prevNameShort = prevNameShort;
    }

    public String getPrevUrl()
    {
        return prevUrl;
    }

    public void setPrevUrl(String prevUrl)
    {
        this.prevUrl = prevUrl;
    }
}
