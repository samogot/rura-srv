package ru.ruranobe.wicket.components.admin.formitems;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.mybatis.tables.Volume;
import ru.ruranobe.wicket.RuraConstants;

import java.util.Date;
import java.util.List;

/**
 * Created by samogot on 17.09.15.
 */
public class VolumeTableRowPanel extends Panel
{
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
        add(new DropDownChoice<Project>("project", projects).setChoiceRenderer(new ChoiceRenderer<Project>("title", "projectId"))
                                                            .setOutputMarkupId(true));
        add(new TextField<Float>("sequenceNumber"));
        add(new TextField<String>("author"));
        add(new TextField<String>("illustrator"));
        add(new TextField<Date>("releaseDate"));
        add(new TextField<String>("isbn"));
        add(new DropDownChoice<String>("volumeType", RuraConstants.VOLUME_TYPE_LIST));
        add(new Select<String>("volumeStatus")
                .add(new SelectOptions<String>("basic", VOLUME_STATUS_BASIC_LIST, optionRenderer))
                .add(new SelectOptions<String>("external", VOLUME_STATUS_EXTERNAL_LIST, optionRenderer))
                .add(new SelectOptions<String>("not_in_work", VOLUME_STATUS_IN_WORK_LIST, optionRenderer))
                .add(new SelectOptions<String>("in_work", VOLUME_STATUS_NOT_IN_WORK_LIST, optionRenderer))
                .add(new SelectOptions<String>("published", VOLUME_STATUS_PUBLISHED_LIST, optionRenderer)));
        add(new TextField<String>("externalUrl"));
        add(new TextArea<String>("annotation"));
        add(new CheckBox("adult"));
    }

    public final List<String> VOLUME_STATUS_BASIC_LIST = new ImmutableList.Builder<String>()
            .add(RuraConstants.VOLUME_STATUS_HIDDEN)
//            .add(RuraConstants.VOLUME_STATUS_AUTO)
            .build();
    public final List<String> VOLUME_STATUS_EXTERNAL_LIST = new ImmutableList.Builder<String>()
            .add(RuraConstants.VOLUME_STATUS_EXTERNAL_DROPPED)
            .add(RuraConstants.VOLUME_STATUS_EXTERNAL_ACTIVE)
            .add(RuraConstants.VOLUME_STATUS_EXTERNAL_DONE)
            .build();
    public final List<String> VOLUME_STATUS_IN_WORK_LIST = new ImmutableList.Builder<String>()
            .add(RuraConstants.VOLUME_STATUS_NO_ENG)
            .add(RuraConstants.VOLUME_STATUS_FREEZE)
            .add(RuraConstants.VOLUME_STATUS_ON_HOLD)
            .add(RuraConstants.VOLUME_STATUS_QUEUE)
            .build();
    public final List<String> VOLUME_STATUS_NOT_IN_WORK_LIST = new ImmutableList.Builder<String>()
            .add(RuraConstants.VOLUME_STATUS_ONGOING)
            .add(RuraConstants.VOLUME_STATUS_TRANSLATING)
            .add(RuraConstants.VOLUME_STATUS_PROOFREAD)
            .build();
    public final List<String> VOLUME_STATUS_PUBLISHED_LIST = new ImmutableList.Builder<String>()
            .add(RuraConstants.VOLUME_STATUS_DECOR)
            .add(RuraConstants.VOLUME_STATUS_DONE)
            .build();

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
}
