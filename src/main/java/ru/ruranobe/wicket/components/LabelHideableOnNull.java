package ru.ruranobe.wicket.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.string.Strings;

/**
 * Created by Samogot on 11.05.2015.
 */
public class LabelHideableOnNull extends Label
{
    public LabelHideableOnNull(String id)
    {
        super(id);
    }

    @Override
    public boolean isVisible()
    {
        return !Strings.isEmpty(getDefaultModelObjectAsString());
    }
}
