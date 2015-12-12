$(document).ready(function () {
    $('.module_name').click(function () {
        $(this).children('.fa').toggleClass("fa-chevron-right fa-chevron-down").end()
            .toggleClass("opened").next('.actions').slideToggle($('#contents-module').length ? reinitAffix : undefined);
    });

    $('.ellipses-left').trimLeft();

    var banH = $('#all-projects-module .banners a').height();
    if ($('.module').length != 0) {
        var lasts = $('.module').last().offset().top + $('.module').last().height();
        if (lasts > $('.leftColumn .content').height()) {
            var needDelete = parseInt((((lasts - $('.leftColumn .content').height()) / $('#all-projects-module .banners a').height()) + 1), 10);
            var Blength = $('#all-projects-module .banners a').length;
            var deleteFrom = Blength - needDelete + 1;
            if (deleteFrom < 4) deleteFrom = 4;
            $('#all-projects-module .banners a').slice(deleteFrom + 1).hide();
            $('#all-projects-module .banners').append('<span class="more moreprojects">Больше</span>');
            $('.moreprojects').click(function () {
                $(this).remove();
                $('#all-projects-module .banners a:hidden').slideDown($('#contents-module').length ? reinitAffix : undefined);
            });
        }
    }
    if ($('.miniSearch').length != 0 || $('#main-search').length != 0) {
        var element = $('.miniSearch').length != 0 ? $('.miniSearch') : $('#main-search')
        element.children('input').typeahead({
            hint: true,
            highlight: true,
            minLength: 1
        }, {
            name: 'projects',
            display: 'title',
            source: function (query, syncResults, asyncResults) {
                $.get('/api/projects/get/all?params=name_ru;url;name_jp;name_romaji;title', function (data) {
                    var matches, substringRegex;
                    matches = [];
                    substrRegex = new RegExp(query, 'i');
                    $.each(data, function (i, str) {
                        if (str == null || !str.url) return;
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
                suggestion: function (data) {
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

        element.children('button').click(function () {
            location.href = "https://cse.google.ru/cse/publicurl?cx=016828743293566058131:ctxseqkthgk&q=" + element.children('input').val();
            ;
        });
    }
});
