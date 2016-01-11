package ru.ruranobe.wicket.components.admin.formitems;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.webpages.admin.VolumeEdit;

import java.util.List;

/**
 * Created by samogot on 17.09.15.
 */
public class VolumeTableRowPanel extends Panel
{
    private final IOptionRenderer<String> optionRenderer = new IOptionRenderer<String>()
    {
        @Override
        public String getDisplayValue(String object)
        {
            return RuraConstants.VOLUME_STATUS_TO_FULL_TEXT.get(object);
        }

        @Override
        public IModel<String> getModel(String value)
        {
            return Model.of(value);
        }
    };


    public VolumeTableRowPanel(String id, IModel<Volume> model, List<Project> projects)
    {
        super(id, model);
        add(new TextField<String>("urlPart").setRequired(true).setLabel(Model.of("Ссылка")));
        add(new TextField<String>("nameFile").setRequired(true).setLabel(Model.of("Имя для файлов")));
        add(new TextField<String>("nameTitle").setRequired(true).setLabel(Model.of("Заголовок")));
        add(new TextField<String>("nameJp"));
        add(new TextField<String>("nameEn"));
        add(new TextField<String>("nameRu"));
        add(new TextField<String>("nameRomaji"));
        add(new TextField<String>("nameShort"));
        add(new DropDownChoice<>("project", projects).setChoiceRenderer(new ChoiceRenderer<Project>("title", "projectId"))
                                                     .setOutputMarkupId(true));
        add(new TextField<Float>("sequenceNumber"));
        add(new TextField<String>("author"));
        add(new TextField<String>("illustrator"));
        add(new DateTextField("releaseDate", "dd.MM.yyyy"));
        add(new TextField<String>("isbn"));
        add(new DropDownChoice<>("volumeType", RuraConstants.VOLUME_TYPE_LIST));
        add(new Select<String>("volumeStatus")
                .add(new SelectOptions<>("basic", RuraConstants.VOLUME_STATUS_BASIC_LIST, optionRenderer))
                .add(new SelectOptions<>("external", RuraConstants.VOLUME_STATUS_EXTERNAL_LIST, optionRenderer))
                .add(new SelectOptions<>("not_in_work", RuraConstants.VOLUME_STATUS_IN_WORK_LIST, optionRenderer))
                .add(new SelectOptions<>("in_work", RuraConstants.VOLUME_STATUS_NOT_IN_WORK_LIST, optionRenderer))
                .add(new SelectOptions<>("published", RuraConstants.VOLUME_STATUS_PUBLISHED_LIST, optionRenderer)));
        add(new TextField<String>("externalUrl"));
        add(new TextArea<String>("annotation"));
        add(new CheckBox("adult"));
        add(new BookmarkablePageLink("link", VolumeEdit.class, model.getObject().getUrlParameters()));
    }
}
