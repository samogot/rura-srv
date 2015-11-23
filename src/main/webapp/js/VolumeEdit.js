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

var $images = $('#images');
var $imagesSelect = $images.find('.list-group.select');
$imagesSelect.on("sortupdate", function (event, ui) { // для списка изображений задаем обработчик перетягивания
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


//upload

$images.find('.admin-header').find('.btn-success').eq(0).fileupload({
    url: $images.data('upload-url'),
    dataType: 'json',
    formData: {
        ctype: 'main'
    },
    acceptFileTypes: /(\.|\/)(jpeg|png|jpg)$/i,
    previewMaxHeight: 180,
    previewMaxWidth: 260,
    imageQuality: 100,
    dropZone: $imagesSelect
});


// настраиваем jQuery File Upload для кнопки заменить или затягивания файла на существующую ирасту
//$('.btn-image-replace').each(function () {
//    var $this = $(this);
//    $(this).fileupload({
//        url: $('#images').data('upload-url'),
//        dataType: 'json',
//        formData: {
//            ctype: $this.closest('.image-data-main').length ? 'main' : 'color',
//            num: $this.closest('.image-data').attr('id').substr(5),
//            id: $('#' + $this.closest('.image-data').attr('id') + '_id').val()
//        },
//        acceptFileTypes: /(\.|\/)(jpeg|png|jpg)$/i,
//        previewMaxHeight: 180,
//        previewMaxWidth: 260,
//        maxNumberOfFiles: 1,
//        dropZone: $this.closest('.image-data-main,.image-data-color')
//    }).on('fileuploadadd', function (e, data) {
//        data.files[0].num = $this.closest('.image-data').attr('id').substr(5);
//        data.files[0].ctype = $this.closest('.image-data-main').length ? 'main' : 'color';
//        $this.closest('.image-data-main,.image-data-color').find('img').after('<center class="btn-image-replace"><i class="fa fa-spinner fa-spin"></i></center>').detach();
//        $('#imageform .progress').collapse('show');
//        data.submit();
//    })
//});

//$('#btn-image-add,.btn-image-replace').on('fileuploadprocessalways', function (e, data) {
//    console.log(data);
//    var index = data.index,
//        file = data.files[index];
//    if (file.preview) { // заменяем иконку загрузки за превюшку, как только она становится доступна
//        $('#images .list-group.select a[href="#image' + file.num + '"] center').empty().append(file.preview);
//        var newCanvas = $(file.preview).clone().get(0);
//        newCanvas.getContext('2d').drawImage(file.preview, 0, 0);
//        $('#image' + file.num + ' .image-data-' + file.ctype + ' center').empty().append(newCanvas);
//    }
//    if (file.error) // или выводим сообщение об ошибке обработки на клиенте
//        $('#imageform .progress').after('<div class="alert alert-danger alert-dismissible fade in" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>Ошибка!</strong> ' + file.error + '</div>');
//}).on('fileuploadprogressall', function (e, data) { // обновляем прогресбар
//    var progress = parseInt(data.loaded / data.total * 100, 10);
//    $('#imageform .progress-bar').css('width', progress + '%');
//    $('#imageform .progress-bar').attr('aria-valuenow', progress);
//    $('#imageform .progress-bar span').text(progress + '% Complete');
//}).on('fileuploaddone', function (e, data) { // при завершении загрузки заменяем превюшку на img тег с адресом уже загруженной ирасты
//    $('#imageform .progress').collapse('hide');
//    console.log(data) // с сервера в json`е должны прийти поля url и id
//    var $button = $(this);
//    $.each(data.result.files, function (index, file) {
//        if (file.url) {
//            $button.closest('.image-data-main,.image-data-color').find('input:eq(1)').val(moment(data._time).format('DD.MM.YYYY HH:mm:ss'));
//            $button.closest('.image-data-main,.image-data-color').find('input:eq(2)').val(file.name);
//            $button.closest('.image-data-main,.image-data-color').find('center').empty().append($('<img>').attr('src', file.url).addClass('img-responsive'));
//            $('#image' + data.files[index].num).find('.image-data-' + data.files[index].ctype).find('center').empty().append($('<img>').attr('src', file.url).addClass('img-responsive')).append($('<input type="file" class="fileupload">'));
//            $('#image' + data.files[index].num + '_id').val(file.id);
//            $('#imageselect a[href="#image' + data.files[index].num + '"] center').empty().append($('<img>').attr('src', file.url));
//        } else if (file.error) // выводим ошибку возвращенную с сервера
//            $('#imageform .progress').after('<div class="alert alert-danger alert-dismissible fade in" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>Ошибка!</strong> ' + file.error + '</div>');
//    });
//    $('#imageform .progress').collapse('hide');
//}).on('fileuploadfail', function (e, data) { // выводим ошибку аякса
//    $('#imageform .progress').after('<div class="alert alert-danger alert-dismissible fade in" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>Ошибка!</strong> Загрузка не удалась</div>');
//});

// настраиваем jQuery File Upload для кнопки добавить или затягивания файла на список иллюстраций
//$('#btn-image-add').fileupload({
//    url: "/rura/loading_files.php", // "обработка" загруженых велась у меня на локально денвере. скорее всего без этого вообще работать не будет
//    dataType: 'json',
//    formData: {
//        ctype: 'main'
//    },
//    acceptFileTypes: /(\.|\/)(jpe?g|png|jpg)$/i,
//    previewMaxHeight: 180,
//    previewMaxWidth: 260,
//    imageQuality: 100,
//    dropZone: $('#imageselect')
//}).on('fileuploadadd', function (e, data) { // при добавлении файла сразу создаем елемент в #imageselect
//    $.each(data.files, function (index, file) {
//        if (!data.last_num) data.last_num = $('#imageselect a[href^="#image"]').length;
//        if (!data.last_num) data.last_num = 0;
//        img++;
//        file.num = ++data.last_num; // num - внутренний/временный айдишник иллюстраций фронтенда - порядок добавления
//        file.ctype = 'main'; // ctype - color type. основная ираста или покрас
//        $('<a data-toggle="collapse" data-parent="#imageform" href="#image' + file.num + '" aria-expanded="true" aria-controls="image' + file.num + '" class="list-group-item"> <i class="fa fa-ellipsis-v move ui-sortable-handle"></i> <center><i class="fa fa-spinner fa-spin"></i></center> <span class="hidden-sm hidden-xs">' + file.name + '</span> </a>').appendTo('#imageselect');
//        $('<div class="panel"><div class="row collapse image-data" role="tabpanel" id="image' + file.num + '"><input type="hidden" id="image' + file.num + '_id"><input type="hidden" id="image' + file.num + '_order"><input type="hidden" id="image' + file.num + '_chapter_id"><input type="hidden" id="image' + file.num + '_delete"><div class="col-xs-6 image-data-main"><h3>Основа <button type="button" class="btn btn-default btn-image-replace" title="Заменить изображение"><i class="fa fa-retweet"></i><input type="file" class="fileupload"></button></h3><center class="btn-image-replace"><input type="file" class="fileupload"><i class="fa fa-spinner fa-spin"></i></center><div class="form-group"><label for="image' + file.num + '_main_date">Дата загрузки</label><input type="text" class="form-control disable" disabled id="image' + file.num + '_main_date" value="' + moment(data._time).format('DD.MM.YYYY HH:mm:ss') + '"></div><div class="form-group"><label for="image' + file.num + '_main_name">Имя файла</label><input type="text" class="form-control disable" disabled id="image' + file.num + '_main_name" value="' + file.name + '"></div></div><div class="col-xs-6 image-data-color"><h3>Покрас <button type="button" class="btn btn-default btn-image-replace" title="Добавить изображение"><i class="fa fa-plus"></i><input type="file" class="fileupload"></button></h3><center class="btn-image-replace"><input type="file" class="fileupload"></center><div class="form-group"><label for="image' + file.num + '_color_date">Дата загрузки</label><input type="text" class="form-control disable" disabled id="image' + file.num + '_color_date" value=""></div><div class="form-group"><label for="image' + file.num + '_color_name">Имя файла</label><input type="text" class="form-control disable" disabled id="image' + file.num + '_color_name" value=""></div></div></div></div>').appendTo('#imageform');
//    });
//    $('#imageform .progress').collapse('show'); // показываем прогресбар
//    data.submit(); // начинаем загрузку
//    replaceImage();
//});