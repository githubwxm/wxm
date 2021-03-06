var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        del: function (id) {
            var self = this;
            var param = {};
            param.id = id;
            var defrend = fnr.ajaxJson("/voucher/api/qr/delete", param, {method: "GET"});
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
            var self = this;
            var params = {
                title: "修改二维码模板",
                width: 1200,
                height: 800,
                url: "/voucher/qr/editqr.html?id=" + id,
                callbackSucc: function (json) {
                    self.subFormData.supplyprod_id = json.id;
                    self.prop.supplyprod_name = json.name;
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
                        return fnr.ajaxJson("/voucher/api/qr/selectQrList", params, options);
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
