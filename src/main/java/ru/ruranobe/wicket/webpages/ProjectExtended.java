package ru.ruranobe.wicket.webpages;

import java.io.Serializable;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.Project;

public class ProjectExtended implements Serializable
{
    private final Project project;
    private final ExternalResource imageResource;

    public ProjectExtended(Project project, ExternalResource image)
    {
        this.project = project;
        this.imageResource = image;
    }

    public ExternalResource getExternalResource()
    {
        return imageResource;
    }

    public Project getProject()
    {
        return project;
    }
}