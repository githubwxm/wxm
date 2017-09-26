var rootVue;
require_js_file([],function(Vue,fnr){
    rootVue = new Vue({
        el: '[vtag=root]',
        data:{
            fnr:fnr,
            area1:'',
            area2:'',
            area3:'',
            area4:'',
            area5:'',
            area6:''
        },
        ready:function(){ },
        methods: {
            click1:function(){
                var self = this;
<<<<<<< HEAD
                var params = {title:'demo',url:'/demo/dialog/dialog.html',callbackSucc:function(result){
=======
                var params = {title:'demo',url:'/voucher/demo/dialog/dialog.html',callbackSucc:function(result){
>>>>>>> fix_master
                    self.area1 = result;
                }};
                fnr.iDialog(params);
            },
            click2:function(){
                var self = this;
                var params = {callbackCancel:function(){ self.area2 = 'ok'; }};
                fnr.alert('hello',params);
            },
            click3:function(){
                var self = this;
                var params = {
                    callbackSucc:function(){ self.area3 = '你这个sb';},
                    callbackCancel:function(){ self.area3 = '你这个不想做sb的sb';}
                };
                fnr.confirm('你真的要做sb吗',params);
            },
            click4:function(){
                var self = this;
<<<<<<< HEAD
                var params = {title:'demo',url:'/demo/dialog/dialog.html',parent:'curr',callbackSucc:function(result){
=======
                var params = {title:'demo',url:'/voucher/demo/dialog/dialog.html',parent:'curr',callbackSucc:function(result){
>>>>>>> fix_master
                    self.area4 = result;
                }};
                fnr.iDialog(params);
            },
            click5:function(){
                var self = this;
                var params = {parent:'curr',callbackCancel:function(){ self.area5 = 'ok'; }};
                fnr.alert('hello',params);
            },
            click6:function(){
                var self = this;
                var params = {
                    parent:'curr',
                    callbackSucc:function(){ self.area6 = '你这个sb';},
                    callbackCancel:function(){ self.area6 = '你这个不想做sb的sb';}
                };
                fnr.confirm('你真的要做sb吗',params);
            }
        }
    });
});
