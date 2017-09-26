var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            subFormData: {
                name: '-',
                code: '',
                device_group_id: fnr.getQueryString("groupId")
            },
            componentid__: fnr.getQueryString("componentid__")
        },
        ready: function () {

        },
        methods: {
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
<<<<<<< HEAD
                    var defrend = fnr.ajaxJson("../api/device/addDevice", self.subFormData, {});
=======
                    var defrend = fnr.ajaxJson("/voucher/api/device/addDevice", self.subFormData, {});
>>>>>>> fix_master
                    defrend.then(function (result) {
                        var resp = result.json();
                        if (resp.code == 200) {
                            fnr.iDialogSucc(self.componentid__);
                        }
                    });
                }
            }
        }
    });
});
