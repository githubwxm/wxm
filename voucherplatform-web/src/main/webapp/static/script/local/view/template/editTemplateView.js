var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            subFormData: {
                name: '',
                sms: '',
                printText: ''
            },
            id: fnr.getQueryString("id"),
            componentid__: fnr.getQueryString("componentid__"),
            readonly: false
        },
        ready: function () {
            if (this.id != undefined) {
                this.loadData();
            }
        },
        methods: {
            loadData: function () {
                var self = this;
                var param = {};
                param.id = self.id;
<<<<<<< HEAD
                var defrend = fnr.ajaxJson("../api/template/get", param, {method: "GET"});
=======
                var defrend = fnr.ajaxJson("/voucher/api/template/get", param, {method: "GET"});
>>>>>>> fix_master
                defrend.then(function (result) {
                    var resp = result.json();
                    fnr.mergeJson(resp.data, self.subFormData);
                    self.readonly = resp.data.defaultOption == '1';
                });
            },
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
<<<<<<< HEAD
                    var defrend = fnr.ajaxJson("../api/template/update", self.subFormData, {});
=======
                    var defrend = fnr.ajaxJson("/voucher/api/template/update", self.subFormData, {});
>>>>>>> fix_master
                    defrend.then(function (result) {
                        var resp = result.json();
                        if (resp.code == 200) {
                            fnr.alert('操作成功');
                            fnr.iDialogSucc(self.componentid__);
                        } else {
                            fnr.alert(resp.message);
                        }
                    });
                }
            }
        }
    });
});
