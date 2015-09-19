/**
 * Created by samogot on 17.09.15.
 */


function toggleColumnVisibility($checkbox) {
    $checkbox.closest('.admin-block').find($checkbox.attr('data-toggle')).toggle($checkbox.prop('checked'));
}
function toggleAllColumnsVisibility() {
    $('.column-visibility-checkbox').each(function () {
        toggleColumnVisibility($(this));
    });
}
function updateScrollbar($adminTable) {
    var $scrollbar = $adminTable.find('.scrollbar');
    var $scrollable = $adminTable.find('.scrollable');
    var width = $scrollable.width();
    $scrollbar.width(width);
    $scrollbar.find('.innerScrollbar').width($scrollable.children('table').width());
    var x = $scrollbar.get(0).scrollLeft;
    $scrollable.get(0).scrollLeft = x;
    $adminTable.find('.header-copy').css({
        'margin-left': -x - 1,
        clip: 'rect(0, ' + (width + x) + 'px, auto, ' + x + 'px)'
    });
}
function resizeHead($table) {
    $table.find('thead.header > tr:first > th').each(function (i, h) {
        $table.find('thead.header-copy > tr > th:eq(' + i + ')').css({
            width: $(h).outerWidth(),
            display: $(h).css('display')
        });
    });
    $table.find('thead.header-copy').css('width', $table.outerWidth());
}
function updateAllAdminTables() {
    $('.admin-table').each(function () {
        updateScrollbar($(this));
        resizeHead($(this).find('table'));
    });
}
function addAdminTableRowStub(newId, formId) {
    var $newItemStub = $('<tr>').attr('id', newId);
    var $form = $('#' + formId);
    var $selected = $form.find('tr.info');
    if ($selected.length)
        $selected.after($newItemStub);
    else
        $form.find('tbody').append($newItemStub);
    toggleAllColumnsVisibility();
    $(window).resize();
}
function removeAdminTableRow(formId) {
    var selected = $('#' + formId).find('tr.info');
    var selectNext = selected.next();
    if (!selectNext.length) selectNext = selected.prev();
    selected.detach();
    selectNext.click();
}

$(document).on('click', '.admin-table tbody tr', function () {
    var $row = $(this);
    var $adminBlock = $row.closest('.admin-block');
    $adminBlock.find('tr.info').removeClass('info');
    $row.addClass('info');
    $.event.trigger('selectionChange', [$row.index()], $adminBlock.get(0), false);
}).on('click', '.column-visibility-checkbox', function () {
    toggleColumnVisibility($(this));
    $(window).resize();
});

$(window).on('reinitAffix', function () {
    $('.admin-table').each(function () {
        var $adminTable = $(this);
        $adminTable.find('.scrollbar').removeData('bs.affix').removeClass('affix affix-top affix-bottom').css('top', '').affix({
            offset: {
                top: function () {
                    return $adminTable.find('.scrollable').offset().top - $(window).height() + $('#scrollbar').height();
                },
                bottom: function () {
                    var $scrollable = $adminTable.find('.scrollable');
                    return $(document).height() - $scrollable.offset().top - $scrollable.outerHeight();
                }
            }
        });
        var $adminBlock = $adminTable.closest('.admin-block');
        var $adminHeader = $adminBlock.find('.admin-header');
        $adminTable.find('.header-copy').removeData('bs.affix').removeClass('affix affix-top affix-bottom').css('top', '').affix({
            offset: {
                top: $adminBlock.offset().top,
                bottom: $(document).height() - $adminBlock.offset().top - $adminBlock.outerHeight() + $adminHeader.outerHeight() + 50
            }
        });
    });
}).resize(updateAllAdminTables);

$(function () {
    $(".admin-table .table").colResizable({
        fixed: false,
        liveDrag: false,
        postbackSafe: true,
        headerOnly: false,
        overflow: true,
        onResize: function () {
            $(window).resize();
        }
    }).each(function () {
        var $header = $(this).find('.header');
        $header.clone().removeClass('header').addClass('header-copy').insertAfter($header);
    });
    $('.scrollbar').on('scroll', function () {
        updateScrollbar($(this).closest('.admin-table'))
    });
    toggleAllColumnsVisibility();
    updateAllAdminTables();
    $(window).resize();
});
