package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.Volume;

public interface VolumesMapper 
{
    public Volume getVolumeByUrl(String url);
}
