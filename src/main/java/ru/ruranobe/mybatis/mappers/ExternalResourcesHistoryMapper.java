package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.ExternalResourceHistory;

public interface ExternalResourcesHistoryMapper
{

    public int insertExternalResourceHistory(ExternalResourceHistory externalResourceHistory);

    public void updateExternalResourceHistory(ExternalResourceHistory externalResourceHistory);
}
