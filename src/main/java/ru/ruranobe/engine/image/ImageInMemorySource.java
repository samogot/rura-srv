package ru.ruranobe.engine.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ImageInMemorySource implements ImageSource
{
    private final byte[] imageContent;
    private InputStream inputStream;

    public ImageInMemorySource(byte[] imageContent)
    {
        this.imageContent = imageContent;
        this.inputStream = new ByteArrayInputStream(imageContent);
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
            inputStream.reset();
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
