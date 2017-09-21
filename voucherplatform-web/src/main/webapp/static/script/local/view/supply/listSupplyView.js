var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        auth: function (supplyId) {
            var params = {
                title: '供应商授权查看',
                url: '/role/listRole.html?supplyId=' + supplyId,
                width: 1200,
                height: 800
            };
            fnr.iDialog(params);
        },
        prodSyncMode: function (ticketSysId, prodAddType) {
            if (ticketSysId == 0 || ticketSysId == '') {
                return "-";
            } else if (prodAddType == "1" || prodAddType == 1) {
                return "手动";
            } else if (prodAddType == "0" || prodAddType == 0) {
                return "自动";
            }
            return "-";
        },
        bindTicketSys: function (supplyId, ticketSysId) {
            var self = this;
            var params = {
                title: '选择票务系统',
                url: '/ticketSys/listTicketSys.html?supplyId=' + supplyId + '&ticketSysId=' + ticketSysId,
                width: 1200,
                height: 800,
                callbackSucc: function () {
                    self.$parent.reload();
                }
            };
            fnr.iDialog(params);
        },
        showProd: function (id) {
            var params = {
                title: '查看票务产品',
                url: '/supply/listSupplyProd.html?supplyId=' + id,
                width: 1200,
                height: 800,
            };
            fnr.iDialog(params);
        },
        editConf: function (supplyId) {
            var params = {
                title: '修改配置信息',
                url: "/supply/editConf.html?supplyId=" + supplyId,
                width: 800,
                height: 600
            };
            fnr.iDialog(params);
        },
        editSignType: function (supplyId) {
            var params = {
                title: '修改签名方式',
                url: "/supply/editSignType.html?supplyId=" + supplyId,
                width: 800,
                height: 600
            };
            fnr.iDialog(params);
        },
        syncProd: function (id) {
            var param = {};
            param.supplyId = id;
            var defrend = fnr.ajaxJson("../api/supply/syncProd", param, {method: "GET"});
            defrend.then(function (result) {
                var resp = result.json();
                if (resp.code == 200) {
                    fnr.alert('同步产品操作已提交请求');
                } else {
                    fnr.alert(resp.message);
                }
            });
        },
        downConf: function (id) {
            window.open("../api/supply/downConf?supplyId=" + id);
        },
        getSignType: function (signType) {
            return this.$parent.signType[signType].toUpperCase();
        },
        select: function (id, name) {
            var componentid__ = fnr.getQueryString("componentid__");
            fnr.iDialogSucc(componentid__, {id: id, name: name});
        }
    });

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            dtSetting: {
                remote: {
                    link: function (params, options) {
                        return fnr.ajaxJson("../api/supply/selectSupplyList", params, options);
                    },
                    options: {method: 'GET', alertMessage: 0},
                    isLoadOnPageInit: false
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            queryFormData: {
                name: "",
            },
            signType: []
        },
        ready: function () {
            this.loadSignType();
        },
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
