$(document).ready(function () {
     $('#main-search input').typeahead({
         hint: true,
         highlight: true,
         minLength: 1
     }, {
         name: 'projects',
         display: 'nameEn',
         source: function (query, syncResults, asyncResults) {
             $.get('/api/projects/get/all?params=name_ru;url;name_jp;name_en;name_romaji;title', function (data) {
                 var matches, substringRegex;
                 matches = [];
                 substrRegex = new RegExp(query, 'i');
                 $.each(data, function (i, str) {
                     if (str == null) return;
                     var match = {
                         match: '',
                         title: str.title,
                         link: str.url
                     };
                     if (substrRegex.test(str.title)) {
                         matches.push(match);
                         return;
                     }
                     if (substrRegex.test(str.nameRu))
                         match.match += ' ' + str.nameRu;
                     if (substrRegex.test(str.nameEn))
                         match.match += ' ' + str.nameEn;
                     if (substrRegex.test(str.nameRomaji))
                         match.match += ' ' + str.nameRomaji;
                     if (substrRegex.test(str.nameJp))
                         match.match += ' ' + str.nameJp;
                     if (match.match != '') {
                         match.match = match.match.substr(1);
                         matches.push(match);
                     }
                 });
                 asyncResults(matches);
             });
         },
         templates: {
             empty: function (data) {
                 return [
                     '<div class="empty-message" align="center" style="width:100%">',
                     'Извините, данный проект не найден :С',
                     '</div>'
                 ].join('\n')
             },
             suggestion: function(data) {
                 return '<p style="cursor:pointer"><strong>' + data.title + '</strong>' +
                     (data.match ? '<small class="text-muted" style="font-size:12px"><br>' + data.match + '</small>' : '') +
                     '</p>';
             }
         }
     }).bind('typeahead:select', function (ev, selection) {
         location.href = '/r/' + selection.link;
     }).bind('typeahead:autocomplete', function (ev, selection) {
         location.href = '/r/' + selection.link;
     });
     $('.twitter-typeahead').css('vertical-align', 'bottom');
     $('#main-search button').click(function () {
         location.href = "https://cse.google.ru/cse/publicurl?cx=016828743293566058131:ctxseqkthgk&q=" + $('#main-search .tt-input').val();
     });
 });
