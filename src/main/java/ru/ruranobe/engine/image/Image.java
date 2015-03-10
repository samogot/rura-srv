package ru.ruranobe.engine.image;

import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class Image 
{
    public static enum ImageServiceSystem
    {
        PICASSA, YANDEX_DISK, LOCALLY_STORED_ON_SERVER
    }

    public Image(ImageSource imageSource, String mimeType, String title, String path)
    {
        this.imageSource = imageSource;
        this.mimeType = mimeType;
        this.title = title;
        this.path = path;
    }

    public void putPathOnImageServiceSystem(ImageServiceSystem imageServiceSystem, String path)
    {
        imageServiceSystemToUrl.put(imageServiceSystem, path);
    }
    
    public String getPathOnImageServiceSystem(ImageServiceSystem imageServiceSystem)
    {
        return imageServiceSystemToUrl.get(imageServiceSystem);
    }
    
    public Set<ImageServiceSystem> getImageServiceSystems()
    {
        return imageServiceSystemToUrl.keySet();
    }
    
    public String getMimeType()
    {
        return mimeType;
    }

    public String getTitle()
    {
        return title;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public ImageSource getImageSource()
    {
        return imageSource;
    }
    
    public static String getFileExtensionByMimeType(String mimeType)
    {
        return mimeTypeToFileExtension.get(mimeType);
    }
    
    private final String mimeType;
    private final String title;
    private final String path;
    private final ImageSource imageSource;
    private final EnumMap<ImageServiceSystem, String> imageServiceSystemToUrl = 
            new EnumMap<ImageServiceSystem, String>(ImageServiceSystem.class);
    private static final Map<String, String> mimeTypeToFileExtension = 
            new ImmutableMap.Builder<String, String>().
            put("image/gif","gif").
            put("image/jpeg","jpg").
            put("image/png","png").build();
}
