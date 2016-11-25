package ru.ruranobe.wicket.webpages.special;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import ru.ruranobe.engine.wiki.parser.ChapterTextParser;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.sidebar.ActionsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.RequisitesSidebarModule;
import ru.ruranobe.wicket.webpages.admin.VolumeEdit;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.ArrayList;
import java.util.List;

public class Faq extends SidebarLayoutPage
{
    public Faq()
    {
        setStatelessHint(true);

        class Question
        {
            int questionNumber;
            String questionText;
            String textHtml;

            public Question(int questionNumber, String questionText, String textHtml)
            {
                this.questionNumber = questionNumber;
                this.questionText = questionText;
                this.textHtml = textHtml;
            }

            public String getQuestionId()
            {
                return "question" + questionNumber;
            }
        }
        List<Question> questions = new ArrayList<>();

        //StringBuilder faqText = new StringBuilder();
        //List<ContentsHolder> contentsHolders = new ArrayList<ContentsHolder>();

        Volume faqVolume;
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        try (SqlSession session = sessionFactory.openSession())
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            faqVolume = volumesMapperCacheable.getVolumeByUrl("system/faq");
            redirectTo404IfArgumentIsNull(faqVolume);

            ChaptersMapper chaptersMapper = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            List<Chapter> faqChapters = chaptersMapper.getChaptersByVolumeId(faqVolume.getVolumeId());

            int questionNumber = 0;
            boolean committingNeeded = false;
            TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            for (Chapter faqChapter : faqChapters)
            {
                if (faqChapter.getTextId() != null)
                {
                    committingNeeded = ChapterTextParser.getChapterText(faqChapter, session, textsMapper, false) || committingNeeded;
                    questions.add(new Question(++questionNumber, faqChapter.getTitle(), faqChapter.getText().getTextHtml()));
                }
            }

            if (committingNeeded)
            {
                session.commit();
            }
        }

        ListView<Question> faqQuestions = new ListView<Question>("faqQuestions", questions)
        {
            @Override
            protected void populateItem(final ListItem<Question> listItem)
            {
                Question question = listItem.getModelObject();

                WebMarkupContainer questionHref = new WebMarkupContainer("questionHref");
                questionHref.add(new AttributeModifier("href", "#" + question.getQuestionId()));

                Label questionNumber = new Label("questionNumber", question.questionNumber + ":");
                questionHref.add(questionNumber);

                Label questionText = new Label("questionText", question.questionText);
                questionHref.add(questionText);

                listItem.add(questionHref);

                WebMarkupContainer questionId = new WebMarkupContainer("questionId");
                questionId.setMarkupId(question.getQuestionId());
                questionId.setOutputMarkupId(true);

                questionId.add(new Label("htmlText", question.textHtml).setEscapeModelStrings(false));
                listItem.add(questionId);

                add(listItem);
            }
        };
        add(faqQuestions);
        if (faqVolume.getTopicId() != null)
        {
            add(new CommentsPanel("comments", faqVolume.getTopicId()));
        }
        else
        {
            add(new WebMarkupContainer("comments"));
        }

        sidebarModules.add(new ActionsSidebarModule(VolumeEdit.class, faqVolume.getUrlParameters()));
        sidebarModules.add(RequisitesSidebarModule.makeDefault());
        sidebarModules.add(new ProjectsSidebarModule());
        sidebarModules.add(new FriendsSidebarModule());
        //   sidebarModules.add(new ContentsModule("sidebarModule", contentsHolders));
    }


    @Override
    protected String getPageTitle()
    {
        return "FAQ - РуРанобэ";
    }

}
