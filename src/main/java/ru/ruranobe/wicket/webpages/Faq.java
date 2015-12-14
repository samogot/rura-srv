package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Text;
import ru.ruranobe.mybatis.entities.tables.*;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.ArrayList;
import java.util.List;

public class Faq extends SidebarLayoutPage
{
    public Faq()
    {
        setStatelessHint(true);

        ExternalResource faqImageResource = null;
        class Question
        {
            int questionNumber;
            String questionText;
            String textHtml;

            public String getQuestionId()
            {
                return "question"+questionNumber;
            }

            public Question(int questionNumber, String questionText, String textHtml)
            {
                this.questionNumber = questionNumber;
                this.questionText = questionText;
                this.textHtml = textHtml;
            }
        }
        List<Question> questions = new ArrayList<Question>();

        //StringBuilder faqText = new StringBuilder();
        //List<ContentsHolder> contentsHolders = new ArrayList<ContentsHolder>();

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            Volume faqVolume = volumesMapperCacheable.getVolumeByUrl("system/faq");
            if (faqVolume == null)
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }

            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                    getCacheableMapper(session, ExternalResourcesMapper.class);
            faqImageResource = externalResourcesMapperCacheable.getExternalResourceById(faqVolume.getImageOne());

            ChaptersMapper chaptersMapper = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            List<Chapter> faqChapters = chaptersMapper.getChaptersByVolumeId(faqVolume.getVolumeId());

            int questionNumber = 0;
            TextsMapper textsMapper = CachingFacade.getCacheableMapper(session, TextsMapper.class);
            for (Chapter faqChapter: faqChapters)
            {
                Integer textId = faqChapter.getTextId();
                if (textId != null)
                {
                    Text text = textsMapper.getHtmlInfoById(faqChapter.getTextId());
                    if (Strings.isEmpty(text.getTextHtml()))
                    {
                        WikiParser wikiParser = new WikiParser(textId, faqChapter.getChapterId(), text.getTextWiki());
                        text.setTextHtml(wikiParser.parseWikiText(new ArrayList<String>(), true));
                        textsMapper.updateText(text);
                    }

                    questions.add(new Question(questionNumber++, faqChapter.getTitle(), text.getTextHtml()));

                   /* String chapterLink = "#" + diaryChapter.getUrlPart();
                    ContentsHolder holder = new ContentsHolder(chapterLink, diaryChapter.getTitle());
                    contentsHolders.add(holder);*/
                }
            }
        }
        finally
        {
            session.close();
        }

        WebMarkupContainer faqImage = new WebMarkupContainer("faqImage");
        if (faqImageResource != null)
        {
            faqImage.add(new AttributeModifier("src", faqImageResource.getUrl()));
        }
        add(faqImage);

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

                String title = "<h4 class=\"h4\">" + question.questionText + "</h4>";
                questionId.add(new Label("htmlText", title + question.textHtml).setEscapeModelStrings(false));
                listItem.add(questionId);

                add(listItem);
            }
        };
        add(faqQuestions);

        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
     //   sidebarModules.add(new ContentsModule("sidebarModule", contentsHolders));
    }



}
