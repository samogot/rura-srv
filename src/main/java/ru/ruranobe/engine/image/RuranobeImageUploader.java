package ru.ruranobe.engine.image;

public class RuranobeImageUploader implements ImageUploader
{

    @Override
    public String uploadImage()
    {
        YandexDiskUtils.uploadFile(null, null, null, null);
        return PicasaUtils.uploadImage(null, null, null, null);       
    }

    public static RuranobeImageUploader getInstance()
    {
        return RURANOBE_IMAGE_UPLOADER;
    }

    private RuranobeImageUploader()
    {        
    }
    
    private static final RuranobeImageUploader RURANOBE_IMAGE_UPLOADER = new RuranobeImageUploader();
}
