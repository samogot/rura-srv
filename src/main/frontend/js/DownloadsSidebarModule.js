$('.module .download-images, .module .download-bw').change(function (e) {
    var $actions = $(this).closest('.actions');
    var images = $actions.find('.download-images').prop('checked');
    var bw = $actions.find('.download-bw').prop('disabled', !images).prop('checked');
    $actions.find('a.download').each(function () {
        var $link = $(this);
        var $italic = $link.next('.italic');
        var baseUrl = $link.attr('href').replace(/\?.*/, '');
        var body = $link.text();
        if (!images) {
            $link.attr('href', baseUrl + '?pic=0');
            $link.attr('title', body + ' без иллюстраций');
            $italic.text('Без иллюстраций');
        }
        else if (bw === true) {
            $link.attr('href', baseUrl);
            $link.attr('title', body + ' c оригинальными черно-белыми иллюстрациями');
            $italic.text('С ч/б иллюстрациями');
        }
        else if (bw === false) {
            $link.attr('href', baseUrl + '?color=1');
            $link.attr('title', body + ' c цветными иллюстрациями вместо черно-белых');
            $italic.text('С цвет. иллюстрациями');
        }
        else {
            $link.attr('href', baseUrl);
            $link.attr('title', body + ' c иллюстрациями');
            $italic.text('С иллюстрациями');
        }
    })
});