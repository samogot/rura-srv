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
            Integer orderNumber, String author, String illustrator, Date releaseDate, 
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
        this.orderNumber = orderNumber;
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

    public Integer getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber)
    {
        this.orderNumber = orderNumber;
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
    
    private Integer volumeId;
    private Integer projectId;
    private String url;
    private String nameFile;
    private String nameTitle;
    private String nameJp;
    private String nameEn;
    private String nameRu;
    private String nameShort;
    private Integer orderNumber;
    private String author;
    private String illustrator;
    private Date releaseDate;
    private String isbn;
    private String externalUrl;
    private String annotation;
    private static final long serialVersionUID = 1L;
}
