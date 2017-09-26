/**
 * Created by xiangzw on 2017/5/10.
 */
var rootVue;
require_js_file(['vueValidator'], function (Vue, fnr, validator) {
    Vue.use(validator);
    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            subFormData: {
                oldPassword: '',
                password: '',
                password2: ''
            }
        },
        //页面初始化加载
        ready: function () {
            this.componentid__ = fnr.getQueryString('componentid__');
        },
        methods: {
            save: function () {
                this.$validate();
                if (!this.$checkSubForm.valid) return;

                var self = this;
                var params = {};
                params.oldPassword = self.subFormData.oldPassword;
                params.passWord = self.subFormData.password;
                params.password2 = self.subFormData.password2;
                if (params.passWord != params.password2) {
                    fnr.alertErr('要修改的密码和确认密码不一致！');
                    return;
                }
<<<<<<< HEAD
                var deferred = fnr.ajaxJson('api/user/updatePassword', params);
=======
                var deferred = fnr.ajaxJson('/voucher/api/user/updatePassword', params);
>>>>>>> fix_master
                deferred.then(function (result) {
                    var resp = result.json();
                    if (resp.code == 200)
                    {
                        alert("密码修改成功，请重新登录");
                        window.top.location='/';
                    }
                }, function (error) {
                    fnr.alertErr('密码修改失败！');
                }).catch(function (exception) {
                    fnr.alertErr('服务器异常!');
                });
            },
            succ: function () {
                fnr.iDialogSucc(this.componentid__);
            },
            cancel: function () {
                fnr.iDialogCancel(this.componentid__);
            }
        }
    });
});