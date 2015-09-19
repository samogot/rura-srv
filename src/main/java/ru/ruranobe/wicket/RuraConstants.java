package ru.ruranobe.wicket;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public class RuraConstants
{
    public static final String PATH_TO_P12_API_KEY = "C:/Users/Viktor/test/API Project-4143964bc63e.p12";
    public static final String GOOGLE_APPLICATION_NAME = "ruranobe";
    public static final String GOOGLE_TOKEN_SERVER_URL = "https://accounts.google.com/o/oauth2/token";
    public static final String PATH_TO_CONFIGURATION_FILE = "/home/samogot/RuraConfig.xml";
    public static final String PATH_TO_CONFIGURATION_FILE_SCHEMA = "/home/samogot/RuraConfig.xsd";
    public static final String UPDATE_TYPE_PUBLISH = "Опубликован";
    public static final String UPDATE_TYPE_TRANSLATE = "Обновлен перевод";
    public static final String UPDATE_TYPE_PROOFREAD = "Глобальная редактура";
    public static final String UPDATE_TYPE_IMAGES = "Обновление иллюстраций";
    public static final Map<String, String> UPDATE_TYPE_TO_ICON_CLASS =
            new ImmutableMap.Builder<String, String>()
                    .put(RuraConstants.UPDATE_TYPE_PUBLISH, "update-publish")
                    .put(RuraConstants.UPDATE_TYPE_TRANSLATE, "update-translate")
                    .put(RuraConstants.UPDATE_TYPE_PROOFREAD, "update-proofread")
                    .put(RuraConstants.UPDATE_TYPE_IMAGES, "update-images")
                    .build();

    public static final String VOLUME_TYPE_RANOBE = "Ранобэ";
    public static final String VOLUME_TYPE_SIDESTORY = "Побочные истории";
    public static final String VOLUME_TYPE_DOUJINSHI = "Авторские додзинси";
    public static final String VOLUME_TYPE_OTHER = "Другое";

    public static final List<String> VOLUME_TYPE_LIST = new ImmutableList.Builder<String>()
            .add(RuraConstants.VOLUME_TYPE_RANOBE)
            .add(RuraConstants.VOLUME_TYPE_SIDESTORY)
            .add(RuraConstants.VOLUME_TYPE_DOUJINSHI)
            .add(RuraConstants.VOLUME_TYPE_OTHER)
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
    public static final ImmutableBiMap<String, String> VOLUME_STATUS_TO_FULL_TEXT =
            new ImmutableBiMap.Builder<String, String>()
                    .put(RuraConstants.VOLUME_STATUS_HIDDEN, "Скрыт")
                    .put(RuraConstants.VOLUME_STATUS_EXTERNAL_DROPPED, "Заброшенный сторонний перевод")
                    .put(RuraConstants.VOLUME_STATUS_EXTERNAL_ACTIVE, "Активный сторонний перевод")
                    .put(RuraConstants.VOLUME_STATUS_EXTERNAL_DONE, "Завершенный сторонний перевод")
                    .put(RuraConstants.VOLUME_STATUS_NO_ENG, "Отсутствует анлейт")
                    .put(RuraConstants.VOLUME_STATUS_FREEZE, "Заморожен")
                    .put(RuraConstants.VOLUME_STATUS_ON_HOLD, "Приостановлен")
                    .put(RuraConstants.VOLUME_STATUS_QUEUE, "Очередь перевода")
                    .put(RuraConstants.VOLUME_STATUS_ONGOING, "Перевод в онгоинге")
                    .put(RuraConstants.VOLUME_STATUS_TRANSLATING, "Перевод")
                    .put(RuraConstants.VOLUME_STATUS_PROOFREAD, "Редактура")
                    .put(RuraConstants.VOLUME_STATUS_DECOR, "Не оформлен")
                    .put(RuraConstants.VOLUME_STATUS_DONE, "Завершен")
                    .build();

    public static final Map<String, String> VOLUME_STATUS_FULL_TEXT_TO_STATUS =
            VOLUME_STATUS_TO_FULL_TEXT.inverse();

    public static final ImmutableMap<String, String> VOLUME_STATUS_TO_LABEL_TEXT =
            new ImmutableMap.Builder<String, String>()
                    .put(RuraConstants.VOLUME_STATUS_EXTERNAL_DROPPED, "сторонний перевод")
                    .put(RuraConstants.VOLUME_STATUS_EXTERNAL_ACTIVE, "сторонний перевод")
                    .put(RuraConstants.VOLUME_STATUS_EXTERNAL_DONE, "сторонний перевод")
                    .put(RuraConstants.VOLUME_STATUS_NO_ENG, "нет анлейта")
                    .put(RuraConstants.VOLUME_STATUS_FREEZE, "заморожен")
                    .put(RuraConstants.VOLUME_STATUS_ON_HOLD, "приостановлен")
                    .put(RuraConstants.VOLUME_STATUS_QUEUE, "очередь")
                    .put(RuraConstants.VOLUME_STATUS_ONGOING, "онгоинг")
                    .put(RuraConstants.VOLUME_STATUS_TRANSLATING, "перевод")
                    .put(RuraConstants.VOLUME_STATUS_PROOFREAD, "редакт")
                    .put(RuraConstants.VOLUME_STATUS_DECOR, "не оформлен")
                    .put(RuraConstants.VOLUME_STATUS_DONE, "завершен")
                    .build();

    public static final String NO_COVER_IMAGE = "http://ruranobe.ru/w/images/thumb/a/ad/nopic.png/300px-nopic.png";

    private RuraConstants()
    {
    }
}
