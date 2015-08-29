/**
 * Created by samogot on 28.08.15.
 */
function reinitAffix() {
    $(window).off('.affix');
    $(window).trigger('reinitAffix');
    $(window).scroll();
}
$(window).resize(reinitAffix);
$(reinitAffix);