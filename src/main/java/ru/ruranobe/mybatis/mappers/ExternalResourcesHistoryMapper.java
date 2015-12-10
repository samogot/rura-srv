package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.ExternalResourceHistory;

public interface ExternalResourcesHistoryMapper
{
    int insertExternalResourceHistory(ExternalResourceHistory externalResourceHistory);

    void updateExternalResourceHistory(ExternalResourceHistory externalResourceHistory);
}
