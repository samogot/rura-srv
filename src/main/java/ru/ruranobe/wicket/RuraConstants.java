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
    public static final String VOLUME_STATUS_AUTO = "auto";
    public static final String VOLUME_STATUS_EXTERNAL_DROPPED = "external_dropped";
    public static final String VOLUME_STATUS_EXTERNAL_ACTIVE = "external_active";
    public static final String VOLUME_STATUS_EXTERNAL_DONE = "external_done";
    public static final String VOLUME_STATUS_NO_ENG = "no_eng";
    public static final String VOLUME_STATUS_FREEZE = "freeze";
    public static final String VOLUME_STATUS_ON_HOLD = "on_hold";
    public static final String VOLUME_STATUS_QUEUE = "queue";
    public static final String VOLUME_STATUS_ONGOING = "ongoing";
    public static final String VOLUME_STATUS_TRANSLATING = "translating";
    public static final String VOLUME_STATUS_PROOFREAD = "proofread";
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
