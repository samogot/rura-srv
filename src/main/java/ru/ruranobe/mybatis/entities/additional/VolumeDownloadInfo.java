package ru.ruranobe.mybatis.entities.additional;

import java.io.Serializable;

public class VolumeDownloadInfo implements Serializable
{
    public boolean isDownload()
    {
        return download;
    }

    public void setDownload(boolean download)
    {
        this.download = download;
    }

    public boolean isImages()
    {
        return images;
    }

    public void setImages(boolean images)
    {
        this.images = images;
    }

    public boolean isColors()
    {
        return colors;
    }

    public void setColors(boolean colors)
    {
        this.colors = colors;
    }

    private boolean download;
    private boolean images;
    private boolean colors;

    private static final long serialVersionUID = 1L;
}
