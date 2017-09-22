var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            lenSetting: {
                options: [
                    {key: '16', value: '16位'},
                    {key: '17', value: '17位'},
                    {key: '18', value: '18位'},
                    {key: '19', value: '19位'},
                    {key: '20', value: '20位'},
                ],
                fields: {root: 'data', key: 'id', value: 'name'}
            },
            sizeSetting: {
                options: [
                    {key: '100', value: '100px'},
                    {key: '200', value: '200px'},
                    {key: '300', value: '300px'},
                    {key: '400', value: '400px'},
                    {key: '500', value: '500px'},
                ],
                fields: {root: 'data', key: 'id', value: 'name'}
            },

            errorRateSetting: {
                options: [
                    {key: 'L', value: 'L[7%]'},
                    {key: 'M', value: 'M[15%]'},
                    {key: 'Q', value: 'Q[25%]'},
                    {key: 'H', value: 'H[30%]'},
                ],
                fields: {root: 'data', key: 'id', value: 'name'}
            },
            subFormData: {
                name: '',
                len: 16,
                prefix: '',
                postfix: '',
                errorRate: 'M',
                size: 200,
                foreColor: '#000',
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
                    url: "../supply/dialogSupply.html",
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
                    url: "../supply/dialogSupplyProd.html?supplyId=" + self.subFormData.supply_id,
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
                    var defrend = fnr.ajaxJson("../api/qr/create", self.subFormData, {});
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
                location.href = "../qr/listqr.html";
            },
        }
    });
});
