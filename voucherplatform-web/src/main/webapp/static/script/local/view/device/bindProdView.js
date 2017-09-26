/**
 * Created by Linv2 on 2017-07-19.
 */
/**
 * Created by Linv2 on 2017-07-06.
 */
var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        bind: function (prodId, status) {
            var self = this;
            var param = {};
            param.groupId = self.$parent.queryFormData.groupId;
            param.list = new Array();
            param.list.push({prodId: prodId, status: status});
<<<<<<< HEAD
            var defrend = fnr.ajaxJson("../api/device/setProd", param);
=======
            var defrend = fnr.ajaxJson("/voucher/api/device/setProd", param);
>>>>>>> fix_master
            defrend.then(function (result) {
                var resp = result.json();
                if (resp.code == 200) {
                    self.reload();
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
<<<<<<< HEAD
                        return fnr.ajaxJson("../api/device/getProd", params, options);
=======
                        return fnr.ajaxJson("/voucher/api/device/getProd", params, options);
>>>>>>> fix_master
                    },
                    options: {method: 'GET', alertMessage: 0},
                    //isLoadOnPageInit: false
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            queryFormData: {
                supplyId: fnr.getQueryString("supplyId"),
                groupId: fnr.getQueryString("groupId")
            },
        },
        ready: function () {
            /// this.loadBindProd();
        }
        ,
        methods: {
            loadBindProd: function () {
                var self = this;
                var param = {};
                param.groupId = self.groupId;
<<<<<<< HEAD
                var defrend = fnr.ajaxJson("../api/device/getProd", param, {method: "GET"});
=======
                var defrend = fnr.ajaxJson("/voucher/api/device/getProd", param, {method: "GET"});
>>>>>>> fix_master
                defrend.then(function (result) {
                    console.log(resp);
                    var resp = result.json();
                    if (resp.code == 200) {
                        self.reload();
                    }
                });
            }
            ,
            queryFormSubmit: function () {
                this.$refs.dtList.query();
            }
            ,
            reload: function () {
                this.$refs.dtList.query();
            }
        }
    })
    ;
});
