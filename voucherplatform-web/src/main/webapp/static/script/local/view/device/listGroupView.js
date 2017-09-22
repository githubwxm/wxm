var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        getSignType: function (signType) {
            return this.$parent.signType[signType].toUpperCase();
        },
        addDevice: function (id) {
            var self = this;
            var params = {
                title: '添加设备',
                url: '/device/addDevice.html?groupId=' + id,
                width: 800,
                height: 400,
                callbackSucc: function () {
                    self.$parent.reload();
                }
            };
            fnr.iDialog(params);
        },
        bindProd: function (id, supplyId) {
            var params = {
                title: '绑定可验证的产品',
                url: '/device/bindProd.html?groupId=' + id + '&supplyId=' + supplyId,
                width: 1200,
                height: 800,
            };
            fnr.iDialog(params);
        },
        showDevice: function (id) {
            var self = this;
            var params = {
                title: '查看已添加的设备',
                url: '/device/listDevice.html?groupId=' + id,
                width: 1200,
                height: 800,
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
                        return fnr.ajaxJson("../api/device/selectGroupList", params, options);
                    },
                    options: {method: 'GET', alertMessage: 0},
                    isLoadOnPageInit: false
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            queryFormData: {
                // supplyId: fnr.getQueryString("supplyId"),
                // platformId: fnr.getQueryString("platformId"),
                // authId: '',
                // authKey: '',
                // code: '',
                // name: ''
            },
            isDialog: fnr.getQueryString("componentid__") != undefined,
            componentid__: fnr.getQueryString("componentid__"),

            signType: []
        },
        ready: function () {
            this.loadSignType();
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
    })
    ;
});
