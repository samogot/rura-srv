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

function setFeedbackLiFadeOut() {
    var $li = $(this);
    setTimeout(function () {
        $li.fadeOut('slow', function () {
            $li.detach();
        });
    }, 5000);
}

function updateFeedbackPanelTimeout(panelOrForm) {
    $(panelOrForm).find('.feedbackPanel').find('li').each(setFeedbackLiFadeOut);
}

function addAlert(form, style, text) {
    $li = $('<li class="feedbackPanel' + style + '"><span class="feedbackPanel' + style + '">' + text + '</span></li>');
    $feedbackPanel = $(form).find('.feedbackPanel');
    if (!$feedbackPanel.length)
        $feedbackPanel = $('<ul class="feedbackPanel">').appendTo($(form).find('.admin-header div:last-child'));
    $feedbackPanel.append($li);
    setFeedbackLiFadeOut.call($li);
}