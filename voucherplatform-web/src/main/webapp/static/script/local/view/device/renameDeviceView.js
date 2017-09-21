var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            subFormData: {
                name: decodeURI(fnr.getQueryString("name")),
                id: fnr.getQueryString("id")
            },
            componentid__: fnr.getQueryString("componentid__")
        },
        ready: function () {

        }
        ,
        methods: {
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
                    var defrend = fnr.ajaxJson("../api/device/renameDevice", self.subFormData, {});
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
