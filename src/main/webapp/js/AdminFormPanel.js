/**
 * Created by samogot on 28.08.15.
 */
$(window).on('reinitAffix', function () {
    $('.admin-header').each(function () {
        $(this).removeData('bs.affix').removeClass('affix affix-top affix-bottom').css('top', '').affix({
            offset: {
                top: $(this).offset().top,
                bottom: $(document).height() - $(this).closest('form').offset().top - $(this).closest('form').outerHeight() + $(this).outerHeight() + 75
            }
        });
    });
});
$(document).on('click', '.feedbackPanel li', function () {
    $(this).detach();
});
function updateFeedbackPanelTimeout(panel) {
    $(panel).find('li').each(function () {
        var li = $(this);
        setTimeout(function () {
            li.fadeOut('slow', function () {
                li.detach();
            });
        }, 5000);
    });
}