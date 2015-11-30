/* КОММЕНТАРИИ */
$(document).ready(function () {
    moment.locale('ru');
    var topicId = $('.comments').empty().data('topic-id');
    if (topicId) {
        $.getJSON('/f/api/topic/' + topicId + '/posts', function (data) {
            $.each(data.posts, function (i, item) {
                var comment = '<div class="comment"><div class="avatar pull-left"><img src="#" width="80" height="80"></div>' +
                    '<div class="name pull-left bold">' + item.author_username + '</div>' +
                    '<div class="timeAgo">' + moment.unix(item.timestamp).fromNow() + '</div>' +
                    '<div class="commentText">' + item.post_text + '</div></div>';
                $(".comments").append(comment);
            });
        });
    }
});