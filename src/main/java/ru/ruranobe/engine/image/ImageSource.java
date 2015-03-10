package ru.ruranobe.engine.image;

import java.io.InputStream;

public interface ImageSource 
{
    public InputStream getInputStream();
    
    /**
     * rewinds input stream. It can be be read from the very beginning 
     * after executing this command. 
     * IMPORTANT. Some implementations may not support this command.
     */
    public void rewind();
    public void close();
}
