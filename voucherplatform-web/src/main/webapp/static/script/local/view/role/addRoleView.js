var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);
    fnr.dataPageTable('data-table-dyn', {
        addRole: function (id) {
            var self = this;
            var param = {};
            param.supplyId = self.$parent.supplyId;
            param.platformId = id;
<<<<<<< HEAD
            var deferred = fnr.ajaxJson("../api/action/createRole", param);
=======
            var deferred = fnr.ajaxJson("/voucher/api/action/createRole", param);
>>>>>>> fix_master
            deferred.then(function (result) {
                var resp = result.json();
                if (resp.code == 200) {
                    fnr.alert(resp.message);
                    window.location = 'listRole.html?supplyId=' + self.$parent.supplyId + '&componentid__=' + self.$parent.componentid__;
<<<<<<< HEAD
                    self.$parent.succ();
=======
					try{
						self.$parent.succ();	
					}catch(e){
						
					}
>>>>>>> fix_master
                }
            }, function (error) {
                fnr.alertErr('操作失败！');
            }).catch(function (exception) {
                fnr.alertErr('服务器异常!');
            });
        },
        subProdName: function (id) {
<<<<<<< HEAD
            var params = {title: '票据详情', url: '/prod/viewSubProd.html?productSubId=' + id, width: 1200, height: 800};
=======
            var params = {title: '票据详情', url: '/voucher/prod/viewSubProd.html?productSubId=' + id, width: 1200, height: 800};
>>>>>>> fix_master
            fnr.iDialog(params);
        }
    });

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            dtSetting: {
                //page:{record_count:20},
                remote: {
                    link: function (params, options) {
<<<<<<< HEAD
                        return fnr.ajaxJson("../api/action/selectPlatformList", params, options);
=======
                        return fnr.ajaxJson("/voucher/api/action/selectPlatformList", params, options);
>>>>>>> fix_master
                    },
                    options: {method: 'GET', alertMessage: 0}
                }
                //result:{dataRoot:'data.list',pageRoot:'data'},
            },
            setting: {},
            isDialog: fnr.getQueryString("componentid__") != undefined,
            componentid__: fnr.getQueryString("componentid__"),
            supplyId: fnr.getQueryString("supplyId"),
            isDialog: fnr.getQueryString("componentid__") != undefined
        },
        ready: function () {
        }
        ,
        methods: {
            queryFormSubmit: function () {
                this.$refs.dtList.query();
            }
            ,
            back: function () {
                window.location = "listRole.html?supplyId=" + this.supplyId + "&componentid__=" + this.componentid__;
            },
            reload: function () {
                this.$refs.dtList.query();
            }
        }
    })
    ;
});
