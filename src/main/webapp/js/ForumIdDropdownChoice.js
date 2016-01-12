$.getJSON('/f/api/board/forums', function (forums) {
    var forumMap = {'0': {children: []}};
    var unknown = {children: [], forum_id: 0, forum_name: 'Unknown'};
    var i;
    for (i = 0; i < forums.length; ++i) {
        forumMap[forums[i].forum_id] = forums[i];
        forums[i].children = [];
    }
    for (i = 0; i < forums.length; ++i)
        (forumMap[forums[i].parent_id] || unknown).children.push(forums[i]);
    for (i = 0; i < forums.length; ++i)
        forums[i].children.sort(function (a, b) {
            return a.forum_name.localeCompare(b.forum_name)
        });
    forumMap[0].children.push(unknown);

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