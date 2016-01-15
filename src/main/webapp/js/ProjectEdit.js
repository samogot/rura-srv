$(function () {
    $('.issue-date-input').datetimepicker({
        format: 'DD.MM.YYYY',
        locale: 'ru',
        icons: {
            time: 'fa fa-clock-o',
            date: 'fa fa-calendar',
            up: 'fa fa-chevron-up',
            down: 'fa fa-chevron-down',
            previous: 'fa fa-chevron-left',
            next: 'fa fa-chevron-right',
            today: 'fa fa-calendar-check-o',
            clear: 'fa fa-trash-o',
            close: 'fa fa-close'
        }
    });
});
$('#volumes').on('addnewitem', function (e, d) {
    $(d.row).find('.issue-date-input').datetimepicker({
        format: 'DD.MM.YYYY',
        locale: 'ru',
        icons: {
            time: 'fa fa-clock-o',
            date: 'fa fa-calendar',
            up: 'fa fa-chevron-up',
            down: 'fa fa-chevron-down',
            previous: 'fa fa-chevron-left',
            next: 'fa fa-chevron-right',
            today: 'fa fa-calendar-check-o',
            clear: 'fa fa-trash-o',
            close: 'fa fa-close'
        }
    });
});