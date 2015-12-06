package ru.ruranobe.engine.image;

import com.google.common.collect.ImmutableMap;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.files.StorageService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.EnumMap;
import java.util.Map;

public class RuraImage
{

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public RuraImage(File imageFile, String extension, String title)
    {
        this.imageFile = imageFile;
        this.extension = extension;
        this.title = title;
        loadInputStream();
        String mimeType;
        try
        {
            mimeType = URLConnection.guessContentTypeFromStream(imageStream);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Unbale to get mimeType from file. ", ex);
        }
        if (Strings.isEmpty(mimeType))
        {
            mimeType = FILE_EXTENSION_TO_MIME_TYPE.get(extension);
        }
        this.mimeType = mimeType;
    }

    public File getImageFile()
    {
        return imageFile;
    }

    public String getPath()
    {
        return path;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getTitle()
    {
        return title;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public InputStream getInputStream()
    {
        return imageStream;
    }

    public String getPathOnImageServiceSystem(StorageService storageService)
    {
        return imageServiceSystemToUrl.get(storageService);
    }

    public void setPathOnImageServiceSystem(StorageService storageService, String pathOnImageServiceSystem)
    {
        imageServiceSystemToUrl.put(storageService, pathOnImageServiceSystem);
    }

    public String getThumbnailPathOnImageServiceSystem(StorageService storageService)
    {
        return imageServiceSystemToThumbnailUrl.get(storageService);
    }

    public void setThumbnailPathOnImageServiceSystem(StorageService storageService, String pathOnImageServiceSystem)
    {
        imageServiceSystemToThumbnailUrl.put(storageService, pathOnImageServiceSystem);
    }

    public void rewind()
    {
        loadInputStream();
    }

    private void loadInputStream()
    {
        try
        {
            this.imageStream = new FileInputStream(imageFile);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public void close()
    {
        if (imageStream != null)
        {
            try
            {
                imageStream.close();
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    private static final Map<String, String> FILE_EXTENSION_TO_MIME_TYPE =
            new ImmutableMap.Builder<String, String>().
                                                              put("gif", "image/gif").
                                                              put("jpg", "image/jpeg").
                                                              put("jpeg", "image/jpeg").
                                                              put("png", "image/png").
                                                              put("ico", "image/vnd.microsoft.icon").
                                                              put("bmp", "image/vnd.wap.wbmp")
                                                      .build();
    private InputStream imageStream;
    private final File imageFile;
    private String path;
    private String filename;
    private final String mimeType;
    private final String extension;
    private final String title;
    private final Map<StorageService, String> imageServiceSystemToUrl =
            new EnumMap<StorageService, String>(StorageService.class);
    private final Map<StorageService, String> imageServiceSystemToThumbnailUrl =
            new EnumMap<StorageService, String>(StorageService.class);
}
