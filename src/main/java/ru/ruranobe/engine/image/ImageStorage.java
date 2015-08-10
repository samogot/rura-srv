package ru.ruranobe.engine.image;

public class ImageStorage
{
    public ImageStorage(String serviceName, String storagePath)
    {
        this.serviceName = serviceName;
        this.storagePath = storagePath;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public String getStoragePath()
    {
        return storagePath;
    }

    private String serviceName;
    private String storagePath;
}
