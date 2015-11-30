/**
 * Created by samogot on 28.08.15.
 */
$(window).on('reinitAffix', function () {
    var contentsModule = $('#contents-module').children('div');
    contentsModule.removeData('bs.affix').removeClass('affix affix-top affix-bottom').css('top', '').affix({
        offset: {
            top: contentsModule.offset().top - 20,
            bottom: $('footer').outerHeight(true)
        }
    });
});