drop table orphus_comments;
drop table chapter_images;
drop table external_resources;
drop table chapters;
drop table volumes;
drop table projects;
drop table texts;
drop table users;

create table users
(
  user_id int(11) primary key auto_increment,
  username varchar(64),
  realname varchar(255),
  pass varchar(32),
  pass_recovery_token varchar(255),
  pass_recovery_token_date datetime,
  email varchar(255),
  email_token varchar(255),
  email_token_date datetime,
  email_activated bool,
  registration_date datetime
);

create table texts
(
  text_id int(11) primary key auto_increment,
  text_wiki mediumtext,
  text_html mediumtext
);

create table orphus_comments
(
   chapter_id int(11),
   paragraph int(11),
   start_offset int(11),
   original_text varchar(255),
   replacement_text varchar(255),
   created_when datetime
);

create table projects
(
  project_id int(11) primary key auto_increment,
  parent_id int(11),
  url varchar(16),
  title varchar(1023),
  order_number int(11),
  banner_hidden bool,
  project_hidden bool,
  annotation text
);

create table volumes
(
  volume_id int(11) primary key auto_increment,
  project_id int(11),
  url varchar(16),
  name_file varchar(255),
  name_title varchar(255),
  name_jp varchar(255),
  name_en varchar(255),
  name_ru varchar(255),
  name_short varchar(64),
  order_number int(11),
  author varchar(255),
  illustrator varchar(255),
  release_date date,
  ISBN varchar(16),
  external_url varchar(255),
  annotation text
);

create table chapters
(
  chapter_id int(11) primary key auto_increment,
  volume_id int(11),
  text_id int(11),
  url varchar(16),
  title varchar(1023),
  order_number int(11),
  published bool,
  nested bool
);

create table chapter_images
(
  chapter_id int(11),
  volume_id int(11),
--  image_id int(11),
  non_colored_image_id int(11), 
  colored_image_id int(11),
  order_number int(11)
);

create table external_resources
(
  resource_id int(11) primary key auto_increment,
  user_id int(11),
  mime_type varchar(255),
  url varchar(255),
  title varchar(255),
  uploaded_when datetime
);

alter table chapter_images add constraint fk_colored_image_id foreign key (colored_image_id) references external_resources (resource_id);
alter table chapter_images add constraint fk_non_colored_image_id foreign key (non_colored_image_id) references external_resources (resource_id);
alter table chapter_images add constraint fk_volume_id foreign key (volume_id) references volumes (volume_id);
alter table chapter_images add constraint fk_chapter_id foreign key (chapter_id) references chapters (chapter_id);
alter table external_resources add constraint fk_user_id foreign key (user_id) references users (user_id);
alter table chapters add constraint fk_volume_id2 foreign key (volume_id) references volumes (volume_id);
alter table chapters add constraint fk_text_id foreign key (text_id) references texts (text_id);
alter table volumes add constraint fk_project_id foreign key (project_id) references projects (project_id);
alter table projects add constraint fk_parent_id foreign key (parent_id) references projects (project_id);
alter table orphus_comments add constraint fk_chapter_id2 foreign key (chapter_id) references chapters (chapter_id);
alter table orphus_comments add primary key (chapter_id, paragraph, start_offset, original_text, replacement_text);

alter table chapters add constraint chapter_unique_url UNIQUE (url);
alter table volumes add constraint volume_unique_url UNIQUE (url);
alter table projects add constraint prject_unique_url UNIQUE (url);



/* Insert data for testing. Only for development purposes. */
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
values (1,1,1,'ch1', 'Неожиданная глава', 1, 1, 1);