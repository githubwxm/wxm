var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        join: function (id) {
            var self = this;
            var param = {};
            param.applyId = self.$parent.applyId;
            param.groupId = id;
            var defrend = fnr.ajaxJson("../api/device/deviceAudit", param);
            defrend.then(function (result) {
                var resp = result.json();
                if (resp.code == 200) {
                    fnr.iDialogSucc(self.$parent.componentid__);
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
                        return fnr.ajaxJson("../api/device/selectGroupList", params, options);
                    },
                    options: {method: 'GET', alertMessage: 0},
                    isLoadOnPageInit: false
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            queryFormData: {},
            signType: [],
            componentid__: fnr.getQueryString("componentid__"),
            applyId: fnr.getQueryString("id")
        },
        ready: function () {
            this.loadSignType();
        }
        ,
        methods: {
            queryFormSubmit: function () {
                this.$refs.dtList.query();
            },
            reload: function () {
                this.$refs.dtList.query();
            },
            loadSignType: function () {
                var self = this;
                var defrend = fnr.ajaxJson("../api/supply/getSignType", {}, {method: "GET"});
                defrend.then(function (result) {
                    var resp = result.json();
                    if (resp.code == 200) {
                        fnr.each(resp.data, function (k, v) {
                            self.signType[v.value] = v.name;
                        });
                        self.queryFormSubmit();
                    }
                });
            }
        }
    });
});
