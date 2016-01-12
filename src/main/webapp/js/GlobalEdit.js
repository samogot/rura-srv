var users = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.nonword('username'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    remote: {
        url: '/api/users/search?q=%QUERY',
        wildcard: '%QUERY'
    }
});

function initUserTypeahead() {
    var $this = $(this);

    function onselect(e, data) {
        $this.find('.user-id-hidden').val(data.userId);
        $this.closest('.form-item').find('.role-select').prop('disabled', !data.userId);
        if (!data.userId)
            $this.find('.typeahead').typeahead('val', '').typeahead('close');
    }

    $this.find('.typeahead').typeahead({
        hint: true,
        highlight: true,
        minLength: 1
    }, {
        name: 'users',
        display: 'username',
        source: users,
        templates: {
            empty: function () {
                return '<div class="clear-user-message" align="center" style="width:100%">' +
                    'Пользователь не найден. Очистить' +
                    '</div>';
            },
            suggestion: function (data) {
                return '<div>' + data.username + '</div>';
            }
        }
    }).bind('typeahead:select', onselect).bind('typeahead:autocomplete', onselect);

    $this.on('click', '.clear-user-message', function (e) {
        return onselect(e, {});
    });
}

$('#teamMembers').on('addnewitem', function (e, d) {
    console.log(e, d);
    $(d.form).find('.username-input').each(initUserTypeahead);
});

$('.username-input').each(initUserTypeahead);

function initMemberTeamsLabels() {
    var $teamMembers = $('#teamMembers');
    var $listGroup = $teamMembers.find('.list-group.select');
    var $formItems = $teamMembers.find('.form-item');
    var $noTeamHeading = $('<a class="list-group-item heading">&lt;без команды&gt;</a>');
    $listGroup.prepend($noTeamHeading);
    $('#teams').find('.form-item').each(function () {
        var $teamHeading = $noTeamHeading.clone();
        var teamName = $(this).find('.name-input').val();
        $teamHeading.text(teamName);
        var firstId = $formItems.find('option:selected:contains("' + teamName + '"):eq(0)').closest('.form-item').prop('id');
        if (firstId)
            $listGroup.find('a[href="#' + firstId + '"]').before($teamHeading);
    });
}
$(initMemberTeamsLabels);