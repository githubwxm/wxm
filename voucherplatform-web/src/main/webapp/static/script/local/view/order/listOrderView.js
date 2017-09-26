/**
 * Created by Linv2 on 2017-07-07.
 */
/**
 * Created by Linv2 on 2017-07-07.
 */
var rootVue;
require_js_file(['vueValidator', 'vuePicker',], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        getSyncStatus: function (status) {
            if (status == "1") {
                return "已同步";
            } else {
                return "未同步";
            }
        }

    });

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            syncStatusSetting: {
                options: [
                    {key: '', value: '全部'},
                    {key: '1', value: '已同步'},
                    {key: '0', value: '未同步'},
                ],
                fields: {root: 'data.list', key: 'id', value: 'name'}
            },
            dtSetting: {
                //page:{record_count:20},
                remote: {
                    link: function (params, options) {
<<<<<<< HEAD
                        return fnr.ajaxJson("../api/order/selectOrderList", params, options);
=======
                        return fnr.ajaxJson("/voucher/api/order/selectOrderList", params, options);
>>>>>>> fix_master
                    },
                    options: {method: 'GET', alertMessage: 0}
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            queryFormData: {
                orderCode: null,
                platformOrderId: null,
                supplyOrderId: null,
                status: null,
                mobile: null,
                idNumber: null,
                voucherNumber: null
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
