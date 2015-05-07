package ru.ruranobe.mybatis.tables;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Created by Samogot on 07.05.2015.
 */
public interface PageRepresentable
{
    public Class getLinkClass();

    public PageParameters getUrlParameters();
}
