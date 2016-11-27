# use ruranobe;

SET NAMES UTF8;
ALTER TABLE external_resources_history DROP FOREIGN KEY fk_history_project_id;
ALTER TABLE external_resources_history DROP FOREIGN KEY fk_history_volume_id;
ALTER TABLE external_resources_history DROP FOREIGN KEY fk_history_chapter_image_id;
DROP TABLE IF EXISTS user_groups;
DROP TABLE IF EXISTS user_group_types;
DROP TABLE IF EXISTS orphus_comments;
DROP TABLE IF EXISTS chapter_images;
DROP TABLE IF EXISTS updates;
DROP TABLE IF EXISTS bookmarks;
DROP TABLE IF EXISTS paragraphs;
DROP TABLE IF EXISTS chapters;
DROP TABLE IF EXISTS volume_release_activities;
DROP TABLE IF EXISTS volume_statuses;
DROP TABLE IF EXISTS volumes;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS external_resources;
DROP TABLE IF EXISTS external_resources_history;
DROP TABLE IF EXISTS texts_history;
DROP TABLE IF EXISTS texts;
DROP TABLE IF EXISTS team_members;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS teams;
DROP TABLE IF EXISTS volume_activities;

CREATE TABLE users
(
  user_id                  INT(11) PRIMARY KEY AUTO_INCREMENT,
  username                 VARCHAR(64) UNIQUE NOT NULL,
  realname                 VARCHAR(255),
  pass                     TINYBLOB           NOT NULL,
  pass_version             INT(11) UNSIGNED   NOT NULL,
  pass_recovery_token      VARCHAR(255),
  pass_recovery_token_date DATETIME,
  email                    VARCHAR(255),
  email_token              VARCHAR(255),
  email_token_date         DATETIME,
  email_activated          BOOL               NOT NULL,
  registration_date        DATETIME           NOT NULL,
  #--user_settings
  converter_type           ENUM('fb2',
                                'docx',
                                'epub')       NOT NULL,
  navigation_type          ENUM('Главам',
                                'Подглавам')  NOT NULL,
  convert_with_imgs        BOOL               NOT NULL,
  adult                    BOOL               NOT NULL,
  prefer_colored_imgs      BOOL               NOT NULL,
  show_hidden_content      BOOL               NOT NULL,
  convert_imgs_size        INT(11)            NOT NULL,
  forum_user_id            INT(11) UNSIGNED    DEFAULT NULL,
  INDEX (username),
  INDEX (email),
  INDEX (pass_recovery_token),
  INDEX (email_token)
);

CREATE TABLE texts
(
  text_id   INT(11) PRIMARY KEY AUTO_INCREMENT,
  text_wiki MEDIUMTEXT NOT NULL,
  text_html MEDIUMTEXT,
  footnotes MEDIUMTEXT,
  contents  MEDIUMTEXT
);

CREATE TABLE orphus_comments
(
  chapter_id       INT(11)      NOT NULL,
  paragraph        VARCHAR(255) NOT NULL,
  start_offset     INT(11)      NOT NULL,
  original_text    VARCHAR(255) NOT NULL,
  replacement_text VARCHAR(255) NOT NULL,
  optional_comment VARCHAR(255),
  user_id          INT(11),
  user_ip          VARCHAR(15),
  created_when     DATETIME     NOT NULL,
  INDEX (created_when),
  INDEX (chapter_id, created_when)
);

CREATE TABLE projects
(
  project_id         INT(11) PRIMARY KEY AUTO_INCREMENT,
  parent_id          INT(11),
  image_id           INT(11),
  url                VARCHAR(32) UNIQUE,
  title              VARCHAR(1023)     NOT NULL,
  name_jp            VARCHAR(255),
  name_en            VARCHAR(255),
  name_ru            VARCHAR(255),
  name_romaji        VARCHAR(255),
  author             VARCHAR(255),
  illustrator        VARCHAR(255),
  original_design    VARCHAR(255),
  original_story     VARCHAR(255),
  order_number       INT(11)           NOT NULL,
  banner_hidden      BOOL              NOT NULL,
  project_hidden     BOOL              NOT NULL,
  onevolume          BOOL              NOT NULL,
  works              BOOL              NOT NULL,
  franchise          TEXT,
  annotation         TEXT,
  forum_id           INT(11) UNSIGNED    DEFAULT NULL,
  issue_status       VARCHAR(255),
  translation_status VARCHAR(255),
  status             ENUM('Выпускается',
                          'Окончен',
                          'Переведен') NOT NULL,
  requisite_id       INT(11),
  INDEX (url),
  INDEX (order_number)
);

CREATE TABLE volumes
(
  volume_id          INT(11) PRIMARY KEY          AUTO_INCREMENT,
  project_id         INT(11)             NOT NULL,
  image_one          INT(11),
  image_two          INT(11),
  image_three        INT(11),
  image_four         INT(11),
  url                VARCHAR(32) UNIQUE  NOT NULL,
  name_file          VARCHAR(255)        NOT NULL,
  name_title         VARCHAR(255)        NOT NULL,
  name_jp            VARCHAR(255),
  name_en            VARCHAR(255),
  name_ru            VARCHAR(255),
  name_romaji        VARCHAR(255),
  name_short         VARCHAR(64),
  sequence_number    FLOAT,
  author             VARCHAR(255),
  illustrator        VARCHAR(255),
  original_design    VARCHAR(255),
  original_story     VARCHAR(255),
  release_date       DATE,
  ISBN               VARCHAR(16),
  external_url       VARCHAR(255),
  volume_type        ENUM('Ранобэ',
                          'Побочные истории',
                          'Авторские додзинси',
                          'Другое')      NOT NULL,
  volume_status      ENUM(
    -- не в работе
    'on_hold',
    'no_eng',
    'freeze',
    'queue',
    -- специальный статус
    'hidden',
    'auto',
    -- сторонний
    'external_dropped',
    'external_active',
    'external_done',
    -- в работе
    'ongoing',
    'translating',
    'proofread',
    -- опубликован
    'decor',
    'done',
    'license')                           NOT NULL,
  volume_status_hint VARCHAR(255),
  adult              BOOL                NOT NULL,
  annotation         TEXT,
  topic_id           INT(11) UNSIGNED             DEFAULT NULL,
  requisite_id       INT(11),
  INDEX (url),
  UNIQUE (project_id, sequence_number)
);

CREATE TABLE volume_statuses
(
  status_id    INT(11) PRIMARY KEY AUTO_INCREMENT,
  full_text    VARCHAR(32),
  label_text   VARCHAR(32),
  label_class  ENUM('default',
                    'primary',
                    'success',
                    'info',
                    'warning',
                    'danger'),
  option_group ENUM('basic',
                    'external',
                    'not_in_work',
                    'in_work',
                    'published',
                    'licensed')
);

CREATE TABLE chapters
(
  chapter_id   INT(11) PRIMARY KEY AUTO_INCREMENT,
  volume_id    INT(11)       NOT NULL,
  text_id      INT(11),
  url          VARCHAR(32) UNIQUE,
  title        VARCHAR(1023) NOT NULL,
  order_number INT(11)       NOT NULL,
  publish_date DATETIME,
  nested       BOOL          NOT NULL,
  INDEX (url),
  INDEX (volume_id, order_number)
);

CREATE TABLE chapter_images
(
  chapter_image_id     INT(11) PRIMARY KEY AUTO_INCREMENT,
  chapter_id           INT(11),
  volume_id            INT(11) NOT NULL,
  --  image_id int(11), NOT NULL
  non_colored_image_id INT(11) NOT NULL,
  colored_image_id     INT(11),
  order_number         INT(11) NOT NULL,
  adult                BOOL    NOT NULL
);

CREATE TABLE external_resources
(
  resource_id   INT(11) PRIMARY KEY AUTO_INCREMENT,
  user_id       INT(11)      NOT NULL,
  mime_type     VARCHAR(255) NOT NULL,
  url           VARCHAR(511) NOT NULL,
  thumbnail     VARCHAR(511) NOT NULL,
  title         VARCHAR(255),
  uploaded_when DATETIME     NOT NULL,
  width         INT,
  height        INT,
  history_id    INT(11)      NOT NULL
);

CREATE TABLE external_resources_history
(
  history_id       INT(11) PRIMARY KEY AUTO_INCREMENT,
  uploaded_when    DATETIME              NOT NULL,
  colored_type     ENUM('main', 'color') NOT NULL,
  project_id       INT(11)               NOT NULL,
  volume_id        INT(11),
  chapter_image_id INT(11)
);

CREATE TABLE volume_activities
(
  activity_id   INT(11) PRIMARY KEY AUTO_INCREMENT,
  activity_name VARCHAR(255) UNIQUE   NOT NULL,
  activity_type ENUM('text', 'image') NOT NULL
);

CREATE TABLE teams
(
  team_id           INT(11) PRIMARY KEY AUTO_INCREMENT,
  team_name         VARCHAR(255) UNIQUE NOT NULL,
  team_website_link VARCHAR(255)
);

CREATE TABLE team_members
(
  member_id INT(11) PRIMARY KEY AUTO_INCREMENT,
  user_id   INT(11),
  team_id   INT(11),
  nickname  VARCHAR(64) UNIQUE NOT NULL,
  INDEX (team_id, nickname)
);

CREATE TABLE volume_release_activities
(
  release_activity_id INT(11) PRIMARY KEY AUTO_INCREMENT,
  volume_id           INT(11) NOT NULL,
  activity_id         INT(11) NOT NULL,
  member_id           INT(11) NOT NULL,
  order_number        INT(11) NOT NULL,
  team_show_label     BOOLEAN NOT NULL    DEFAULT FALSE,
  team_show_status    ENUM('show_none',
                           'show_nick',
                           'show_team'),
  INDEX (volume_id, order_number),
  INDEX (volume_id, activity_id, order_number)
);

CREATE TABLE updates
(
  update_id   INT(11) PRIMARY KEY AUTO_INCREMENT,
  project_id  INT(11)                        NOT NULL,
  volume_id   INT(11)                        NOT NULL,
  chapter_id  INT(11),
  update_type ENUM('Опубликован',
                   'Обновлен перевод',
                   'Глобальная редактура',
                   'Обновление иллюстраций') NOT NULL,
  show_time   DATETIME                       NOT NULL,
  description VARCHAR(255),
  INDEX (project_id, show_time),
  INDEX (show_time),
  INDEX (update_type),
  INDEX (update_type, show_time)
);

CREATE TABLE texts_history
(
  current_text_id  INT(11) PRIMARY KEY AUTO_INCREMENT,
  previous_text_id INT(11),
  user_id          INT(11)  NULL,
  chapter_id       INT(11)  NULL,
  insertion_time   DATETIME NOT NULL
);

CREATE TABLE bookmarks
(
  bookmark_id  INT(11) PRIMARY KEY AUTO_INCREMENT,
  chapter_id   INT(11),
  user_id      INT(11)      NOT NULL,
  paragraph_id VARCHAR(255) NOT NULL,
  created_when DATETIME     NOT NULL
);

CREATE TABLE paragraphs
(
  paragraph_id   VARCHAR(255) PRIMARY KEY,
  paragraph_text TEXT    NOT NULL,
  text_id        INT(11) NOT NULL
);

CREATE TABLE user_groups
(
  user_id  INT(11) NOT NULL,
  group_id INT(11) NOT NULL
);

CREATE TABLE user_group_types
(
  group_id   INT(11)             NOT NULL PRIMARY KEY AUTO_INCREMENT,
  group_name VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO user_group_types VALUES (1, 'ADMIN');
INSERT INTO user_group_types VALUES (2, 'TEAM MEMBER');
INSERT INTO user_group_types VALUES (3, 'WORKS');

CREATE TABLE requisites (
  requisite_id              INT AUTO_INCREMENT  NOT NULL,
  title                     VARCHAR(255)        NOT NULL,
  qiwi                      VARCHAR(20)         NULL,
  wmr                       VARCHAR(13)         NULL,
  wmu                       VARCHAR(13)         NULL,
  wmz                       VARCHAR(13)         NULL,
  wme                       VARCHAR(13)         NULL,
  wmb                       VARCHAR(13)         NULL,
  wmg                       VARCHAR(13)         NULL,
  wmk                       VARCHAR(13)         NULL,
  wmx                       VARCHAR(13)         NULL,
  yandex                    VARCHAR(20)         NULL,
  paypal                    VARCHAR(254)        NULL,
  card                      VARCHAR(16)         NULL,
  bitcoin                   VARCHAR(34)         NULL,
  show_yandex_money_button  BOOLEAN DEFAULT 0   NOT NULL,
  show_yandex_card_button   BOOLEAN DEFAULT 0   NOT NULL,
  show_yandex_mobile_button BOOLEAN DEFAULT 0   NOT NULL,
  paypal_button_id          VARCHAR(13)         NULL,
  CONSTRAINT PK_REQUISITES PRIMARY KEY (requisite_id)
);

ALTER TABLE paragraphs ADD CONSTRAINT fk_paragraph_text_id FOREIGN KEY (text_id) REFERENCES texts (text_id);

ALTER TABLE bookmarks ADD CONSTRAINT fk_bookmark_paragraph_id FOREIGN KEY (paragraph_id) REFERENCES paragraphs (paragraph_id);

ALTER TABLE orphus_comments ADD CONSTRAINT fk_orphus_paragraph_id FOREIGN KEY (paragraph) REFERENCES paragraphs (paragraph_id);

ALTER TABLE bookmarks ADD CONSTRAINT fk_user_bookmark_id FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE texts_history ADD CONSTRAINT fk_current_text_id FOREIGN KEY (current_text_id) REFERENCES texts (text_id);

ALTER TABLE texts_history ADD CONSTRAINT fk_previous_text_id FOREIGN KEY (previous_text_id) REFERENCES texts (text_id);

ALTER TABLE texts_history ADD CONSTRAINT fk_texts_history_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE texts_history ADD CONSTRAINT fk_texts_history_chapter_id FOREIGN KEY (chapter_id) REFERENCES chapters (chapter_id)
  ON DELETE SET NULL;

ALTER TABLE chapter_images ADD CONSTRAINT fk_colored_image_id FOREIGN KEY (colored_image_id) REFERENCES external_resources (resource_id);

ALTER TABLE chapter_images ADD CONSTRAINT fk_non_colored_image_id FOREIGN KEY (non_colored_image_id) REFERENCES external_resources (resource_id);

ALTER TABLE chapter_images ADD CONSTRAINT fk_volume_id FOREIGN KEY (volume_id) REFERENCES volumes (volume_id)
  ON DELETE CASCADE;

ALTER TABLE chapter_images ADD CONSTRAINT fk_chapter_id FOREIGN KEY (chapter_id) REFERENCES chapters (chapter_id);


ALTER TABLE external_resources ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE external_resources ADD CONSTRAINT fk_history_id FOREIGN KEY (history_id) REFERENCES external_resources_history (history_id);

ALTER TABLE external_resources_history ADD CONSTRAINT fk_history_project_id FOREIGN KEY (project_id) REFERENCES projects (project_id);
ALTER TABLE external_resources_history ADD CONSTRAINT fk_history_volume_id FOREIGN KEY (volume_id) REFERENCES volumes (volume_id);
ALTER TABLE external_resources_history ADD CONSTRAINT fk_history_chapter_image_id FOREIGN KEY (chapter_image_id) REFERENCES chapter_images (chapter_image_id)
  ON DELETE SET NULL;

ALTER TABLE chapters ADD CONSTRAINT fk_volume_id2 FOREIGN KEY (volume_id) REFERENCES volumes (volume_id)
  ON DELETE CASCADE;

ALTER TABLE chapters ADD CONSTRAINT fk_text_id FOREIGN KEY (text_id) REFERENCES texts (text_id);


ALTER TABLE volumes ADD CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES projects (project_id);

ALTER TABLE volumes ADD CONSTRAINT fk_image_one FOREIGN KEY (image_one) REFERENCES external_resources (resource_id);
ALTER TABLE volumes ADD CONSTRAINT fk_image_two FOREIGN KEY (image_two) REFERENCES external_resources (resource_id);
ALTER TABLE volumes ADD CONSTRAINT fk_image_three FOREIGN KEY (image_three) REFERENCES external_resources (resource_id);
ALTER TABLE volumes ADD CONSTRAINT fk_image_four FOREIGN KEY (image_four) REFERENCES external_resources (resource_id);

ALTER TABLE volumes
  ADD CONSTRAINT fk_volumes_requisite_id FOREIGN KEY (requisite_id) REFERENCES requisites (requisite_id);


ALTER TABLE projects ADD CONSTRAINT fk_parent_id FOREIGN KEY (parent_id) REFERENCES projects (project_id);

ALTER TABLE projects ADD CONSTRAINT fk_image_id FOREIGN KEY (image_id) REFERENCES external_resources (resource_id);

ALTER TABLE projects
  ADD CONSTRAINT fk_projects_requisite_id FOREIGN KEY (requisite_id) REFERENCES requisites (requisite_id);


ALTER TABLE orphus_comments ADD CONSTRAINT fk_chapter_id2 FOREIGN KEY (chapter_id) REFERENCES chapters (chapter_id)
  ON DELETE CASCADE;

ALTER TABLE orphus_comments ADD CONSTRAINT fk_user_id2 FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE orphus_comments ADD PRIMARY KEY (chapter_id, paragraph, start_offset, original_text, replacement_text);


ALTER TABLE updates ADD CONSTRAINT fk_u_project_id FOREIGN KEY (project_id) REFERENCES projects (project_id);

ALTER TABLE updates ADD CONSTRAINT fk_u_volume_id FOREIGN KEY (volume_id) REFERENCES volumes (volume_id);

ALTER TABLE updates ADD CONSTRAINT fk_u_chapter_id FOREIGN KEY (chapter_id) REFERENCES chapters (chapter_id);


ALTER TABLE team_members ADD CONSTRAINT fk_team_id FOREIGN KEY (team_id) REFERENCES teams (team_id);

ALTER TABLE team_members ADD CONSTRAINT fk_user_id3 FOREIGN KEY (user_id) REFERENCES users (user_id);


ALTER TABLE volume_release_activities ADD CONSTRAINT fk_ra_volume_id FOREIGN KEY (volume_id) REFERENCES volumes (volume_id)
  ON DELETE CASCADE;

ALTER TABLE volume_release_activities ADD CONSTRAINT fk_member_id FOREIGN KEY (member_id) REFERENCES team_members (member_id);

ALTER TABLE volume_release_activities ADD CONSTRAINT fk_activity_id FOREIGN KEY (activity_id) REFERENCES volume_activities (activity_id);

ALTER TABLE user_groups ADD CONSTRAINT fk_ug_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE user_groups ADD CONSTRAINT fk_ug_group_id FOREIGN KEY (group_id) REFERENCES user_group_types (group_id);

/*
 * Do MySQL and MariaDB index foreign key columns automatically?
 * @See http://stackoverflow.com/questions/304317/does-mysql-index-foreign-key-columns-automatically  
 */
