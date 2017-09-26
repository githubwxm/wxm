define(['fnr'],function(fnr){
<<<<<<< HEAD
    fnr.request.refundListAllByCore = function(params,options){ return fnr.ajaxJson('api/local/core/refund_rule/list',params,options); };
=======
    fnr.request.refundListAllByCore = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/refund_rule/list',params,options); };
>>>>>>> fix_master
});