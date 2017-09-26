var rootVue;
require_js_file(['epRequestUrl'],function(Vue,fnr){
    fnr.dataPageTable();
    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            dtSetting:{
                header:['id','标题','操作'],
                remote:{
<<<<<<< HEAD
                    link:'api/local/client/demo',
=======
                    link:'/voucher/api/local/client/demo',
>>>>>>> fix_master
                },
                fields:[
                    {field:'id'}, {field:'name'},
                    {field:function(line){
                        var str = '<a href="javascript:;" onclick="window.rootVue.edit('+line.id+');">修改</a>';
                        str += '<a href="javascript:;" onclick="window.rootVue.remove('+line.id+');">删除</a>';
                        return str;
                    }}
                ]
            },
            queryFormData: {
                name: ''
            }
        },
        ready:function(){},
        methods: {
            queryFormSubmit:function(){
                this.$refs.dtList.query();
            },
            reload:function(){
                this.$refs.dtList.reload();
            },
            edit:function(id){
                var params = {};
                if(id == undefined || id==''){
<<<<<<< HEAD
                    params = {url:'/demo/curd/view.html',title:'添加'};
                }else{
                    params = {url:'/demo/curd/view.html?id='+id,title:'编辑'};
=======
                    params = {url:'/voucher/demo/curd/view.html',title:'添加'};
                }else{
                    params = {url:'/voucher/demo/curd/view.html?id='+id,title:'编辑'};
>>>>>>> fix_master
                }
                var self = this;
                params.callbackSucc = function(){
                    self.reload();
                };
                fnr.iDialog(params);
            },
            remove: function(id){
                var self = this;
                fnr.confirm('确定删除?',{callbackSucc:function(){
<<<<<<< HEAD
                    var deferred = fnr.ajaxJson('api/local/client/demo/delete',{id:id});
=======
                    var deferred = fnr.ajaxJson('/voucher/api/local/client/demo/delete',{id:id});
>>>>>>> fix_master
                    deferred.then(function(result){
                        resp = result.json();
                        if(resp.code == 200){
                            self.reload();
                        }
                    });
                }});
            }
        }
    });
});