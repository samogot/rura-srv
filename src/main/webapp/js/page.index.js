 $(document).ready(function() {
     var substringMatcher = function(strs) {
         return function findMatches(q, cb) {
             var matches, substringRegex;
             matches = [];
             substringRegex = new RegExp(q, 'i');
             $.each(strs, function(i, str) {
                 if (substringRegex.test(str.name)) matches.push({
                     name: str.name,
                     link: str.link
                 });
             });
             cb(matches);
         };
     };

     var projects = [
     {
         name: 'Sword Art Online', link: 'sao'
     }, {
         name: 'Непутевый ученик в школе магии', link: 'mknr'
     }, {
         name: 'Durarara!!', link: 'drrr'
     }, {
         name: 'Accel World', link: 'aw'
     }, {
         name: 'Heavy Object', link: 'ho'
     }, {
         name: 'Моя юношеская романтическая комедия оказалась неправильной, как я и предполагал', link: 'oregairu'
     }, {
         name: 'Проблемные дети приходят из другого мира, верно?', link: 'mtikk'
     }, {
         name: 'Owari no Chronicle', link: 'onc'
     }, {
         name: 'Чайка − принцесса с гробом', link: 'hnc'
     }, {
         name: 'Неужели искать встречи в подземелье — неправильно?', link: ''
     }, {
         name: 'High School DxD', link: ''
     }, {
         name: 'Магия напрокат', link: ''
     }, {
         name: 'Эроманга-сэнсэй', link: 'ems'
     }, {
         name: 'Рандеву с жизнью', link: ''
     }, {
         name: 'Клятва обретенной сестре, новоявленной Повелительнице демонов', link: ''
     }, {
         name: 'Становление Героя Щита', link: ''
     }, {
         name: 'Madan no Ou to Vanadis', link: ''
     }, {
         name: 'Rokka no Yuusha', link: ''
     }, {
         name: 'Моя девушка и подруга детства чрезмерно ссорятся', link: ''
     }, {
         name: 'Апокалипсис Алиса', link: ''
     }, {
         name: 'Oda Nobuna no Yabou', link: ''
     }, {
         name: 'Танец Клинка Элементалиста', link: ''
     }, {
         name: 'The Breaker', link: ''
     }, {
         name: 'Сасами-сан@лентяйка', link: ''
     }, {
         name: 'Рубаки', link: ''
     }, {
         name: 'Призванный убийца', link: ''
     }, {
         name: 'Overlord', link: ''
     }, {
         name: 'Toradora!', link: ''
     }, {
         name: 'Дуэлянт странствующей богини', link: ''
     }, {
         name: 'Убийцы Драконов', link: ''
     }, {
         name: 'Tsuyokute New Saga', link: ''
     }, {
         name: 'Серия CITY', link: ''
     }, {
         name: 'Кошечка из Сакурасо', link: ''
     }, {
         name: 'Log Horizon', link: ''
     }, {
         name: 'Tokyo Ravens', link: ''
     }, {
         name: 'No Game No Life', link: ''
     }, {
         name: 'Пусть твоя душа упокоится в Магдале', link: ''
     }, {
         name: 'Некий Магический Индекс', link: ''
     }, {
         name: 'Серия Харухи Судзумии', link: ''
     }, {
         name: 'Волчица и пряности', link: ''
     }, {
         name: 'Чудачество любви не помеха', link: ''
     }, {
         name: 'Лакей Богов', link: ''
     }, {
         name: 'All You Need Is Kill', link: ''
     }, {
         name: 'Наше путешествие на край исчезающего мира', link: ''
     }, {
         name: 'Sugar Dark: Девушка в погребальном мраке', link: ''
     }, {
         name: 'Безжизненный мир', link: ''
     }, {
         name: 'Final Fantasy VII', link: ''
     }, {
         name: 'Aldnoah.Zero extra 02'
     }
     ];

     $('#main-search').find('input').typeahead({
         hint: true,
         minLength: 1
     }, {
         name: 'projects',
         display: 'name',
         source: substringMatcher(projects),
         templates: {
             empty: [
                 '<div class="empty-message" align="center" style="width:100%">',
                 'Извините, данный проект не найден :С',
                 '</div>'
             ].join('\n'),
             suggestion: function(data) {
                 return '<p><strong>' + data.name + '</strong> – ' + data.link + '</p>';
             }
         }
     }).on('typeahead:selected, typeahead:autocomplete', function(event, selection) {
        location.href='/r/'+selection.link;
    });
     $('.twitter-typeahead').css('vertical-align', 'bottom');
 });
