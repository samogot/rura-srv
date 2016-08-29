package ru.ruranobe.engine;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruranobe.config.ApplicationContext;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Volume;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForumApiUtils
{

    public static Integer createForum(Project project)
    {
        LOG.debug("start createForum");
        try
        {
            URL url = new URL(String.format(CREATE_FORUM_URL, ApplicationContext.getForumApiSecret()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            writeForumJson(project, connection.getOutputStream());
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return null;
            }
            Integer forum_id = readForumIdFromJson(connection.getInputStream());
            connection.disconnect();

            if (forum_id != null)
            {
                project.setForumId(forum_id);
            }
            LOG.debug("stop createForum " + forum_id);
            return forum_id;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateForum(Project project)
    {
        LOG.debug("start updateForum");
        try
        {
            if (project.getForumId() == null)
            {
                return false;
            }
            URL url = new URL(String.format(MODIFY_FORUM_URL, project.getForumId(), ApplicationContext.getForumApiSecret()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");

            writeForumJson(project, connection.getOutputStream());
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return false;
            }
            Integer forum_id = readForumIdFromJson(connection.getInputStream());
            connection.disconnect();

            LOG.debug("stop updateForum ");
            return project.getForumId().equals(forum_id);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteForum(Project project)
    {
        LOG.debug("start deleteForum");
        try
        {
            if (project.getForumId() == null)
            {
                return false;
            }
            URL url = new URL(String.format(MODIFY_FORUM_URL, project.getForumId(), ApplicationContext.getForumApiSecret()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return false;
            }
            Integer forum_id = readForumIdFromJson(connection.getInputStream());
            connection.disconnect();

            LOG.debug("stop deleteForum");
            return project.getForumId().equals(forum_id);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Integer createTopic(Volume volume, int forumId)
    {
        LOG.debug("start createTopic");
        try
        {
            URL url = new URL(String.format(CREATE_TOPIC_URL, forumId, ApplicationContext.getForumApiSecret()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            writeTopicJson(volume, connection.getOutputStream());
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return null;
            }
            Integer topic_id = readTopicIdFromJson(connection.getInputStream());
            connection.disconnect();

            if (topic_id != null)
            {
                volume.setTopicId(topic_id);
            }
            LOG.debug("stop createTopic");
            return topic_id;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateTopic(Volume volume)
    {
        LOG.debug("start updateTopic");
        try
        {
            if (volume.getTopicId() == null)
            {
                return false;
            }
            URL url = new URL(String.format(MODIFY_TOPIC_URL, volume.getTopicId(), ApplicationContext.getForumApiSecret()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");

            writeTopicJson(volume, connection.getOutputStream());
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return false;
            }
            Integer topic_id = readTopicIdFromJson(connection.getInputStream());
            connection.disconnect();

            LOG.debug("stop updateTopic");
            return volume.getTopicId().equals(topic_id);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteTopic(Volume volume)
    {
        LOG.debug("start deleteTopic");
        try
        {
            if (volume.getTopicId() == null)
            {
                return false;
            }
            URL url = new URL(String.format(MODIFY_TOPIC_URL, volume.getTopicId(), ApplicationContext.getForumApiSecret()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return false;
            }
            Integer topic_id = readTopicIdFromJson(connection.getInputStream());
            connection.disconnect();

            LOG.debug("stop deleteTopic");
            return volume.getTopicId().equals(topic_id);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Integer readForumIdFromJson(InputStream inputStream) throws IOException
    {
        Integer forum_id = null;
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        reader.beginObject();
        while (reader.hasNext())
        {
            if (reader.nextName().equals("forum_id"))
            {
                forum_id = reader.nextInt();
                break;
            }
            else
            {
                reader.skipValue();
            }
        }
        reader.close();
        return forum_id;
    }

    private static void writeForumJson(Project project, OutputStream outputStream) throws IOException
    {
// TODO: исправить, как станет понятен механизм работы тут
/*        int parentForumId, accessCopyForumId;
        if (project.getWorks())
        {
            parentForumId = WORKS_LIST_FORUM_ID;
            if (project.getProjectHidden())
            {
                accessCopyForumId = WORKS_HIDDEN_FORUM_ID;
            }
            else
            {
                accessCopyForumId = WORKS_SHOWN_FORUM_ID;
            }
        }
        else
        {
            if (project.getProjectHidden())
            {
                accessCopyForumId = MAIN_HIDDEN_FORUM_ID;
            }
            else
            {
                accessCopyForumId = MAIN_SHOWN_FORUM_ID;
            }
            if (project.getOrderNumber() <= 13)
            {
                parentForumId = TOP_LIST_FORUM_ID;
            }
            else
            {
                parentForumId = MAIN_LIST_FORUM_ID;
            }
        }

        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream));
        writer.beginObject();
        writer.name("forum_name").value(project.getTitle());
        writer.name("forum_parent_id").value(parentForumId);
        writer.name("forum_type").value(1);
        writer.name("display_subforum_list").value("1");
        writer.name("display_on_index").value("1");
        writer.name("enable_quick_reply").value(64);
        writer.name("forum_perm_from").value(accessCopyForumId);
        writer.endObject();
        writer.close();*/
    }

    private static Integer readTopicIdFromJson(InputStream inputStream) throws IOException
    {
        Integer topic_id = null;
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        reader.beginObject();
        while (reader.hasNext())
        {
            if (reader.nextName().equals("topic_id"))
            {
                topic_id = reader.nextInt();
                break;
            }
            else
            {
                reader.skipValue();
            }
        }
        reader.close();
        return topic_id;
    }

    private static void writeTopicJson(Volume volume, OutputStream outputStream) throws IOException
    {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream));
        writer.beginObject();
        writer.name("topic_title").value(volume.getNameTitle());
        writer.name("topic_body").value(String.format(TOPIC_BODY_TEMPLATE, volume.getUrl(), volume.getNameTitle()));
        writer.endObject();
        writer.close();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ForumApiUtils.class);
    private static final int TOP_LIST_FORUM_ID = 717;
    private static final int MAIN_LIST_FORUM_ID = 317;
    private static final int MAIN_SHOWN_FORUM_ID = 317;
    private static final int MAIN_HIDDEN_FORUM_ID = 774;
    private static final int WORKS_LIST_FORUM_ID = 716;
    private static final int WORKS_SHOWN_FORUM_ID = 716;
    private static final int WORKS_HIDDEN_FORUM_ID = 775;
    private static final String CREATE_FORUM_URL = "http://ruranobe.ru/f/api/forum?secret=%s";
    private static final String MODIFY_FORUM_URL = "http://ruranobe.ru/f/api/forum/%d?secret=%s";
    private static final String CREATE_TOPIC_URL = "http://ruranobe.ru/f/api/forum/%d/topics?secret=%s";
    private static final String MODIFY_TOPIC_URL = "http://ruranobe.ru/f/api/topic/%d?secret=%s";
    private static final String TOPIC_BODY_TEMPLATE = "Обсуждение [url=/r/%s]%s[/url]";
}
