package ru.ruranobe.engine.image;

public class ImageStorage
{
    public ImageStorage(String serviceName, String storagePath, String storageFileName)
    {
        this.serviceName = serviceName;
        this.storagePath = storagePath;
        this.storageFileName = storageFileName;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public String getStoragePath()
    {
        return storagePath;
    }

    public String getStorageFileName()
    {
        return storageFileName;
    }

    private String serviceName;
    private String storagePath;
    private String storageFileName;
}
