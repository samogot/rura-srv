package ru.ruranobe.engine.files;

import ru.ruranobe.engine.files.StorageService;
import ru.ruranobe.engine.image.ImageStorage;
import ru.ruranobe.engine.image.RuraImage;

import java.io.*;

public class LocalStorageService
{
    public static RuraImage uploadFile(RuraImage toUpload, ImageStorage storage)
    {
        String filePath = toUpload.getPath() + "/" + toUpload.getTitle() + "." + toUpload.getExtension();
        try
        {
            OutputStream out = new FileOutputStream(filePath);

            InputStream in = toUpload.getInputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer, 0, buffer.length)) > 0)
            {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
            out.close();

            toUpload.setPathOnImageServiceSystem(StorageService.LOCAL_STORAGE, filePath);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Unable to upload file locally on server.", ex);
        }
        return toUpload;
    }
}
