var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            subFormData: {
                sms: '',
                printText: '',
                supply_id: null,
                supplyprod_id: null,
            },
            prop: {
                supply_name: '',
                supplyprod_name: ''
            }
        },
        ready: function () {

        },
        methods: {
            querySupply: function () {
                var self = this;
                var params = {
                    title: "请选择供应商",
                    width: 1200,
                    height: 800,
                    url: "/voucher/supply/dialogSupply.html",
                    callbackSucc: function (json) {
                        self.subFormData.supply_id = json.id;
                        self.prop.supply_name = json.name;
                        self.subFormData.supplyprod_id = null;
                        self.prop.supplyprod_name = null;
                    }
                };
                fnr.iDialog(params);
            },
            clearSupply: function () {
                var self = this;
                self.subFormData.supply_id = null;
                self.prop.supply_name = null;
                self.subFormData.supplyprod_id = null;
                self.prop.supplyprod_name = null;
            },
            querySupplyProd: function () {
                var self = this;
                if (!self.subFormData.supply_id) {
                    fnr.alertErr("请先选择供应商");
                    return;
                }
                var params = {
                    title: "请选择供应商",
                    width: 1200,
                    height: 800,
                    url: "/voucher/supply/dialogSupplyProd.html?supplyId=" + self.subFormData.supply_id,
                    callbackSucc: function (json) {
                        self.subFormData.supplyprod_id = json.id;
                        self.prop.supplyprod_name = json.name;
                    }
                };
                fnr.iDialog(params);
            },
            clearSupplyProd: function () {
                self.subFormData.supplyprod_id = null;
                self.prop.supplyprod_name = null;
            },
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
                    var defrend = fnr.ajaxJson("/voucher/api/template/create", self.subFormData, {});
                    defrend.then(function (result) {
                        var resp = result.json();
                        if (resp.code == 200) {
                            fnr.alert('操作成功');
                            self.callBack();
                        } else {
                            fnr.alert(resp.message);
                        }
                    });
                }
            },
            callBack: function () {
                location.href = "/voucher/template/listTemplate.html";
            },
        }
    });
});
