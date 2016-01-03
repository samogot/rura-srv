$(document).ready(function() {
    $('.module_name').click(function() {
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
            $('.moreprojects').click(function() {
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
                source: function(query, syncResults, asyncResults) {
                    $.get('/api/projects?fields=name_ru,url,name_jp,name_romaji,title', function (data) {
                        var matches, substringRegex;
                        matches = [];
                        substrRegex = new RegExp(query, 'i');
                        $.each(data, function(i, str) {
                            if (str == null || !str.url) return;
                            var match = {
                                match: '',
                                    title: str.title,
                                    link: str.url
                            };
                            if (substrRegex.test(str.title))
                                match.match = ' ';
                            if (substrRegex.test(str.nameRu))
                                match.match += ' ' + str.nameRu;
                            if (substrRegex.test(str.nameEn))
                                match.match += ' ' + str.nameEn;
                            if (substrRegex.test(str.nameRomaji))
                                match.match += ' ' + str.nameRomaji;
                            if (substrRegex.test(str.nameJp))
                                match.match += ' ' + str.nameJp;
                            if (match.match != '') {
                                match.match = match.match.trim();
                                matches.push(match);
                            }
                        });
                        asyncResults(matches);
                    });
                },
                templates: {
                    empty: function(data) {
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
            }).bind('typeahead:select', function(ev, selection) {
                location.href = '/r/' + selection.link;
            }).bind('typeahead:autocomplete', function(ev, selection) {
                location.href = '/r/' + selection.link;
            });

            element.children('button').click(function() {
                location.href = "https://cse.google.ru/cse/publicurl?cx=016828743293566058131:ctxseqkthgk&q=" + element.find('.tt-input').val();
            });
            if ($('#main-search').length != 0) $('#main-search span').css('vertical-align', 'bottom');
    }

});

function supportsLocalStorage() {
    try {
        return 'localStorage' in window && window['localStorage'] !== null;
    } catch (e) {
        return false;
    }
}

function saveSettings(options) {
    if (!supportsLocalStorage()) {
        return false;
    }
    localStorage.setItem(options.key, options.item);
}

function loadSettings() {
    if (!supportsLocalStorage()) {
        return false;
    }
    if (localStorage.getItem("night") == "true") {
        $('body').addClass("night");
        $('a.navbar-brand img').attr('src', '/img/logo1_night.png');
        if ($('.faq-img').length) $('.faq-img').attr('src', '/img/faq_night.png');
        if ($('.diary-img').length) $('.diary-img').attr('src', '/img/diary_night.png');
        $('.daynight-button .fa').toggleClass('fa-sun-o fa-moon-o');
    } else {
        $('body').removeClass("night");
        $('a.navbar-brand img').attr('src', '/img/logo1.png');
        if ($('.faq-img').length) $('.faq-img').attr('src', '/img/faq.png');
        if ($('.diary-img').length) $('.diary-img').attr('src', '/img/diary.png');
    }
}
$(document).ready(function() {
    loadSettings()
});
$('.daynight-button').on('click', function(e) {
    if ($(this).children('.fa').hasClass('fa-sun-o')) {
        $('body').addClass("night");
        $('a.navbar-brand img').attr('src', '/img/logo1_night.png');
        if ($('.faq-img').length) $('.faq-img').attr('src', '/img/faq_night.png');
        if ($('.diary-img').length) $('.diary-img').attr('src', '/img/diary_night.png');
        saveSettings({
            key: 'night',
            item: true
        });
    } else {
        $('body').removeClass("night");
        $('a.navbar-brand img').attr('src', '/img/logo1.png');
        if ($('.faq-img').length) $('.faq-img').attr('src', '/img/faq.png');
        if ($('.diary-img').length) $('.diary-img').attr('src', '/img/diary.png');
        saveSettings({
            key: 'night',
            item: false
        });
    }
    $(this).children('.fa').toggleClass('fa-sun-o fa-moon-o');
});




