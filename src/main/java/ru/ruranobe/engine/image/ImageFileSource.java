package ru.ruranobe.engine.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageFileSource implements ImageSource
{
    private final File imageFile;
    private InputStream inputStream;

    public ImageFileSource(File imageFile)
    {
        this.imageFile = imageFile;
        try
        {
            this.inputStream = new FileInputStream(imageFile);
        } catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public InputStream getInputStream()
    {
        return inputStream;
    }

    @Override
    public void rewind()
    {
        try
        {
            this.inputStream = new FileInputStream(imageFile);
        } catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close()
    {
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            } catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }
}
