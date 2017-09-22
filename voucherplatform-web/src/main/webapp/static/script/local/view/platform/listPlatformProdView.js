/**
 * Created by Linv2 on 2017-07-07.
 */
var rootVue;
require_js_file(['vueValidator', 'vuePicker',], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        auth: function () {
            alert(0);
        },
        edit: function (id) {
            location.href = "/prod/editSub.html?productSubId=" + id;
        },
        subProdName: function (id) {
            var params = {title: '票据详情', url: '/prod/viewSubProd.html?productSubId=' + id, width: 1200, height: 800};
            fnr.iDialog(params);
        }
    });

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            prodTypeSetting: {
                options: [
                    {key: '', value: '--请选择--'},
                ],
                remote: {
                    link: 'api/action/selectProdTyeList',
                    options: {method: 'GET', alertMessage: 0}
                },
                fields: {root: 'data.list', key: 'id', value: 'name'}
            },
            dtSetting: {
                //page:{record_count:20},
                remote: {
                    link: function (params, options) {
                        return fnr.ajaxJson("../api/action/selectPlatformProdList", params, options);
                    },
                    options: {method: 'GET', alertMessage: 0}
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            queryFormData: {
                name: "",
                supplyId: null,
                platformId: fnr.getQueryString("platformId"),
                supplyprodId: null,
                platformProdCode: null,
                prodType: null

            },
            isDialog: fnr.getQueryString("componentid__") != undefined
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
