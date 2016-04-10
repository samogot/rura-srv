/**
 * Created by samogot on 29.08.15.
 */
function initFileUpload(inputSelector) {
    var $input = $(inputSelector);
    var $image = $input.next('.file-image');
    var $form = $input.closest('.admin-block');
    var $progress = $form.find('.progress');
    $input.fileupload({
        url: $input.attr('data-upload-url'),
        headers: {'Wicket-Ajax': true, 'Wicket-Ajax-BaseURL': Wicket.Ajax.baseUrl, 'Accept': 'application/json'},
        dataType: 'json',
        acceptFileTypes: /(\.|\/)(jpe?g|png)$/i,
        previewMaxHeight: 73,
        previewMaxWidth: 220,
        maxNumberOfFiles: 1
    }).on('fileuploadadd', function (e, data) {
        $progress.collapse('show');
        $image.attr('src', 'loading.gif');
        data.submit();
    }).on('fileuploadprocessalways', function (e, data) {
        var index = data.index,
            file = data.files[index];
        if (file.error) { // выводим сообщение об ошибке обработки на клиенте
            $form.find('.feedbackPanel').append('<li class="feedbackPanelERROR"><strong>Ошибка!</strong> ' + file.error + '</li>');
            updateFeedbackPanelTimeout($form);
        }
    }).on('fileuploadprogressall', function (e, data) { // обновляем прогресбар
        var progress = parseInt(data.loaded / data.total * 100, 10);
        $progress.find('.progress-bar').css('width', progress + '%').attr('aria-value', progress)
            .find('span').text(progress + '% Complete');
    }).on('fileuploaddone', function (e, data) { // при завершении загрузки заменяем превюшку на img тег с адресом уже загруженной ирасты
        $progress.collapse('hide');
        //console.log(data) // с сервера в json`е должны прийти поля url и id
        $.each(data.result.files, function (index, file) {
            if (file.url) {
                $image.attr('src', file.url);
            } else if (file.error) {// выводим ошибку возвращенную с сервера
                $form.find('.feedbackPanel').append('<li class="feedbackPanelERROR"><strong>Ошибка!</strong> ' + file.error + '</li>');
                updateFeedbackPanelTimeout($form);
            }
        });
    }).on('fileuploadfail', function (e, data) { // выводим ошибку аякса
        $form.find('.feedbackPanel').append('<li class="feedbackPanelERROR"><strong>Ошибка!</strong> Загрузка не удалась</li>');
        updateFeedbackPanelTimeout($form);
    });
}