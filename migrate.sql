INSERT ignore INTO `ruranobe`.`projects`(`project_id`, `parent_id`, `url`, `title`, `order_number`, `is_banner_hidden`, `is_project_hidden`, `annotation`) 
SELECT `series_id`, `parent_id`, `name_url`, `title`,`series_id`,0,0,'' FROM `ruranobe_db`.`main_series` f WHERE parent_id is null;

INSERT ignore INTO `ruranobe`.`projects`(`project_id`, `parent_id`, `url`, `title`, `order_number`, `is_banner_hidden`, `is_project_hidden`, `annotation`) 
SELECT `series_id`, `parent_id`, null, `title`,`series_id`,0,0,'' FROM `ruranobe_db`.`main_series` f WHERE parent_id is not null and exists (select * from `ruranobe`.`projects` where project_id=f.parent_id);
INSERT ignore INTO `ruranobe`.`projects`(`project_id`, `parent_id`, `url`, `title`, `order_number`, `is_banner_hidden`, `is_project_hidden`, `annotation`) 
SELECT `series_id`, `parent_id`, null, `title`,`series_id`,0,0,'' FROM `ruranobe_db`.`main_series` f WHERE parent_id is not null and exists (select * from `ruranobe`.`projects` where project_id=f.parent_id);
INSERT ignore INTO `ruranobe`.`projects`(`project_id`, `parent_id`, `url`, `title`, `order_number`, `is_banner_hidden`, `is_project_hidden`, `annotation`) 
SELECT `series_id`, `parent_id`, null, `title`,`series_id`,0,0,'' FROM `ruranobe_db`.`main_series` f WHERE parent_id is not null and exists (select * from `ruranobe`.`projects` where project_id=f.parent_id);
INSERT ignore INTO `ruranobe`.`projects`(`project_id`, `parent_id`, `url`, `title`, `order_number`, `is_banner_hidden`, `is_project_hidden`, `annotation`) 
SELECT `series_id`, `parent_id`, null, `title`,`series_id`,0,0,'' FROM `ruranobe_db`.`main_series` f WHERE parent_id is not null and exists (select * from `ruranobe`.`projects` where project_id=f.parent_id);

INSERT INTO `ruranobe`.`volumes`(`volume_id`, `project_id`, `url`, `name_file`, `name_title`, `name_jp`, `name_en`, `name_ru`, `name_short`, `order_number`, `author`, `illustrator`, `release_date`, `ISBN`, `external_url`, `annotation`, `type`, `status`) 
SELECT `release_id`, `series_id`, `name_url`, `name_main`, `name_title`, `name_jp`, `name_en`, `name_ru`, `name_short`, `series_num`, `autor`, `illustrator`, `date`, `ISBN`, `external_url`, `annotation`, `type`, 1 FROM `ruranobe_db`.`main_releases`;

INSERT INTO `ruranobe`.`chapters`(`chapter_id`, `volume_id`, `text_id`, `url`, `title`, `order_number`, `published`, `nested`)
SELECT `chapter_id`, `release_id`, null, `url`, `title`, `order`, 1, `level`>1 FROM `ruranobe_db`.`main_chapter`;

create temporary table ch_text(ch integer, txt integer);
insert into ch_text
SELECT distinct chapter_id, old_id 
FROM `ruranobe_db`.`main_releases` r 
inner join `ruranobe_db`.main_chapter c using (release_id)
inner join `ruranobe_db`.mw_page p on page_title=concat(r.`name_url`, '/',c.`url`)
inner join `ruranobe_db`.mw_revision rv on rev_page=page_id
inner join `ruranobe_db`.mw_text t on old_id=rev_text_id
WHERE rev_timestamp=(select max(rev_timestamp) from `ruranobe_db`.mw_revision where rev_page=p.page_id);
INSERT ignore INTO `ruranobe`.`texts`(`text_id`, `text_wiki`, `text_html`)
SELECT `old_id`, `old_text`, '' FROM `ruranobe_db`.`mw_text` 
inner join ch_text on old_id=txt;
UPDATE `ruranobe`.`chapters` SET `text_id`=(select txt from ch_text where ch=chapter_id);