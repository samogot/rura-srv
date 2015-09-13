/**
 * Created by samogot on 28.08.15.
 */
$(window).on('reinitAffix', function () {
    $('.admin-affix').each(function () {
        var self = $(this);
        $(this).removeData('bs.affix').removeClass('affix affix-top affix-bottom').css('top', '').affix({
            offset: {
                top: function () {
                    return self.parent().offset().top - 53;
                }, // в открепленнойм стиле у меня стоит отступ 10 пикселов от верхнего края экрана, знасит откреплять мы начинаем за 10 пикселей от позиции родительского елемента
                bottom: function () {
                    return $(document).height() - self.parent().outerHeight(true) - self.parent().offset().top;
                } // нижняя граница, конда плагину нужно снова закрепить admin-affix считается по разнице нижней граници родительского елемента и нижнего края экрана
            } // то есть мы аффиксируем admin-affix в пределах его родительского елемента. предпологается что размер родительского елемента растягивается за счет наполнения list-group.select, но нужно где то явно указать min-height что бы в случае если елементов в list-group.select меньше чем высота самого admin-affix, родительский елемень не схлопнулся при откреплении admin-affix
        });
    });
});

$(function () {
    $('.list-group.select.sortable:not(.ui-sortable)').sortable({ // включаем jquery-ui sortable
        items: "a:not(:first-child.heading)", // запрещаем передвигать что либо выше первого heading (не может быть картинок вне глав)
        handle: '.move' // задаем маркер перетягивания
    }).on("sortupdate", function (event, ui) { // для списка изображений задаем обработчик перетягивания
        var order = 0; // теперь нам нужно для каждого изображения поставить его порядковый номер и id главы к которой оно принадлежит
        $(this).children().each(function () { // вообще делать это линейно для всех елементов при любом изменении не очень хорошо. правельные было бы обновлять только для тех изображений для которых что-то изменилось. но мне было лень
            $($(this).attr('href')).find('.order-number').val(++order); // посчитали порядок
        });
    });
});

function addFormItemStub(newId, formId) {
    var newItemStub = $('<div>').attr('id', newId);
    $('#' + formId).find('.admin-affix').append(newItemStub);
}
function addSelectorItemStub(newId, formId) {
    var newItemStub = $('<a>').attr('id', newId);
    var listGroup = $('#' + formId).find('.list-group.select');
    var selected = listGroup.filter('.sortable').find('.list-group-item.active');
    if (selected.length)
        selected.after(newItemStub);
    else
        listGroup.append(newItemStub);
}
function removeAdminAffixItem(formId) {
    var selected = $('#' + formId).find('.list-group.select .list-group-item.active');
    var selectNext = selected.nextAll(':not(.heading)').eq(0);
    if (!selectNext.length) selectNext = selected.prevAll(':not(.heading)').eq(0);
    $(selected.attr('href')).parent().detach();
    selected.detach();
    selectNext.click();
}
function setMoveHandlerPadding(itemId) {
    var $item = $('#' + itemId);
    var $moveHandler = $item.find('.move.fa.fa-ellipsis-v');
    var padding = ($item.height() - $moveHandler.height()) / 2;
    $moveHandler.css({'padding-top': padding, 'padding-bottom': padding});
}

$(document).on('keyup', '.name-input', function () {
    var input = $(this);  // засовываем этот объект в переменную (на всякий случай)
    var itemId = input.closest('.form-item').attr('id');
    var selectItem = $('.list-group-item[href="#' + itemId + '"]');
    var nameLabel = selectItem.children('.name-label');
    nameLabel.text(input.val());
}).on('click', 'a.list-group-item', function (event) {
    var adminBlock = $(this).closest('.admin-block');
    adminBlock.find('.form-item.in').collapse('hide');
    var formItem = $($(this).attr('href'));
    formItem.find('.name-input').focus();
    event.preventDefault();
    $.event.trigger('selectionChange', [$(this).index()], adminBlock.get(0), false);
    formItem.one('hidden.bs.collapse.rura', function () {
        formItem.collapse('show');
        formItem.off('shown.bs.collapse.rura');
    }).one('shown.bs.collapse.rura', function () {
        formItem.off('hidden.bs.collapse.rura');
    });
});

