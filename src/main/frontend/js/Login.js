/* АВТОРИЗАЦИЯ НА ФОРУМЕ */
$(document).ready(function () {
    $(".login-form").submit(function () {
        var login = $("#inputLoginModal").val().trim();
        var password = $("#inputPasswordModal").val().trim();
        if (login && password) {
            $.ajax({
                       url: '/f/api/user/login',
                       type: "post",
                       contentType: "application/json",
                       data: JSON.stringify({username: login, password: password}),
                       dataType: "json",
                       async: false
                   });
        }
    });
    $(".user-actions-form").submit(function () {
        $.ajax({
                   url: '/f/api/user/logout',
                   type: "get",
                   contentType: "application/json",
                   dataType: "json",
                   async: false
               });
    });
});