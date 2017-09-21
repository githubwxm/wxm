var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            subFormData: {
                name: '',
                supply_id: null,
                description: null,
            },
            prop: {
                supply_name: '',
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
                    }
                };
                fnr.iDialog(params);
            },
            clearSupply: function () {
                var self = this;
                self.subFormData.supply_id = null;
                self.prop.supply_name = null;
            },
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
                    var defrend = fnr.ajaxJson("../api/device/addGroup", self.subFormData, {});
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
                location.href = "../device/listGroup.html";
            },
        }
    });
});
