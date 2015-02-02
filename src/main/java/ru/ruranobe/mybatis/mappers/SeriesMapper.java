package ru.ruranobe.mybatis.mappers;

import java.util.List;
import ru.ruranobe.mybatis.tables.Series;

public interface SeriesMapper 
{
    public void insertSeries(Series series);
    public Series getSeriesById(Integer seriesId);
    public Series getSeriesByUrl(String url);
    public List<Series> getAllSeries();
    public void updateSeries(Series series);
    public void deleteSeries(Integer seriesId);
}
