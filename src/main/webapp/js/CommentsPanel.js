/* КОММЕНТАРИИ */
$(document).ready(function () {
    moment.locale('ru');
    $.views.settings.allowCode= true;

    var topicId = $('.comments').empty().data('topic-id');
    var form = $("#newPost");
    var reply = form.find(".reply");
    var newComment = form.find(".new-comment");
    var commentTemplate = $.templates("#commentTemplate");
    var commentHelpers = {
        greater: function(a, b) { return a > b; }
    };
    if (topicId) {
        updateComments(commentTemplate, commentHelpers);
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
                               updateComments(commentTemplate, commentHelpers);
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
    function updateComments(commentTemplate, commentHelpers) {
        $('.comments').empty();
        $.getJSON('/f/api/topic/' + topicId + '/posts', {limit: 20, sort: "desc", olderThan: 1}, function (data) {
            $(".comments").append(commentTemplate.render(data, commentHelpers));
            $('.comment .commentText').each(function(i, el){
              if($(el).height()>44) {$(el).addClass('overflowed');$(el).parent().append('<a href="return" class="expand"> Подробнее...</a>')};
            });
      });
    }
    $('body').on('click','.comment .expand',function(e){
      e.preventDefault();
      $(this).parent().children('.commentText').removeClass('overflowed');$(this).remove();
    })
});