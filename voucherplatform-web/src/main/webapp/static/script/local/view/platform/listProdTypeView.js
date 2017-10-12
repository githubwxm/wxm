/**
 * Created by Linv2 on 2017-07-07.
 */
var rootVue;
require_js_file(['vueValidator', 'vuePicker',], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        edit: function (id) {
            var self = this;
            var params = {
                title: '添加分类',
                url: '/voucher/platform/addProdType.html?id=' + id,
                width: 1200,
                height: 800,
                callbackSucc: function () {
                    self.$parent.reload();
                }
            };
            fnr.iDialog(params);
        },
        del: function (id) {
            var self = this;
            var param = {};
            param.prodTypeId = id;
            var defrend = fnr.ajaxJson("/voucher/api/action/delProdType", param, {method: "GET"});
            defrend.then(function (result) {
                var resp = result.json();
                if (resp.code == 200) {
                    self.$parent.reload();
                }
            });
        }
    });

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            dtSetting: {
                //page:{record_count:20},
                remote: {
                    link: function (params, options) {
                        return fnr.ajaxJson("/voucher/api/action/selectProdTyeList", params, options);
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
            },
            addProdType: function () {
                var self = this;
                var params = {
                    title: '添加分类',
                    url: '/voucher/platform/addProdType.html',
                    width: 1200,
                    height: 800,
                    callbackSucc: function () {
                        self.reload();
                    }
                };
                fnr.iDialog(params);
            }
        }
    });
});
