 $(document).ready(function() {
     $('#main-search input').typeahead({
         hint: true,
         highlight: true,
         minLength: 1
     }, {
         name: 'projects',
         display: 'nameEn',
         source: function (query, syncResults, asyncResults) {
             $.get('/api/projects/get/all?params=name_ru;url;name_jp;name_en', function (data) {
                 var matches, substringRegex;
                 matches = [];
                 substrRegex = new RegExp(query, 'i');
                 $.each(data, function (i, str) {
                     if (str == null) return;
                     if (substrRegex.test(str.nameEn) || substrRegex.test(str.nameRu) || substrRegex.test(str.nameJp)) {
                         str.nameEn = (str.nameEn == undefined) ? '' : str.nameEn;
                         str.nameRu = (str.nameRu == undefined) ? '' : str.nameRu;
                         str.nameJp = (str.nameJp == undefined) ? '' : str.nameJp;
                         matches.push({
                             nameEn: str.nameEn,
                             nameRu: str.nameRu,
                             nameJp: str.nameJp,
                             link: str.url
                         });
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
                 return '<p style="cursor:pointer"><strong>' + data.nameEn + '</strong><small class="text-muted" style="font-size:12px"><br>' + data.nameJp + ' ' + data.nameRu + '</small></p>';
             }
         }
     }).on('typeahead:selected, typeahead:autocomplete', function(event, selection) {
         location.href = '/r/' + selection.link;
     });
     $('.twitter-typeahead').css('vertical-align', 'bottom');
     $('#main-search button').click(function () {
         location.href = "https://cse.google.ru/cse/publicurl?cx=016828743293566058131:ctxseqkthgk&q=" + $('#main-search .tt-input').val();
         ;
     });
 });
