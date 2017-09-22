/**
 * Created by zoujing on 2017/1/13 0013.
 */
define(['fnr'],function(fnr){
    fnr.request.hotelSalesList = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/can_sale/list',params,options); };
    fnr.request.hotelDetailList = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sub/can_sale/list',params,options); };
    fnr.request.buyHotelTicked = function(params,options){ return fnr.ajaxJson('api/local/core/order/hotel/create',params,options); };
    fnr.request.queryStockAndPrice = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/stock_price',params,options); };
    fnr.request.buyHotelGroupTicked = function(params,options){ return fnr.ajaxJson('api/local/core/order/hotel/group/create',params,options); };
});