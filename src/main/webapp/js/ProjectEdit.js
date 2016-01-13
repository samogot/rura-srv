$(function () {
    $('.issue-date-input').datetimepicker({
        format: 'DD.MM.YYYY'
    });
});
$('#volumes').on('addnewitem', function (e, d) {
    console.log(e, d);
    $(d.row).find('.issue-date-input').datetimepicker({
        format: 'DD.MM.YYYY'
    });
});