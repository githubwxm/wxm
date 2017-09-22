var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        edit: function (id) {
            location.href = "../prod/editSub.html?productSubId=" + id;
        },
        subProdName: function (id) {
            var params = {title: '票据详情', url: '../prod/viewSubProd.html?productSubId=' + id, width: 1200, height: 800};
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
                        return fnr.ajaxJson("../api/action/selectRoleList", params, options);
                    },
                    options: {method: 'GET', alertMessage: 0}
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            queryFormData: {
                supplyId: fnr.getQueryString("supplyId"),
                platformId: fnr.getQueryString("platformId"),
                authId: '',
                authKey: '',
                code: '',
                name: ''
            },
            isDialog: fnr.getQueryString("componentid__") != undefined,
            componentid__: fnr.getQueryString("componentid__")
        },
        ready: function () {
        }
        ,
        methods: {
            queryFormSubmit: function () {
                this.$refs.dtList.query();
            }
            ,
            createRoleButton: function () {
                window.location = "addRole.html?supplyId=" + this.queryFormData.supplyId + "&componentid__=" + this.componentid__;
            }
            ,
            reload: function () {
                this.$refs.dtList.query();
            }
        }
    })
    ;
});
