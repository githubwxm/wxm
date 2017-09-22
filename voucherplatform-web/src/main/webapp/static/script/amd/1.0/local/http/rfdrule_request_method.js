define(['fnr'],function(fnr){
    fnr.request.refundListAllByCore = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/refund_rule/list',params,options); };
});