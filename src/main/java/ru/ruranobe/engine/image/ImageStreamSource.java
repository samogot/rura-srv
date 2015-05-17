package ru.ruranobe.engine.image;

import java.io.IOException;
import java.io.InputStream;

public class ImageStreamSource implements ImageSource
{
    private final InputStream inputStream;

    public ImageStreamSource(InputStream inputStream)
    {
        if (inputStream.markSupported())
        {
            inputStream.mark(Integer.MAX_VALUE);
        }
        this.inputStream = inputStream;
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
        }
        catch (IOException ex)
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
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }
}
