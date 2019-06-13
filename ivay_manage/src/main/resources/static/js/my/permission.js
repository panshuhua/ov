function checkPermission() {
    var pers = [];
    $.ajax({
        type: 'get',
        url: '/manage/permissions/owns',
        contentType: "application/json; charset=utf-8",
        async: false,
        success: function (data) {
            pers = data;
            $("[permission]").each(function () {
                var per = $(this).attr("permission");
                if ($.inArray(per, data) < 0) {
                    $(this).hide();
                }
            });
        }
    });
    return pers;
}

function getLoginAuditRole() {
    var pers = "";
    $.ajax({
        type: 'get',
        url: '/manage/permissions/getLoginAuditRole',
        contentType: "application/json; charset=utf-8",
        async: false,
        success: function (data) {
            pers = data;
        }
    });
    return pers;
}

function getAllAudit() {
    var pers = [];
    $.ajax({
        type: 'get',
        url: '/manage/xAuditUsers/listAudit',
        contentType: "application/json; charset=utf-8",
        async: false,
        success: function (data) {
            pers = data.data;
        }
    });
    return pers;
}