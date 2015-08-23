# use ruranobe;

DROP TABLE IF EXISTS orphus_comments;
DROP TABLE IF EXISTS chapter_images;
DROP TABLE IF EXISTS updates;
DROP TABLE IF EXISTS chapters;
DROP TABLE IF EXISTS volume_release_activities;
DROP TABLE IF EXISTS volumes;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS external_resources;
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
  pass                     VARCHAR(32)        NOT NULL,
  pass_recovery_token      VARCHAR(255),
  pass_recovery_token_date DATETIME,
  email                    VARCHAR(255),
  email_token              VARCHAR(255),
  email_token_date         DATETIME,
  email_activated          BOOL               NOT NULL,
  registration_date        DATETIME           NOT NULL,
  adult                    BOOL               NOT NULL
);

CREATE TABLE texts
(
  text_id   INT(11) PRIMARY KEY AUTO_INCREMENT,
  text_wiki MEDIUMTEXT NOT NULL,
  text_html MEDIUMTEXT,
  footnotes MEDIUMTEXT,
  contents MEDIUMTEXT
);

CREATE TABLE orphus_comments
(
  chapter_id       INT(11)      NOT NULL,
  paragraph        INT(11)      NOT NULL,
  start_offset     INT(11)      NOT NULL,
  original_text    VARCHAR(255) NOT NULL,
  replacement_text VARCHAR(255) NOT NULL,
  optional_comment VARCHAR(255),
  user_id          INT(11),
  user_ip          VARCHAR(15),
  created_when     DATETIME     NOT NULL
);

CREATE TABLE projects
(
  project_id     INT(11) PRIMARY KEY AUTO_INCREMENT,
  parent_id      INT(11),
  image_id       INT(11),
  url            VARCHAR(32) UNIQUE,
  title          VARCHAR(1023) NOT NULL,
  name_jp        VARCHAR(255),
  name_en        VARCHAR(255),
  name_ru        VARCHAR(255),
  name_romaji    VARCHAR(255),
  author         VARCHAR(255),
  illustrator    VARCHAR(255),
  order_number   INT(11)       NOT NULL,
  banner_hidden  BOOL          NOT NULL,
  project_hidden BOOL          NOT NULL,
  onevolume      BOOL          NOT NULL,
  franchise      TEXT,
  annotation     TEXT
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
  name_file          VARCHAR(255) NOT NULL,
  name_title         VARCHAR(255) NOT NULL,
  name_jp            VARCHAR(255),
  name_en            VARCHAR(255),
  name_ru            VARCHAR(255),
  name_romaji        VARCHAR(255),
  name_short         VARCHAR(64),
  sequence_number    FLOAT,
  author             VARCHAR(255),
  illustrator        VARCHAR(255),
  original_design    VARCHAR(255), -- по заказу малфа, для спиноф серий с другим иллюстратором
  release_date       DATE,
  ISBN               VARCHAR(16),
  external_url       VARCHAR(255),
  volume_type        ENUM('Ранобэ',
                          'Побочные истории',
                          'Авторские додзинси',
                          'Другое')      NOT NULL DEFAULT 'Ранобэ',
  volume_status      ENUM('hidden',
                          'auto',
                          -- сторонний
                          'external_dropped',
                          'external_active',
                          'external_done',
                          -- не в работе
                          'no_eng',
                          'freeze',
                          'on_hold',
                          'queue',
                          -- в работе
                          'ongoing',
                          'translating',
                          'proofread',
                          -- опубликован
                          'decor',
                          'done')        NOT NULL DEFAULT 'on_hold',
  volume_status_hint VARCHAR(255),
  adult              BOOL                NOT NULL,
  annotation         TEXT
);

CREATE TABLE chapters
(
  chapter_id   INT(11) PRIMARY KEY AUTO_INCREMENT,
  volume_id    INT(11)       NOT NULL,
  text_id      INT(11),
  url          VARCHAR(32) UNIQUE,
  title        VARCHAR(1023) NOT NULL,
  order_number INT(11)       NOT NULL,
  published    BOOL          NOT NULL,
  nested       BOOL          NOT NULL
);

CREATE TABLE chapter_images
(
  chapter_image_id INT(11) PRIMARY KEY AUTO_INCREMENT,
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
  user_id       INT(11)             NOT NULL,
  mime_type     VARCHAR(255)        NOT NULL,
  url           VARCHAR(255) UNIQUE NOT NULL,
  title         VARCHAR(255),
  uploaded_when DATETIME            NOT NULL
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
  nikname   VARCHAR(64) UNIQUE NOT NULL,
  active    BOOLEAN            NOT NULL
);

CREATE TABLE volume_release_activities
(
  release_activity_id INT(11) PRIMARY KEY AUTO_INCREMENT,
  volume_id           INT(11) NOT NULL,
  activity_id         INT(11) NOT NULL,
  member_id           INT(11) NOT NULL,
  team_hidden         BOOLEAN NOT NULL
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
  description VARCHAR(255)
);

ALTER TABLE chapter_images ADD CONSTRAINT fk_colored_image_id FOREIGN KEY (colored_image_id) REFERENCES external_resources (resource_id);

ALTER TABLE chapter_images ADD CONSTRAINT fk_non_colored_image_id FOREIGN KEY (non_colored_image_id) REFERENCES external_resources (resource_id);

ALTER TABLE chapter_images ADD CONSTRAINT fk_volume_id FOREIGN KEY (volume_id) REFERENCES volumes (volume_id) ON DELETE CASCADE;

ALTER TABLE chapter_images ADD CONSTRAINT fk_chapter_id FOREIGN KEY (chapter_id) REFERENCES chapters (chapter_id);


ALTER TABLE external_resources ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (user_id);


ALTER TABLE chapters ADD CONSTRAINT fk_volume_id2 FOREIGN KEY (volume_id) REFERENCES volumes (volume_id) ON DELETE CASCADE;

ALTER TABLE chapters ADD CONSTRAINT fk_text_id FOREIGN KEY (text_id) REFERENCES texts (text_id);


ALTER TABLE volumes ADD CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES projects (project_id);

ALTER TABLE volumes ADD CONSTRAINT fk_image_one FOREIGN KEY (image_one) REFERENCES external_resources (resource_id);
ALTER TABLE volumes ADD CONSTRAINT fk_image_two FOREIGN KEY (image_two) REFERENCES external_resources (resource_id);
ALTER TABLE volumes ADD CONSTRAINT fk_image_three FOREIGN KEY (image_three) REFERENCES external_resources (resource_id);
ALTER TABLE volumes ADD CONSTRAINT fk_image_four FOREIGN KEY (image_four) REFERENCES external_resources (resource_id);


ALTER TABLE projects ADD CONSTRAINT fk_parent_id FOREIGN KEY (parent_id) REFERENCES projects (project_id);

ALTER TABLE projects ADD CONSTRAINT fk_image_id FOREIGN KEY (image_id) REFERENCES external_resources (resource_id);


ALTER TABLE orphus_comments ADD CONSTRAINT fk_chapter_id2 FOREIGN KEY (chapter_id) REFERENCES chapters (chapter_id);

ALTER TABLE orphus_comments ADD CONSTRAINT fk_user_id2 FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE orphus_comments ADD PRIMARY KEY (chapter_id, paragraph, start_offset, original_text, replacement_text);


ALTER TABLE updates ADD CONSTRAINT fk_u_project_id FOREIGN KEY (project_id) REFERENCES projects (project_id);

ALTER TABLE updates ADD CONSTRAINT fk_u_volume_id FOREIGN KEY (volume_id) REFERENCES volumes (volume_id);

ALTER TABLE updates ADD CONSTRAINT fk_u_chapter_id FOREIGN KEY (chapter_id) REFERENCES chapters (chapter_id);


ALTER TABLE team_members ADD CONSTRAINT fk_team_id FOREIGN KEY (team_id) REFERENCES teams (team_id);

ALTER TABLE team_members ADD CONSTRAINT fk_user_id3 FOREIGN KEY (user_id) REFERENCES users (user_id);


ALTER TABLE volume_release_activities ADD CONSTRAINT fk_ra_volume_id FOREIGN KEY (volume_id) REFERENCES volumes (volume_id) ON DELETE CASCADE;

ALTER TABLE volume_release_activities ADD CONSTRAINT fk_member_id FOREIGN KEY (member_id) REFERENCES team_members (member_id);

ALTER TABLE volume_release_activities ADD CONSTRAINT fk_activity_id FOREIGN KEY (activity_id) REFERENCES volume_activities (activity_id);

ALTER TABLE updates ADD INDEX (project_id);

ALTER TABLE updates ADD INDEX (project_id, show_time);

ALTER TABLE updates ADD INDEX (show_time);

ALTER TABLE updates ADD INDEX (update_type);

ALTER TABLE updates ADD INDEX (update_type, show_time);


/* Insert data for testing. Only for development purposes. */
/*
insert into projects (project_id, parent_id, url, title, order_number, banner_hidden, project_hidden, annotation)
values (1, null, 'mknr', 'mahouka', 1, 0, 0, 'Какая-то глупая аннотация');

insert into volumes (volume_id, project_id, url, name_file, name_title, name_jp, name_en, name_ru, name_short, order_number, author, illustrator, release_date, ISBN, external_url, annotation)
values(1, 1, 'mknr/v1', 'name_file', 'name_title', '魔法科高校の劣等生', 'mahouka bla bla', 'Махока', 'name_short', 1, 'Keiko', 'Keiko', sysdate(), '978-4048705974', null, 'еще одна глупая аннотация');

insert into texts(text_id, text_wiki, text_html)
values(1,'==Глава 0==

Магия.

Это не выдумка и не сказка, а реальная технология, долгое время незнакомая людям.

Первый зафиксированный случай применения магии произошел в 1999 году.

Инцидент, в котором офицеры полиции с помощью специальных сил остановили теракт, спланированный группой фанатиков, которая пыталась использовать ядерное оружие для исполнения пророчества об уничтожении человечества, стал первым зафиксированным случаем использования магии в современной истории.

Первоначально эти необычные способности назывались «Сверхъестественной силой». Наличие этой силы у человека объяснялось наследственностью или внезапной мутацией, которую невозможно вызвать искусственно и в дальнейшем сделать массовой.

Но это было ошибкой.

Исследования «Сверхъестественной силы» как восточными, так и западными влиятельными странами,  выявили существование людей, наделенных «Магией». Поэтому стало возможным воспроизвести «Сверхъестественную силу» посредством «Магии».

Конечно, для этого нужен талант. Только те, кто имеет хорошие способности с самого рождения, могут достичь мастерства, которое поставит их на один уровень с теми, кто обладает способностями в искусстве или науках.

Сверхъестественная сила была систематизирована посредством магии, а магия стала техническим навыком. «Пользователи сверхъестественных сил» стали называться «Операторами магии».

Опытные Операторы магии, способные подавить даже ядерное вооружение, — это мощнейшее оружие страны.

В конце 21-го столетия — в 2095 году, различные страны мира, далекие от объединения, были втянуты в гонку по обучению Операторов магии (Волшебников).



Отделение Национального университета магии, Первая старшая школа.

Высшее магическое учреждение, известное тем, что из него ежегодно выпускается наибольшее количество учеников поступающих в Государственный университет магии.

В тоже время это элитная школа, которая выпускает множество первоклассных Операторов магии (Волшебников).

Что касается магического образования, нет официальной позиции по обеспечению равных возможностей по обучению.

Страна не может позволить себе такой роскоши.

Более того, детские идеалистические обсуждения явного неравенства между одаренными и бесталанными не допускаются.

Обучаются лишь одаренные.

И только самые перспективные ученики.

Таков мир магии.

В этой элитной школе, ученики с самого зачисления разделены на преуспевающих и неуспевающих.

Даже если это всего лишь двое только что поступивших, они не обязательно равны.

Даже если они родные брат и сестра.', null);

insert into chapters (chapter_id, volume_id, text_id, url, title, order_number, published, nested)
values (1,1,1,'ch1', 'Неожиданная глава', 1, 1, 1);*/