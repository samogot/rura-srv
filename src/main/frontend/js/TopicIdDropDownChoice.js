$('input.topic-id-input').each(function () {
    var $select = $('<select>').attr({
        'class': $(this).attr('class'),
        'name': $(this).attr('name'),
        'id': $(this).attr('id')
    });
    $('<option>').attr('value', '').text('<без темы>').appendTo($select);
    // var $buttons = $('<div class="btn-group btn-group-justified" role="group">' +
    //     '<div class="btn-group" role="group">' +
    //     '<button type="button" class="btn btn-success new-topic"><i class="fa fa-plus"></i> Создать тему под том</button>' +
    //     '</div>' +
    //     '<div class="btn-group" role="group">' +
    //     '<button type="button" class="btn btn-warning update-topic" disabled><i class="fa fa-refresh"></i> Обновить текущую тему</button>' +
    //     '</div>' +
    //     '</div>');
    // $buttons.find('.new-topic').click(function () {
    //     var title = $('#info').find('input[name="item:nameTitle"]').val();
    //     var link = location.href.replace('/a/', '/r/');
    //     $.ajax({
    //         url: '/f/api/forum/' + $this.attr('data-forum-id') + '/topics?secret=' + new_topic_secret,
    //         type: "post",
    //         contentType: "application/json",
    //         data: JSON.stringify({
    //             topic_title: title,
    //             topic_body: 'Обсуждение [url=' + link + ']' + title + '[/url]'
    //         }),
    //         dataType: "json",
    //         success: function (d) {
    //             $('<option>').attr('value', d.topic_id).text(title).appendTo($select);
    //             $select.val(d.topic_id);
    //         },
    //         beforeSend: function (xhr) {
    //             xhr.withCredentials = true;
    //         }
    //     });
    // });
    // $buttons.find('.update-topic').click(function () {
    //
    // });
    var promises = [$.when()];
    var $this = $(this);
    if ($this.attr('data-forum-id'))
        promises.push($.getJSON('/f/api/forum/' + $this.attr('data-forum-id') + '/topics'));
    if ($this.val())
        promises.push($.getJSON('/f/api/topic/' + $(this).val()).then(function (result) {
            if (result.forum_id != $this.attr('data-forum-id'))
                return $.getJSON('/f/api/forum/' + result.forum_id + '/topics');
        }));
    $.when.apply(this, promises).then(function (res0, res1, res2) {
        if (res1 && res1[0] && res1[0].topics) {
            res1[0].topics.sort(function (a, b) {
                return a.topic_title.localeCompare(b.topic_title)
            });
            for (var i = 0; i < res1[0].topics.length; ++i)
                $('<option>').attr('value', res1[0].topics[i].topic_id).text(res1[0].topics[i].topic_title).appendTo($select);
        }
        if (res2 && res2[0] && res2[0].topics) {
            res2[0].topics.sort(function (a, b) {
                return a.topic_title.localeCompare(b.topic_title)
            });
            for (var i = 0; i < res2[0].topics.length; ++i)
                $('<option>').attr('value', res2[0].topics[i].topic_id).text(res2[0].topics[i].topic_title).appendTo($select);
        }
        $select.val($this.val());
        $this/*.after($buttons)*/.after($select).detach();
    });
});