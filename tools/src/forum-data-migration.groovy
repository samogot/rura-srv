#!/usr/bin/env groovy
@GrabConfig(systemClassLoader = true)
@Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.36')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7.1')
@GrabExclude(group = 'xerces', module = 'xercesImpl')

import groovy.sql.Sql
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

import java.sql.SQLException

import static java.security.MessageDigest.getInstance

class forumDataMigration {

    final Sql mw;
    final Sql site;
    final Sql forum;
    final RESTClient api;

    static void main(args) {
        def forumRootId = 317;
        def avatarsCount = 286;
        def forumUserSecret = '123';
        def forumApi = new RESTClient('https://ruranobe.ru/f/api/', ContentType.JSON)
        def mw = connect('localhost', 'ruranobe_db', 'ruranobe_db', '123');
        def site = connect('localhost', 'ruranobe', 'ruranobe', '123');
        def forum = connect('localhost', 'ruranobe_forum', 'ruranobe_forum', '123');
        def migrationTool = new forumDataMigration(mw, site, forum, forumApi);
        println("Migrating projects")
        migrationTool.migrateProjects(forumRootId);
        println("Migrating volumes")
        migrationTool.migrateVolumes(forumUserSecret);
        println("Migrating users")
        migrationTool.migrateUsers(avatarsCount)
        println("Migrating comments")
        migrationTool.migrateComments()
        println("Update volumes texts")
        migrationTool.updateVolumeTexts();
        println("Update converter caches")
        migrationTool.updateConverterCaches();
        println("Migration completed")
    }

    static Sql connect(String host, String db, String user, String password) {
        return Sql.newInstance("jdbc:mysql://${host}/${db}?useUnicode=true&characterEncoding=UTF-8", user, password, 'com.mysql.jdbc.Driver');
    }

    forumDataMigration(mw, site, forum, api) {
        this.mw = mw
        this.site = site
        this.forum = forum
        this.api = api;
    }

    def migrateProjects(forumRootId) {
        def forumIds = new HashMap<Long, Long>();
        site.eachRow("""
SELECT
    project_id, parent_id, title
FROM
    projects
ORDER BY
    parent_id IS NULL DESC, parent_id ASC;
""") { project ->
            def parent = project.parent_id ? forumIds.get(project.parent_id) : forumRootId
            def result = api.post(path: "forum/${parent}",
                                  body: [forum_name: project.title],
                                  requestContentType: ContentType.JSON)
            assert result.status == 200
            forumIds.put(project.project_id, result.data.forum_id)
            site.executeUpdate("""
UPDATE
    projects
SET forum_id = ${result.data.forum_id}
WHERE project_id = ${project.project_id};
""")
        }
    }

    def migrateVolumes(forumUserSecret) {
        site.eachRow("""
SELECT
    vol.volume_id AS volume_id, vol.name_title AS name_title, prj.forum_id AS forum_id
FROM
    volumes vol LEFT JOIN projects prj ON vol.project_id = prj.project_id
ORDER BY
    vol.project_id, vol.sequence_number;
""") { volume ->
            def result = api.post(path: "forum/${volume.forum_id}/topics",
                                  query: [secret: forumUserSecret],
                                  body: [topic_title: volume.name_title, topic_body: 'Обсуждение ' + volume.name_title],
                                  requestContentType: ContentType.JSON)
            assert result.status == 200
            site.executeUpdate("""
UPDATE
    volumes
SET topic_id = ${result.data.topic_id}
WHERE volume_id = ${volume.volume_id};
""")
        }
    }

    def migrateUsers(avatarsCount) {
        site.eachRow("""
SELECT
  user_id, username, email, registration_date
FROM
    users
ORDER BY
    email_activated DESC, user_id;
""") { user ->
            def unix_time = user.registration_date ? user.registration_date.getTime() / 1000 : 0
            def username_clean = api.post(path: "service/utf8_clean_string",
                                  body: [text: user.username],
                                  requestContentType: ContentType.JSON)
            assert username_clean.status == 200
            def email = user.email.toLowerCase()
            def email_hash = api.post(path: "service/email_hash",
                                  body: [text: email],
                                  requestContentType: ContentType.JSON)
            assert email_hash.status == 200
            def unique_id = api.post(path: "service/unique_id",
                                      body: [text: 'c'],
                                      requestContentType: ContentType.JSON)
            assert unique_id.status == 200
            def avatarId = user.username.collect { (int)it }.sum() % avatarsCount
            def avatar = "ruranobe/${avatarId}.jpg".toString()
            try {
                def ids = forum.executeInsert("""
INSERT INTO
    phpbb_users (group_id, user_permissions, user_regdate, username, username_clean, user_email, user_email_hash, user_lang, user_timezone, user_dateformat, user_form_salt, user_avatar, user_avatar_type, user_avatar_width, user_avatar_height, user_sig)
VALUES
    (2, '', :user_regdate, :username, :username_clean, :email, :email_hash, 'ru', 'Europe/Moscow', 'd M Y, H:i', :user_form_salt, :avatar, 'avatar.driver.local', 150, 150, '');
""", [user_regdate: unix_time, username: user.username, username_clean: username_clean.data.text, email: email,
      email_hash: email_hash.data.text, user_form_salt: unique_id.data.text, avatar: avatar])
                if (ids && ids.size()) {
                    def forum_user_id = ids[0][0]
                    forum.executeInsert("""
INSERT INTO
    phpbb_user_group (group_id, user_id, group_leader, user_pending)
VALUES
    (2, ${forum_user_id}, 0, 0) ;
""")
                    site.executeUpdate("""
UPDATE
    users
SET forum_user_id = ${forum_user_id}
WHERE user_id = ${user.user_id};
""")
                }
            } catch (SQLException ignore) {
            }
        }
    }

    def migrateComments() {
        def digest = getInstance("MD5")
        site.eachRow("""
SELECT
    vol.volume_id AS volume_id, vol.url AS url, vol.topic_id AS topic_id, vol.name_title AS name_title, prj.forum_id AS forum_id
FROM
    volumes vol LEFT JOIN projects prj ON vol.project_id = prj.project_id
ORDER BY
    volume_id;
""") { volume ->
            if (volume.url && volume.topic_id) {
                def page = mw.firstRow("""
SELECT
    page_id
FROM
    mw_page
WHERE
    page_title = ${volume.url}
""")
                if (page && page.page_id) {
                    def subject = 'Re: ' + volume.name_title
                    mw.eachRow("""
SELECT
    Comment_user_id AS user_id, Comment_username AS username, Comment_Text AS text, Comment_Date AS date, Comment_IP as ip
FROM
    mw_Comments
WHERE
    Comment_Page_ID = ${page.page_id} AND Comment_Parent_ID = 0 ORDER BY Comment_Date;
""") { comment ->
                        def user = site.firstRow("""
SELECT
    forum_user_id, username
FROM
    users
WHERE
    user_id = ${comment.user_id}
""")
                        def user_id = user && user.forum_user_id ? user.forum_user_id : 1
                        def username = user && user.username ? user.username : comment.username
                        def unix_time = comment.date ? comment.date.getTime() / 1000 : 0
                        byte[] bytes = comment.text.bytes;
                        def post_checksum = digest.digest(bytes).encodeHex().toString()
                        forum.executeInsert("""
INSERT INTO
    phpbb_posts (topic_id, forum_id, poster_id, poster_ip, post_time, post_username, post_subject, post_text, post_checksum, post_visibility)
VALUES
    (${volume.topic_id}, ${volume.forum_id},  ${user_id}, :ip, :post_time, :post_username, :post_subject, :post_text, :post_checksum, 1);
""", [ip: comment.ip, post_time: unix_time, post_username: username, post_subject: subject,
      post_text: comment.text, post_checksum: post_checksum])
                    }
                }
            }
        }
    }

    def updateVolumeTexts() {
        site.eachRow("""
SELECT
    vol.volume_id AS volume_id, vol.url AS url, vol.name_title AS name_title
FROM
    volumes vol
ORDER BY
    vol.project_id, vol.sequence_number;
""") { volume ->
            println(volume.name_title)
            "https://ruranobe.ru/r/${volume.url}/text".toURL().text
        }
    }

    def download(String address, String filename)
    {
        def file = new FileOutputStream(filename)
        def out = new BufferedOutputStream(file)
        out << new URL(address).openStream()
        out.close()
    }

    def updateConverterCaches() {
        site.eachRow("""
SELECT
    vol.volume_id AS volume_id, vol.url AS url, vol.name_title AS name_title, vol.name_file AS name_file
FROM
    volumes vol
ORDER BY
    vol.project_id, vol.sequence_number;
""") { volume ->
            println(volume.name_title)
            try {
                download("https://ruranobe.ru/d/fb2/${volume.url}".toString(), volume.name_file + ".fb2")
            } catch (IOException e) {
                println e.getMessage()
            }
            try {
                download("https://ruranobe.ru/d/fb2/${volume.url}?pic=0".toString(), volume.name_file + "_nopic.fb2")
            } catch (IOException e) {
                println e.getMessage()
            }
            try {
                download("https://ruranobe.ru/d/docx/${volume.url}".toString(), volume.name_file + ".docx")
            } catch (IOException e) {
                println e.getMessage()
            }
            try {
                download("https://ruranobe.ru/d/epub/${volume.url}".toString(), volume.name_file + ".epub")
            } catch (IOException e) {
                println e.getMessage()
            }
        }
    }
}
