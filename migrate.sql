INSERT ignore INTO `ruranobe`.`projects`(`project_id`, `parent_id`, `url`, `title`, `order_number`)
SELECT `series_id`, `parent_id`, `name_url`, `title`,`series_id` FROM `ruranobe_db`.`main_series` f WHERE `parent_id` is null;

INSERT ignore INTO `ruranobe`.`projects`(`project_id`, `parent_id`, `url`, `title`, `order_number`, `banner_hidden`, `project_hidden`)
SELECT `series_id`, `parent_id`, null, `title`,`series_id`,1,1 FROM `ruranobe_db`.`main_series` f
WHERE parent_id is not null and exists (select * from `ruranobe`.`projects` where `project_id`=f.parent_id);

INSERT INTO `ruranobe`.`projects`(`parent_id`, `url`, `title`, `name_jp`, `name_en`, `name_ru`, `order_number`, `franchise`, `annotation`, `onevolume`) 
SELECT null, `name_url`, `name_title`, `name_jp`, `name_en`, `name_ru`, `release_id`,"* 1 том", `annotation`, 1 FROM `ruranobe_db`.`main_releases` where `series_id` is null;

INSERT INTO `ruranobe`.`volumes`(`volume_id`, `project_id`, `url`, `name_file`, `name_title`, `name_jp`, `name_en`, `name_ru`,
                                 `name_short`,  `sequence_number`, `author`, `illustrator`, `release_date`, `ISBN`, `external_url`, `annotation`, `volume_type`, `volume_status`, adult)
SELECT `release_id`, ifnull(`series_id`, (select project_id from `ruranobe`.`projects` where `url`=`name_url`)), if(`series_id` is not null,`name_url`,concat(`name_url`,'/v1')), 
       `name_main`, `name_title`, `name_jp`, `name_en`, `name_ru`, `name_short`, `series_num`, `autor`, `illustrator`, `date`, `ISBN`, `external_url`, `annotation`
       , CASE `type`
            when 'ranobe_vol' then 1
            when 'side_story' then 2
            when 'doujinshi' then 3
            when 'doujinshi_ss' then 3
            when 'manga' then 0
            when 'materials' then 4
         end
       , CASE `status`
            when 'hidden' then 1
            when 'announced' then 6
            when 'not_translating' then 7
            when 'external' then 4
            when 'wait_translator' then 9
            when 'wait_eng' then 6
            when 'freezed_translator' then 7
            when 'freezed_eng' then 6
            when 'ongoing' then 10
            when 'translating' then 11
            when 'proofreading' then 12
            when 'decor' then 13
            when 'done' then 14
         end, 0 FROM `ruranobe_db`.`main_releases`;

INSERT INTO `ruranobe`.`chapters`(`chapter_id`, `volume_id`, `text_id`, `url`, `title`, `order_number`, `published`, `nested`)
SELECT `chapter_id`, `release_id`, null, concat(`name_url`, '/',`url`), `title`, `order`, 0, `level`>1
FROM `ruranobe_db`.`main_chapter` inner join `ruranobe_db`.`main_releases` using(`release_id`);

INSERT INTO `ruranobe`.`updates`(`project_id`, `volume_id`, `chapter_id`, `update_type`, `show_time`, `description`)
SELECT ifnull(`series_id`, (select project_id from `ruranobe`.`projects` where `url`=`name_url`)), `release_id`, `chapter_id`
	   , CASE u.`type`
            when 'translate' then 1
            when 'proofread' then 3
            when 'edit' then 4
            when 'other' then 1
         end, u.`date`, `text` 
FROM `ruranobe_db`.`main_last_updates` u inner join `ruranobe_db`.`main_releases` using(`release_id`);


INSERT INTO `ruranobe`.`teams`(`team_id`, `team_name`, `team_website_link`)
SELECT `command_id`, `title`, `link` FROM `ruranobe_db`.`main_comands`;

INSERT INTO `ruranobe`.`volume_activities`(`activity_id`, `activity_name`, `activity_type`)
SELECT `job_id`, `title`, if(`job_id` between 3 and 7,'image','text') FROM `ruranobe_db`.`main_jobs`;

INSERT IGNORE INTO `ruranobe`.`users`(`user_id`, `username`, `realname`, `pass`, `email`, `email_activated`, `registration_date`, `adult`) 
SELECT `user_id`, `user_name`, `user_real_name`, `user_password`, `user_email`, `user_email_authenticated`, `user_registration`, 1 FROM `ruranobe_db`.`mw_user`;

INSERT INTO `ruranobe`.`members`(`member_id`, `user_id`, `team_id`, `nikname`, `active`)
SELECT `worker_id`, `user_id`, `command_id`, `nikname`, `active` FROM `ruranobe_db`.`main_workers`;

INSERT INTO `ruranobe`.`volume_release_activities`(`volume_id`, `activity_id`, `member_id`, `team_hidden`)
SELECT `release_id`, `job_id`, `worker_id`, !`show_command` FROM `ruranobe_db`.`main_release_workers`;








create temporary table prj_text(prj integer, txt integer);

insert into prj_text
SELECT distinct series_id, old_id
FROM `ruranobe_db`.`main_series` s
inner join `ruranobe_db`.mw_page p on page_title=`name_url`
inner join `ruranobe_db`.mw_revision rv on rev_page=page_id
inner join `ruranobe_db`.mw_text t on old_id=rev_text_id
WHERE rev_timestamp=(select max(rev_timestamp) from `ruranobe_db`.mw_revision where rev_page=p.page_id) and parent_id is null;

UPDATE `ruranobe`.`projects`
INNER JOIN `prj_text` on prj=project_id
INNER JOIN `ruranobe_db`.`mw_text` on txt=old_id
SET `annotation`=trim(substring_index(old_text,'\n\n''''''',1))
where old_text like '%\n\n''''''%';

UPDATE `ruranobe`.`projects`
INNER JOIN `prj_text` on prj=project_id
INNER JOIN `ruranobe_db`.`mw_text` on txt=old_id
SET `author`=trim(substring_index(substring_index(old_text,'Автор: ''''''',-1),'''''''',1))
where old_text like '%Автор: ''''''%';

UPDATE `ruranobe`.`projects`
INNER JOIN `prj_text` on prj=project_id
INNER JOIN `ruranobe_db`.`mw_text` on txt=old_id
SET `illustrator`=trim(substring_index(substring_index(old_text,'Иллюстратор: ''''''',-1),'''''''',1))
where old_text like '%Иллюстратор: ''''''%';

UPDATE `ruranobe`.`projects`
INNER JOIN `prj_text` on prj=project_id
INNER JOIN `ruranobe_db`.`mw_text` on txt=old_id
SET `franchise`=trim('\n' from substring_index(substring_index(trim(LEADING '''' from trim(substring_index(old_text,'Франшиза:',-1))),'==',1),'{{',1))
where old_text like '%Франшиза:%';

UPDATE ruranobe.projects SET name_jp='ソードアート・オンライン', name_en='Sword Art Online', name_ru=null WHERE project_id = 1;
UPDATE ruranobe.projects SET name_jp='ログ・ホライズン', name_en='Log Horizon', name_ru=null WHERE project_id = 3;
UPDATE ruranobe.projects SET name_jp='狼と香辛料', name_en='Spice and Wolf', name_ru='Волчица и пряности' WHERE project_id = 7;
UPDATE ruranobe.projects SET name_jp='デュラララ!!', name_en='Durarara!!', name_ru='Дюрарара!!' WHERE project_id = 8;
UPDATE ruranobe.projects SET name_jp='魔法科高校の劣等生', name_en='Mahouka Koukou no Rettousei', name_ru='Непутевый ученик в школе магии' WHERE project_id = 9;
UPDATE ruranobe.projects SET name_jp='ヘヴィーオブジェクト', name_en='Heavy Object', name_ru='Тяжелый Объект' WHERE project_id = 11;
UPDATE ruranobe.projects SET name_jp='アクセル・ワールド', name_en='Accel World', name_ru='Ускоренный мир' WHERE project_id = 12;
UPDATE ruranobe.projects SET name_jp='終わりのクロニクル', name_en='Owari no Chronicle', name_ru='Хроники конца света' WHERE project_id = 13;
UPDATE ruranobe.projects SET name_jp='中二病でも恋がしたい!', name_en=null, name_ru='У неё синдром восьмиклассника, но я всё равно хочу любить её!' WHERE project_id = 14;
UPDATE ruranobe.projects SET name_jp='オール・ユー・ニード・イズ・キル', name_en='All You Need Is Kill', name_ru='Тебе Лишь Нужно Убивать' WHERE project_id = 15;
UPDATE ruranobe.projects SET name_jp='ノーゲーム．ノーライフ', name_en='No Game No Life', name_ru=null WHERE project_id = 17;
UPDATE ruranobe.projects SET name_jp='さまよう神姫の剣使徒', name_en='The Wandering Goddess'' Duelist', name_ru='Дуэлянт странствующей богини' WHERE project_id = 18;
UPDATE ruranobe.projects SET name_jp='東京レイヴンズ', name_en='Tokyo Ravens', name_ru='Токийские вороны' WHERE project_id = 24;
UPDATE ruranobe.projects SET name_jp='とらドラ！', name_en='ToraDora!', name_ru=null WHERE project_id = 25;
UPDATE ruranobe.projects SET name_jp='やはり俺の青春ラブコメはまちがっている', name_en=null, name_ru=null WHERE project_id = 26;
UPDATE ruranobe.projects SET name_jp='エロマンガ先生', name_en='EroManga Sensei', name_ru='Эроманга-сэнсэй' WHERE project_id = 27;
UPDATE ruranobe.projects SET name_jp='問題児たちが異世界から来るそうですよ?', name_en='Problem Children are Coming from Another World, Aren''t They?', name_ru='Проблемные дети приходят из другого мира, верно?' WHERE project_id = 28;
UPDATE ruranobe.projects SET name_jp='とある魔術の禁書目録', name_en='Toaru Majutsu no Index', name_ru='Некий Магический Индекс' WHERE project_id = 29;
UPDATE ruranobe.projects SET name_jp='魔弾の王と戦姫', name_rj='Madan no Ou to Vanadis', name_ru=null WHERE project_id = 33;
UPDATE ruranobe.projects SET name_jp='盾の勇者の成り上がり', name_rj='Tate no Yuusha no Nariagari', name_ru='Становление Героя Щита' WHERE project_id = 34;
UPDATE ruranobe.projects SET name_jp='棺姫のチャイカ', name_en='Chaika - The Coffin Princess', name_ru='Чайка − принцесса с гробом' WHERE project_id = 35;
UPDATE ruranobe.projects SET name_jp='マグダラで眠れ', name_en='May your soul rest in Magdala', name_ru='Пусть твоя душа упокоится в Магдале' WHERE project_id = 37;
UPDATE ruranobe.projects SET name_jp='さくら荘のペットな彼女', name_rj='Sakurasou no Pet na Kanojo', name_ru='Кошечка из Сакурасо' WHERE project_id = 38;
UPDATE ruranobe.projects SET name_jp='オーバーロード', name_en='Overlord', name_ru='Властелин' WHERE project_id = 39;
UPDATE ruranobe.projects SET name_jp='レンタルマギカ', name_en='Rental Magica', name_ru=null WHERE project_id = 43;
UPDATE ruranobe.projects SET name_jp='黙示録アリス', name_en='Apocalypse Alice', name_ru=null WHERE project_id = 44;
UPDATE ruranobe.projects SET name_jp='ダンジョンに出会いを求めるのは間違っているだろうか', name_rj='Dungeon ni Deai wo Motomeru no wa Machigatteiru Darou ka', name_ru=null WHERE project_id = 45;
UPDATE ruranobe.projects SET name_jp='新妹魔王の契約者（テスタメント）', name_rj='Shinmai Maou no Tesutamento', name_ru=null WHERE project_id = 46;
UPDATE ruranobe.projects SET name_jp='ハイスクールD×D', name_en='High School D×D', name_ru=null WHERE project_id = 48;
UPDATE ruranobe.projects SET name_jp='世界の終わりの世界録', name_rj='Sekai no Owari no Encore', name_ru=null WHERE project_id = 49;






create temporary table ch_text(ch integer, txt integer);

insert into ch_text
SELECT distinct chapter_id, old_id
FROM `ruranobe_db`.`main_releases` r
inner join `ruranobe_db`.main_chapter c using (release_id)
inner join `ruranobe_db`.mw_page p on page_title=concat(r.`name_url`, '/',c.`url`)
inner join `ruranobe_db`.mw_revision rv on rev_page=page_id
inner join `ruranobe_db`.mw_text t on old_id=rev_text_id
WHERE rev_timestamp=(select max(rev_timestamp) from `ruranobe_db`.mw_revision where rev_page=p.page_id) and length(trim(old_text))>0;

INSERT ignore INTO `ruranobe`.`texts`(`text_id`, `text_wiki`, `text_html`)
SELECT `old_id`, trim(`old_text`), '' FROM `ruranobe_db`.`mw_text` inner join ch_text on old_id=txt;

UPDATE `ruranobe`.`chapters`
INNER JOIN `ch_text` on `ch`=`chapter_id`
SET `text_id`=`txt`, `published`=1;