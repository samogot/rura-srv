$('input.topic-id-input').each(function () {
    var $select = $('<select>').attr({
        'class': $(this).attr('class'),
        'name': $(this).attr('name'),
        'id': $(this).attr('id')
    });
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
        if (res1) {
            res1[0].topics.sort(function (a, b) {
                return a.topic_title.localeCompare(b.topic_title)
            });
            for (var i = 0; i < res1[0].topics.length; ++i)
                $('<option>').attr('value', res1[0].topics[i].topic_id).text(res1[0].topics[i].topic_title).appendTo($select);
        }
        if (res2) {
            res2[0].topics.sort(function (a, b) {
                return a.topic_title.localeCompare(b.topic_title)
            });
            for (var i = 0; i < res2[0].topics.length; ++i)
                $('<option>').attr('value', res2[0].topics[i].topic_id).text(res2[0].topics[i].topic_title).appendTo($select);
        }
        $select.val($this.val());
        $this.after($select).detach();
    });
});