/**
 * Created by Linv2 on 2017-07-07.
 */
var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            subFormData: {
                id: fnr.getQueryString("supplyId"),
                conf: '',
            },
            componentid__: fnr.getQueryString("componentid__")
        },
        ready: function () {
            if (this.subFormData.id != undefined) {
                this.loadData();
            }
        },
        methods: {
            loadData: function () {
                var self = this;
                var param = {};
                param.id = self.subFormData.id;
<<<<<<< HEAD
                var defrend = fnr.ajaxJson("../api/supply/get", param, {method: "GET"});
=======
                var defrend = fnr.ajaxJson("/voucher/api/supply/get", param, {method: "GET"});
>>>>>>> fix_master
                defrend.then(function (result) {
                    var resp = result.json();
                    if (resp.code == 200) {
                        self.subFormData.conf = resp.data.conf;
                        // self.callBack();
                    }
                });
            },
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
<<<<<<< HEAD
                    var defrend = fnr.ajaxJson("../api/supply/updateConf", self.subFormData, {});
=======
                    var defrend = fnr.ajaxJson("/voucher/api/supply/updateConf", self.subFormData, {});
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
            /*新增or修改主产品End*/
            //*成功回调*/
            callBack: function () {
                fnr.iDialogSucc(this.componentid__);
            }
        }
    });
});
