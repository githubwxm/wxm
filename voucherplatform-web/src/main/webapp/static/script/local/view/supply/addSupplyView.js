var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            provinceSetting: {
                options: [
                    {key: '', value: '--请选择--'},
                ],
                fields: {root: 'data', key: 'id', value: 'name'}
            },
            citySetting: {
                options: [
                    {key: '', value: '--请选择--'}
                ],
                fields: {root: 'data', key: 'id', value: 'name'}
            },
            areaSetting: {
                options: [
                    {key: '', value: '--请选择--'}
                ],
                fields: {root: 'data', key: 'id', value: 'name'}
            },
            upSetting: {
                amount: 5
            },
            queruFormData: {
                product_id: ''
            },
            subFormData: {
                name: '',
                region: '',
                address: '',
                phone: '',
                agent: '',
                description: '',
                area: '',
                props: {
                    province: '',
                    city: '',
                    area: ''
                }
            },
            provinceInit: 0,//省初始化状态
            cityInit: 0,//市初始化状态
            areaInit: 0,//县区初始化状态
            addressInit: 0,//详细地址初始化状态
            tel: true,
            phone: true,
            choosestr: []
        },
        ready: function () {
            var self = this;
            self.fillSel(self.provinceSetting, location_config["0"]);
            //this.subFormData.product_id = fnr.getQueryString('product_id');
            // this.queruFormData.product_id = fnr.getQueryString('product_id');

        },
        methods: {
            fillSel: function (selSetting, list) {
                selSetting.options = [{key: '', value: '--请选择--'}];
                if (list) {
                    fnr.each(list, function (k, v) {
                        selSetting.options.push({key: v.code, value: v.name});
                    });
                }
            },
            /*新增or修改主产品Strat*/
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
<<<<<<< HEAD
                    var defrend = fnr.ajaxJson("../api/supply/create", self.subFormData, {});
=======
                    var defrend = fnr.ajaxJson("/voucher/api/supply/create", self.subFormData, {});
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
<<<<<<< HEAD
                location.href = "/supply/listSupply.html";
=======
                location.href = "/voucher/supply/listSupply.html";
>>>>>>> fix_master
            },
            upload: function () {

            },
            /*加载省市区三级联动Start*/
            provinceChange: function () {
                var self = this;
                setTimeout(function () {
                    if (self.subFormData.props.province == '') {
                        self.$refs.city.reset();
                    } else {
                        self.fillSel(self.citySetting, location_config[self.subFormData.props.province]);
                        self.subFormData.props.city = '';
                    }
                    self.$refs.area.reset();
                }, 100);
            },
            cityChange: function () {
                var self = this;
                setTimeout(function () {
                    if (self.subFormData.props.city == '') {
                        self.$refs.area.reset();
                    } else {
                        self.fillSel(self.areaSetting, location_config[self.subFormData.props.city]);
                    }
                }, 100);
            },
            /*加载省市区三级联动End*/
            /*加载景点类型静态参数*/
            queryProductType: function () {
                var self = this;
                var productType = {
                    category: 'ProductEvlType'
                };
                // var defrend = fnr.request.param(productType);
                // defrend.then(function (result) {
                //     resp = result.json();
                //     if (resp.code == 200) {
                //         self.choosestr = resp.data;
                //     }
                // });
            },
            /*telChange:function(){
             var result = this.subFormData.props.tel.split(",");
             for(var i=0;i<result.length;i++){
             /^(\d{3,4}-)?\d{7,8}([\,](\d{3,4}-)?\d{7,8})*$/
             /^(((^(\d{3,4}-)?\d{7,8})$|(13[0-9]{9}))[\,])+$/
             var reg=/^(^(\d{3,4}-)?\d{7,8})$|(13[0-9]{9})$/;
             if(!reg.test(result[i])){
             //fnr.alert("对不起，您输入的电话号码"+result[i]+"格式不正确!");
             this.tel = false;
             }else{
             this.tel = true;
             }
             }
             },*/
            /*checkPhone:function(){
             var reg = /^(((1[0-9][0-9]{1})|(15[0-9]{1}))+\d{8})$/;
             if(!reg.test(this.subFormData.phone)){
             //fnr.alert("对不起，您输入的电话号码格式不正确!");
             this.phone = false;
             }else{
             this.phone = true;
             }
             },*/
            check: function (line) {
                if (line.checked == 0) {
                    line.checked = '1';
                } else if (line.checked == 1) {
                    line.checked = '0';
                }
            }
        }
    });
});
