package ru.ruranobe.wicket.components;

import javafx.util.Pair;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

/**
 * Created by Samogot on 04.05.2015.
 */
public class CoverCarousel extends Panel
{
    public CoverCarousel(String id, final String markupId, List<Pair<String, String>> coversList)
    {
        super(id);
        setMarkupId(markupId);
        add(new AttributeAppender("class", " carousel slide"));
        add(new AttributeModifier("data-ride", "carousel"));
        ListView<Pair<String, String>> carouselIndicators = new ListView<Pair<String, String>>("carouselIndicators", coversList)
        {
            @Override
            protected void populateItem(ListItem<Pair<String, String>> item)
            {
                WebMarkupContainer slideIndicator = new WebMarkupContainer("slideIndicator");
                slideIndicator.add(new AttributeModifier("data-target", "#" + markupId));
                slideIndicator.add(new AttributeModifier("data-slide-to", item.getIndex()));
                slideIndicator.add(new AttributeModifier("title", item.getModelObject().getKey()));
                if (item.getIndex() == 0) slideIndicator.add(new AttributeAppender("class", " active"));
                item.add(slideIndicator);
            }
        };
        add(carouselIndicators);

        ListView<Pair<String, String>> carouselSlides = new ListView<Pair<String, String>>("carouselSlides", coversList)
        {
            @Override
            protected void populateItem(ListItem<Pair<String, String>> item)
            {
                WebMarkupContainer slideContainer = new WebMarkupContainer("slideContainer");
                if (item.getIndex() == 0) slideContainer.add(new AttributeAppender("class", " active"));
                item.add(slideContainer);
                WebMarkupContainer slideImage = new WebMarkupContainer("slideImage");
                slideImage.add(new AttributeModifier("src", item.getModelObject().getValue()));
                slideImage.add(new AttributeModifier("alt", item.getModelObject().getKey()));
                slideContainer.add(slideImage);
            }
        };
        add(carouselSlides);
    }
}
