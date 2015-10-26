/**
 * Created by samogot on 19.09.15.
 */
$(document).on('keyup change', '.updates-input', function () {
    var $formItem = $(this).closest('.form-item');
    var $chapterSelect = $formItem.find('select.updates-input');
    var $dateInput = $formItem.find('input.updates-input');
    setNameLabelText($formItem, $dateInput.val().split(' ')[0] + ': ' + $chapterSelect.children(':selected').text().replace('　', ''));
}).on('keyup change', '.member-input', function () {
    var $formItem = $(this).closest('.form-item');
    var $memberInput = $formItem.find('input.member-input');
    var $activitySelect = $formItem.find('select.member-input');
    setNameLabelText($formItem, $memberInput.val() + ' - ' + $activitySelect.children(':selected').text());
}).on('change', '.nested-checkbox', function () {
    var $input = $(this);
    var $formItem = $input.closest('.form-item');
    findNameLabel($formItem).toggleClass('sub-chapter', $input.is(':checked'));
});