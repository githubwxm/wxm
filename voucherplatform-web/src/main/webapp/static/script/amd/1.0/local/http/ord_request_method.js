define(['fnr'],function(fnr){
    // fnr.request.listProdByEpId = function(params,options){ return fnr.ajaxJson('api/local/client/testproduct/listProdByEpId',params,options); };
    fnr.request.createOrder = function(params,options){return fnr.ajaxJson('api/local/core/order/create',params,options);};
    fnr.request.refundOrder = function(params,options){return fnr.ajaxJson('api/local/core/order/refund/apply',params,options);};
    // fnr.request.refundOrder = function(params,options){return fnr.ajaxJson('api/local/core/order/refund/ota/apply',params,options);};
    fnr.request.payOrder = function(params,options) {return fnr.ajaxJson('api/local/core/order/payment',params,options);};
    fnr.request.lstSupplierOrder = function(params,options) {return fnr.ajaxJson('api/local/core/order/platform/list/supplier',params,options)};
    fnr.request.reSendTicket = function(params,options) {return fnr.ajaxJson('api/local/core/order/resend/ticket',params,options)};
    /*团队订单退订*/
    fnr.request.refundGroupOrder = function(params,options) {return fnr.ajaxJson('api/local/core/order/refund/group/apply',params,options)};
    /*团队订单重新发票*/
    fnr.request.reSendGroupTicket = function(params,options) {return fnr.ajaxJson('api/local/core/order/resend/group/ticket',params,options)};

    /*酒店订单退订*/
    fnr.request.refundHotelOrder = function(params,options) {return fnr.ajaxJson('api/local/core/order/refund/hotel/apply',params,options)};

    fnr.request.queryRefundRecord = function(params,options) {return fnr.ajaxJson('api/local/client/RefundOrder/queryRecord',params,options)};
    fnr.request.queryRefundGroupRecord = function(params,options) {return fnr.ajaxJson('api/local/client/RefundOrder/queryGroupRecord',params,options)};

    /*酒店订单核销*/
    fnr.request.HotelOrderClearance = function(params,options) {return fnr.ajaxJson('api/local/core/order/consume/hotel/ticket',params,options)};

    /* 线路订单创建 */
    fnr.request.createItineraryOrder = function(params,options) {return fnr.ajaxJson('api/local/core/order/line/create',params,options)};

    /* 线路订单退订 */
    fnr.request.refundItineraryOrder = function(params,options) {return fnr.ajaxJson('api/local/core/order/refund/line/apply',params,options)};
});


