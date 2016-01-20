package ru.ruranobe.wicket.components.modals;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.Token;
import ru.ruranobe.misc.smtp.Email;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;

import java.util.Arrays;

public class ModalUserSettings extends Panel
{
    public ModalUserSettings(final String id)
    {
        super(id, Model.of("false"));

        final User user = LoginSession.get().getUser();

        setDefaultModel(new CompoundPropertyModel<>(user));

        StatelessForm userSettings = new StatelessForm("userSettings");
        userSettings.add(new DropDownChoice<>("converterType", Arrays.asList("fb2", "docx", "epub")));
        userSettings.add(new DropDownChoice<>("navigationType", Arrays.asList("Главам", "Подглавам")));
        userSettings.add(new CheckBox("convertWithImgs"));
        userSettings.add(new CheckBox("adult"));
        userSettings.add(new CheckBox("preferColoredImgs"));
        userSettings.add(new TextField<>("convertImgsSize"));
        userSettings.add(new Button("saveUserSettings")
        {
            @Override
            public void onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    CachingFacade.getCacheableMapper(session, UsersMapper.class).updateUser(user);
                    session.commit();
                }
            }
        });
        add(userSettings);
    }

    private static final long serialVersionUID = 1L;
}