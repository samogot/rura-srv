/**
 * Created by samogot on 28.08.15.
 */
$(document).on('selectionChange', '.admin-block', function (e, index) {
    $(this).find('.selectable-only').prop('disabled', !Number.isInteger(index));
});