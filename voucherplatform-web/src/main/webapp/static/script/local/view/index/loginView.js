var rootVue;
require_js_file(['vueValidator'], function (Vue, fnr, validator) {
    Vue.use(validator);
    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            page_config: global_config.page,
            errMessage: '',
            formData: {
                userName: '', passWord: '',
            }
        },
        //加载首页个性设置
        ready: function () {
            /*var self = this;
<<<<<<< HEAD
             var url = 'api/local/client/system/load_config';
=======
             var url = '/voucher/api/local/client/system/load_config';
>>>>>>> fix_master
             var deferred = fnr.ajaxJson(url, {}, {method:'POST',alertMessage:0});*/
            /*deferred.then(function(result){
             var resp = result.json();
             if(resp.code == 200){
             if(resp.data.title){
             self.page_config.title = resp.data.title;
             }
             //底部版权信息
             if(resp.data.address){
             self.page_config.address = resp.data.address;
             }
             if(resp.data.service_phone){
             self.page_config.service_phone = resp.data.service_phone;
             }
             if(resp.data.copyright){
             self.page_config.copyright = resp.data.copyright;
             }
             if(resp.data.login_pic){
             self.page_config.login_pic = global_config.resBaseUrl + resp.data.login_pic;
             }
             if(resp.data.main_pic){
             self.page_config.main_pic = global_config.resBaseUrl + resp.data.main_pic;
             }
             if(resp.data.logo_pic){
             self.page_config.logo.src = global_config.resBaseUrl + resp.data.logo_pic;
             }
             }
             });*/
        },
        methods: {
            login: function () {
                this.$validate();
                if (!this.$checkSubForm.valid) {
                    if (this.$checkSubForm.userName.required) {
                        this.errMessage = '请输入手机号码！';
                        return;
                    }
                    if (this.$checkSubForm.passWord.required) {
                        this.errMessage = '请输入密码！';
                        return;
                    }
                }
                this.errMessage = '';

<<<<<<< HEAD
                var deferred = fnr.ajaxJson("api/user/login",this.formData);
=======
                var deferred = fnr.ajaxJson("/voucher/api/user/login",this.formData);
>>>>>>> fix_master
                var self = this;
                deferred.then(function (result) {
                    resp = result.json();
                    if (resp.code == 200) {
<<<<<<< HEAD
                        window.location = 'index/index.html';
=======
                        window.location = '/voucher/index/index.html';
>>>>>>> fix_master
                        return;
                    }
                    self.errMessage = resp.message;
                });
            }
        }
    });
});

var img = new Image();
<<<<<<< HEAD
//img.src = "../static/style/img/banr1_02.jpg";
=======
//img.src = "/voucher/static/style/img/banr1_02.jpg";
>>>>>>> fix_master
img.src = global_config.page.main_pic;
img.onload = function () {
    document.getElementById('myImage').src = this.src;
}
