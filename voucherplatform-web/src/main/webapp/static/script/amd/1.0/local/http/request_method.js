define(['fnr'], function (fnr) {
    fnr.request.login = function (params, options) {
        return fnr.ajaxJson('/voucher/api/user/login', params, options);
    };
    fnr.request.getLoginUser = function (params, options) {
        return fnr.ajaxJson('/voucher/api/user/getLoginUser', params, {method: "GET"});
    };
    fnr.request.upload = function (params, options) {
        return fnr.ajaxJson('/voucher/api/local/client/upload', params, options);
    };
    fnr.request.params = function (params, options) {
        return fnr.ajaxJson('/voucher/api/local/client/param', params, options);
    };
    fnr.request.param = function (params, options) {
        return fnr.ajaxJson('/voucher/api/local/client/param/params', params, options);
    };
});
