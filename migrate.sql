# to ensure all image migrate correctly, run "hhvm maintenance/refreshLinks.php"

# use ruranobe;

SET NAMES UTF8;

INSERT IGNORE INTO projects (project_id, parent_id, url, title, order_number)
  SELECT
    series_id,
    parent_id,
    name_url,
    title,
    series_id
  FROM ruranobe_db.main_series f
  WHERE parent_id IS NULL;

INSERT IGNORE INTO projects (project_id, parent_id, url, title, order_number, banner_hidden, project_hidden)
  SELECT
    series_id,
    parent_id,
    NULL,
    title,
    series_id,
    1,
    1
  FROM ruranobe_db.main_series f
  WHERE parent_id IS NOT NULL
        AND exists(SELECT *
                   FROM projects
                   WHERE project_id = f.parent_id);

INSERT INTO projects (parent_id, url, title, name_jp, name_en, name_ru, order_number, franchise, annotation, onevolume)
  SELECT
    NULL,
    name_url,
    name_title,
    nullif(name_jp, ''),
    nullif(name_en, ''),
    nullif(name_ru, ''),
    release_id,
    '* 1 том',
    nullif(annotation, ''),
    1
  FROM ruranobe_db.main_releases
  WHERE series_id IS NULL;

INSERT INTO volumes (volume_id, project_id, url, name_file, name_title, name_jp, name_en, name_ru, name_short,
                     sequence_number, author, illustrator, release_date, ISBN, external_url, annotation, volume_type,
                     volume_status, adult)
  SELECT
    release_id,
    ifnull(series_id, (SELECT project_id
                       FROM projects
                       WHERE url = name_url)),
    if(series_id IS NOT NULL, name_url, concat(name_url, '/v1')),
    name_main,
    name_title,
    nullif(name_jp, ''),
    nullif(name_en, ''),
    nullif(name_ru, ''),
    nullif(name_short, ''),
    nullif(series_num, 0),
    nullif(autor, ''),
    nullif(illustrator, ''),
    nullif(date, ''),
    nullif(ISBN, ''),
    nullif(external_url, ''),
    nullif(annotation, ''),
    CASE type
    WHEN 'ranobe_vol'
      THEN 1
    WHEN 'side_story'
      THEN 2
    WHEN 'doujinshi'
      THEN 3
    WHEN 'doujinshi_ss'
      THEN 3
    WHEN 'manga'
      THEN 0
    WHEN 'materials'
      THEN 4
    END,
    CASE status
    WHEN 'hidden'
      THEN 5
    WHEN 'announced'
      THEN 1
    WHEN 'not_translating'
      THEN 2
    WHEN 'external'
      THEN 8
    WHEN 'wait_translator'
      THEN 4
    WHEN 'wait_eng'
      THEN 2
    WHEN 'freezed_translator'
      THEN 3
    WHEN 'freezed_eng'
      THEN 2
    WHEN 'ongoing'
      THEN 10
    WHEN 'translating'
      THEN 11
    WHEN 'proofreading'
      THEN 12
    WHEN 'decor'
      THEN 13
    WHEN 'done'
      THEN 14
    END,
    0
  FROM ruranobe_db.main_releases;

INSERT INTO chapters (chapter_id, volume_id, text_id, url, title, order_number, nested)
  SELECT
    chapter_id,
    release_id,
    NULL,
    concat(v.url, '/', nullif(ch.url, '')),
    title,
    `order`,
    level > 1
  FROM ruranobe_db.main_chapter ch
    INNER JOIN volumes v ON (volume_id = release_id);

INSERT INTO chapters (volume_id, text_id, url, title, order_number, nested)
  SELECT DISTINCT
    v.volume_id,
    NULL,
    page_title,
    'Текст',
    1,
    0
  FROM ruranobe_db.mw_page p
    INNER JOIN ruranobe_db.mw_revision rv ON rev_page = page_id
    INNER JOIN ruranobe_db.mw_text t ON old_id = rev_text_id
    INNER JOIN volumes v ON (substr(page_title, 1, length(page_title) - 5) = url)
  WHERE rev_timestamp = (SELECT max(rev_timestamp)
                         FROM ruranobe_db.mw_revision
                         WHERE rev_page = p.page_id)
        AND trim(old_text) != '{{Полный текст}}'
        AND page_title LIKE '%/text';

INSERT INTO updates (project_id, volume_id, chapter_id, update_type, show_time, description)
  SELECT
    coalesce((SELECT parent_id
              FROM projects
              WHERE series_id = project_id),
             series_id,
             (SELECT project_id
              FROM projects
              WHERE url = name_url)),
    release_id,
    chapter_id,
    CASE u.type
    WHEN 'translate'
      THEN 1
    WHEN 'proofread'
      THEN 3
    WHEN 'edit'
      THEN 4
    WHEN 'other'
      THEN 1
    END,
    u.date,
    text
  FROM ruranobe_db.main_last_updates u INNER JOIN ruranobe_db.main_releases USING (release_id);


INSERT INTO teams (team_id, team_name, team_website_link)
  SELECT
    command_id,
    title,
    link
  FROM ruranobe_db.main_comands;

INSERT INTO volume_activities (activity_id, activity_name, activity_type)
  SELECT
    job_id,
    title,
    if(job_id BETWEEN 3 AND 7, 'image', 'text')
  FROM ruranobe_db.main_jobs;

INSERT IGNORE INTO users (user_id, username, realname, pass, pass_version, email, email_activated, registration_date, converter_type, navigation_type, convert_with_imgs, adult, prefer_colored_imgs, convert_imgs_size)
  SELECT
    user_id,
    user_name,
    user_real_name,
    user_password,
    0,
    user_email,
    user_email_authenticated,
    user_registration,
    'fb2',
    'Главам',
    1,
    1,
    1,
    1080
  FROM ruranobe_db.mw_user;

UPDATE users usr INNER JOIN ruranobe_db.mw_user mw ON usr.user_id = mw.user_id
SET
  usr.pass = SUBSTRING(mw.user_password, 4),
  usr.pass_version = 1
WHERE mw.user_password LIKE ":A:%";

UPDATE users usr
  INNER JOIN
  ruranobe_db.mw_user mw
    ON
      usr.user_id = mw.user_id
SET
  usr.pass = SUBSTRING(mw.user_password, 4),
  usr.pass_version = 2
WHERE
  mw.user_password LIKE ":B:%";

UPDATE users usr
  INNER JOIN
  ruranobe_db.mw_user mw
    ON
      usr.user_id = mw.user_id
SET
  usr.pass = SUBSTRING(mw.user_password, 9),
  usr.pass_version = 3
WHERE
  mw.user_password LIKE ":pbkdf2:%";

INSERT INTO user_groups
  SELECT
    ug_user,
    1
  FROM ruranobe_db.mw_user_groups
  WHERE `ug_group` IN ('bureaucrat', 'sysop');

INSERT INTO user_groups
  SELECT
    ug_user,
    2
  FROM ruranobe_db.mw_user_groups
  WHERE `ug_group` IN ('bureaucrat', 'sysop', 'proofreader');

INSERT INTO team_members (member_id, user_id, team_id, nickname, active)
  SELECT
    worker_id,
    user_id,
    command_id,
    nikname,
    active
  FROM ruranobe_db.main_workers;

INSERT INTO volume_release_activities (volume_id, activity_id, member_id, team_hidden)
  SELECT
    release_id,
    job_id,
    worker_id,
    !show_command
  FROM ruranobe_db.main_release_workers;


CREATE TEMPORARY TABLE prj_text (
  prj INTEGER,
  pg  INTEGER,
  txt INTEGER
);

INSERT INTO prj_text
  SELECT DISTINCT
    series_id,
    page_id,
    old_id
  FROM ruranobe_db.main_series s
    INNER JOIN ruranobe_db.mw_page p ON page_title = name_url
    INNER JOIN ruranobe_db.mw_revision rv ON rev_page = page_id
    INNER JOIN ruranobe_db.mw_text t ON old_id = rev_text_id
  WHERE rev_timestamp = (SELECT max(rev_timestamp)
                         FROM ruranobe_db.mw_revision
                         WHERE rev_page = p.page_id)
        AND parent_id IS NULL;

UPDATE projects
  INNER JOIN prj_text ON prj = project_id
  INNER JOIN ruranobe_db.mw_text ON txt = old_id
SET annotation = trim(substring_index(old_text, '\n\n''''''', 1))
WHERE old_text LIKE '%\n\n''''''%';

UPDATE projects
  INNER JOIN prj_text ON prj = project_id
  INNER JOIN ruranobe_db.mw_text ON txt = old_id
SET author = trim(substring_index(substring_index(old_text, 'Автор: ''''''', -1), '''''''', 1))
WHERE old_text LIKE '%Автор: ''''''%';

UPDATE projects
  INNER JOIN prj_text ON prj = project_id
  INNER JOIN ruranobe_db.mw_text ON txt = old_id
SET illustrator = trim(substring_index(substring_index(old_text, 'Иллюстратор: ''''''', -1), '''''''', 1))
WHERE old_text LIKE '%Иллюстратор: ''''''%';

UPDATE projects
  INNER JOIN prj_text ON prj = project_id
  INNER JOIN ruranobe_db.mw_text ON txt = old_id
SET issue_status = trim(substring_index(substring_index(old_text, 'Статус: ''''''Выпуск ', -1), ', перевод', 1))
WHERE old_text LIKE '%Статус: ''''''Выпуск %';

UPDATE projects
  INNER JOIN prj_text ON prj = project_id
  INNER JOIN ruranobe_db.mw_text ON txt = old_id
SET translation_status = trim(substring_index(substring_index(old_text, ', перевод ', -1), '''''''', 1))
WHERE old_text LIKE '%Статус:%, перевод %';

UPDATE projects
  INNER JOIN prj_text ON prj = project_id
  INNER JOIN ruranobe_db.mw_text ON txt = old_id
SET franchise = trim('\n' FROM substring_index(
    substring_index(trim(LEADING '''' FROM trim(substring_index(old_text, 'Франшиза:', -1))), '==', 1), '{{', 1))
WHERE old_text LIKE '%Франшиза:%';

UPDATE projects
SET order_number = instr((SELECT old_text
                          FROM ruranobe_db.mw_revision
                            INNER JOIN ruranobe_db.mw_text ON old_id = rev_text_id
                          WHERE rev_page = 7460
                          ORDER BY rev_timestamp DESC
                          LIMIT 1), concat('|', url, '|'))
WHERE parent_id IS NULL;

UPDATE projects
SET order_number = 9999, project_hidden = TRUE, banner_hidden = TRUE
WHERE order_number = 0;

DROP TEMPORARY TABLE prj_text;

UPDATE projects
SET name_jp = 'ソードアート・オンライン', name_en = 'Sword Art Online', name_ru = NULL
WHERE project_id = 1;
UPDATE projects
SET name_jp = 'ログ・ホライズン', name_en = 'Log Horizon', name_ru = NULL
WHERE project_id = 3;
UPDATE projects
SET name_jp = '狼と香辛料', name_en = 'Spice and Wolf', name_ru = 'Волчица и пряности'
WHERE project_id = 7;
UPDATE projects
SET name_jp = 'デュラララ!!', name_en = 'Durarara!!', name_ru = 'Дюрарара!!'
WHERE project_id = 8;
UPDATE projects
SET name_jp = '魔法科高校の劣等生', name_en = 'Mahouka Koukou no Rettousei',
  name_ru   = 'Непутевый ученик в школе магии'
WHERE project_id = 9;
UPDATE projects
SET name_jp = 'ヘヴィーオブジェクト', name_en = 'Heavy Object', name_ru = 'Тяжелый Объект'
WHERE project_id = 11;
UPDATE projects
SET name_jp = 'アクセル・ワールド', name_en = 'Accel World', name_ru = 'Ускоренный мир'
WHERE project_id = 12;
UPDATE projects
SET name_jp = '終わりのクロニクル', name_en = 'Owari no Chronicle',
  name_ru   = 'Хроники конца света'
WHERE project_id = 13;
UPDATE projects
SET name_jp = '中二病でも恋がしたい!', name_en = NULL,
  name_ru   = 'У неё синдром восьмиклассника, но я всё равно хочу любить её!'
WHERE project_id = 14;
UPDATE projects
SET name_jp = 'オール・ユー・ニード・イズ・キル', name_en = 'All You Need Is Kill',
  name_ru   = 'Тебе Лишь Нужно Убивать'
WHERE project_id = 15;
UPDATE projects
SET name_jp = 'ノーゲーム．ノーライフ', name_en = 'No Game No Life', name_ru = NULL
WHERE project_id = 17;
UPDATE projects
SET name_jp = 'さまよう神姫の剣使徒', name_en = 'The Wandering Goddess'' Duelist',
  name_ru   = 'Дуэлянт странствующей богини'
WHERE project_id = 18;
UPDATE projects
SET name_jp = '東京レイヴンズ', name_en = 'Tokyo Ravens', name_ru = 'Токийские вороны'
WHERE project_id = 24;
UPDATE projects
SET name_jp = 'とらドラ！', name_en = 'ToraDora!', name_ru = NULL
WHERE project_id = 25;
UPDATE projects
SET name_jp = 'やはり俺の青春ラブコメはまちがっている', name_en = NULL, name_ru = NULL
WHERE project_id = 26;
UPDATE projects
SET name_jp = 'エロマンガ先生', name_en = 'EroManga Sensei', name_ru = 'Эроманга-сэнсэй'
WHERE project_id = 27;
UPDATE projects
SET name_jp = '問題児たちが異世界から来るそうですよ?',
  name_en   = 'Problem Children are Coming from Another World, Aren''t They?',
  name_ru   = 'Проблемные дети приходят из другого мира, верно?'
WHERE project_id = 28;
UPDATE projects
SET name_jp = 'とある魔術の禁書目録', name_en = 'Toaru Majutsu no Index',
  name_ru   = 'Некий Магический Индекс'
WHERE project_id = 29;
UPDATE projects
SET name_jp = '魔弾の王と戦姫', name_romaji = 'Madan no Ou to Vanadis', name_ru = NULL
WHERE project_id = 33;
UPDATE projects
SET name_jp = '盾の勇者の成り上がり', name_romaji = 'Tate no Yuusha no Nariagari',
  name_ru   = 'Становление Героя Щита'
WHERE project_id = 34;
UPDATE projects
SET name_jp = '棺姫のチャイカ', name_en = 'Chaika - The Coffin Princess',
  name_ru   = 'Чайка − принцесса с гробом'
WHERE project_id = 35;
UPDATE projects
SET name_jp = 'マグダラで眠れ', name_en = 'May your soul rest in Magdala',
  name_ru   = 'Пусть твоя душа упокоится в Магдале'
WHERE project_id = 37;
UPDATE projects
SET name_jp = 'さくら荘のペットな彼女', name_romaji = 'Sakurasou no Pet na Kanojo',
  name_ru   = 'Кошечка из Сакурасо'
WHERE project_id = 38;
UPDATE projects
SET name_jp = 'オーバーロード', name_en = 'Overlord', name_ru = 'Властелин'
WHERE project_id = 39;
UPDATE projects
SET name_jp = 'レンタルマギカ', name_en = 'Rental Magica', name_ru = NULL
WHERE project_id = 43;
UPDATE projects
SET name_jp = '黙示録アリス', name_en = 'Apocalypse Alice', name_ru = NULL
WHERE project_id = 44;
UPDATE projects
SET name_jp   = 'ダンジョンに出会いを求めるのは間違っているだろうか', name_ru = NULL,
  name_romaji = 'Dungeon ni Deai wo Motomeru no wa Machigatteiru Darou ka'
WHERE project_id = 45;
UPDATE projects
SET name_jp   = '新妹魔王の契約者（テスタメント）',
  name_romaji = 'Shinmai Maou no Tesutamento', name_ru = NULL
WHERE project_id = 46;
UPDATE projects
SET name_jp = 'ハイスクールD×D', name_en = 'High School D×D', name_ru = NULL
WHERE project_id = 48;
UPDATE projects
SET name_jp   = '世界の終わりの世界録',
  name_romaji = 'Sekai no Owari no Encore', name_ru = NULL
WHERE project_id = 49;


CREATE TEMPORARY TABLE ch_text (
  ch  INTEGER,
  pg  INTEGER,
  txt INTEGER
);

INSERT INTO ch_text
  SELECT DISTINCT
    chapter_id,
    page_id,
    old_id
  FROM chapters c
    INNER JOIN ruranobe_db.mw_page p ON page_title = c.url
    INNER JOIN ruranobe_db.mw_revision rv ON rev_page = page_id
    INNER JOIN ruranobe_db.mw_text t ON old_id = rev_text_id
  WHERE rev_timestamp = (SELECT max(rev_timestamp)
                         FROM ruranobe_db.mw_revision
                         WHERE rev_page = p.page_id)
        AND length(trim(old_text)) > 0;

INSERT IGNORE INTO texts (text_id, text_wiki, text_html)
  SELECT
    old_id,
    trim(old_text),
    ''
  FROM ruranobe_db.mw_text
    INNER JOIN ch_text ON old_id = txt;

UPDATE chapters
  INNER JOIN ch_text ON ch = chapter_id
SET text_id = txt, publish_date = now();

INSERT INTO external_resources_history (history_id, uploaded_when, project_id)
  SELECT
    page_id,
    img_timestamp,
    project_id
  FROM ruranobe_db.mw_image
    INNER JOIN projects ON concat('sidebanner-', url, '.png') = img_name
    INNER JOIN ruranobe_db.mw_page ON (img_name = page_title AND page_namespace = 6);

INSERT INTO external_resources (user_id, mime_type, url, thumbnail, title, uploaded_when, history_id, width, height)
  SELECT
    img_user,
    concat(img_major_mime, '/', img_minor_mime),
    concat('https://ruranobe.ru/images/', substr(md5(img_name), 1, 1), '/', substr(md5(img_name), 1, 2), '/',
           img_name),
    concat('https://ruranobe.ru/images/thumb/', substr(md5(img_name), 1, 1), '/', substr(md5(img_name), 1, 2), '/',
           replace(img_name, '%', '%%'), '/%dpx-', replace(img_name, '%', '%%')),
    img_name,
    img_timestamp,
    page_id,
    img_width,
    img_height
  FROM ruranobe_db.mw_image
    INNER JOIN projects ON concat('sidebanner-', url, '.png') = img_name
    INNER JOIN ruranobe_db.mw_page ON (img_name = page_title AND page_namespace = 6);

UPDATE projects p
  INNER JOIN external_resources r ON concat('sidebanner-', p.url, '.png') = r.title
SET image_id = resource_id;

INSERT IGNORE INTO external_resources_history (history_id, uploaded_when, project_id, volume_id)
  SELECT DISTINCT
    page_id,
    img_timestamp,
    project_id,
    volume_id
  FROM ruranobe_db.mw_image
    INNER JOIN ruranobe_db.mw_imagelinks ON il_to = img_name
    INNER JOIN ch_text ON il_from = pg
    INNER JOIN ruranobe_db.mw_page ON (img_name = page_title AND page_namespace = 6)
    INNER JOIN chapters ON ch = chapter_id
    INNER JOIN volumes USING (volume_id);

INSERT INTO external_resources (user_id, mime_type, url, thumbnail, title, uploaded_when, history_id, width, height)
  SELECT DISTINCT
    img_user,
    concat(img_major_mime, '/', img_minor_mime),
    concat('https://ruranobe.ru/images/', substr(md5(img_name), 1, 1), '/', substr(md5(img_name), 1, 2), '/',
           img_name),
    concat('https://ruranobe.ru/images/thumb/', substr(md5(img_name), 1, 1), '/', substr(md5(img_name), 1, 2), '/',
           replace(img_name, '%', '%%'), '/%dpx-', replace(img_name, '%', '%%')),
    img_name,
    img_timestamp,
    page_id,
    img_width,
    img_height
  FROM ruranobe_db.mw_image
    INNER JOIN ruranobe_db.mw_imagelinks ON il_to = img_name
    INNER JOIN ch_text ON il_from = pg
    INNER JOIN ruranobe_db.mw_page ON (img_name = page_title AND page_namespace = 6)
  ORDER BY img_name;

INSERT INTO chapter_images (chapter_id, volume_id, non_colored_image_id, colored_image_id, order_number, adult)
  SELECT
    chapter_id,
    volume_id,
    resource_id,
    NULL,
    resource_id,
    0
  FROM external_resources
    INNER JOIN ruranobe_db.mw_imagelinks ON il_to = title
    INNER JOIN ch_text ON il_from = pg
    INNER JOIN chapters ON ch = chapter_id;

UPDATE external_resources_history h
  INNER JOIN external_resources r USING (history_id)
  INNER JOIN chapter_images ch ON r.resource_id = ch.non_colored_image_id
SET h.chapter_image_id = ch.chapter_image_id;

INSERT IGNORE INTO external_resources_history (history_id, uploaded_when, project_id, volume_id)
  SELECT DISTINCT
    img_page.page_id,
    img_timestamp,
    project_id,
    volume_id
  FROM ruranobe_db.mw_image
    INNER JOIN ruranobe_db.mw_imagelinks ON il_to = img_name
    INNER JOIN ruranobe_db.mw_page ON page_id = il_from
    INNER JOIN volumes ON page_title = url
    INNER JOIN ruranobe_db.main_releases ON release_id = volume_id
    INNER JOIN ruranobe_db.mw_page img_page ON (img_name = img_page.page_title AND img_page.page_namespace = 6)
  WHERE img_name LIKE concat(replace(cover, ' ', '_'), '.%');

INSERT IGNORE INTO external_resources (user_id, mime_type, url, thumbnail, title, uploaded_when, history_id, width, height)
  SELECT DISTINCT
    img_user,
    concat(img_major_mime, '/', img_minor_mime),
    concat('https://ruranobe.ru/images/', substr(md5(img_name), 1, 1), '/', substr(md5(img_name), 1, 2), '/',
           img_name),
    concat('https://ruranobe.ru/images/thumb/', substr(md5(img_name), 1, 1), '/', substr(md5(img_name), 1, 2), '/',
           replace(img_name, '%', '%%'), '/%dpx-', replace(img_name, '%', '%%')),
    img_name,
    img_timestamp,
    img_page.page_id,
    img_width,
    img_height
  FROM ruranobe_db.mw_image
    INNER JOIN ruranobe_db.mw_imagelinks ON il_to = img_name
    INNER JOIN ruranobe_db.mw_page ON page_id = il_from
    INNER JOIN volumes ON page_title = url
    INNER JOIN ruranobe_db.main_releases ON release_id = volume_id
    INNER JOIN ruranobe_db.mw_page img_page ON (img_name = img_page.page_title AND img_page.page_namespace = 6)
  WHERE img_name LIKE concat(replace(cover, ' ', '_'), '.%');

UPDATE volumes
  INNER JOIN ruranobe_db.main_releases ON release_id = volume_id
  INNER JOIN external_resources r ON r.title LIKE concat(replace(cover, ' ', '_'), '.%')
SET image_one = resource_id;

DROP TEMPORARY TABLE ch_text;


UPDATE texts
SET text_wiki = replace(text_wiki, '\n\n', '\n');

# повторить для замены всех иллюстраций

UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';
UPDATE texts
SET text_wiki = concat(
    left(text_wiki, locate('{{Иллюстрация|', text_wiki) + 12),
    substr(`text_wiki`, locate('}}', text_wiki, locate('{{Иллюстрация|', text_wiki))))
WHERE text_wiki LIKE '%{{Иллюстрация|%';


CREATE TEMPORARY TABLE ch_parent_first (
  parent_new_text_id                   INTEGER UNIQUE,
  parent_chapter_id                    INTEGER UNIQUE,
  first_chapter_id                     INTEGER UNIQUE,
  first_url                            VARCHAR(32),
  first_text_id                        INTEGER UNIQUE,
  min_first_chapter_image_order_number INTEGER
);

INSERT INTO ch_parent_first
  SELECT
    if(text_wiki LIKE '%===%', @merge_id := @merge_id + 1, NULL),
    p.chapter_id,
    f.chapter_id,
    f.url,
    t.text_id,
    (SELECT order_number
     FROM chapter_images
     WHERE f.chapter_id = chapter_id
     ORDER BY order_number
     LIMIT 1)
  FROM chapters p
    INNER JOIN chapters f USING (volume_id)
    LEFT JOIN texts t ON (f.text_id = t.text_id)
    , (SELECT @merge_id := max(text_id)
       FROM texts) xxx
  WHERE p.text_id IS NULL
        AND !p.nested AND f.nested
        AND f.order_number = (SELECT order_number
                              FROM chapters n
                              WHERE p.volume_id = n.volume_id
                                    AND n.order_number > p.order_number
                              ORDER BY order_number
                              LIMIT 1);

INSERT INTO texts (text_id, text_wiki)
  SELECT
    parent_new_text_id,
    left(text_wiki, locate('===', text_wiki) - 1)
  FROM ch_parent_first
    INNER JOIN texts ON text_id = first_text_id
  WHERE parent_new_text_id IS NOT NULL;

UPDATE `chapters`
SET `url` = 'sao/p1/c2'
WHERE `chapter_id` = '930';
UPDATE `chapters`
SET `url` = 'zl/v1/a1'
WHERE `chapter_id` = '6012';
UPDATE `chapters`
SET `url` = 'zl/v1/a2'
WHERE `chapter_id` = '6013';
UPDATE `chapters`
SET `url` = 'zl/v1/a3'
WHERE `chapter_id` = '6026';
UPDATE `chapters`
SET `url` = 'zl/v1/a4'
WHERE `chapter_id` = '6039';
UPDATE `chapters`
SET `url` = 'zl/v1/a5'
WHERE `chapter_id` = '6040';
UPDATE `chapters`
SET `url` = 'zl/v1/a6'
WHERE `chapter_id` = '6041';
UPDATE `chapters`
SET `url` = 'zl/v2/a7'
WHERE `chapter_id` = '6045';
UPDATE `chapters`
SET `url` = 'zl/v2/a8'
WHERE `chapter_id` = '6046';
UPDATE `chapters`
SET `url` = 'zl/v2/a9'
WHERE `chapter_id` = '6047';
UPDATE `chapters`
SET `url` = 'tnynn/trans/v5'
WHERE `chapter_id` = '6305';
UPDATE `chapters`
SET `url` = 'drrr/v4/e'
WHERE `chapter_id` = '4891';

UPDATE chapters
  INNER JOIN ch_parent_first ON chapter_id = parent_chapter_id
SET text_id    = parent_new_text_id,
  publish_date = now(),
  url          = coalesce(url, if(right(first_url, 1) = 'p',
                                  left(first_url, length(first_url) - 1),
                                  if(right(first_url, 3) LIKE 'ch%',
                                     left(first_url,
                                          length(first_url) - 3),
                                     left(first_url,
                                          length(first_url) - 2))));

DELETE FROM ch_parent_first
WHERE parent_new_text_id IS NULL;


UPDATE texts
  INNER JOIN ch_parent_first ON text_id = first_text_id
SET text_wiki = substr(text_wiki, locate('===', text_wiki));

UPDATE texts
SET text_wiki = substr(text_wiki, locate('\n', text_wiki, locate('==', text_wiki)) + 1)
WHERE text_wiki LIKE '==%' OR text_wiki LIKE '\n==%';

UPDATE chapter_images
  INNER JOIN ch_parent_first ON first_chapter_id = chapter_id
  INNER JOIN texts ON parent_new_text_id = text_id
SET chapter_images.chapter_id = parent_chapter_id
WHERE text_wiki LIKE '{{Иллюстрация}}%'
      AND order_number < min_first_chapter_image_order_number
                         + round((char_length(text_wiki) - char_length(replace(text_wiki, '{{Иллюстрация}}', ''))) /
                                 char_length('{{Иллюстрация}}'), 0);


DROP TEMPORARY TABLE ch_parent_first;


INSERT INTO projects
SET url = 'system', title = 'Системный проект', banner_hidden = TRUE, project_hidden = TRUE;

SET @system_project_id = last_insert_id();

INSERT INTO volumes (project_id, url, name_title, volume_type, volume_status) VALUES
  (@system_project_id, 'system/diary', 'Дневник Руйки', 'Другое', 'hidden'),
  (@system_project_id, 'system/faq', 'FAQ', 'Другое', 'hidden'),
  (@system_project_id, 'system/aboutus', 'О нас', 'Другое', 'hidden'),
  (@system_project_id, 'system/contact', 'Связь', 'Другое', 'hidden'),
  (@system_project_id, 'system/help', 'Помощь', 'Другое', 'hidden'),
  (@system_project_id, 'system/recruit', 'Набор в команду', 'Другое', 'hidden');

INSERT INTO chapters (volume_id, url, title, order_number) VALUES
  ((SELECT volume_id
    FROM volumes
    WHERE url = 'system/aboutus'), 'system/aboutus/text', 'Текст', 1),
  ((SELECT volume_id
    FROM volumes
    WHERE url = 'system/contact'), 'system/contact/text', 'Текст', 1),
  ((SELECT volume_id
    FROM volumes
    WHERE url = 'system/help'), 'system/help/text', 'Текст', 1),
  ((SELECT volume_id
    FROM volumes
    WHERE url = 'system/recruit'), 'system/recruit/text', 'Текст', 1);

INSERT INTO texts
SET text_wiki = '==Команда RuRa-team==
Команда RuRa-team, чье название является производным от названия РуРанобэ, включает множество человек, которые в меру своего времени, уровня лени и количества фанатизма занимаются переводами ранобэ и связанных с ними материалов на русский язык.

''''''Текущий состав команды:''''''

===Руководство команды===
<ul> <li> ''''''Malfurik'''''' - куратор (вторая роль - эдитор) <li> ''''''Samogot'''''' - тех.поддержка (вторая роль - эдитор) <li> ''''''Rindroid'''''' - глава PR-отдела (основная роль - переводчик) <li> ''''''Rezel'''''' - координатор (основная роль - переводчик) <li> ''''''Elberet'''''' - казначей (основная роль - переводчик)</ul>

===Старший состав===
<ul> <li> ''''''arknarok'''''' - старший переводчик <li> ''''''krass09'''''' - помощник старшего переводчика <li> ''''''Бурда'''''' - старший редактор <li> ''''''Melassa'''''' - помощник старшего редактора <li> ''''''Moxnat'''''' - старший эдитор <li> ''''''Rozettka'''''' - помощник старшего эдитора (вторая роль - редактор) <li> ''''''Storm'''''' - новостник (вторая роль - колорист)</ul>

===Основной состав===
====Отдел переводчиков====
<ul> <li> Akdotu <li> ArtemAvix <li> Bas026 <li> Chill-san <li> idiffer (сверщик) <li> Eicosane <li> Efemische <li> Evri <li> Kaonasi <li> Moyashy <li> Mugu <li> Orophin Ancalimon <li> Shum <li> Soundwave1900 <li> wakher <li> zawa0zawa <li> Дурилка Картонная</ul>

====Редакторский отдел====
<ul> <li> Akerman <li> Ametrin <li> Blanka <li> Chaika <li> Eula <li> Fengo <li> J.Black (пруфридер) <li> Joseph <li> Kandaru <li> mokkowolf <li> Monstrrro <li> MrNyashko <li> Laurel <li> Lessa <li> Shrrg (вторая роль - переводчик) <li> SnipeR 02 <li> Takeshi <li> Undecimus <li> Urania <li> Venza <li> Viorteya <li> vvrusanov <li> WhitoNek <li> Zwerg (пруфридер) <li> Мастачка <li> Милликки <li> Трампо <li> Чеширчик</ul>

====Эдиторский отдел (работа с иллюстрациями)====
<ul> <li> Eragot <li> IzeNik <li> Kalamandea <li> My4uTeJlb <li> NoodLez <li> Traident <li> Pruzjinka - Художница</ul>

====Технический отдел====
<ul> <li> Glebggg <li> Keiko <li> Star Cheater <li> Александр Латкун <li> Влад Комельков</ul>

==Руйка-сан==
Довольно частое явление, когда проект обзаводится своим талисманом или, проще говоря, маскотом. Сия закономерность не обошла и нас.
<img alt="Ruika.png" src="https://ruranobe.ru/images/thumb/8/89/Ruika.png/200px-Ruika.png" width="200" height="420" align=left>
Зовут эту особу Хонто Руйка (本冬涙蘭''''''/''''''ほんとう　るいか). Имя "Руйка" переводится как "Трепетная Орхидея", говоря о нашем отношении к проекту. Еще имя можно прочитать как "Руран", что является "отсылкой" на название команды. Фамилия "Хонто" - "Зимняя книга", является отсылкой на нашу деятельность и время рождения нашей команды как "RuRa-team" (январь 2014 года). Стоит отметить, что "Хонто" записано такими кандзи (本冬), которые не используются в обычных случаях.
Кстати, если вы заметили, на шляпе Руйки присутствует заколка в форме золотого цветка. Этот цветок - камон (он же клановый герб) нашей команды. На нем изображена орхидея.
<img alt="mon.png" src="https://ruranobe.ru/images/thumb/a/a8/mon.png/150px-mon.png" width="150" height="156"  align=right>
Откуда появилась Руйка никто толком не знает. Наверно она возникла из бездны фантазии очередного ОЯШа, мечтающего встретить в библиотеке красавицу-тихоню. Правда реальность сильно отличалась от вымысла, ну что ж поделать.
Как и любой маскот, Руйка в некотором смысле олицетворяет собой весь наш дух. Порой она напориста и упорна, а порой ленива и вяла. Она имеет привычку придираться к деталям, хотя иногда может не заметить "бревно в глазу". Ей нравится как переводить, так и писать самой. О ее фанатичности в отношении реставрации иллюстраций лучше лишний раз не упоминать. Не говоря уже о ее извращенных наклонностях. Все это увенчается "легкой" ноткой упоротости ее характера.';

UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/aboutus/text';

INSERT INTO texts
SET text_wiki = 'Что-то не работает? Хотите сотрудничать? А может просто передать привет? Тогда вы по адресу!
Связаться с нами вы можете посредством электронной почты - ''''''support@ruranobe.ru''''''
Если вы не получили ответ на свое письмо, то не расстраиваетесь: мы либо не знаем что вам ответить (вы уж больно постарались, работая над ним), либо еще не увидели его (что еще менее вероятно).';

UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/contact/text';

INSERT INTO texts
SET text_wiki = 'Наш проект сугубо некоммерческий, но это не значит, что помощь нам не нужна. Существует четыре основных варианта помощи.
==Помощь финансовая==
Кто бы там что ни говорил, но деньги всегда нужны. И мы не исключение.
Поэтому если вы можете отплатить нам "монетой", то вы можете это сделать посредством перевода на:

''''''QIWI''''''-кошелек:
<ul> <li> ''''''+79116857099'''''' </ul>

''''''Webmoney''''''-кошельки:
<ul> <li> ''''''R125820793397 (Рубль)'''''' <li> ''''''U911921912420 (Гривна)'''''' <li> ''''''Z608138208963 (Доллар)'''''' <li> ''''''E656434626499 (Евро)'''''' </ul>

''''''Яндекс''''''-деньги:
<ul> <li> ''''''410012692832515'''''' </ul>

Счет для перевода с ''''''кредитных карт'''''':
<ul> <li> ''''''4890 4941 5384 9302'''''' </ul>

Средства требуются и идут на развитие проекта. Поэтому вкладывая свои пять копеек, вы помогаете нам делать релизы лучше (уж поверьте, это так).
==Помощь в распространении==
В меру своих возможностей мы стараемся по максимуму распространять наши релизы в сети. Будь то фан-группы в вк или еще какое партнерство. Вы тоже нам можете помочь в этом деле. Как? Рассказывайте о нас везде, где можете и считаете это уместным. Делать это можно как буквально, так и банально расставляя "лайки" и "рассказать друзьям" в вк. Вам кажется это неэффективным? Отнюдь. Возможно кто-то увидит ваш репост или услышит от вас лично о нас и в итоге также станет нашим читателем, а то и вовсе захочет помочь или вступить в нашу команду. Так или иначе, ваши действия помогают нам развиваться.
Кстати, если вы хотите сотрудничать с нами на уровне проекта, то можете связаться по <a href="https://beta.ruranobe.ru/contact">почте</a>.
==Помощь защитная==
Есть такая штука, как уважение к чужому труду. Мы со своей стороны стараемся всегда ее соблюдать. Однако далеко не все так делают. Выражается это в копипасте наших материалов на различные ресурсы. Это, конечно, заманчиво, но мы по большей части против таких действий. Причина связана с тем, что мы постоянно что-то редактируем на сайте и выложенное быстро устаревает. Поэтому куда больший смысл давать ссылку на источник, откуда и так все можно скачать (fb2 у нас автоконвертируемые, все мы это знаем). Кто-то может сказать, что мы сами выкладываем сторонние тексты, но тут стоит оговориться: а) мы делаем это лишь при наличии договренности; б) как правило, в таких релизах мы что-то тоже сделали(эдит, например).
Тем не менее, гоняться за всеми самостоятельно - воевать с мельницами. Но если вы хотите нам помочь, сообщайте вышеназванным ресурсам на соответствующие "нарушения" и попросите выпилить выложенный текст и сделать ссылку на источник/скачивание с нашего сайта (по возможности). Ну а если вас шлют куда подальше, говоря "Вот пусть они сами и пишут нам" - пожалуйста, опишите детали по <a href="https://beta.ruranobe.ru/contact">почте</a>.
==Помощь RAWочная==
У вас есть ненужный том ранобэ? Или артбук? Вы можете отсканировать и переслать его нам. Свяжитесь с нами по <a href="https://beta.ruranobe.ru/contact">почте</a> чтобы обговорить подробности.
''''''ВНИМАНИЕ: не пытайтесь присылать нам найденные в сети сканы - то что есть в общем доступе, как правило, уже лежит в наших архивах!''''''';

UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/help/text';

INSERT INTO texts
SET text_wiki = 'Как известно, у человека рук только две, и даже в режиме Цезаря он может писать лишь два письма. Так и у нас: количество рук ограничено, поэтому требуются новые.

Прежде чем читать дальше этот топик, подумайте: стоит ли вам ''''''ввязываться'''''' в ''''''это?'''''' Готовы ли тратить ''''''большое количество времени'''''' на переводы/редактирование текста/работу с иллюстрациями? Вопрос актуален по двум причинам.

Во-первых, ''''''ранобэ - не манга''''''. Здесь идет ''''''колоссальная'''''' нагрузка на все сферы, особенно на ''''''переводчика''''''. Во-вторых, большая часть наших новоприбывших ''''''отсеивается'''''' по той причине, что они ''''''не справляются'''''' с количеством работы. И это при том, что сроков и дедлайнов в команде, как таковых, ''''''нет''''''.

Если вы все еще ''''''хотите'''''' работать с нами, будучи ''''''готовыми'''''' к новым ''''''трудностям жизни'''''', то мы рады.


''''''В команде актуальны следующие вакансии:''''''
<ul> <li> ''''''Эдиторы''''''. Клинить-тайпить-фотошопить. Такая судьба наших эдиторов. Работать приходится с разными сканами: от манги до плакатов из ранобэ. Для принятия требуется опыт работы с фотошопом. Люди, не слышавшие о клинерских фильтрах в 90% случаев (исключение составляют только те, когда слышали о фильтрах, о которых не слышал обрабатывающий заявку) не проходят автоматически. <li> ''''''Редакторы переводов.'''''' В ДАННЫЙ МОМЕНТ НАБОР РЕДАКТОРОВ ВРЕМЕННО ЗАКРЫТ ПО ВНУТРИКОМАНДНЫМ ПРИЧИНАМ! ЕСЛИ КТО-ТО ХОЧЕТ ОСТАВИТЬ ЗАЯВКУ, УБЕДИТЕЛЬНАЯ ПРОСЬБА ПОДОЖДАТЬ. КАК ТОЛЬКО ПРИДЕТ ВРЕМЯ, НАБОР СНОВА ОТКРОЕТСЯ. <li> ''''''Медиа-отдел.'''''' Данная роль подходит тем, кто не готов тратить каждый день огромное количество времени на перевод\редакт\эдит. Нам требуются креативщики, новостники (требуются знания английского и начальные знания японского), фотошоперы (фотожабы, композиции и пр.), художники, дизайнеры и все, чьи навыки оказались бы полезны проекту. </ul>

''''''В команде неактуальны, но возможны следующие вакансии:''''''
<ul> <li> ''''''Переводчики. Внимание!'''''' На данный момент набор на переводчиков закрыт в связи с количеством проектов. Прием в переводчики возможен лишь для людей, переводящих какой-то проект отдельно (должно быть переведено не менее 1 тома), а также для японистов. </ul>

''''''Еще два общих момента для всех желающих вступить:''''''
<ul> <li> Кооперацию работы и флуда мы осуществляем посредством Skype. <li> В команде всячески приветствуется активность. Отсутствие таковой не есть хорошо, сами согласитесь.</ul>

==Заявка на вступление в команду==
Свою заявку вы оставляете в ''''''комментах'''''' этой страницы, оформляя ее по следующему шаблону:

<ol> <li> ''''Имя, основной никнейм (используется в стаффе команды), роль в команде (если вы потенциальный переводчик - перечитайте правила приема переводчиков) , которую хотите взять на себя (указана в тексте выше);'''' <li> ''''Возраст;'''' <li> ''''Часовой пояс (от МСК);'''' <li> ''''Сколько часов в сутки вы готовы уделять своей работе?'''' <li> ''''Вкратце расскажите о своих умениях;'''' <li> ''''Когда и где вы услышали о нашей команде?'''' <li> ''''Чего бы вы хотели добиться в качестве одного из членов команды?'''' <li> ''''Что вы можете рассказать о себе помимо вышеназванного? (последний вопрос свободный и, по сути, предлагает вам выплеснуть обо всем "наболевшем", а также позволяет узнать о вас нечто неожиданное, если такое имеется);'''' <li> ''''Ваш логин Skype.'''' (обязательно! микрофон не нужен)</ol>

Как только вам ответят в комментах, ожидайте выхода на связь.
''''''Важное примечание'''''' Обязательно выполните следующие операции со своим скайпом:
<ol> <li> Открываете вкладку инструменты -> настройки.  <li> Выбираете слева вкладку "безопасность". Справа будет два поля: "Принимать звонки..." и "Принимать чаты..." <li> В обоих пунктах выберите "от кого угодно"</ol>
Данная операция нужна для того, чтобы мы не испытывали проблем при добавлении вас в скайпе.';

UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/recruit/text';

SELECT volume_id
INTO @diary_volume_id
FROM volumes
WHERE url = 'system/diary';

INSERT INTO chapters (volume_id, url, title) VALUES
  (@diary_volume_id, 'system/diary/19122015', '19 декабря 2015'),
  (@diary_volume_id, 'system/diary/07122015', '7 декабря 2015'),
  (@diary_volume_id, 'system/diary/01122015', '1 декабря 2015'),
  (@diary_volume_id, 'system/diary/31102015', '31 октября 2015'),
  (@diary_volume_id, 'system/diary/30092015', '30 сентября 2015'),
  (@diary_volume_id, 'system/diary/25092015', '25 сентября 2015'),
  (@diary_volume_id, 'system/diary/11092015', '11 сентября 2015'),
  (@diary_volume_id, 'system/diary/21082015', '21 августа 2015'),
  (@diary_volume_id, 'system/diary/06082015', '6 августа 2015'),
  (@diary_volume_id, 'system/diary/03082015', '3 августа 2015'),
  (@diary_volume_id, 'system/diary/25062015', '25 июня 2015'),
  (@diary_volume_id, 'system/diary/22062015', '22 июня 2015'),
  (@diary_volume_id, 'system/diary/30052015', '30 мая 2015'),
  (@diary_volume_id, 'system/diary/26052015', '26 мая 2015'),
  (@diary_volume_id, 'system/diary/19052015', '19 мая 2015'),
  (@diary_volume_id, 'system/diary/24042015', '24 апреля 2015'),
  (@diary_volume_id, 'system/diary/17042015', '17 апреля 2015'),
  (@diary_volume_id, 'system/diary/13042015', '13 апреля 2015'),
  (@diary_volume_id, 'system/diary/05042015', '5 апреля 2015'),
  (@diary_volume_id, 'system/diary/02042015', '2 апреля 2015'),
  (@diary_volume_id, 'system/diary/21032015', '21 марта 2015'),
  (@diary_volume_id, 'system/diary/01032015', '1 марта 2015'),
  (@diary_volume_id, 'system/diary/15022015', '15 февраля 2015'),
  (@diary_volume_id, 'system/diary/11022015', '11 февраля 2015'),
  (@diary_volume_id, 'system/diary/17012015', '17 января 2015'),
  (@diary_volume_id, 'system/diary/01012015', '1 января 2015'),
  (@diary_volume_id, 'system/diary/26122014', '26 декабря 2014'),
  (@diary_volume_id, 'system/diary/20122014', '20 декабря 2014'),
  (@diary_volume_id, 'system/diary/14122014', '14 декабря 2014'),
  (@diary_volume_id, 'system/diary/10122014', '10 декабря 2014'),
  (@diary_volume_id, 'system/diary/07122014', '7 декабря 2014');

UPDATE chapters
SET order_number = chapter_id
WHERE volume_id = @diary_volume_id;

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, я в шоке с погоды. Сейчас вообще зима, или как? Я попросту обалдеваю от того, что творится на улице. Сначала проливные дожди, потом снег, отчего на дорогах появилась куча грязи, потом снова дождь и снег (порой даже в один день!). Когда, наконец, температура приблизилась к нулю и даже немного ушла в минус, я обрадовалась. Думала, что хоть теперь наступят морозы, и Новый Год пройдет как надо. Ан шиш там! Снова потепление, грязь и дожди! Что за черт! Я неистово протестую против такого издевательства! ｏ(＞＜；)○\nНо хватит о грустном. Надо подумать о чем-нибудь другом. О, точно! Малф-чан вернулся из армии! Целый год его не было. Правда, отпраздновать это не получится, ибо уже началась веселуха. Можно лишь помечтать о спокойствии. (︶︹︺)\nКстати, надо подумать, а не стоит ли устроить что-нибудь на Новый Год. Все-таки большой праздник. Надо хорошенько все обмыслить.\nВпрочем, похоже, не у одной меня такие мысли. Ди-тян где-то коротнуло, а заодно и еще нескольких редов. В итоге, они, вдохновенно упарываясь (да еще как!), усиленно что-то мастрячат. Да и еще и мне не показывают! Дескать, не готово еще, и вообще это сюрприз, и все в таком духе. Конспираторы… Как-то Бурда-кун на Ди-тян плохо влияет, раньше она такой не была. Ну, надеюсь, хуже от этого не будет.';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/19122015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, они все-таки это закончили. Вывели все же эту неприличность в массы. Этот Афтердарк. Ну вот и как теперь спокойно ходить, когда вокруг сплошь извращенцы? Хотя, признаюсь, некоторые моменты там ничего так, я бы… НЕТ. Нет, Руйка, не думай об этом. Ты не такая. Сопротивляйся!\nФух. А вообще, я тут, так сказать, «недовольна». Откуда эти типы взяли ту картинку со мной, мне интересно? А? Да, она была нарисована знакомым мне художником по моим фантазиям (даже не хочу о них вспоминать, слишком стыдно и неприлично). Но я ее спрятала! Так откуда? Что за фигня! Теперь все увидят мои тайные мысли… Ааааа…\nМожно, правда, хотя бы частично отыграться на ребятах, постоянно ссылаясь на их тормознутость. Но получится ли нужный эффект? Надо серьезно все обдумать! А то что все издевательства постоянно на меня сыплются. Не согласная я! Надо отыгрываться!\nP.S. И не было бы вокруг Афдердарка надоедающих споров еще…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/07122015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, и снова я в делах. Даже и написать-то некогда и пары строчек. Вон, сколько уже прошло времени. Впрочем, ничего удивительного. И работы прибавилось, и ребята скучать не дают. Эхх… (*-_-) \nА все это Малф-чан. Я рада, что он скоро вернется, но в последнее время он развил бурную деятельность. Такой хаос у нас творится. Постоянно кто-нибудь из ребят приходит посоветоваться, узнать мое мнение, попросить помощи и так далее. Пожалейте меня, я ведь не железная! ｏ(＞＜；)○ Грандиозность планов, конечно же, опять зашкаливает. Куда ж без этого. Типичные мы. Но можно все-таки и поспокойнее. Ай, ладно. Я прекрасно понимаю их цели, потому не буду ругать так сильно. Мне Валеры хватает, который опять (!) подозрительно себя ведет. Если он что-то замышляет, надо бы поскорей это узнать, пока опять не… ну, и так ясно, что с этого выходит. Особенно для меня!\nКстати, помимо всего прочего, я как-то умудряюсь выделять время на разбор и изучение родословной. Пусть и загрузка приличная, дело я бросать не буду. Тем более такое.\nО, а еще сегодня первый день зимы! С одной стороны, мне хочется кричать «Яхууу!», а с другой, запереться дома и никуда не выходить, а то погода такая, что жуть просто. Я больше люблю сухую погоду, а не то, что творится за окном. Бррр… Где там мой пледик…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/01122015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, все же отошла я после всех событий, что случились ранее. С трудом, конечно, но ребята помогли. Что бы я без них делала. Да и работа заставляла отвлекаться. Но все же те события заставили меня задуматься. Слишком много странного и неизвестного (еще и Валера подозрительно смотрит иногда. Неспроста это!). Потому я решила покопаться в своей родословной, вдруг что да найду. Интересно ведь.\nА тем временем с Хэллоуином всех! Сегодня мы с ребятами веселимся! У меня такой шикарный наряд! (((o(*ﾟ▽ﾟ*)o))) Его снова притащил Валера (и где он их только достает?). Прямо мой профессиональный праздник. Не зря ж девушек ведьмами называют, хи-хи. Ох, я сегодня оторвусь… Держитесь все!\nP.S. К празднику Красс-кун написал небольшой рассказик. Вышло очень интересно. Но у меня ощущение, что рассказ не так прост, как кажется на первый взгляд…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/31102015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, мне… страшно. После кровавой луны я не могу прийти в себя. Памятуя о ней, я серьезно готовилась, но… Похоже, это снова произошло. Я совсем ничего не помню о том, что происходило с вечера накануне до середины следующего дня. Очнулась у себя дома, почему-то на диване, и рядом дневник. Я боюсь, что меня снова настигла та сила, и произошли ужасные события. Возможно, даже где-то пролилась кровь. Это угнетает меня больше всего. Ведь вряд ли кто-нибудь что-либо узнает, если я права.\nИ если все верно, я, наверное, снова призывала «их»… Хуже быть просто не может. По обрывкам воспоминаний прошлых случаев я смогла понять, на что они способны.\nЯ не хочу, чтобы это снова повторилось… Мне страшно от того, что убийства повторятся…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/30092015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, посмотрела я тут на календарь и вспомнила кое-что важное. Со всеми этими заботами я совсем забыла. Грядет событие, которое может изменить все. Нужно готовиться.\n«Темная» луна уже близко…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/25092015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, сегодня мне не хочется рассказывать о каких-либо событиях. Просто охота поговорить о всяких мелочах. Например, какая непонятная погода стоит у меня в последнее время. Я вообще ей удивляюсь. Невозможно предугадать, что будет завтра. Все меняется так быстро, что не успеваешь привыкнуть. То дожди, то солнечно, то просто тучи, то все вместе. И это постоянно меняется и чередуется без всякой на то логики. Погода словно издевается. Я очень надеюсь, что не только у меня так. Хочу теплый солнечный сентябрь! (＞﹏＜)\nИ что-то я обленилась. Серьезно. Лень прямо-таки бьет изо всех щелей. Хочется бросить все дела и сесть почитать что-нибудь. Даже если понимаешь, что нужно обязательно сделать то-то или то-то. Понимаешь… но тут же нападает дикое желание забить. А ведь такого не должно быть, я ведь девушка трудолюбивая. Наверное, заработалась просто… а может, еще и заразилась бездельничаньем от кого-нибудь из ребят. (￣_￣)・・・ В общем, исправляться мне надо, исправляться…\nЕще мне очень хочется попутешествовать. Вот потянуло меня посмотреть мир. Правда, не знаю пока, куда податься сначала, ведь везде есть интересное. Глаза прямо-таки разбегаются. Вроде и туда хочется, и туда, и туда… Но блин, так не выйдет! Надо выбирать, а это так сложно… Все же у меня нет кучи свободного времени и денег. Впрочем, подумываю о центральной Европе с ее невероятно красивыми небольшими городами. Вроде не так далеко и не так дорого. Но я таки жажду посетить множество разных мест! Надеюсь, у меня получится.\nP.S. Рин-сана с днем рождения! Всего ему самого наилучшего!';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/11092015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, есть тут у меня парочка новостей. Правда, насчет них у меня двоякие чувства. С одной стороны, хочется поделиться, а с другой, это несколько смущающе. Эмммм… Ладно… Так и быть, пересилю себя и расскажу.\nЯ тут относительно недавно ездила на берег залива отдохнуть. Пока еще тепло, можно посидеть на песочке, ну и так далее. И казалось, день пройдет спокойно, но нет. В середине дня на пляж пришла группа людей с фотосъемкой. Заметили меня. Их главный попросил принять участие в съемке. Как мне сказали, это для одного журнала. Дескать, знаменуем окончание лета и все в таком духе. И все бы ничего, но надо было стать… русалкой. Русалкой! Разумеется, я дико опешила. Впрочем, меня так слезно умоляли, что в итоге я не смогла отказаться. Снимки получились очень классные! Правда! Вот только сниматься стеснительно оказалось. Наряд-то тот еще был. Но я справилась! (⌒ω⌒) Будет что вспомнить потом.\nА еще ребята готовят одну дико смущающую штуку. Да еще и с таким воодушевлением! Вокруг меня одни извращенцы… (￣_￣)\nИ меня радует, что наши читатели участвуют в конкурсах. Но мне хочется более активного участия! Нужно больше медалек!';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/21082015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, я тут несколько в шоке. Буквально пару дней назад, когда мы с ребятами сидели и обсуждали будущие дела, пришел Ороф-сан и заявил, что его забирают в армию. Вот так. Мы от удивления аж застыли на месте. Нежданно-негаданно возникла такая нехорошая ситуация. До сих пор поверить не могу. (；ω；) Теперь придется как минимум год быть без Ороф-сана. А ведь это еще не самое худшее. Может возникнуть одна крайне нежелательная ситуация (но о ней я я говорить не буду, слишком уж нехорошая она). Впрочем, не будем каркать, пусть все будет хорошо.\nДа и лучше не думать о плохом. Я тут заходила по делам на работу к Арк-сану. Он там успешно похимичил с техникой, в результате чего получилась довольно забавная штука. Мы ее сфоткали, надеюсь, остальным она тоже понравится.\nА еще тут мне внезапно прислали один крайне неожиданный подарок. Видимо, кто-то увидел одну из моих фотографий, и по ее мотиву нарисовал довольно реалистичную картину. Получилось очень классно, мне понравилось. Потом мы ее всем покажем, пусть полюбуются на красивую меня. (*⌒―⌒*)';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/06082015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, и снова, снова я в трудовых буднях. Вообще поражаюсь этому факту. Только сейчас я стала понимать, что лета толком у меня не будет. Вот и теперь, сидела я себе спокойно, а потом внезапно меня поставили перед фактом, что надо ехать в командировку по делам. Причем надолго. И ничего тут не поделаешь. Все, что мне осталось, – повздыхать да начать собираться. И лишь на днях я вернулась домой. Поразительно, что он остался цел, ведь у ребят есть запасной ключ. Можно бесконечно этому удивляться.\nНо вообще, в командировке было довольно интересно. Порой мы делали интересные вещи, даже экспериментировали (я ходила в лабораторном халатике, да). Потом надо будет показаться ребятам фотки этого.\nИ кстати, я рада, что они не сидели без дела, пока меня не было. Много чего подготовили. Ну, кроме, разве, Бурды-куна, который долго сидел без дела в силу некоторых обстоятельств. Представляю, как на него ругались за такое, хихи. Ребятам бы теперь не останавливаться (я надеюсь на это!).\nСейчас у меня в мыслях лишь тихий, мирный отдых. Хотя бы недельку позаниматься своими делами и ничего более. Хотя я прямо-таки чувствую, что мне этого не дадут. Но я буду верить в свое светлое бездельничание!\nИ да, не успела я приехать, как Валера предложил мне кое в чем поучаствовать в недалеком будущем. Разумеется, зная Валеру, все не так просто. Надо будет подумать…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/03082015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, я… я… Я не знаю, что мне делать! Я п-просто… Да как такое могло произойти… К-как мне отреагировать… Я уехала на два дня, поэтому ничего сразу не заметила, но сегодня… Как только я увидела прошлую запись…\nИяяяяяяяяяяяяя! Я не могу спокойно смотреть! (//ω//) Пока я не видела, э… э-этот… этот тип… Аааааа, он дописал прошлую запись! Да еще такое! (＞_＜) Как он мог? Как он мог?! Г-гад… полный…\nИииии… Что еще более важно, он видел все мои записи! Все! Аааааааа, как же мне стыдно… (*/▽＼*) Я же туда чего только не писала… Черт, черт! Я же теперь ему даже ему в глаза не смогу нормально посмотреть! Хиииииии! Вот и как теперь вести себя в такой ситуации… (╥﹏╥)';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/25062015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, я помираю… Это лето меня когда-нибудь добьет. Такая жара, что на солнце хоть не появляйся. Что уж там – даже в тени сидеть трудно. Все окна нараспашку, но спасительный ветерок в последнее время дует так вяло, что все бесполезно. В итоге, вентилятор я практически не выключаю. Даже не знаю, как он до сих пор еще не сломался. Хотя о чем это я? Лучше не каркать, а то иначе сломаюсь я. Надо постучать по дереву. Где там оно… А вообще тут есть и свои плюсы. Из-за жары Валера снизил свою «активность», так что стало поспокойнее. Но лишь на этом фронте…\nДа-да, ребята опять выдумают что не надо. Точнее, Малф-чан. Вместо того чтобы подтолкнуть всех к возвращению к работе, он устроил реформы. В общем, он как всегда. ┐(￣～￣)┌ Разумеется, все это окружали грандиозные споры и суматоха, которые мне, конечно же, пришлось наблюдать. И ведь еще ничего не успокоилось. От всего этого я с тоской вспоминаю про аквапарк. Хотелось бы сходить туда еще раз или еще куда-нибудь, но… Если уж туда не попасть, то чего там говорить о других местах. Если ребята дадут мне хоть немного продыху, я уже буду счастлива. Но я не теряю надежды!\nА еще я люблю ходить голой по дому, хахахаха! Пока еще никто не видел, но кто знает, может, все изменится! Точно! Может, попробовать пройтись так по улице? Вечером, конечно, но должно выйти крайне интересно!';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/22062015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, сегодня ко мне пришли ребята. Причем толпой и все радостные. Оказалось, что наших читателей стало уже очень много. Поэтому ребята на радостях решили это дело отпраздновать. Разумеется, другого места, кроме как у меня дома, они не нашли. (￣_￣) но раз уж такой случай, я их поддержала.\nЧто будут делать ребята, если соберутся скопом вместе? Конечно же, сходить с ума, упарываться, делать непонятные штуки и тд. Другого от них и не ждешь. Но мне нравится эта атмосфера, она не дает скучать.\nЧтобы разбавить немного сумасшедшую атмосферу, я даже надела костюм горничной, который притащил Валера (и откуда только достал?). Мне он показался милым, хотя это несколько смущало. Впрочем, ребятам тоже понравилось. И конечно же они не раз меня сфотографировали в нем и выставили на общее обозрение. И конечно же, мои гневные возражения никто не послушал. А инициатор этой идеи (нетрудно догадаться, кто!) успел спрятаться раньше, чем я подумала, не прибить бы его.\nНо все равно мы отлично провели время. Я даже надеюсь, что это событие хоть как-то ускорит ребят. Хватит им уже ничего не делать!';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/30052015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, наконец-таки я отдохнула. Теперь я чувствую себя свежо и снова готова работать и работать. А все благодаря Валере! Кто бы мог подумать?! Я согласилась на его предложение, поэтому на выходных мы ходили в аквапарк. Ни разу там до этого не бывала, поэтому впечатлений просто куча. Незабываемые эмоции! (*´▽`*)\nЯ правда взяла с собой кое-чего из книг, но едва Валера сфотографировал меня первый раз и увидел эти самые книги, он их тут же отобрал и не возвращал, пока мы там находились. Вообще, за эти выходные я очень поражалась Валере. Никогда я его еще таким не видела. Он веселил меня, помогал, даже ухаживал иногда (как мне казалось). Разумеется, свои типичные шуточки он не оставил, но их было куда меньше обычного. Но хоть меня это крайне удивляло, я решила сконцентрироваться на веселье.\nЕсли бы Валера вел себя так почаще, я бы… наверно… может быть… Ай, неважно.\nНу, а теперь, раз уж у меня есть новые силы, надо пытаться растормошить ребят, а то они совсем все либо с головой ушли в учебу, либо ленятся. А ведь нашу работу ждут!';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/26052015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, давненько я ничего не писала. Столько дел, столько дел… Никакого спокойствия. Мало того что у меня есть и свои дела, так еще ребята постоянно забегают. Я уже даже сбилась со счета, сколько раз они у меня побывали, прося помочь с учебой. Экзамены у многих скоро все-таки. Но мне интересно, они правда думают, что я настолько умная? Я же тоже многого не знаю! Плюс их вечные рабочие обсуждения. Разумеется, тоже у меня. С Рин-саном еще долго обсуждали тему SAO. И я даже не говорю о Бурде-куне, Резе-няне и Мохнате-куне. Это просто жуть какая-то! Пока Элб-сану нездоровилось, чего они только не устраивали. Я еле справлялась с ними. Хорошо хоть в последнее время как-то успокоились. Немудрено, что я сильно устала.\nЯ так хотела отдохнуть… И тут на помощь пришел, кто бы мог подумать, Валера! Пришел недавно ко мне и спросил, не хочу ли я сходить в аквапарк. Он и я, так как у него есть билеты. При этом он выглядел на удивление честно (даже немного смущался вроде, или мне показалось?). Я была в шоке! (□_□)\nПослушала я еще радио-интервью. Кое-где ребята поошибались, кое-где могли и получше рассказать. Но мне понравилось! Я даже местами посмеялась!';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/19052015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, я рада, что побывала на «Анимии». Было так интересно! И столько разных приветливых людей! Я много с кем пообщалась и пофоткалась. Правда, Элб-сан и Резе-нян слишком стеснялись и старались избегать подобного. Но от видео с мастер-классами не отвертелись! И правильно, надо больше общаться с людьми. Кстати, как вам мой образ с фестиваля? Красиво, правда? (*≧ω≦*) Надеюсь, вам тоже понравилось. И я планирую еще поучаствовать на фестивалях в будущем!\nА теперь о менее радостном. Приехала я, значит, домой, и что оказалось? Кто-то лазил в мой дневник! Узнаю, кто это сделал, прибью! Никакой совести! (╬ Ò﹏Ó) Нельзя трогать чужие вещи без спроса, да еще такие. Есть у меня подозрения, что это Бурда-кун или Мохнат-кун, ибо сомневаюсь, что у остальных есть на то причины, хотя это и бездоказательно. Хм… впрочем, может еще и Валера. С него-то станется. Хотя я все же надеюсь, что он не такой.\nПосле фестиваля надо немного отдохнуть, а потом снова за дела. Но так не хочется после веселья…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/24042015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, мое волнение почти достигло предела. Все-таки завтра стартует «Анимия». Я уже просто не знаю, что делать. Бегаю по всему дому, а то чувства зашкаливают. Еще немного, и мне предстоит показывать то, что я приготовила к фестивалю. А это так стеснительно! (*ﾉωﾉ) Надеюсь, я справлюсь и покажу себя во всей красе. Нельзя же посрамить ребят!\nКстати, на время фестиваля у нас перемирие, а то бы мы с Элб-саном и Резе-няном передрались бы. Вот закончится все, тогда… продолжим… (￢ ￢) Хотя я не сомневаюсь, что за наше отсутствие Бурда-кун и Мохнат-кун что-нибудь задумают. Ну да ладно, так просто нас не возьмут.\nА пока нужно насладиться фестивалем!\nP.S. А дело, что ребята затевали, прошло успешно. Я так и знала…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/17042015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, теперь мне приходится всегда быть настороже, когда в поле зрения попадает кто-нибудь из той самой троицы. Никогда не знаешь, что можно от них ожидать. Первая наша стычка вылилась в полномасштабное сражение. Мне пришла поставка чая (закупилась оптом и по дешевке, ведь ребята постоянно ко мне приходят), я его еще не успела на тот момент разобрать. Итог: весь этот чай превратился в снаряды, которыми мы закидывали друг друга. И мой дом оказался весь завален чаем. (个_个) Он валялся повсюду… Ужас просто. Я его потом кучу времени убирала, хорошо хоть Элб-сан помог.\nПосле этого мы перешли на скрытые уловки. Стали выдумывать различные уловки. Так, Бурда-кун попытался найти мою заначку, пока я не вижу, но его ждал сюрприз. В один из шкафчиков я сунула мышеловку (да-да, я немного позлодействовала). Собственно, его сдержанные высказывания и ругань были слышны даже в другой комнате, хи-хи. В другой раз Мохнат-кун подсунул мне книжку, которой я очень интересовалась. Естественно, я увлеклась. А в это время он успешно упер часть запасов. Гад. Знал, чем меня можно отвлечь.\nНо я так просто не сдамся, мы еще посмотрим кто кого.\nP.S. А тем временем ребята мутят одно крупное дело. Скоро узнаем, выгорит ли оно…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/13042015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, я зла. Я очень зла! Все мои опасения оправдались. Чуяло мое сердце, что вскорости спокойствия мне не ждать.\nПришел ко мне, значит, Элб-сан в гости. Все-таки давно мы не виделись. А через некоторое время заглянули еще Резе-нян и Бурда-кун. И вот их я зря пустила… Полчасика мы просидели спокойно, а потом они вышли из комнаты. Я не стала спрашивать куда и зачем, мало ли какие причины у них есть. Но когда через десять минут они все еще не вернулись, я задумалась. Попросив Элб-сана подождать, я пошла искать этих двоих. И что же я увидела? Эта парочка (даже не знаю, как их назвать) обнаружилась около того места, где я специально спрятала запасы чая! (｀皿´＃) Видимо, они не ожидали, что я появлюсь, ибо на их лицах отразился ужас. Я попыталась было их поймать, но они вовремя очухались и умудрились убежать от меня. Я выглянула в окно и увидела там еще Мохнат-куна, к которому эти двое присоединились. У его ног что-то лежало. Все-таки они у меня уперли часть спрятанного! Бурда-кун начал скандировать нечто вроде «Да начнутся чайные войны!», а Резе-нян и Мохнат-кун развернули транспарант с надписью «Травяной чай в массы». В тот момент я была готова их прибить! ＼＼٩(๑`^´๑)۶／／\nНемного успокоившись, я попросила у Элб-сана помощи. Они хотят войны? Они ее получат!';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/05042015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, сегодня мне хочется немного пожаловаться. Эти проходимцы (а иначе их и не назвать) совсем не хотят успокаиваться. Такое устроили на 1 апреля. А я ведь говорила им, что надо придумать что-нибудь другое, но со мной мало кто согласился. И что в итоге? Такой бум вышел. Сильно подозреваю, что идея принадлежала Валере (как раз в его стиле!), но кто бы сомневался, что он в этом признается. Да и остальные хранили молчание… маслята! Когда-нибудь я им устрою!\nНо все же я целиком поддерживаю ребят после высказываний некоторых личностей. Без моей поддержки они не останутся!\nО! Вспомнила! Еще же Элб-сан вернулся! Теперь все снова дико завертится.\nКстати, я тут за последние пару дней заметила, что Бурда-кун, Резе-нян и Мохнат-кун подозрительно себя ведут. Собираются вместе, перешептываются, как-то странно по сторонам глядят. Да и Валера хитро ухмыляется. Ох, чую, не к добру это…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/02042015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, в кои-то веки я смогла спокойно сесть и попить чаю. В полной тишине и спокойствии. Никаких воплей, диких обсуждений и бегающего Резе-няна. Так я думала, ага… Даже так меня достали. Пока я отдыхала, кто-то умудрился сфотографировать меня! Вот гад! Так это еще и не все! Мало того что сфотографировали, еще решили, что этого мало, и нарисовали на основе фотографии картину. Нет, получилось очень даже красиво, но сказать-то мне об этом могли! Нельзя подобное делать тайком от человека. Подозреваю, что это опять был Валера, хотя он упорно отнекивался и делал честное лицо, когда я его спрашивала. Я слишком хорошо его знаю, чтобы поверить…\nУ ребят сейчас какие-то непонятные периоды действий. То они ничего не делают, то начинают яростно спорить о чем-то, то разговаривают о работе (нечастое явление!)… И так все постоянно чередуется. Каждый раз у них происходит что-то такое, отчего я не могу их понять. Конечно, в итоге, не поскучаешь, но иногда хотелось бы знать, что творится у них в головах.\nА иногда я себя странно чувствую… Весна, что ли, на меня так действует?..';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/21032015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, сегодня я узнала кое-какие вещи, что ввели меня в ступор. Оказывается, среди ребят есть так называемая группа махо-седзе. И среди них чайная фея, пивная лоли, злобная ведьма-трап, самобот-тян и чифирная ведьма. (ﾟロﾟ)  А еще есть трио психов-переводчиков. Почему они так себя называют? Ребята, вы чего там выдумываете, а?\nА еще Малф-чан смог поклинить иллюстрации. В армии. С телефона. С ТЕЛЕФОНА!!! (O_O) Как он вообще умудрился?! Я в шоке…\nНу да ладно… Зная ребят, с них-то станется. Кстати, Резе-нян наказан. И сидит теперь без чая. Он все-таки нашел тогда специально спрятанный травяной чай и упер его. Хоть и пришлось за ним побегать, но я его поймала. Вот так вот.\nА теперь о самом интересном. Через полтора месяца мы с Элб-саном и Резе-няном едем на фестиваль «Анимия»! Представим там нашу команду. Я вся в предвкушении! (≧◡≦) Там должно быть очень интересно! И я кое-что готовлю к фестивалю, но вам пока не скажу. И мы обязательно расскажем, как все прошло!\nИ с первым днем весны!';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/01032015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, мне до сих пор грустно после вчерашнего дня. Нет, поначалу все шло хорошо. Я встретилась с ребятами, я подарила им подарки, они – мне. Я очень обрадовалась тому, что они мне подарили. Затем мы просто замечательно повеселились. А потом… потом… Пришел он… Все из-за этого… типа… Я честно собиралась подарить ему шоколад, который сама сделала. Конечно, это смущает, потому я немного замялась. А он… он… вдруг усмехнулся и сказал: «Неужели ты хочешь подарить мне шоколад на День Святого Саунда?». Да еще и заявил, что в моем стиле подарить нечто, вроде «Аленки». Несмотря на то, что у него такой характер, я все же не смогла сдержаться и треснула его дневником, что лежал поблизости (хорошо, что он не порвался). Этот Валера! Было очень больно и обидно. Я-то надеялась, что все пройдет хорошо. После этого день был безнадежно испорчен.\nДаже не знаю, как теперь ко всему этому относиться…\nДа еще и Малф-чан снова сильно заболел…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/15022015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, давненько я ничего не писала. Не то чтобы ничего не происходило за это время, просто все более-менее обыденно. Ребята трудятся, не покладая рук, а я им помогаю, чем смогу. Периодически мы собираемся у меня, чтобы весело провести время. Хотя в последние разы Резе-нян как-то подозрительно себя ведет в некоторые моменты. Такое ощущение, будто он пытается найти травяной чай, что я спрятала от них. Резе-нян такой Резе-нян. ┐(￣～￣)┌\nКстати! Не так давно Малф-чан смог установить нормальную связь из армии с нами. Теперь мы можем нормально общаться с ним, а он с нами. И хотя все же плохо, что его нет рядом, разговоры уже радуют. Хотя, едва он появился, ребят унесло куда-то не туда. Мда… (￢_￢) С Малф-чаном не соскучишься.\nА еще я немного волнуюсь. Скоро же день Святого Валентина! Подарят ли мне что-нибудь ребята? Очень важный вопрос! Я-то точно им подарю (а стоит ли дарить что-то Валере?), но пока придумала только для Резе-няна и Красса-куна. Они сейчас часто думают о яблочном чае. Но у меня еще есть время подумать! Уж я-то постараюсь!\nP.S. А еще я готовлюсь кое к чему, что будет в ближайшее время. Но это пока секретные сведения. (^_-)';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/11022015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, наконец-то я закончила праздновать свой день рождения. Ух, и затянулось же все! Но я отлично повеселилась, было классно! Ребята меня так тепло поздравили. А еще подарили такое прекрасное платье! Мне сказали, что оно мне очень идет. ヽ(*⌒▽⌒*)ﾉ  Даже Валера меня поздравил! Хотя и в своем стиле, но так и быть, в этот раз я его прощу.\nЯ очень рада, что у меня есть такие замечательные ребята, и надеюсь, что они останутся со мной навсегда.\nP.S. Забыла сказать. Они подарили мне еще открытку, куда записали свои поздравления. (Мугу-кун, я верю в тебя!) Вот, взгляните.\n<a data-toggle="collapse" href="#congratulations2015" aria-expanded="false" aria-controls="congratulations2015">''''''Поздравления''''''</a>\n<div class="collapse" id="congratulations2015"><div class="well">\n{{Подзаголовок|От ''''''Elberet''''''}}\nРуй-тян, с Днем Рождения! Элберет-дес.\nПолгода прошло с нашего знакомства, а ты уже так подросла! Когда наша команда по переводу Махоуки присоединялась к тебе, то мы даже не думали, какого размаха достигнет наша с тобой деятельность. У тебя в твоей личной библиотеке уже столько книжек. А сколько ещё будет! Ведь всё ещё только начинается. Кстати, в этом году жди от меня два новых сюрприза. Снова буду тебя радовать.\nА в целом... Желаю тебе счастья и процветания. Хочу, чтобы ты своими книжками радовала множество других людей, и чтобы их количество лишь увеличивалось год от года. А я не подведу с расширением твоей библиотеки. Ты же знаешь, что я один из пяти психов-помощников. И пусть я являюсь темной стороной РуРы, но тебя я буду радовать лишь светлыми и положительными эмоциями. \nС Днем Рождения!!!\n{{Подзаголовок|От ''''''Mugu''''''}}\nС др, крч.\nP.S. до армии смогу.\n{{Подзаголовок|От ''''''Storm''''''}}\nРуйка-сан, с днём рождения тебя! Желаю тебе дальнейшего процветания, пополнения твоего гардероба (Чайнафуку тебе очень идёт!), пополнения книжной полочки, которая за минувший год успела пополниться на более чем 100+ томов. Уверен, что ты покоришь всех и вся, а наша команда будет стремиться помочь тебе во всех твоих начинаниях!\n{{Подзаголовок|От ''''''Samogot''''''}}\nРуечка, искренне поздравляю тебя с днем рождения! Я знаю тебя с самого рождения, ты мне, можно сказать, как дочь, и наблюдать, как ты растешь и хорошеешь с каждым днем, для меня наибольшее счастье. Сегодня твой первый день рождения, но назвать тебя младенцем уже не поворачивается язык — за этот год ты набралась сил так, как многие не смогут и за 10. Конечно, в первую очередь, я говорю не о внешности, а о твоих деяниях, о твоем развитии и прогрессе. Хотя и про внешность забывать не стоит, ведь в этот день ты столь прекрасна, что я просто не могу отвести глаз! Это платье просто супер! Надеюсь, ты покажешь нам еще много таких же прелестных нарядов :). Ну а если серьезно, то хочу пожелать тебе продолжать расти и самосовершенствоваться, пусть твоя слава разносится с каждым днем, но при этом постарайся не загордиться и оставаться такой же доброй ко всем, как и сейчас. Желаю тебе долгих лет жизни, наполненных счастьем, а в твоем окружении пусть будет все спокойно без каких-либо конфликтов.\n{{Подзаголовок|От ''''''Rezel''''''}}\nС днем рождения Ру-тан.\nМы тут с Широяшей и Изаёй-тян посовещались и вспомнили про самый лучший подарок на свете. Порно-кролик была от него в восторге(правда в момент сожгла сие чудо дотла, вот незадача). Ну и мы решили слегка модифицировать "это". \nМы представляем наш шедевр - Растение руйкоед! Количество тентаклей увеличено на 50%, а его эффективность на 100%! Надеюсь, тебе понравится.\nОт Злобной кошкодевочки-переводчицы Резель нян-нян, Широяши и Изаёй-тян.\n{{Подзаголовок|От ''''''Яма Сэки''''''}}\nЯ тут совсем недавно и со многими уже успел познакомится, но для меня ты, Руйка, оказалась другом и товарищем))) Так что желаю тебе оставаться такой же энергичной и задорной, чтобы заставлять улыбаться читателей и поднимать им настроение!!!)\n{{Подзаголовок|От ''''''Moyashy''''''}}\nС Днем Рождения тебя, Руйка-тан!\О/ Мы знакомы уже полгода и за это время успели подружиться =* Желаю тебе увеличения библиотеки, много вкусняшек, чая, типичной рураупоротости и удачных фоток от Валеры~ P.S. чайна-фуку очень тебе идет \О/\n{{Подзаголовок|От ''''''krass09''''''}}\nРуйка-тян, со первым днём рождения тебя!\nВ этот светлый день пусть мир падёт к подолу твоего чайна-фуку.\nИ да будут заказывать тосты "За Руйку! Кампай!"\nДальнейшего роста и привлечения сторонников, чтобы не иссякала воля к переводу у жрецов Твоих.\nИ да захватит культ Твой весь ранобэ-сегмент рунета. И да не дерзнёт никто поднять руку на перевод адептов твоих.\nВо имя Перевода, Редакта и Эдита, Ранобэминь!\n{{Подзаголовок|От ''''''Takeshi''''''}}\nС днем рождения, дорогая Руйка-чан!\nЧего тебе пожелать? Сложный вопрос. Думаю, чтобы ты и дальше была такой, какая ты сейчас. Веселая, красивая, шумная. И почаще надевай своё милое китайское платье. Ведь оно тебе так идет.\nА мы с ребятами и дальше будем пополнять твою библиотеку. Надеюсь, тебе будет очень интересно. Я познакомился с тобой недавно, но надеюсь, что надолго.\nЕще раз с днем рождения! Твой Такеши, член клуба любителей чайна-фуку.\n{{Подзаголовок|От ''''''Rindroid''''''}}\n<center>\n''''Зимой рождённая дева,''''\n''''Озаряешь тернистый мой путь.''''\n''''Вечного, яркого света!''''\n</center>\n{{Подзаголовок|От ''''''Валеры''''''}}\nНу что ж, Руйка. С днем рождения тебя. Так и быть, сегодня трогать тебя не буду, ха-ха. Даже не знаю, что сказать. Успехов тебе, что ли. Ну и развлекай меня почаще, ха-ха. Я всегда буду рядом, помни это.\n</div></div>';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/17012015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, наконец-то мы начали отходить от празднования Нового Года. А то сколько же можно. Впрочем, отпраздновали мы замечательно. Вы ведь тоже, да? (o^▽^o)\nКстати, сидели недавно с Элб-саном за чашечкой чая-улуна. Чтобы его заварить, надо засыпать в заварочный чайник чай на 1/3, потом закрыть его и встряхнуть несколько раз. После этого заливают воду (примерно 90°C) на ¾ и выдерживают несколько минут. Ой, кажется, я опять увлеклась…\nТак вот, не так давно я потеряла кошелек, со всеми карточками и номерами счетов (Валера сказал, что я растяпа. Опять! Правда, за это получил по голове), и за разговором Элб-сан предложил присмотреть за новыми, чтобы подобное не повторилось. Теперь я чувствую, что мои счета в безопасности.\n(Кстати, травяной чай я спрятала (￣_￣) )\nЯ тут еще заметила, что у ребят появилась дикая увлеченность игрой Nekopara. О ней столько разговоров! Я, конечно, понимаю, что там милые девушки, но я ведь тоже красивая! Бу! Надеюсь, ребята не забудут и на меня обращать внимание. (￣ヘ￣)';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/07012015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, сегодня замечательный праздник!\nНовый год наступил!\nЯ так рада, что не нахожу себе места. o(≧▽≦)o\nСегодня мы с ребятами празднуем. Будем веселиться до упаду. Я от всей души их поздравляю! (Даже Валеру, хотя он этого не заслуживает) Я счастлива, что они есть у меня, и желаю им всего самого лучшего в новом году. Не представляю, что бы я делала без них. Я уверена, что в этом году они будут удивлять и радовать меня не меньше, чем в предыдущем.\nИ еще раз. Поздравляю!\nP.S. А Релизопад получился отличным, ребята как всегда молодцы.';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/01012015';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, все я была права, ребята явно планируют что-то глобальное. Такое ощущение, что они там с ума посходили (а ведь я им травяной чай не давала!).\nЯ поинтересовалась об этом у Валеры, но он сделал вид, будто не понимает, о чем я. Вот жук! Точно что-то знает, но не хочет мне говорить! Ну это я ему потом еще припомню.\nКогда я рядом, ребята тоже вроде бы спокойные ходят, но когда меня нет… Кажется, я пару раз слышала чьи-то вопли «неограниченные релизы релизятся». Да еще стишки странные, со всякими фразами вроде «я выстоял лень, чтоб написать многабукав» и «собаки форматировали жесткий диск». Порой я не понимаю, что творится в голове у ребят. Конечно, я могла бы докопаться до них и узнать все, что нужно, но я как-то стесняюсь досаждать им. Пожалуй, просто подожду, пока они сами все не расскажут…\nИ еще Новый Год скоро же! Надо готовиться!';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/26122014';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, сегодня я прилично потратилась. Зашла в чайный магазин купить что-нибудь новое и увидела сиккимский чай. Раньше я его в магазине не видела, видимо, завезли совсем недавно. И он оказался очень дорогим! Но я не смогла удержаться… (→_→)\nОказывается, надо уметь правильно заваривать чай! Я просмотрела много информации на эту тему, а потом пошла пробовать. Тут тебе и количество чая надо учитывать, и температуру воды и… ой, кажется, я немного увлеклась. (^ω~)\nА еще мы с ребятами недавно собирались вместе. И, по-моему, давать им травяной чай было моей ошибкой. После этого они вдруг начали обсуждать возможные варианты исцеления нашего сайта. Кажется, там звучали такие фразы, как «поставить свечку в храме», «синтоизм», «подорожник», «кровь девственниц», «покупка оберегов» и «желчь дракона». Что-то травяной чай странно действует на ребят…\nКстати, из-за слов Резе-няна мне стало казаться, что что-то намечается, вот только не могу понять что именно…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/20122014';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, я в печали. Сегодня я узнала, что Малф-чан покинул нас на год. Целый год! А я не смогла с ним попрощаться… (╥﹏╥)\nВидимо, ребята именно поэтому ничего мне не говорили – знали, что я буду грустить. Но я ни в чем их не виню. Скорей всего я бы очень сильно расплакалась при прощании. Ребята не хотели, чтобы мне было очень печально.\nВалера сказал, что в этом нет ничего такого, что для грусти нет повода. Да что он <br>понимает! (＞_＜)\nПойду-ка я выпью чаю… Наверно, жасминового, он успокаивает. Я бы хотела, чтобы Малф-чан выпил его со мной…\nP.S. Я буду сильной и буду ждать, когда он вернется. Мы с ребятами сделаем все возможное, чтобы к его приезду команда процветала еще сильней.';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/14122014';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, сегодня меня захлестывают чувства.\nЭтот Валера! Хоть я и просила его удалить то фото, он, похоже, пошел к ребятам и показал им его. И они его выложили. Я обиделась. Вот гад! Попадись он только мне! До сих пор не показывается мне на глаза. \nНо позже ко мне пришли ребята и сказали, чтобы я не гневалась. Что я получилась очень милая на том фото, потому они и решили его выложить. Сначала я не поверила, но они уверяли так жарко, что я согласилась. Я была так счастлива.\nТеперь я даже и не знаю, что думать. Меня переполняют противоречивые чувства. Но все же… наверно… Наверно, я прощу Валеру. Но если он больше не будет так делать.\nP.S. Кажется, ребята что-то скрывают от меня. Это подозрительно…';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/10122014';

INSERT INTO texts
SET
  text_wiki = 'Дорогой дневник, сегодня 7 декабря 2014 года. С сегодняшнего дня я постараюсь рассказать о некоторых событиях из своей жизни.\nДаже и не знаю, с чего начать. Столько всего произошло, столько мыслей и чувств накопилось. Мы с ребятами совершили множество разных вещей, всего даже и не упомнишь. Но ты не подумай, я очень счастлива.\nНедавно я узнала, что Арк-сан и Саунд-бо одновременно со своими переводами готовят что-то связанное с «Героем Щита». Вроде бы они назвали это «Становление перевода» или как-то так. Хотя я и попросила дать почитать наброски, Арк-сан отказал мне, объясняя это тем, что лучше мне это не видеть – столько эмоций там выражено, а он не хочет, чтобы я плохо о нем подумала (видимо, там не все прилично). И, каждый день видя его возмущения по этому поводу, я в этом не сомневаюсь. Но я ведь все равно увижу, хи-хи~\nКстати, недавно мы с ребятами играли в снежки и лепили снеговиков. Было очень весело! Правда, Валера меня там очень неудачно сфотографировал. Бу! Надеюсь, что это фото он никуда не выложит...';
UPDATE chapters
SET text_id = last_insert_id()
WHERE url = 'system/diary/07122014';


SELECT volume_id
INTO @faq_volume_id
FROM volumes
WHERE url = 'system/faq';

INSERT INTO texts
SET
  text_wiki = 'Основная часть наших проектов переводятся с английского. При необходимости сверяемся с японским. Есть также несколько проектов, которые переводятся напрямую с японского. Подробнее можно узнать, тыкнув в нужный том (там указывается, кто за что отвечает и с какого языка идет перевод).';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/01', order_number = 01, text_id = last_insert_id(),
  title       = 'С какого языка вы переводите?';

INSERT INTO texts
SET
  text_wiki = 'Здесь работает принцип субъективного желания. Говоря простым языком - все зависит от переводчиков, редакторов и эдиторов. Мы не можем предсказать, когда наша левая пятка воспылает и сделает перевод, редакт и эдит. Правда иногда в комментариях к переводимому тому переводчик, способный контролировать свою левую пятку, сам пишет о графиках, ''''''но в подавляющем большинстве случаев (то есть почти никогда) мы не даем НИКАКИХ сроков, чтобы никого не обнадеживать (особенно в условиях возможных форс-мажоров).''''''';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/02', order_number = 02, text_id = last_insert_id(),
  title       = 'Где можно узнать дату следующего релиза?';

INSERT INTO texts
SET
  text_wiki = 'Это самая громадная проблема у нас на сайте. Тут всё дело в кэше. Причем как в кэше самого сайта, так и кэше у вас в браузере. Для снижения нагрузки наш сайт кэширует страницы в статическом виде и отображает их анонимным пользователям. Иногда этот механизм глючит и не обновляет кэш при внесении изменений. Кроме того браузер, увидев, что страницы "статические", может их закэшировать еще и на своей стороне, и тогда даже если мы обновим кэш на сайте вы все равно будете видеть старую версию. В случае возникновения подобных проблем:\n<ol><!--\n--><li> В первую очередь удалите кэш у себя на компьютере; <!--\n--><li> Переходите на прямую на страницы глав, это не всегда удобно но обычно на них проблем с кэшем нет; <!--\n--><li> Зарегистрируйтесь у нас на сайте. Кэширование используется только для анонимных пользователей; <!--\n--><li> Если вдруг первые три пункта не сработали, или вам ну ''''очень'''' не хочется регистрироваться - сообщите нам об этом <!--\n--></ol>';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/03', order_number = 03, text_id = last_insert_id(),
  title       = 'В Контакте и на сайте висит сообщение об апдейте, но когда я захожу на сайт, то всё равно не вижу новых глав. Почему?';

INSERT INTO texts
SET
  text_wiki = 'Основополагающим принципом команды является отсутствие дедлайнов. Да и у всех переводчиков разные навыки перевода. А кроме непосредственно перевода текст должен пройти через редактора. Все мы люди взрослые, кушать тоже хотим, и у многих членов команды переводческая деятельность является хобби. Так что не обессудьте.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/04', order_number = 04, text_id = last_insert_id(),
  title       = 'Почему некоторые тайтлы переводятся медленнее других?';

INSERT INTO texts
SET
  text_wiki = 'Мало текст этих иллюстраций перевести, его нужно еще и поместить на эти иллюстрации. Этим занимаются специальные люди - эдиторы. Хороших эдиторов, способных клинить-тайпить-фотошопить в природе мало, и ценятся они как зеница ока, поэтому найти их сложновато. Но если вдруг вы почувствовали в себе силы стать на путь эдитора - можете попробовать подать заявку на странице "Набор в команду".';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/05', order_number = 05, text_id = last_insert_id(),
  title       = 'Почему в некоторых тайтлах не переведены иллюстрации?';

INSERT INTO texts
SET text_wiki = 'Члены команды - лентяи и забыли обновить проектные страницы.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/06', order_number = 06, text_id = last_insert_id(),
  title       = 'Почему на странице Х написано нет анлейта, хотя он есть?';

INSERT INTO texts
SET
  text_wiki = 'Подобные сообщения модерируются самими переводчиками. Они могут быть просто выпилены, может быть оставлена просьба не постить подобное... ну а если кто-то достанет переводчиков совсем сильно, то будет забанен. От того, что вы спросите, перевод быстрее не выйдет.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/07', order_number = 07, text_id = last_insert_id(),
  title       = 'Отношение переводчиков к фразочкам плана "эхскореебы", "переводитебыстрее", "когдапрода" (и все фразы с аналогичным значением), "зачем вы взялись за этот шлак, лучше бы переводили тайтлх" и им подобные:';

INSERT INTO texts
SET
  text_wiki = 'Скорее всего, вы нарвались на агро Темной Элбы... А если серьёзно, то мы удаляем все комментарии, которые либо являются спамом, либо в ультимативной форме требуют продолжения перевода, либо напрямую оскорбляют членов нашей команды. Так же комментарии с информацией об ошибках удаляются после того как переводчик/редактор их рассмотрит.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/08', order_number = 08, text_id = last_insert_id(),
  title       = 'Мой комментарий пропал. Почему?';

INSERT INTO texts
SET
  text_wiki = 'Вы капитально нарвались на ярость Темной Элберет и попали в наш бан. Такое случается ОЧЕНЬ редко, но если постараться - возможно всё. Обычно это происходит, когда вы серьёзно оскорбили нашу команду. Сперва бан вешается на несколько дней, но всегда есть шанс поймать его навсегда. Так что, пожалуйста, не гневите Элбу...';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/09', order_number = 09, text_id = last_insert_id(),
  title       = 'Я не могу писать комментарии на сайте. Почему?';

INSERT INTO texts
SET text_wiki = '90+ человек. Подробнее о нашем составе можно узнать в разделе "[[RuRa-team|О команде]]".';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/10', order_number = 10, text_id = last_insert_id(),
  title       = 'Большой ли у вас состав команды?';

INSERT INTO texts
SET
  text_wiki = 'У нас в команде есть правило - мы работаем только с тем, что нам интересно.Сейчас все переводчики заняты своими проектами, но вы можете предложить в комментариях серию ранобэ.В случае чего мы добавим ее в "банк работ" (хотя это не дает никаких гарантий того, что мы возьмемся за перевод этой серии).';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/11', order_number = 11, text_id = last_insert_id(),
  title       = 'Планируете ли вы переводить ранобэ *здесь могло быть ваше название*?';

INSERT INTO texts
SET
  text_wiki = 'Для того, чтобы поднять на рассмотрение вопрос о переводе конкретного тайтла, нужно иметь хотя бы один целиком переведенный том. В противном случае мы браться за работу не будем (исключением могут быть лишь случаи, когда у нас есть прямой контакт с анлейтером и гарантии, что он не бросит работу на полпути). ';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/12', order_number = 12, text_id = last_insert_id(),
  title       = 'Сколько,  глав должно быть переведено на английский, чтобы ваша команда взялась за перевод на русский?';

INSERT INTO texts
SET text_wiki = 'Мы допускаем такую возможность, но по максимуму стараемся избегать подобных "столкновений".';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/13', order_number = 13, text_id = last_insert_id(),
  title       = 'Возьмется ли ваша команда за проект, который уже переводит кто-то еще?';

INSERT INTO texts
SET
  text_wiki = 'Система транслитерации имен и названий, использование нецензурных выражений или суффиксов (а также прочих японизмов, которые могут/не могут передаваться на русский язык) - решение переводчика, за которым закреплен конкретный проект. ';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/14', order_number = 14, text_id = last_insert_id(),
  title       = 'Почему в разных релизах имеются различия на предмет использования именных суффиксов, Поливанова/Хепбёрна, наличие/отсутствие нецензурных выражений? Или, например, имена/названия были переведены как "A", а не "B"?';

INSERT INTO texts
SET
  text_wiki = 'Причины разные, но если рассматривать два примера из вопроса, то ответ следующий:\n<ul><!--\n--><li> «Sword Art Online» в сеттинге является также названием VRMMO-игры. А такие названия редко локализуются (World Of Warcraft, Lineage и т.д.); <!--\n--><li> «Accel World» сохранился по следующей причине: в 16 томе промелькнула интересная деталь - термин «Accel World» впервые встречается в речи персонажей, точнее они впервые называют его не 加速世界, а アクセル・ワールド (второй вариант - название тайтла записанное катаканой). И теперь эти "термины" имеют несколько разное значение. <!--\n--></ul>';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/15', order_number = 15, text_id = last_insert_id(),
  title       = 'Почему названия некоторых серий ранобэ остались не переведенными (например, «Sword Art Online» или «Accel World»)?';

INSERT INTO texts
SET
  text_wiki = 'Нет, мы занимаемся только ранобэ, манга-адаптациями и частично "прикладываем руку" к переводу и озвучке экранизаций, если наши возможности нам это позволяют. Можно, конечно, упомянуть, что члены нашей команды занимаются(-лись) сторонними проектами сами по себе или в других командах, но это другая история.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/16', order_number = 16, text_id = last_insert_id(),
  title       = 'Будете ли вы заниматься чем-либо, кроме ранобэ?';

INSERT INTO texts
SET
  text_wiki = 'Настоятельно советуем проверять наличие переводов на английском на Бака-цки.Ну или на NanoDesu.Нужно только немного поискать, и если повезет - найдете.Например, если набрать DATE a Live на б/ц, то найдется анлейт.Подобные вопросы мы просто игнорируем.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/17', order_number = 17, text_id = last_insert_id(),
  title       = 'Есть ли ранобэ *Здесь могло быть ваше название* в английском переводе?';

INSERT INTO texts
SET text_wiki = 'Пишите в комментарии тома, где найдена ошибка. Обязательно указывайте главу, в которой она находится.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/18', order_number = 18, text_id = last_insert_id(),
  title       = 'Нашёл ошибку в тексте. Что делать?';

INSERT INTO texts
SET text_wiki = 'Если вы попробуете поискать в гугле/яндексе, то сможете достаточно быстро найти этот самый перевод %)';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/19', order_number = 19, text_id = last_insert_id(),
  title       = 'Хотел начать читать один из релизов, но часть томов помечены как "Сторонний перевод". Возможно ли узнать, где сей перевод можно откопать?';

INSERT INTO texts
SET
  text_wiki = 'По большей части мы позиционируем себя как однокомандный проект, на котором выкладываются релизы только от RuRa-team, однако есть исключения вроде совместных проектов (где к чужому переводу прикладывается наш эдит) или переводов другой команды (Поддался TEAM). В то же время простым выкладыванием чужих переводов мы заниматься не планируем.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/20', order_number = 20, text_id = last_insert_id(),
  title       = 'Почему Вы выкладываете только некоторые сторонние переводы?';

INSERT INTO texts
SET text_wiki = 'Пишите нам на почту (в разделе "Связь" на сайте она указана) - поговорим.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/21', order_number = 21, text_id = last_insert_id(),
  title       = 'Можно ли разместить Ваши переводы у нас на сайте?';

INSERT INTO texts
SET
  text_wiki = 'Да, мы для каждого перевода делаем fb2-, epub- и docx-версии переводов. Причем стоит заметить, что они не простые - любая редактура на сайте, даже малейшая правка, приводит к тому, что они автоматически обновляются, исходя из правки. За это мы можем сказать спасибо автоконвертеру.\nСкачать файлы можно на страницах томов (подсказка ниже). Если что-то не работает, обязательно пишите об этом сюда в комментарии с указанием того, где именно и что не работает.\n[[File:q1.jpg|600px]] ';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/22', order_number = 22, text_id = last_insert_id(),
  title       = 'Будете ли вы делать текстовые файлы переводов?';

INSERT INTO texts
SET
  text_wiki = 'Нет, пока таких планов нет. Ограничиваемся автоконвертацией в электронные документы. Однако есть планы сделать каталог релизов в формате OPDS, поддержка которого есть в кул, мун+ и фб ридере. Поживем - увидим.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/23', order_number = 23, text_id = last_insert_id(),
  title       = 'Есть планы делать приложение-читалку как у baka-tsuki? У них обновленная версия неплохой вышла, хоть и работали над ней они почти 1.5-2 года.';

INSERT INTO texts
SET text_wiki = 'На самом деле реквизиты у нас есть! Хранятся они вот [[Помощь проекту|здесь]].';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/24', order_number = 24, text_id = last_insert_id(),
  title       = 'Почему у вас нет реквизитов для оказания финансовой помощи, или это никак не повлияет на проект?';

INSERT INTO texts
SET text_wiki = 'Смотря что под этим понимается. Если переводчик/команда хочет войти в наш состав - мы не против.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/25', order_number = 25, text_id = last_insert_id(),
  title       = 'Об объединении не может быть и речи?';

INSERT INTO texts
SET
  text_wiki = 'Нельзя. Если вы видите у кого-то необычную аватарку, которая отображается в его профайле - это член команды.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/26', order_number = 26, text_id = last_insert_id(),
  title       = 'Можно ли менять аватарку пользователя на сайте?';

INSERT INTO texts
SET text_wiki = 'Не планируется. Но в будущем все возможно.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/27', order_number = 27, text_id = last_insert_id(),
  title       = 'Планируется ли добавление на сайт наборов стилей для широкоформатных мониторов (16:9)?';

INSERT INTO texts
SET text_wiki = 'Пока что функции отслеживания нет. Мы сообщим сразу же, как подключим ее.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/28', order_number = 28, text_id = last_insert_id(),
  title       = 'А как добавлять релизы в список наблюдения?';

INSERT INTO texts
SET
  text_wiki = 'Вот вам фотоответ. Если у кого-то из команды появится желание перевести его или придет переводчик с томом, оно увидит свет. А так - пока что у нас его нет.\n[[File:q2.jpg|200px]] ';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/29', order_number = 29, text_id = last_insert_id(),
  title       = 'У вас есть ранобэ "Shingeki no Kyojin" ("Атака титанов")?';

INSERT INTO texts
SET
  text_wiki = 'Как вы могли заметить по последним постам, мы начали активно сотрудничать с издательством "Истари-комикс". Некоторые переводчики, редакторы и эдиторы уже работают над проектами издательства, но на текущий момент мы целиком любительский и некоммерческий проект; качественный уровень, отсутствие стартового капитала и спонсирования не позволят нам стать издателем в ближайшем будущем, но все может измениться.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/30', order_number = 30, text_id = last_insert_id(),
  title       = 'Ваш проект будет любительским переводом "без лицензии на продажу" или же в скором времени будете официальными?';

INSERT INTO texts
SET
  text_wiki = 'Как сказано ответом выше, мы - любители. Денег с этого мы не имеем. Если что и приходит на счета, тратятся эти деньги только на улучшение наших релизов (закупка электронных и, иногда, печатных изданий для улучшения иллюстраций/сверки по японскому).';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/31', order_number = 31, text_id = last_insert_id(),
  title       = 'Вы переводите в свободное время и получаете за эту работу какую-то прибыль? Или ради народа, от чистого сердца, надеясь на совесть этого народа?';

INSERT INTO texts
SET
  text_wiki = 'Если речь идет о дополнительном привлечении японистов за деньги - возможно. Если есть спрос, найдется и предложение.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/32', order_number = 32, text_id = last_insert_id(),
  title       = 'Если это была бы творческая работа за которую платили, кто-нибудь стал бы переводить с японского?';

INSERT INTO texts
SET
  text_wiki = 'Издательство Истари комикс уже запустило продажи САО на русском языке. Заказать первые тома можно на сайте издательства и в некоторых интернет магазинах.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/33', order_number = 33, text_id = last_insert_id(),
  title       = 'Не хотите запустить SAO в печать? Возможно ли в будущем увидеть SAO в русском книжном/интернет-магазине?';

INSERT INTO texts
SET text_wiki = 'Опуская саму абсурдность вопроса, ответим по факту: убавьте содержание радуги в своих размышлениях =D';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/34', order_number = 34, text_id = last_insert_id(),
  title       = 'И есть вообще шанс того, что наше правительство (в области культуры) поймет и будет сотрудничать с японским по данной проблеме у нас? Хотя это самый глупый вопрос, который останется навечно сном.';

INSERT INTO texts
SET text_wiki = 'Смотря кому и о чем. Пишите комменты, передадим.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/35', order_number = 35, text_id = last_insert_id(),
  title       = 'А в лс можно кому-то вопрос интересный задать?';

INSERT INTO texts
SET text_wiki = 'Нет, зато у нас есть Эгиль:\n[[FILE:q3.jpg|200px]]';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/36', order_number = 36, text_id = last_insert_id(),
  title       = 'У вас есть Black Jack и шл*хи?';

INSERT INTO texts
SET text_wiki = 'Внешний вид Руйки продуман руководством команды. И она нарисована на заказ в соответствии с этим.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/37', order_number = 37, text_id = last_insert_id(),
  title       = 'А кто придумал внешний вид(дизайн) Руйки-тян? (Она очаровательна~~)';

INSERT INTO texts
SET text_wiki = 'Если на проект из долгого ящика найдется анлейт, то он перейдет в раздел "под вопросом"';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/38', order_number = 38, text_id = last_insert_id(),
  title       = 'Если найдется полный английский перевод на проекты, находящиеся в долгом ящике, вы будете их переводить?';

INSERT INTO texts
SET
  text_wiki = 'В команде нет свободных переводчиков. Но если вы хотите переводить какой-либо тайтл данного жанра, то смотрите требования к переводчику в разделе "Набор в команду", мы всегда рады новым переводчикам.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/39', order_number = 39, text_id = last_insert_id(),
  title       = 'Что насчёт перевода ЛитРПГ?';

INSERT INTO texts
SET text_wiki = 'Это есть в планах.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/40', order_number = 40, text_id = last_insert_id(),
  title       = 'Как насчет добавить на сайт встроенный модуль пометки текста на месте (например, выделив участок текста и нажав определенное сочетание клавиш с/без всплывающей формой для комментария)?';

INSERT INTO texts
SET text_wiki = 'Потому что это требование правообладателей.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/41', order_number = 41, text_id = last_insert_id(),
  title       = 'Почему перевод основной SAO удален с сайта?';

INSERT INTO texts
SET
  text_wiki = 'Пишите на почту или заявку на вступление. Если вы переводчик-одиночка, который только начал переводить, то вступление в команду будет для вас оптимальным вариантом. Перевод может быть размещен минимум с одним готовым томом, но написать на почту с просьбой оценить качество можно уже имея лишь пару глав.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/42', order_number = 42, text_id = last_insert_id(),
  title       = 'Если я на досуге переведу какое-нибудь ранобэ, которого здесь нет, его можно будет здесь выложить? ';

INSERT INTO texts
SET text_wiki = '6 полноценных + полкоманды может немного.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/43', order_number = 43, text_id = last_insert_id(),
  title       = 'А сколько всего японистов в команде?';

INSERT INTO texts
SET text_wiki = 'Нет. У нас сайт перевода. Для фанфиков и оригинальных работ есть другие ресурсы.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/44', order_number = 44, text_id = last_insert_id(),
  title       = 'На сайте Baka-Tsuki есть раздел Original Novels, то бишь, оригинальные романы. Нет ли у вас мыслей о введении чего-то подобного?';

INSERT INTO texts
SET
  text_wiki = 'Онлайн ранобэ это вебки? Пожалуйста: syosetu.com. Практически все бесплатные вебки расположены там. А если вы знаете японский, то без труда отыщите на нем опцию "скачать в виде pdf". Но мы против использования Машинного перевода, также известного как "гуглоёми" для чтения иностранных текстов, японского это касается в ещё большей мере.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/45', order_number = 45, text_id = last_insert_id(),
  title       = 'Можете дать ссылку или название сайта, где можно скачать/читать онлайн ранобэ на японском?';

INSERT INTO texts
SET text_wiki = 'Предложить можно. Но обговаривать это лучше с нами по почте.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/46', order_number = 46, text_id = last_insert_id(),
  title       = 'Возможно ли заказать у вас перевод кое-какого ранобэ за деньги?';

INSERT INTO texts
SET
  text_wiki = 'На 100%... пока не встречались. А вот на 90+% отсебятины анлейтера видели, было дело... Как ни странно, но даже в идеальных анлейтах временами попадаются неточности.\n''''''krass09'''''': Если вы думаете, что анлейт не может быть на 100% кривым, то глубоко ошибаетесь.\n''''''Arknarok'''''': С японского в 2.5 раза легче переводить, чем с английского + сверка. Усилия на перевод одни и те же (поскольку 80% времени перевода - возня с русским, а не с языком оригинала), а сверка занимает еще больше усилий, чем сам перевод. ';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/47', order_number = 47, text_id = last_insert_id(),
  title       = 'Вам легче переводить "напрямую с японского" или сначала переводить "кривой анлейт", а потом уже делать сверку с японским и соответственно добавлять/изменять некоторые моменты (не может же анлейт быть на 100% кривым, ибо это уже будет не оригинальное произведение, а фанфик)?';

INSERT INTO texts
SET text_wiki = 'Все возможно. Зависит от однотомника, ну и интереса переводчика, конечно же. ';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/48', order_number = 48, text_id = last_insert_id(),
  title       = 'Беретесь за однотомники?';

INSERT INTO texts
SET
  text_wiki = 'Здесь представлен список тайтлов, которые вы нам предложили. ''''''По сути, этот список ничего не значит, он не дает никаких гарантий, просто служит для того, чтобы ориентироваться, какие переводы интересны нашим читателям.'''''' На данный момент все переводчики заняты и новых тайтлов брать не собираются, а те, кто собирается, уже определились, что это будет. Нет смысла спрашивать, будем ли мы переводить - ''''''по умолчанию ответ "Нет, не будем, все переводчики заняты"''''''. Просто вы можете предложить какой то тайтл, и, возможно, когда-нибудь, когда у очередного переводчика закончится текущий проект, он выберет что-то из списка. А возможно, и нет. При предложении обязательно указываете ссылку на английский перевод с минимум одним законченным томом.\n<div style="margin-bottom: 5px;"> <div class="panel-heading gray"> <h4 class="panel-title"> <a data-toggle="collapse" href="#bank-maybe" class="collapsed"><div><!--\n--> На рассмотрении (анонсированы/поиск переводчиков/переговоры с другими переводчиками) <!--\n--></div></a></h4></div><div id="bank-maybe" class="panel-collapse collapse" role="tabpanel"><div class="answer"><ul> <!--\n--><li> Sekai no Owari no Encore (заморожен) <!--\n--><li> Gekkou (заморожен, на очень долгой очереди) <!--\n--><li> Alfr-Oethull <!--\n--></ul></div> </div> </div>\n\n<div style="margin-bottom: 5px;"> <div class="panel-heading gray"> <h4 class="panel-title"> <a data-toggle="collapse" href="#bank-real" class="collapsed"><div><!--\n--> Под вопросом (перевод возможен, но нет заинтересованных в переводе членов команды. А может мы просто скрываем от вас то, что переводим что-то из списка ниже) <!--\n--></div></a></h4></div><div id="bank-real" class="panel-collapse collapse" role="tabpanel"><div class="answer"><ul> <!--\n--><li> Absolute Duo - [http://www.baka-tsuki.org/project/index.php?title=Absolute_Duo ссылка] <!--\n--><li> AntiMagic Academy - [http://ranobeclub.com/ranobe/399-antimagic-academy-the-35th-test-platoon-antimagicheskaya-akademiya-35-yy-testovyy-vzvod.html ссылка] <!--\n--><li> AmaBuri - [amaburithetranslation.wordpress.com/volume-1 ссылка] <!--\n--><li> Baccano! - [http://www.baka-tsuki.org/project/index.php?title=Baccano ссылка] <!--\n--><li> Baka to Test to Shoukanjuu - [http://www.baka-tsuki.org/project/index.php?title=Baka_to_Test_to_Shoukanjuu ссылка] <!--\n--><li> Black Bullet - [http://www.baka-tsuki.org/project/index.php?title=Black_Bullet ссылка] <!--\n--><li> Boku wa Tomodachi ga Sukunai (У меня мало друзей) - [http://www.baka-tsuki.org/project/index.php?title=Boku_wa_Tomodachi_ga_Sukunai ссылка] <!--\n--><li> CubexCursedxCurious <!--\n--><li> Dantalian no Shoka - [http://www.baka-tsuki.org/project/index.php?title=Dantalian_no_Shoka ссылка] <!--\n--><li> Fate/strange fake - [http://nakulas.blogspot.ru/2013/07/fate-strange-fake.html ссылка] <!--\n--><li> Gakusen Toshi Asterisk - [http://www.baka-tsuki.org/project/index.php?title=Gakusen_Toshi_Asterisk ссылка] <!--\n--><li> Gate - Jietai Kare no Chi nite, Kaku Tatakeri - [http://www.baka-tsuki.org/project/index.php?title=Gate_-_Thus_the_JSDF_Fought_There ссылка] <!--\n--><li> GJ-bu - [http://www.baka-tsuki.org/project/index.php?title=GJ-Bu ссылка] <!--\n--><li> Golden Time - [http://www.baka-tsuki.org/project/index.php?title=Golden_Time ссылка] <!--\n--><li> Hagure Yuusha no Aesthetica - [http://www.baka-tsuki.org/project/index.php?title=Hagure_Yuusha_no_Aesthetica ссылка] <!--\n--><li> Hakushaku to Yousei - [http://www.baka-tsuki.org/project/index.php?title=Hakushaku_to_Yousei ссылка] <!--\n--><li> Hentai Ouji to Warawanai Neko - [http://hennekothetranslation.wordpress.com/ ссылка] <!--\n--><li> Hidan no Aria - [https://baka-tsuki.org/project/index.php?title=Hidan_no_Aria ссылка] <!--\n--><li> Ichiban Ushiro no Daimaou - [http://www.baka-tsuki.org/project/index.php?title=Ichiban_Ushiro_no_Daimaou ссылка] <!--\n--><li> Infinite Stratos - [http://www.baka-tsuki.org/project/index.php?title=Infinite_Stratos#Volume_1_.28Full_Text.29 ссылка] <!--\n--><li> Juuni Kokuki <!--\n--><li> Kamisama no Memochou - [http://www.baka-tsuki.org/project/index.php?title=Kamisama_no_Memochou ссылка] <!--\n--><li> Kamisu Reina Series - [http://www.baka-tsuki.org/project/index.php?title=Kamisu_Reina_Series ссылка] <!--\n--><li> Konjiki no Word Master - [http://unlimitednovelfailures.mangamatters.com/teaser/konjiki-word-master/ ссылка] <!--\n--><li> Kono Subarashii Sekai ni Shikufuku o - [https://www.baka-tsuki.org/project/index.php?title=Kono_Subarashii_Sekai_ni_Shukufuku_o! ссылка] <!--\n--><li> Maou na Ore to Fushihime no Yubiwa - [https://www.baka-tsuki.org/project/index.php?title=Maou_na_Ore_to_Fushihime_no_Yubiwa|https://www.baka-tsuki.org/project/index.php?title=Maou_na_Ore_to_Fushih... ссылка] <!--\n--><li> Maoyuu Maou Yuusha (Герой при заклятом враге) - [http://maoyuuthetranslation.wordpress.com ссылка] <!--\n--><li> NouCome - [www.baka-tsuki.org/project/index.php?title=Ore_no_Nounai_Sentakushi_ga,_Gakuen_Love_Come_o_Zenryoku_de_Jama_Shiteiru? ссылка] <!--\n--><li> OreImo - [http://www.baka-tsuki.org/project/index.php?title=Ore_no_Imōto_ga_Konna_ni_Kawaii_Wake_ga_Nai ссылка] <!--\n--><li> Omae wo Onii-chan ni Shite Yarouka!? - [krytykal.org/omae-wo/volume-1/ ссылка] <!--\n--><li> Owari no Seraph: Glen`s spinn-off - [owari.tumblr.com/post/120933948866/owari-no-seraph-ln-vol-1-afterword-english ссылка] <!--\n--><li> Psycho Love Comedy - [http://www.baka-tsuki.org/project/index.php?title=Psycho_Love_Comedy ссылка] <!--\n--><li> Re: monster - [https://docs.google.com/document/d/1t4_7X1QuhiH9m3M8sHUlblKsHDAGpEOwymLPTyCfHH0/edit ссылка] <!--\n--><li> Shounen Onmyoji  - [http://shounen-onmyouji.deviantart.com/gallery/?set=23975908&offset=24 http://projectooo.deviantart.com/gallery/ ссылка] <!--\n--><li> Seikoku no Ryuu Kishi - [http://www.baka-tsuki.org/project/index.php?title=Seikoku_no_Ryuu_Kishi ссылка] <!--\n--><li> Silver Cross and Draculea - [http://www.baka-tsuki.org/project/index.php?title=Silver_Cross_and_Draculea ссылка] <!--\n--><li> The World God Only Knows (Одному лишь Богу ведомый мир) - [http://www.baka-tsuki.org/project/index.php?title=The_World_God_Only_Knows ссылка] <!--\n--><li> Tsuki tsuki! - [http://www.baka-tsuki.org/project/index.php?title=Tsuki_Tsuki! ссылка] <!--\n--><li> When Hikaru was on the earth... <!--\n--><li> Unlimited Fafnir - [http://www.baka-tsuki.org/project/index.php?title=Unlimited_Fafnir ссылка] <!--\n--><li> Zero no Tsukaima - [http://www.baka-tsuki.org/project/index.php?title=Zero_no_Tsukaima ссылка] <!--\n--></ul></div> </div> </div>\n\n<div style="margin-bottom: 5px;"> <div class="panel-heading gray"> <h4 class="panel-title"> <a data-toggle="collapse" href="#bank-unreal" class="collapsed"><div><!--\n--> Долгий ящик (нет/не найден/"урывочный" англоязычный перевод) <!--\n--></div></a></h4></div><div id="bank-unreal" class="panel-collapse collapse" role="tabpanel"><div class="answer"><ul> <!--\n--><li> Asobi ni iku yo <!--\n--><li> Dakara Boku wa <!--\n--><li> Dragon Crisis <!--\n--><li> GANTZ/MINUS <!--\n--><li> Hataraku Maou-sama! (Сатана на подработке) <!--\n--><li> Higurashi no Naku Koro ni (Когда плачут цикады) <!--\n--><li> Inou-Battle wa Nichijo-kei no Naka de <!--\n--><li> Inu to Hasami wa Tsukaiyō (Пес и ножницы) <!--\n--><li> K-Project <!--\n--><li> Kamisama no Inai Nichiyoubi - [http://www.baka-tsuki.org/project/index.php?title=Kami-sama_no_Inai_Nichiyoubi ссылка] <!--\n--><li> Kyoukai no Kanata (За гранью) - [http://nakulas.blogspot.ru/2013/04/kyoukai-no-kanata-volume-1-progress.html ссылка] <!--\n--><li> Mawaru Penguindrum <!--\n--><li> Monogatari (series) <!--\n--><li> Nemurenai Akuma to Torikago no Utahime <!--\n--><li> Saenai Heroine no Sodatekata <!--\n--><li> Shoron Genshiken: Hairu Ranto no Yabō ~Return of the OTAKU~ (Genshiken: Return of the Otaku) <!--\n--><li> Risou no Himo Seikatsu <!--\n--><li> Rokujouma No Shinryakusha!? <!--\n--><li> Saiunkoku Monogatari <!--\n--><li> Shakugan no Shana - [http://www.baka-tsuki.org/project/index.php?title=Shakugan_no_Shana ссылка] <!--\n--><li> The Lizard King <!--\n--></ul></div> </div> </div>\n\n<div style="margin-bottom: 5px;"> <div class="panel-heading gray"> <h4 class="panel-title"> <a data-toggle="collapse" href="#bank-never" class="collapsed"><div><!--\n--> Переводиться не будет (есть сторонний активный/завершенный перевод) <!--\n--></div></a></h4></div><div id="bank-never" class="panel-collapse collapse" role="tabpanel"><div class="answer"><ul> <!--\n--><li> 3-Z Class''s Ginpachi Sensei (ищите на торрентах) <!--\n--><li> Campione! <!--\n--><li> Densetsu no Yuusha no Densetsu <!--\n--><li> Kagerou <!--\n--><li> Kore wa Zombie desu ka? (Уж не зомби ли это?) <!--\n--><li> Mushoku Tensei <!--\n--><li> Zhan Long <!--\n--><li> Kokoro Connect <!--\n--></ul></div> </div> </div>';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/49', order_number = 49, text_id = last_insert_id(),
  title       = 'Банк работ';

INSERT INTO texts
SET
  text_wiki = 'Если то, что вас интересует, отсутствует на данной странице, вы можете написать свой вопрос в комменты.\nПросьба:\nа) Если кидаете предложение на перевод - кидайте с ссылкой на англоязычную версию с минимум одним переведенным томом.\nб) Убедитесь, что ваш вопрос новый и отсутствует выше. В противном случае мы просто сотрем его.';
INSERT INTO chapters
SET volume_id = @faq_volume_id, url = 'system/faq/50', order_number = 50, text_id = last_insert_id(),
  title       = 'Если вы не нашли ответ на свой вопрос';
