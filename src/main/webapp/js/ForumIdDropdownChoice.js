$.getJSON('/f/api/board/forums', function (d) {
    var forumMap = {'0': {children: []}};
    var i;
    for (i = 0; i < d.length; ++i) {
        forumMap[d[i].forum_id] = d[i];
        d[i].children = [];
    }
    for (i = 0; i < d.length; ++i)
        forumMap[d[i].parent_id].children.push(d[i]);
    for (i = 0; i < d.length; ++i)
        d[i].children.sort(function (a, b) {
            return a.forum_name.localeCompare(b.forum_name)
        });

    function appendOptions(selectElement, list, prefix) {
        for (var i = 0; i < list.length; ++i) {
            $('<option>').attr('value', list[i].forum_id).text(prefix + list[i].forum_name).appendTo(selectElement);
            appendOptions(selectElement, list[i].children, '- ' + prefix)
        }
    }

    $('.forum-id-input').each(function () {
        var $select = $('<select>').attr({
            'class': $(this).attr('class'),
            'name': $(this).attr('name'),
            'id': $(this).attr('id')
        });
        appendOptions($select, forumMap[0].children, '');
        $select.val($(this).val());
        $(this).after($select).detach();
    });
});