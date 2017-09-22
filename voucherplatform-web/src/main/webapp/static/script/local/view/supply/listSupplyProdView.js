/**
 * Created by Linv2 on 2017-07-06.
 */
var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        edit: function (supplyId, prodId) {
            var self = this;
            var params = {
                title: '票种编辑',
                url: '../supply/addSupplyProd.html?supplyId=' + supplyId + '&prodId=' + prodId,
                width: 1200,
                height: 800,
                callbackSucc: function () {
                    self.$parent.reload();
                }
            };
            fnr.iDialog(params);
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
                //page:{record_count:20},
                remote: {
                    link: function (params, options) {
                        return fnr.ajaxJson("../api/supply/selectSupplyProdList", params, options);
                    },
                    options: {method: 'GET', alertMessage: 0}
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            queryFormData: {
                prodCode: "",
                supplyId: fnr.getQueryString("supplyId")
            },
            isDialog: fnr.getQueryString("componentid__") != undefined,
            showAddProd: false
        },
        ready: function () {
            var self = this;
            if (self.queryFormData.supplyId != undefined) {
                self.loadSupply();
            }

        },
        methods: {
            loadSupply: function () {
                var self = this;
                var param = {};
                param.supplyId = self.queryFormData.supplyId;
                var defrend = fnr.ajaxJson("../api/supply/selectSupply", param, {method: "GET"});
                defrend.then(function (result) {
                    var resp = result.json();
                    if (resp.code == 200) {
                        if (resp.data.prodAddType == "1") {
                            self.showAddProd = true;
                        }
                    }
                });
            },
            queryFormSubmit: function () {
                this.$refs.dtList.query();
            },
            reload: function () {
                this.$refs.dtList.query();
            },
            addProd: function () {

                window.location = "addSupplyProd.html?supplyId=" + this.queryFormData.supplyId + "&componentid__=" + this.componentid__;
            }
        }
    });
});
