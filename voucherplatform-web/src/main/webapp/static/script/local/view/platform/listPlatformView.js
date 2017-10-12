/**
 * Created by Linv2 on 2017-07-06.
 */
var rootVue;
require_js_file(['vueValidator', 'vuePicker',], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        showProd: function (id) {
            var params = {
                title: '查看平台商产品',
                url: "/voucher/platform/listPlatformProd.html?platformId=" + id,
                width: 1200,
                height: 800
            };
            fnr.iDialog(params);
        },
        showSupply: function (id) {
            var params = {
                title: '查看已授权商户',
                url: "/voucher/platform/listRoleSupply.html?platformId=" + id,
                width: 1200,
                height: 800
            };
            fnr.iDialog(params);
        }
    });

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            dtSetting: {
                //page:{record_count:20},
                remote: {
                    link: function (params, options) {
                        return fnr.ajaxJson("/voucher/api/action/selectPlatformList", params, options);
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
