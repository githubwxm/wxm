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
<<<<<<< HEAD
                    url: "../supply/dialogSupply.html",
=======
                    url: "/voucher/supply/dialogSupply.html",
>>>>>>> fix_master
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
<<<<<<< HEAD
                    var defrend = fnr.ajaxJson("../api/device/addGroup", self.subFormData, {});
=======
                    var defrend = fnr.ajaxJson("/voucher/api/device/addGroup", self.subFormData, {});
>>>>>>> fix_master
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
<<<<<<< HEAD
                location.href = "../device/listGroup.html";
=======
                location.href = "/voucher/device/listGroup.html";
>>>>>>> fix_master
            },
        }
    });
});
