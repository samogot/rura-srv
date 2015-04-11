package ru.ruranobe.wicket;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class RuraConstants 
{
    public static final String UPDATE_TYPE_TRANSLATE = "translate";    
    public static final String UPDATE_TYPE_PROOFREAD = "proofread";    
    public static final String UPDATE_TYPE_IMAGES = "images";    
    public static final String UPDATE_TYPE_OTHER = "other";   

    public static final String VOLUME_STATUS_HIDDEN = "hidden";
    public static final String VOLUME_STATUS_ANNOUNCED = "announced";
    public static final String VOLUME_STATUS_NOT_TRANSLATING = "not_translating";
    public static final String VOLUME_STATUS_EXTERNAL = "external";
    public static final String VOLUME_STATUS_WAIT_TRANSLATOR = "wait_translator";
    public static final String VOLUME_STATUS_WAIT_ENGLISH = "wait_eng";
    public static final String VOLUME_STATUS_FREEZED_TRANSLATOR = "freezed_translator";
    public static final String VOLUME_STATUS_FREEZED_ENGLISH = "freezed_eng";
    public static final String VOLUME_STATUS_ONGOING = "ongoing";
    public static final String VOLUME_STATUS_TRANSLATING = "translating";
    public static final String VOLUME_STATUS_PROOFREADING = "proofreading";
    public static final String VOLUME_STATUS_DECOR = "decor";
    public static final String VOLUME_STATUS_DONE = "done";
    
    public static final String VOLUME_TYPE_RANOBE = "ranobe_vol";
    public static final String VOLUME_TYPE_SIDE_STORY = "side_story";
    public static final String VOLUME_TYPE_DOUJINSHI= "doujinshi";
    public static final String VOLUME_TYPE_DOUJINSHI_SIDE_STORY = "doujinshi_ss";
    public static final String VOLUME_TYPE_MATERIALS = "materials";

    public static final Map<String, String> UPDATE_TYPE_TO_ICON_DIV_CLASS = 
            new ImmutableMap.Builder<String, String>()
            .put(UPDATE_TYPE_TRANSLATE, "updIcon type4")
            .put(UPDATE_TYPE_IMAGES, "updIcon type5")
            .put(UPDATE_TYPE_PROOFREAD, "updIcon type3")
            .put(UPDATE_TYPE_OTHER, "updIcon type1")
            .build();
    
    private RuraConstants(){}
}
