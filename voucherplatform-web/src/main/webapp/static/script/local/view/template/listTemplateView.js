var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        del: function (id) {
            var self = this;
            var param = {};
            param.id = id;
<<<<<<< HEAD
            var defrend = fnr.ajaxJson("../api/template/delete", param, {method: "GET"});
=======
            var defrend = fnr.ajaxJson("/voucher/api/template/delete", param, {method: "GET"});
>>>>>>> fix_master
            defrend.then(function (result) {
                var resp = result.json();
                if (resp.code == 200) {
                    fnr.alert('操作成功');
                    self.reload();
                } else {
                    fnr.alert(resp.message);
                }
            });

        },
        edit: function (id) {
            var self=this;
            var params = {
                title: "修改二维码模板",
                width: 1200,
                height: 800,
<<<<<<< HEAD
                url: "../template/editTemplate.html?id=" + id,
=======
                url: "/voucher/template/editTemplate.html?id=" + id,
>>>>>>> fix_master
                callbackSucc: function (json) {
                    self.reload();
                }
            };
            fnr.iDialog(params);
        }
    });

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            dtSetting: {
                remote: {
                    link: function (params, options) {
<<<<<<< HEAD
                        return fnr.ajaxJson("../api/template/selectTemplateList", params, options);
=======
                        return fnr.ajaxJson("/voucher/api/template/selectTemplateList", params, options);
>>>>>>> fix_master
                    },
                    options: {method: 'GET', alertMessage: 0}
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            queryFormData: {
                name: "",
            },
        },
        ready: function () {
        },
        methods: {
            queryFormSubmit: function () {
                this.$refs.dtList.query();
            },
            reload: function () {
                this.$refs.dtList.query();
            }
        }
    });
});
