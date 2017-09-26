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
                code: '',
                name: '',
                description: ''
            },
            supplyId: fnr.getQueryString("supplyId"),
            prodId: fnr.getQueryString("prodId"),
            edit: false,
            componentid__: fnr.getQueryString('componentid__')
        },
        ready: function () {
            var self = this;
            if (self.supplyId == undefined) {
                fnr.alertErr("参数错误");
            }
            if (self.prodId != undefined) {
                self.loadData();
            }


        }
        ,
        methods: {
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
                    self.subFormData.supplyId = self.supplyId;
<<<<<<< HEAD
                    var defrend = fnr.ajaxJson("../api/supply/setProd", self.subFormData);
=======
                    var defrend = fnr.ajaxJson("/voucher/api/supply/setProd", self.subFormData);
>>>>>>> fix_master
                    defrend.then(function (result) {
                        var resp = result.json();
                        if (resp.code == 200) {
                            fnr.alert("操作成功");
                            self.callBack();
                        } else {
                            fnr.alert(resp.message);
                        }
                    });
                }
            }
            ,
            callBack: function () {
                if (this.prodId == undefined) {
                    window.location = "listSupplyProd.html?supplyId=" + this.supplyId + "&componentid__=" + this.componentid__;
                } else {
                    fnr.iDialogSucc(this.componentid__);
                }
            }
            ,
            loadData: function () {
                var self = this;
                var param = {};
                param.supplyId = self.supplyId;
                param.prodId = self.prodId;
<<<<<<< HEAD
                var defrend = fnr.ajaxJson("../api/supply/getProd", param, {method: "GET"});
=======
                var defrend = fnr.ajaxJson("/voucher/api/supply/getProd", param, {method: "GET"});
>>>>>>> fix_master
                defrend.then(function (result) {
                  //  self.edit = result.code == 200;
                    var resp = result.json();
                    self.edit = true;
                    self.subFormData.code = resp.data.code;
                    self.subFormData.name = resp.data.name;
                    self.subFormData.description = resp.data.description;
                });
            }
        }
    })
    ;
});
