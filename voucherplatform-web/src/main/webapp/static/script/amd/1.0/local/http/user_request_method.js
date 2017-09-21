/**
 * Created by Linv2 on 2017-07-04.
 */
define(['fnr'], function (fnr) {
    fnr.request.login = function (params, options) {
        return fnr.ajaxJson('api/user/login', params, options);
    };
    fnr.request.getLoginUser = function (params, options) {
        return fnr.ajaxJson('api/user/getLoginUser', params, {method: "GET"});
    };
    fnr.request.updatePassword = function (params, options) {
        return fnr.ajaxJson('api/user/updatePassword', params, options);
    };
});
