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
    $(d.form).find('.username-input').each(initUserTypeahead);
});

$('.username-input').each(initUserTypeahead);

function initProjectsWorksLabel() {
    var $projects = $('#projects');
    var $listGroup = $projects.find('.list-group.select');
    var $formItems = $projects.find('.form-item');
    var $worksHeading = $('<a class="list-group-item heading" id="works">Works</a>');

    var $contentsLi = $('<li><a href="#works">Works</a></li>');
    var $contentsUl = $('#nav').find('a[href="#projects"]+ul');

    var firstId = $formItems.find('.works-checkbox:checked:eq(0)').closest('.form-item').prop('id');
    if (firstId) {
        $listGroup.find('a[href="#' + firstId + '"]').before($worksHeading);
        $contentsUl.addClass('nav').addClass('ContentNav').css('display', '');
        $contentsUl.append($contentsLi);
    }
}
$(initProjectsWorksLabel);

function initMemberTeamsLabels() {
    var $teamMembers = $('#teamMembers');
    var $listGroup = $teamMembers.find('.list-group.select');
    var $formItems = $teamMembers.find('.form-item');
    var $noTeamHeading = $('<a class="list-group-item heading" id="no-team">&lt;без команды&gt;</a>');
    $listGroup.prepend($noTeamHeading);
    var $contentsUl = $('#nav').find('a[href="#teamMembers"]+ul');
    $contentsUl.addClass('nav').addClass('ContentNav').css('display', '');
    var $contentsLiTemplate = $('<li><a></a></li>');
    $('#teams').find('.form-item').each(function () {
        var teamName = $(this).find('.name-input').val();
        var teamAncor = translit(teamName);
        var $teamHeading = $noTeamHeading.clone();
        var $contentsLi = $contentsLiTemplate.clone();
        $teamHeading.text(teamName).attr('id', teamAncor);
        $contentsLi.find('a').text(teamName).attr('href', '#' + teamAncor);
        var firstId = $formItems.find('option:selected:contains("' + teamName + '"):eq(0)').closest('.form-item').prop('id');
        if (firstId) {
            $listGroup.find('a[href="#' + firstId + '"]').before($teamHeading);
            $contentsUl.append($contentsLi);
        }
    });
}
$(initMemberTeamsLabels);