package ru.ruranobe.engine.image;

public class RuranobeImageUploader implements ImageUploader
{
    
    public RuranobeImageUploader(Image image)
    {
        this.image = image;
    }
    
    @Override
    public Image uploadImage(Image image)
    {
        YandexDiskUtils.uploadFile(image);
        image.getImageSource().rewind();
        PicasaUtils.uploadImage(image);
        image.getImageSource().close();
        return image;
    }
    
    private final Image image;
}
