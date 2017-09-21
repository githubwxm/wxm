define(['fnr'],function(fnr){
    fnr.request.refundListAllByCore = function(params,options){ return fnr.ajaxJson('api/local/core/refund_rule/list',params,options); };
});