package ru.ruranobe.wicket.webpages;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.OrphusComment;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.OrphusCommentsMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.webpages.base.BaseLayoutPage;

import java.text.SimpleDateFormat;
import java.util.Iterator;

public class Orphus extends BaseLayoutPage
{

    public Orphus(PageParameters parameters)
    {
        setStatelessHint(true);

        parsePageParameters(parameters);

        DataView<OrphusComment> orphusRepeater = new DataView<OrphusComment>("orphusRepeater", new OrphusCommentsDataProvider(projectId, volumeId, chapterId))
        {
            @Override
            protected void populateItem(Item<OrphusComment> item)
            {
                final OrphusComment orphusComment = item.getModelObject();

                Label orphusDate = new Label("orphusDate", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                        return sdf.format(orphusComment.getCreatedWhen());
                    }
                });
                item.add(orphusDate);

                Label orphusUsername = new Label("orphusUsername", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        return orphusComment.getUsername();
                    }
                });
                item.add(orphusUsername);

                Label orphusOriginalText = new Label("orphusOriginalText", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        String comment = orphusComment.getParagrap().getParagraphText();
                        return String.format("%s<span class=\"orphusMark\">%s</span>%s",
                                comment.substring(0, orphusComment.getStartOffset()), orphusComment.getOriginalText(),
                                comment.substring(orphusComment.getStartOffset() + orphusComment.getOriginalText().length(),
                                        comment.length()));
                    }
                });
                orphusOriginalText.setEscapeModelStrings(false);
                item.add(orphusOriginalText);

                Label orphusReplacementText = new Label("orphusReplacementText", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        String comment = orphusComment.getParagrap().getParagraphText();
                        return String.format("%s<span class=\"orphusMark\">%s</span>%s",
                                comment.substring(0, orphusComment.getStartOffset()), orphusComment.getReplacementText(),
                                comment.substring(orphusComment.getStartOffset() + orphusComment.getOriginalText().length(),
                                        comment.length()));
                    }
                });
                orphusReplacementText.setEscapeModelStrings(false);

                item.add(orphusReplacementText);
                Label orphusOptionalComment = new Label("orphusOptionalComment", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        return orphusComment.getOptionalComment();
                    }
                });

                item.add(orphusOptionalComment);

                Label projectTitle = new Label("projectTitle", new Model<String>()
                {
                    @Override
                    public String getObject()
                    {
                        return orphusComment.getProjectTitle();
                    }
                });

                item.add(projectTitle);

                PageParameters chapterPageParameters = Chapter.makeUrlParameters(orphusComment.getChapterUrl().split("/", -1));
                BookmarkablePageLink orphusChapterUrl = new BookmarkablePageLink("orphusChapterUrl", Text.class, chapterPageParameters)
                {
                    @Override
                    protected CharSequence getURL()
                    {
                        return super.getURL() + "#" + orphusComment.getParagraph();
                    }
                };
                Label orphusChapterName = new Label("orphusChapterName", orphusComment.getChapterName());
                orphusChapterName.setRenderBodyOnly(true);
                orphusChapterUrl.add(orphusChapterName);

                item.add(orphusChapterUrl);
            }
        };

        orphusRepeater.setItemsPerPage(10);

        add(new PagingNavigator("navigator", orphusRepeater));

        add(orphusRepeater);
    }

    private class OrphusCommentsDataProvider implements IDataProvider<OrphusComment>
    {

        public OrphusCommentsDataProvider(Integer projectId, Integer volumeId, Integer chapterId)
        {
            this.projectId = projectId;
            this.volumeId = volumeId;
            this.chapterId = chapterId;
        }

        @Override
        public Iterator<? extends OrphusComment> iterator(long first, long count)
        {
            Iterator<? extends OrphusComment> iter;
            try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
            {
                OrphusCommentsMapper orphusCommentsMapperCacheable = CachingFacade.getCacheableMapper(session, OrphusCommentsMapper.class);
                iter = orphusCommentsMapperCacheable.getLastOrphusCommentsBy(projectId, volumeId, chapterId, "desc", first, first + count).iterator();
            }
            return iter;
        }

        @Override
        public long size()
        {
            int size;
            try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
            {
                OrphusCommentsMapper orphusCommentsMapperCacheable = CachingFacade.getCacheableMapper(session, OrphusCommentsMapper.class);
                size = orphusCommentsMapperCacheable.getOrphusCommentsSize();
            }
            return size;
        }

        @Override
        public IModel<OrphusComment> model(OrphusComment object)
        {
            return Model.of(object);
        }

        @Override
        public void detach()
        {

        }

        private Integer projectId = null;
        private Integer volumeId = null;
        private Integer chapterId = null;
    }

    private void parsePageParameters(PageParameters parameters)
    {
        String projectUrl = parameters.get("project").toOptionalString();
        String volumeUrl = parameters.get("volume").toOptionalString();
        String chapterUrl = parameters.get("chapter").toOptionalString();

        StringBuilder fullUrl = new StringBuilder();
        if (StringUtils.isNotEmpty(projectUrl))
        {
            fullUrl.append(projectUrl);
            if (StringUtils.isNotEmpty(volumeUrl))
            {
                fullUrl.append("/").append(volumeUrl);
                if (StringUtils.isNotEmpty(chapterUrl))
                {
                    fullUrl.append("/").append(chapterUrl);
                    try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                    {
                        ChaptersMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
                        Chapter chapter = volumesMapperCacheable.getChapterByUrl(fullUrl.toString());
                        if (chapter == null)
                        {
                            throw RuranobeUtils.getRedirectTo404Exception(this);
                        }
                        chapterId = chapter.getChapterId();
                    }
                }
                else
                {
                    try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                    {
                        VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
                        Volume volume = volumesMapperCacheable.getVolumeByUrl(fullUrl.toString());
                        if (volume == null)
                        {
                            throw RuranobeUtils.getRedirectTo404Exception(this);
                        }
                        volumeId = volume.getVolumeId();
                    }
                }
            }
            else
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                    Project project = projectsMapperCacheable.getProjectByUrl(fullUrl.toString());
                    if (project == null)
                    {
                        throw RuranobeUtils.getRedirectTo404Exception(this);
                    }
                    projectId = project.getProjectId();
                }
            }
        }
    }

    /**
     * Wicket has a bag in MountedMapper.getCompatibilityScore for optional parameters which leads to selecting
     * the wrong page. Dumb fix in this class.
     */
    public static class OrphusMountedMapper extends MountedMapper
    {
        String startMountedPath;

        public OrphusMountedMapper(String mountedPath, String startMountedPath)
        {
            super(mountedPath, Orphus.class);
            this.startMountedPath = startMountedPath;
        }

        public int getCompatibilityScore(Request request)
        {
            return this.urlStartsWith(request.getUrl(), startMountedPath) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
    }

    private Integer projectId = null;
    private Integer volumeId = null;
    private Integer chapterId = null;
}
