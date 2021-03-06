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
                var defrend = fnr.ajaxJson("/voucher/api/template/get", param, {method: "GET"});
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
                    var defrend = fnr.ajaxJson("/voucher/api/template/update", self.subFormData, {});
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
