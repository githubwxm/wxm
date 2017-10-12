define(['fnr'],function(fnr){
    //渠道销售情况
    fnr.request.channelSalesList = function(params,options){if (!params.product_code) delete params.product_code; return fnr.ajaxJson('/voucher/api/local/core/report/analysis/sale/consume/list',params,options); };
    //渠道销售数据对比
    fnr.request.channelSalesContrast = function(params,options){if (!params.product_code) delete params.product_code; return fnr.ajaxJson('/voucher/api/local/core/report/analysis/sale/consume/compared',params,options); };
    //渠道供应情况
    fnr.request.channelSupplierList = function(params,options){if (!params.product_code) delete params.product_code; return fnr.ajaxJson('/voucher/api/local/core/report/analysis/supply/consume/list',params,options); };
    //渠道供应数据对比
    fnr.request.channelSupplierContrast = function(params,options){if (!params.product_code) delete params.product_code; return fnr.ajaxJson('/voucher/api/local/core/report/analysis/supply/consume/compared',params,options); };
    //异常订单列表
    fnr.request.abnormalOrderList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/exception/order/exception/list',params,options); };
    //异常订单BY订单Number
    fnr.request.abnormalOrderByNumber = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/exception/order/select/sync',params,options); };
    //手动同步异常订单
    fnr.request.syncAbnormalOrder = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/order/sync',params,options); };
    //模糊搜索产品列表
    fnr.request.queryProductList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/report/analysis/search/product',params,options); };
});