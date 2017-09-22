/**
 * Created by Linv2 on 2017-07-06.
 */
var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        bind: function (id) {
            var self = this;
            var param = {};
            param.id = this.$parent.supplyId;
            param.ticketsysId = id;
            var deferred = fnr.ajaxJson("/voucher/api/supply/updateTicketSys", param);
            deferred.then(function (result) {
                var resp = result.json();
                if (resp.code == 200) {
                    fnr.alert(resp.message);
                    self.$parent.succ();
                }
            }, function (error) {
                fnr.alertErr('操作失败！');
            }).catch(function (exception) {
                fnr.alertErr('服务器异常!');
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
                        return fnr.ajaxJson("/voucher/api/ticketSys/selectTicketSysList", params, options);
                    },
                    options: {method: 'GET', alertMessage: 0}
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            queryFormData: {},
            supplyId: fnr.getQueryString("supplyId"),
            isDialog: fnr.getQueryString("componentid__") != undefined,
            componentid__: fnr.getQueryString("componentid__")
        },
        ready: function () {
        },
        methods: {
            succ: function () {
                fnr.iDialogSucc(this.componentid__);
            },
            queryFormSubmit: function () {
                this.$refs.dtList.query();
            },
            reload: function () {
                this.$refs.dtList.query();
            }
        }
    });
});
