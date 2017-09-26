define(['fnr'],function(fnr){
    /*景点类型*/
<<<<<<< HEAD
    fnr.request.queryParam = function(params,options){ return fnr.ajaxJson('api/local/client/param/queryProductType',params,options); };
    fnr.request.queryParamByid = function(params,options){ return fnr.ajaxJson('api/local/client/param/queryByid',params,options); };
=======
    fnr.request.queryParam = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/param/queryProductType',params,options); };
    fnr.request.queryParamByid = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/param/queryByid',params,options); };
>>>>>>> fix_master
});