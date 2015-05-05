package ru.ruranobe.wicket;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class RuraConstants
{
    public static final String UPDATE_TYPE_PUBLISH = "Опубликован";
    public static final String UPDATE_TYPE_TRANSLATE = "Обновлен перевод";
    public static final String UPDATE_TYPE_PROOFREAD = "Глобальная редактура";
    public static final String UPDATE_TYPE_IMAGES = "Обновление иллюстраций";
    public static final String UPDATE_TYPE_OTHER = "";
    public static final Map<String, String> UPDATE_TYPE_TO_ICON_CLASS =
            new ImmutableMap.Builder<String, String>()
                    .put(RuraConstants.UPDATE_TYPE_PUBLISH, "update-publish")
                    .put(RuraConstants.UPDATE_TYPE_TRANSLATE, "update-translate")
                    .put(RuraConstants.UPDATE_TYPE_PROOFREAD, "update-proofread")
                    .put(RuraConstants.UPDATE_TYPE_IMAGES, "update-images")
                    .build();

    public static final String VOLUME_STATUS_HIDDEN = "hidden";
    @SuppressWarnings("unused")
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

    private RuraConstants()
    {
    }
}
