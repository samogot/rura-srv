package ru.ruranobe.wicket.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.wicket.RuraConstants;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

/**
 * Created by Samogot on 04.05.2015.
 */
public class CoverCarousel extends Panel
{
    public CoverCarousel(String id, List<SimpleEntry<String, ExternalResource>> coversList)
    {
        super(id);
        if (coversList.isEmpty())
        {
            coversList.add(new SimpleEntry<String, ExternalResource>("Нет обложки", RuraConstants.NO_COVER_IMAGE));
        }
        setOutputMarkupId(true);
        final String markupId = getMarkupId();
        add(new AttributeAppender("class", " carousel slide"));
        add(new AttributeModifier("data-ride", "carousel"));
        ListView<SimpleEntry<String, ExternalResource>> carouselIndicators = new ListView<SimpleEntry<String, ExternalResource>>("carouselIndicators", coversList)
        {
            @Override
            protected void populateItem(ListItem<SimpleEntry<String, ExternalResource>> item)
            {
                WebMarkupContainer slideIndicator = new WebMarkupContainer("slideIndicator");
                slideIndicator.add(new AttributeModifier("data-target", "#" + markupId));
                slideIndicator.add(new AttributeModifier("data-slide-to", item.getIndex()));
                slideIndicator.add(new AttributeModifier("title", item.getModelObject().getKey()));
                slideIndicator.setVisible(coversList.size() > 1);
                if (item.getIndex() == 0)
                {
                    slideIndicator.add(new AttributeAppender("class", " active"));
                }
                item.add(slideIndicator);
            }
        };
        add(carouselIndicators);

        ListView<SimpleEntry<String, ExternalResource>> carouselSlides = new ListView<SimpleEntry<String, ExternalResource>>("carouselSlides", coversList)
        {
            @Override
            protected void populateItem(ListItem<SimpleEntry<String, ExternalResource>> item)
            {
                WebMarkupContainer slideContainer = new WebMarkupContainer("slideContainer");
                if (item.getIndex() == 0)
                {
                    slideContainer.add(new AttributeAppender("class", " active"));
                }
                item.add(slideContainer);
                WebMarkupContainer slideImageLink;
                if (item.getModelObject().getValue() != RuraConstants.NO_COVER_IMAGE)
                {
                    slideImageLink = new ExternalLink("slideImageLink", item.getModelObject().getValue().getUrl());
                }
                else
                {
                    slideImageLink = new WebMarkupContainer("slideImageLink");
                    slideImageLink.add(new AttributeModifier("class", ""));
                }
                WebMarkupContainer slideImage = new WebMarkupContainer("slideImage");
                slideImage.add(new AttributeModifier("src", item.getModelObject().getValue().getThumbnail(240)));
                slideImage.add(new AttributeModifier("alt", item.getModelObject().getKey()));
                slideImageLink.add(slideImage);
                slideContainer.add(slideImageLink);
            }
        };
        add(carouselSlides);
    }
}
