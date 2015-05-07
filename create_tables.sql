# use ruranobe;

drop table if exists orphus_comments;
drop table if exists chapter_images;
drop table if exists updates;
drop table if exists chapters;
drop table if exists volume_release_activities;
drop table if exists volumes;
drop table if exists projects;
drop table if exists external_resources;
drop table if exists texts;
drop table if exists team_members;
drop table if exists users;
drop table if exists teams;
drop table if exists volume_activities;

create table users
(
  user_id int(11) PRIMARY KEY AUTO_INCREMENT,
  username varchar(64) UNIQUE NOT NULL,
  realname varchar(255),
  pass varchar(32) NOT NULL,
  pass_recovery_token varchar(255),
  pass_recovery_token_date datetime,
  email varchar(255),
  email_token varchar(255),
  email_token_date datetime,
  email_activated bool NOT NULL,
  registration_date datetime NOT NULL,
  adult bool NOT NULL
);

create table texts
(
  text_id int(11) primary key auto_increment,
  text_wiki mediumtext NOT NULL,
  text_html mediumtext NOT NULL
);

create table orphus_comments
(
   chapter_id int(11) NOT NULL,
   paragraph int(11) NOT NULL,
   start_offset int(11) NOT NULL,
   original_text varchar(255) NOT NULL,
   replacement_text varchar(255) NOT NULL,
   user_id int(11),
   user_ip varchar(15),
   created_when datetime NOT NULL
);

create table projects
(
  project_id int(11) primary key auto_increment,
  parent_id int(11),
  image_id int(11),
  url varchar(32) UNIQUE,
  title varchar(1023) NOT NULL,
  name_jp varchar(255),
  name_en varchar(255),
  name_ru varchar(255),
  name_romaji varchar(255),
  author varchar(255),
  illustrator varchar(255),
  order_number int(11) NOT NULL,
  banner_hidden bool NOT NULL,
  project_hidden bool NOT NULL,
  onevolume bool NOT NULL,
  franchise text,
  annotation text
);

create table volumes
(
  volume_id int(11) primary key auto_increment,
  project_id int(11) NOT NULL,
  image_one int(11),
  image_two int(11),
  image_three int(11),
  image_four int(11),
  url varchar(32) UNIQUE NOT NULL,
  name_file varchar(255),
  name_title varchar(255),
  name_jp varchar(255),
  name_en varchar(255),
  name_ru varchar(255),
  name_romaji varchar(255),
  name_short varchar(64),
  sequence_number float,
  author varchar(255),
  illustrator varchar(255),
  original_design VARCHAR(255), -- по заказу малфа, для спиноф серий с другим иллюстратором
  release_date date,
  ISBN varchar(16),
  external_url varchar(255),
  volume_type enum('Ранобэ','Побочные истории','Авторские додзинси','Другое') NOT NULL DEFAULT 'Ранобэ',
  volume_status enum('hidden','auto'
                    ,'external_dropped','external_active','external_done'
                    ,'no_eng','freeze','on_hold','queue'
                    ,'ongoing','translating','proofread'
                    ,'decor','done') NOT NULL DEFAULT 'onhold',
  volume_status_hint varchar(255),
  adult bool NOT NULL,
  annotation text
);

create table chapters
(
  chapter_id int(11) primary key auto_increment,
  volume_id int(11) NOT NULL,
  text_id int(11),
  url varchar(32) UNIQUE,
  title varchar(1023) NOT NULL,
  order_number int(11) NOT NULL,
  published bool NOT NULL,
  nested bool NOT NULL
);

create table chapter_images
(
  chapter_id int(11),
  volume_id int(11) NOT NULL,
--  image_id int(11), NOT NULL
  non_colored_image_id int(11) NOT NULL,
  colored_image_id int(11),
  order_number int(11) NOT NULL,
  adult bool NOT NULL
);

create table external_resources
(
  resource_id int(11) primary key auto_increment,
  user_id int(11) NOT NULL,
  mime_type varchar(255) NOT NULL,
  url varchar(255) UNIQUE NOT NULL,
  title varchar(255),
  uploaded_when datetime NOT NULL
);

create table volume_activities
(
  activity_id   int(11) primary key auto_increment,
  activity_name varchar(255) UNIQUE NOT NULL,
  activity_type enum('text', 'image') NOT NULL
);

create table teams
(
  team_id int(11) primary key auto_increment,
  team_name varchar(255) UNIQUE NOT NULL,
  team_website_link varchar(255)
);

create table team_members
(
  member_id  int(11) primary key auto_increment,
  user_id int(11),
  team_id int(11),
  nikname varchar(64) UNIQUE NOT NULL,
  active boolean NOT NULL
);

create table volume_release_activities
(
  release_activity_id  int(11) primary key auto_increment,
  volume_id            int(11) NOT NULL,
  activity_id          int(11) NOT NULL,
  member_id            int(11) NOT NULL,
  team_hidden          boolean NOT NULL
);

create table updates
(
  update_id int(11) primary key auto_increment,
  project_id int(11) NOT NULL,
  volume_id int(11) NOT NULL,
  chapter_id int(11),
  update_type enum('Опубликован','Обновлен перевод','Глобальная редактура','Обновление иллюстраций') NOT NULL,
  show_time datetime NOT NULL,
  description varchar(255)
);

alter table chapter_images add constraint fk_colored_image_id foreign key (colored_image_id) references external_resources (resource_id);

alter table chapter_images add constraint fk_non_colored_image_id foreign key (non_colored_image_id) references external_resources (resource_id);

alter table chapter_images add constraint fk_volume_id foreign key (volume_id) references volumes (volume_id);

alter table chapter_images add constraint fk_chapter_id foreign key (chapter_id) references chapters (chapter_id);


alter table external_resources add constraint fk_user_id foreign key (user_id) references users (user_id);


alter table chapters add constraint fk_volume_id2 foreign key (volume_id) references volumes (volume_id);

alter table chapters add constraint fk_text_id foreign key (text_id) references texts (text_id);


alter table volumes add constraint fk_project_id foreign key (project_id) references projects (project_id);

alter table volumes add constraint fk_image_one   foreign key (image_one)   references external_resources (resource_id);
alter table volumes add constraint fk_image_two   foreign key (image_two)   references external_resources (resource_id);
alter table volumes add constraint fk_image_three foreign key (image_three) references external_resources (resource_id);
alter table volumes add constraint fk_image_four  foreign key (image_four)  references external_resources (resource_id);


alter table projects add constraint fk_parent_id foreign key (parent_id) references projects (project_id);

alter table projects add constraint fk_image_id foreign key (image_id) references external_resources (resource_id);


alter table orphus_comments add constraint fk_chapter_id2 foreign key (chapter_id) references chapters (chapter_id);

alter table orphus_comments add constraint fk_user_id2 foreign key (user_id) references users (user_id);

alter table orphus_comments add primary key (chapter_id, paragraph, start_offset, original_text, replacement_text);


alter table updates add constraint fk_u_project_id foreign key (project_id) references projects (project_id);

alter table updates add constraint fk_u_volume_id foreign key (volume_id) references volumes (volume_id);

alter table updates add constraint fk_u_chapter_id foreign key (chapter_id) references chapters (chapter_id);


alter table team_members add constraint fk_team_id foreign key (team_id) references teams(team_id);

alter table team_members add constraint fk_user_id3 foreign key (user_id) references users (user_id);


alter table volume_release_activities add constraint fk_ra_volume_id foreign key (volume_id) references volumes(volume_id);

alter table volume_release_activities add constraint fk_member_id foreign key (member_id) references team_members(member_id);

alter table volume_release_activities add constraint fk_activity_id foreign key (activity_id) references volume_activities(activity_id);

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