/* КОММЕНТАРИИ */
$(document).ready(function () {
    moment.locale('ru');
    $.views.settings.allowCode= true;

    var topicId = $('.comments').empty().data('topic-id');
    var form = $("#newPost");
    var reply = form.find(".reply");
    var newComment = form.find(".new-comment");
    var commentTemplate = $.templates("#commentTemplate");
    if (topicId) {
        updateComments(commentTemplate);
        form.submit(function () {
            var text = newComment.val().trim();
            if (text) {
                reply.prop("disabled", true);
                $.ajax({
                           url: '/f/api/topic/' + topicId + '/posts',
                           type: "post",
                           contentType: "application/json",
                           data: JSON.stringify({topic_body: text}),
                           dataType: "json",
                           success: function () {
                               newComment.val("");
                               reply.prop("disabled", false);
                               updateComments(commentTemplate);
                           },
                           error: function () {
                               reply.prop("disabled", false);
                           },
                           beforeSend: function (xhr) {
                               xhr.withCredentials = true;
                           }
                       });
            }
        });
        reply.prop("disabled", false);
    }
    function updateComments(commentTemplate) {
        $('.comments').empty();
        $.getJSON('/f/api/topic/' + topicId + '/posts', function (data) {
            $(".comments").append(commentTemplate.render(data));
        });
    }
});