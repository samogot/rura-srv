package ru.ruranobe.engine;

import ru.ruranobe.engine.image.ImageStorage;

import java.util.ArrayList;
import java.util.List;

public class Webpage
{

    public Class getPageClass()
    {
        return pageClass;
    }

    public List<ImageStorage> getImageStorages()
    {
        return imageStorages;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Webpage webpage = (Webpage) o;

        return pageClass.equals(webpage.pageClass);

    }

    @Override
    public int hashCode()
    {
        return pageClass.hashCode();
    }

    protected Webpage()
    {

    }

    protected void setPageClass(Class pageClass)
    {
        this.pageClass = pageClass;
    }

    public static class Builder
    {
        public Builder()
        {
            webpage = new Webpage();
        }

        public Builder setPageClass(String pageClass)
        {
            try
            {
                webpage.pageClass = Class.forName(pageClass);
            }
            catch (ClassNotFoundException ex)
            {
                throw new RuntimeException("Unable to locate class " + pageClass, ex);
            }
            return this;
        }

        public Builder addImageStorage(ImageStorage imageStorage)
        {
            webpage.getImageStorages().add(imageStorage);
            return this;
        }

        public Webpage build()
        {
            if (webpage.getPageClass() == null)
            {
                throw new IllegalArgumentException("PageClass is mandatory to build a webpage");
            }
            return webpage;
        }

        private Webpage webpage;
    }

    Class pageClass;
    List<ImageStorage> imageStorages = new ArrayList<>();
}
