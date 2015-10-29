$(document).on('keyup change', '.updates-input', function () {
    var $formItem = $(this).closest('.form-item');
    var $chapterSelect = $formItem.find('select.updates-input');
    var $dateInput = $formItem.find('input.updates-input');
    setNameLabelText($formItem, $dateInput.val().split(' ')[0] + ': ' + $chapterSelect.children(':selected').text().replace('　', ''));
}).on('keyup change', '.member-input', function () {
    var $formItem = $(this).closest('.form-item');
    var $memberInput = $formItem.find('input.member-input');
    var $activitySelect = $formItem.find('select.member-input');
    setNameLabelText($formItem, $memberInput.val() + ' - ' + $activitySelect.children(':selected').text());
}).on('change', '.nested-checkbox', function () {
    var $input = $(this);
    var $formItem = $input.closest('.form-item');
    findNameLabel($formItem).toggleClass('sub-chapter', $input.is(':checked'));
});

function setChapterOrderDictionary() {
    window.chapterOrderDictionary = {'-1': -1};
    $('#chapters').find('.form-item').each(function () {
        chapterOrderDictionary[$(this).find('.chapter-id').val()] = $(this).find('.order-number').val();
    });
}

function sortImagesSelectorItems($listGroup) {
    if (!$listGroup)
        $listGroup = $('#images').find('.list-group.select');
    $listGroup.children().sort(function (a, b) {
        return (chapterOrderDictionary[$(a).data('chapter-id')] - chapterOrderDictionary[$(b).data('chapter-id')])
            + ($(a).data('order-number') - $(b).data('order-number')) / 100000
    }).appendTo($listGroup);
}

function initImagesChapterLabels() {
    window.chapterOrderDictionary = {'-1': -1};
    var $listGroup = $('#images').find('.list-group.select');
    var $coversHeading = $('<a class="list-group-item heading" data-chapter-id="-1" data-order-number="-1"><span class="move">Обложки</span></a>');
    $listGroup.prepend($coversHeading);
    $('#chapters').find('.form-item').each(function () {
        chapterOrderDictionary[$(this).find('.chapter-id').val()] = $(this).find('.order-number').val();
        var $chapHeading = $coversHeading.clone();
        $chapHeading.data('chapter-id', $(this).find('.chapter-id').val())
            .find('.move').text($(this).find('.name-input').val())
            .toggleClass('sub-chapter', $(this).find('.nested-checkbox').is(':checked'));
        $listGroup.prepend($chapHeading);
    });
    sortImagesSelectorItems($listGroup);
}

$(document).on('dragover', function (e) {
    if (window.dropZoneTimeout)
        clearTimeout(window.dropZoneTimeout);
    else $('body').addClass('dragover'); //выставляем класс на body который активирует css отрисовку дропзон

    // при затягивании файла в одну из дропзон, она подсвечивается
    $('.image-data-main,.image-data-color,#images .list-group.select').removeClass('hover');
    $(e.target).closest('.image-data-main,.image-data-color,#images .list-group.select').addClass('hover');

    // если в течении 800мс событие не повторяется предпологаем что никто уже ничего не перетягивает и выключаем отрисовку дропзон
    window.dropZoneTimeout = setTimeout(function () {
        window.dropZoneTimeout = null;
        $('body').removeClass('dragover');
        $('.image-data-main,.image-data-color,#images .list-group.select').removeClass('hover');
    }, 800);
}).on('drop dragover dragstart', function (e) {
    e.preventDefault();
}); // выключаем стандaртное поведение браузера на drag'n'drop

$('#images').find('.list-group.select').on("sortupdate", function (event, ui) { // для списка изображений задаем обработчик перетягивания
    if (ui.item.hasClass('heading')) // если мы перетянули главу, мы должны убедится что не нарушили порядок следовани глав
    {
        var start = 0; // jquery-ui sortable не дает информации о том какой у елемента был индекс до перетягивания
        $(this).children().each(function (i, e) { // поэтому мы находим его линейно
            if ($(e).position().top <= ui.originalPosition.top) // сравнивая по y координате каждого елемента в пикселах с координатой изначального положения
                start = i;
            else return false;
        });
        var end = ui.item.index(); // индекс итоговой позиции известен
        if (start < end) // если мы перетянули вниз
            $(this).children().slice(start, end).filter('.heading').insertAfter(ui.item); // то перемещаем все заголовки (если они есть) между индексами и ставим их в том же порядке после тепущего
        else
            $(this).children().slice(end + 1, start + 1).filter('.heading').insertBefore(ui.item); // в противном случае точно так же ставим перед текущим
    }
    var order = 0, chapter_id = 0; // теперь нам нужно для каждого изображения поставить его порядковый номер и id главы к которой оно принадлежит
    $(this).children().each(function () { // вообще делать это линейно для всех елементов при любом изменении не очень хорошо. правельные было бы обновлять только для тех изображений для которых что-то изменилось. но мне было лень
        if ($(this).is('.heading'))
            chapter_id = $(this).data('chapter-id'); // если мы попали на елемент заголовка - запомнили номер текущей главы
        else {
            $(this).data('chapter-id', chapter_id);
            $($(this).attr('href')).find('.chapter-id').val(chapter_id); // вспомнили последний известный айдишник главы
            $(this).data('order-number', ++order);
        }
    });
});
$('#chapters').find('.list-group.select').on("sortupdate", function (event, ui) {
    setTimeout(function () {
        setChapterOrderDictionary();
        sortImagesSelectorItems();
    }, 100)
});

$(initImagesChapterLabels);