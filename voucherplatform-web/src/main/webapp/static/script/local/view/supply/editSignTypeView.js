/**
 * Created by Linv2 on 2017-07-07.
 */
var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            signTypeSetting: {
                options: [
                    {key: '', value: '--请选择--'},
                ],
                remote: {
                    link: '../api/supply/getSignType',
                    options: {method: "GET"}

                },
                fields: {root: 'data', key: 'value', value: 'name'}
            },
            subFormData: {
                id: fnr.getQueryString("supplyId"),
                signType: '',
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
                var defrend = fnr.ajaxJson("../api/supply/get", param, {method: "GET"});
                defrend.then(function (result) {
                    var resp = result.json();
                    if (resp.code == 200) {
                        self.subFormData.signType = resp.data.signType;
                        // self.callBack();
                    }
                });
            },
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
                    var defrend = fnr.ajaxJson("../api/supply/updateSignType", self.subFormData, {});
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
