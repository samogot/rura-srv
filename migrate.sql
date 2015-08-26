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
      THEN 1
    WHEN 'announced'
      THEN 6
    WHEN 'not_translating'
      THEN 7
    WHEN 'external'
      THEN 4
    WHEN 'wait_translator'
      THEN 9
    WHEN 'wait_eng'
      THEN 6
    WHEN 'freezed_translator'
      THEN 7
    WHEN 'freezed_eng'
      THEN 6
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

INSERT INTO chapters (chapter_id, volume_id, text_id, url, title, order_number, published, nested)
  SELECT
    chapter_id,
    release_id,
    NULL,
    concat(name_url, '/', nullif(url, '')),
    title,
    `order`,
    0,
    level > 1
  FROM ruranobe_db.main_chapter
    INNER JOIN ruranobe_db.main_releases USING (release_id);

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

INSERT IGNORE INTO users (user_id, username, realname, pass, email, email_activated, registration_date, adult)
  SELECT
    user_id,
    user_name,
    user_real_name,
    user_password,
    user_email,
    user_email_authenticated,
    user_registration,
    1
  FROM ruranobe_db.mw_user;

INSERT INTO team_members (member_id, user_id, team_id, nikname, active)
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
SET franchise = trim('\n' FROM substring_index(
    substring_index(trim(LEADING '''' FROM trim(substring_index(old_text, 'Франшиза:', -1))), '==', 1), '{{', 1))
WHERE old_text LIKE '%Франшиза:%';

UPDATE projects
SET order_number = instr((SELECT old_text
                          FROM ruranobe_db.mw_revision
                            INNER JOIN ruranobe_db.mw_text ON old_id = rev_text_id
                          WHERE rev_page = 7460
                          ORDER BY rev_timestamp DESC
                          LIMIT 1), url)
WHERE parent_id IS NULL;

UPDATE projects
SET order_number = 9999
WHERE order_number = 0;

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
  FROM ruranobe_db.main_releases r
    INNER JOIN ruranobe_db.main_chapter c USING (release_id)
    INNER JOIN ruranobe_db.mw_page p ON page_title = concat(r.name_url, '/', c.url)
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
SET text_id = txt, published = 1;


INSERT INTO external_resources (user_id, mime_type, url, title, uploaded_when)
  SELECT
    img_user,
    concat(img_major_mime, '/', img_minor_mime),
    concat('http://ruranobe.ru/w/images/', substr(md5(img_name), 1, 1), '/', substr(md5(img_name), 1, 2), '/',
           img_name),
    img_name,
    img_timestamp
  FROM ruranobe_db.mw_image
    INNER JOIN projects ON concat('sidebanner-', url, '.png') = img_name;

UPDATE projects p
  INNER JOIN external_resources r ON concat('sidebanner-', p.url, '.png') = r.title
SET image_id = resource_id;

INSERT INTO external_resources (user_id, mime_type, url, title, uploaded_when)
  SELECT DISTINCT
    img_user,
    concat(img_major_mime, '/', img_minor_mime),
    concat('http://ruranobe.ru/w/images/', substr(md5(img_name), 1, 1), '/', substr(md5(img_name), 1, 2), '/',
           img_name),
    img_name,
    img_timestamp
  FROM ruranobe_db.mw_image
    INNER JOIN ruranobe_db.mw_imagelinks ON il_to = img_name
    INNER JOIN ch_text ON il_from = pg
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

INSERT IGNORE INTO external_resources (user_id, mime_type, url, title, uploaded_when)
  SELECT DISTINCT
    img_user,
    concat(img_major_mime, '/', img_minor_mime),
    concat('http://ruranobe.ru/w/images/', substr(md5(img_name), 1, 1), '/', substr(md5(img_name), 1, 2), '/',
           img_name),
    img_name,
    img_timestamp
  FROM ruranobe_db.mw_image
    INNER JOIN ruranobe_db.mw_imagelinks ON il_to = img_name
    INNER JOIN ruranobe_db.mw_page ON page_id = il_from
    INNER JOIN volumes ON page_title = url
    INNER JOIN ruranobe_db.main_releases ON release_id = volume_id
  WHERE img_name LIKE concat(replace(cover, ' ', '_'), '.%');

UPDATE volumes
  INNER JOIN ruranobe_db.main_releases ON release_id = volume_id
  INNER JOIN external_resources r ON r.title LIKE concat(replace(cover, ' ', '_'), '.%')
SET image_one = resource_id;


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
  parent_new_text_id                   INTEGER PRIMARY KEY,
  parent_chapter_id                    INTEGER UNIQUE,
  first_chapter_id                     INTEGER UNIQUE,
  first_url                            VARCHAR(32),
  first_text_id                        INTEGER UNIQUE,
  min_first_chapter_image_order_number INTEGER
);

INSERT INTO ch_parent_first
  SELECT
    @merge_id := @merge_id + 1,
    p.chapter_id,
    f.chapter_id,
    f.url,
    t.text_id,
    (SELECT order_number
     FROM chapter_images
     WHERE f.chapter_id = chapter_id
     ORDER BY order_number
     LIMIT 1)
  FROM chapters p, chapters f, texts t, (SELECT @merge_id := max(text_id)
                                         FROM texts) xxx
  WHERE p.text_id IS NULL
        AND f.text_id IS NOT NULL
        AND p.volume_id = f.volume_id
        AND !p.nested AND f.nested
        AND f.order_number = (SELECT order_number
                              FROM chapters n
                              WHERE p.volume_id = n.volume_id
                                    AND n.order_number > p.order_number
                              ORDER BY order_number
                              LIMIT 1)
        AND f.text_id = t.text_id
        AND text_wiki LIKE '%===%';

INSERT INTO texts (text_id, text_wiki)
  SELECT
    parent_new_text_id,
    left(text_wiki, locate('===', text_wiki) - 1)
  FROM ch_parent_first, texts
  WHERE text_id = first_text_id;

UPDATE chapters
  INNER JOIN ch_parent_first ON chapter_id = parent_chapter_id
SET text_id = parent_new_text_id, published = 1, url = if(right(first_url, 1) = 'p',
                                                          left(first_url, length(first_url) - 1),
                                                          if(right(first_url, 3) LIKE 'ch%',
                                                             left(first_url, length(first_url) - 3),
                                                             left(first_url, length(first_url) - 2)));

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