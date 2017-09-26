define(['fnr'],function(fnr){
    /*我的产品列表*/
<<<<<<< HEAD
    fnr.request.prodList = function(params,options){ return fnr.ajaxJson('api/local/core/product/self/list',params,options); };
    /*他供产品列表*/
    fnr.request.procurementList = function(params,options){ return fnr.ajaxJson('api/local/core/product/other/list',params,options); };
    /*他供产品ById不含利润*/
    fnr.request.procurementProdNoPrice = function(params,options){ return fnr.ajaxJson('api/local/core/product/other/price',params,options); };
    /*他供产品ById含利润*/
    fnr.request.procurementProdPrice = function(params,options){ return fnr.ajaxJson('api/local/core/product/other/profit',params,options); };
    /*新增主产品*/
    fnr.request.addProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/add',params,options); };
    /*修改主产品*/
    fnr.request.editProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/update',params,options); };
    /*查询主产品ById*/
    fnr.request.queryProdById = function(params,options){ return fnr.ajaxJson('api/local/core/product/scenery',params,options); };
    /*新增子产品*/
    fnr.request.addProdSub = function(params,options){ return fnr.ajaxJson('api/local/core/product/sub/add',params,options); };
    /*修改子产品*/
    fnr.request.editProdSub = function(params,options){ return fnr.ajaxJson('api/local/core/product/sub/update',params,options); };
    /*/!*查询子产品ById*!/
    fnr.request.queryProdSub = function(params,options){ return fnr.ajaxJson('api/local/core/product/sub/save',params,options); };*/
    /*设置销售计划*/
    fnr.request.addSalp = function(params,options){ return fnr.ajaxJson('api/local/core/plan/config',params,options); };
    /*修改库存*/
    fnr.request.editStock = function(params,options){ return fnr.ajaxJson('api/local/core/plan/update',params,options); };
    /*删除所有销售计划*/
    fnr.request.delSalp = function(params,options){ return fnr.ajaxJson('api/local/core/plan/delete',params,options); };
    /*可分销产品列表*/
    fnr.request.saledProdList = function(params,options){ return fnr.ajaxJson('api/local/core/product/sale/list',params,options); };
    /*查询下游平台商列表*/
    fnr.request.queryPlatfromList = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/list/down',params,options); };
    /*新增退货规则*/
    fnr.request.addRefundRule = function(params,options){ return fnr.ajaxJson('api/local/core/refund_rule/add',params,options); };
    /*退货规则列表*/
    fnr.request.refundRuleList = function(params,options){ return fnr.ajaxJson('api/local/core/refund_rule/list',params,options); };
    /*退货规则详情*/
    fnr.request.refundRuleInfo = function(params,options){ return fnr.ajaxJson('api/local/core/refund_rule/info',params,options); };
    /*修改退货规则*/
    fnr.request.editRefundRule = function(params,options){ return fnr.ajaxJson('api/local/core/refund_rule/update',params,options); };
    /*查询分销商*/
    fnr.request.querySalesList = function(params,options){ return fnr.ajaxJson('api/local/core/product/sale/ep/list',params,options); };
    /*查询分组*/
    fnr.request.queryGroupList = function(params,options){ return fnr.ajaxJson('api/local/core/product/sale/group/list',params,options); };
    /*产品分销给企业*/
    fnr.request.saleEp = function(params,options){ return fnr.ajaxJson('api/local/core/plan/sale/ep',params,options); };
    /*给平台批量上架产品*/
    fnr.request.saleProduct = function(params,options){ return fnr.ajaxJson('api/local/core/plan/on_sale/platform_ep',params,options); };
    /*给平台批量下架产品*/
    fnr.request.productDisable = function(params,options){ return fnr.ajaxJson('api/local/core/plan/off_sale/platform_ep',params,options); };
    /*产品分销给组*/
    fnr.request.saleGroup = function(params,options){ return fnr.ajaxJson('api/local/core/plan/sale/group',params,options); };
    /*产品企业撤销*/
    fnr.request.epDisable = function(params,options){ return fnr.ajaxJson('api/local/core/plan/off_sale/ep',params,options); };
    /*产品分组撤销*/
    fnr.request.groupDisable = function(params,options){ return fnr.ajaxJson('api/local/core/plan/off_sale/group',params,options); };
    /*查询平台商*/
    fnr.request.queryPlatform = function(params,options){ return fnr.ajaxJson('api/local/core/sale/platform_ep/list',params,options); };
    /*查询已分销未分销产品根据平台商ID*/
    fnr.request.queryProuct = function(params,options){ return fnr.ajaxJson('api/local/core/product/platform/distribution',params,options); };
    /* 产品预订信息展现 */
    fnr.request.productView = function(params,options) { return fnr.ajaxJson('api/local/client/Product/viewSubProd',params,options);};
    /* 子产品预定信息展现 */
    fnr.request.subProductView = function(params,options) { return fnr.ajaxJson('api/local/client/Product/viewSubProd',params,options);};
    /* 可售产品列表 */
    fnr.request.lstProduct = function(params,options) { return fnr.ajaxJson('api/local/core/plan/can_sale/sub/list',params,options);};
    /* 查询销售计划 */
    fnr.request.getSalePlane = function(params,options) { return fnr.ajaxJson('api/local/core/plan/sale/calendar/list',params,options);};
    /* 主产品查询 */
    fnr.request.viewProduct = function(params,options) {return fnr.ajaxJson('api/local/core/product/scenery',params,options);};
    /* 子产品查询 */
    fnr.request.viewSubProduct = function(params,options) {return fnr.ajaxJson('api/local/core/product/sub',params,options);};
    /* 凭证查询 */
    fnr.request.queryMaList = function(params,options) {return fnr.ajaxJson('api/local/core/voucher/merchant/list',params,options);};
    /*查询下级供应商*/
    fnr.request.querySupplier = function(params,options) {return fnr.ajaxJson('api/local/core/ep/selectDownSupplier',params,options);};
    /*他供产品上架*/
    fnr.request.procurementSale = function(params,options) {return fnr.ajaxJson('api/local/core/plan/sale/ep/creator',params,options);};
    /*修改他供产品利润*/
    fnr.request.editProcurementSale = function(params,options) {return fnr.ajaxJson('api/local/core/sale/update',params,options);};
    /*查询票务商品列表*/
    fnr.request.queryMaProductList = function(params,options) {return fnr.ajaxJson('api/local/core/voucher/product/list',params,options);};
    /*查询下级供应商*/
    fnr.request.querySupplierList = function(params,options) {return fnr.ajaxJson('api/local/core/ep/select_down_supplier',params,options);};
    /*查询景点类型*/
    fnr.request.lstProductEvlType = function(params,options) {return fnr.ajaxJson('api/local/client/Param/params',params,options);};
    /*退订退款审核配置*/
    fnr.request.refundConfiguration = function(params,options) {return fnr.ajaxJson('api/local/client/product/refundConfiguration',params,options);};
    /*企业未上架产品*/
    fnr.request.queryEpSaleProduct = function(params,options) {return fnr.ajaxJson('api/local/core/product/ep_distribute/list/all',params,options);};
    /*给企业上架产品*/
    fnr.request.saleEpProduct = function(params,options) {return fnr.ajaxJson('api/local/core/plan/on_sale/products/ep',params,options);};
    /*企业已上架产品*/
    fnr.request.queryEpUnshelveProduct = function(params,options) {return fnr.ajaxJson('api/local/core/product/ep_distributed/list/all',params,options);};
    /*给企业下架产品*/
    fnr.request.epProductDisable = function(params,options) {return fnr.ajaxJson('api/local/core/plan/off_sale/products/ep',params,options);};
    /*分组未上架产品*/
    fnr.request.queryGroupSaleProduct = function(params,options) {return fnr.ajaxJson('api/local/core/product/group_distribute/list',params,options);};
    /*给组上架产品*/
    fnr.request.saleGroupProduct = function(params,options) {return fnr.ajaxJson('api/local/core/plan/sale_batch/group',params,options);};
    /*分组已上架产品*/
    fnr.request.queryGroupUnshelveProduct = function(params,options) {return fnr.ajaxJson('api/local/core/product/group_distributed/list/all',params,options);};
    /*给组下架产品*/
    fnr.request.groupProductDisable = function(params,options) {return fnr.ajaxJson('api/local/core/plan/off_sale_batch/group',params,options);};
    /*产品退订退款审核设置查询*/
    fnr.request.auditProdList = function(params,options) {return fnr.ajaxJson('api/local/core/product/audit/list',params,options);};

    /*我的酒店列表*/
    fnr.request.hotelList = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/select_hotel_name',params,options); };
    /*添加酒店*/
    fnr.request.addHotelProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/create',params,options); };
    /*修改酒店*/
    fnr.request.editHotelProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/update',params,options); };
    /*新增房型*/
    fnr.request.addHotelSub = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sub/add',params,options); };
    /*修改房型*/
    fnr.request.editHotelSub = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sub/update',params,options); };
    /*设置酒店销售计划*/
    fnr.request.addHotelSalp = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/batch/create',params,options); };
    /*修改酒店销售计划*/
    fnr.request.editHotelSalp = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/batch/update',params,options); };
    /*销售计划列表*/
    fnr.request.hotelSaleList = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sub/select_hotel_sub',params,options); };
    /*查询销售计划详情*/
    fnr.request.saleView = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/batch/select_hotel_batch_summary',params,options); };
    /*销售计划上架*/
    fnr.request.hotelSaleShelves = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/batch/update_up',params,options); };
    /*销售计划下架*/
    fnr.request.hotelUnSaleShelves = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/down',params,options); };
    /*删除销售计划*/
    fnr.request.deleteHotelSalp = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/batch/delete_hotel_batch',params,options); };
    /*可分销酒店产品列表*/
    fnr.request.saledHotelProdList = function(params,options){ return fnr.ajaxJson('api/local/core/product/sale/list/hotel',params,options); };
    /*查询分销商*/
    fnr.request.queryHotelSalesList = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/select/not_sale',params,options); };
    /*查询可分销组*/
    fnr.request.queryUpHotelGroupList = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/batch/group/up_list',params,options); };
    /*查询可下架组*/
    fnr.request.queryDownHotelGroupList = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/batch/group/down_list',params,options); };
    /*酒店产品销售商上架*/
    fnr.request.saleHotelEp = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/batch/up',params,options); };
    /* 酒店主产品查询 */
    fnr.request.viewHotelProduct = function(params,options) {return fnr.ajaxJson('api/local/core/product/hotel/select/id',params,options);};
    /*销售商下架*/
    fnr.request.saleDisable = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/batch/down',params,options); };
    /*查询酒店子产品详情*/
    fnr.request.summaryView = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sub/select_hotel_sub_summary',params,options); };
    /*酒店产品上架列表*/
    fnr.request.upPlatform = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/select/platform_up_list',params,options); };
    /*酒店产品下架列表*/
    fnr.request.downPlatform = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/select/platform_down_list',params,options); };
    /*酒店产品分组上架*/
    fnr.request.saleHotelGroup = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/batch/group/up',params,options); };
    /*酒店产品分组下架*/
    fnr.request.hotelGroupDisable = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/sale/batch/group/down',params,options); };
    /*产品上架*/
    fnr.request.up = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/batch/update_up',params,options); };
    /*产品下架*/
    fnr.request.down = function(params,options){ return fnr.ajaxJson('api/local/core/product/hotel/batch/update_down',params,options); };

    /*第三方供应主产品*/
    fnr.request.thirdPartySupplyProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/other/park_list',params,options); };
    /*第三方供应子产品*/
    fnr.request.thirdPartySupplyProdSub = function(params,options){ return fnr.ajaxJson('api/local/core/product/other/ticket_list',params,options); };
    /*第三方供应产品列表*/
    fnr.request.thirdPartySupplyProdList = function(params,options){ return fnr.ajaxJson('api/local/core/product/other/ticket_all',params,options); };
    /*获取第三方平台*/
    fnr.request.getThirdParty = function(params,options){ return fnr.ajaxJson('api/local/core/voucher/provider/list',params,options); };
    /*获取第三方平台*/
    fnr.request.getEpSMSDeployList = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/balance_threshold/select/threshold/list',params,options); };
    /*开启或关闭销售商短信通知配置*/
    fnr.request.oddOrOnEpSMSDeploy = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/balance_threshold/create/update',params,options); };

    /*查询零售产品*/
    fnr.request.retailProductList = function(params,options){ return fnr.ajaxJson('api/local/core/sale/reatil/price/list',params,options); };
    /*修改零售产品价格*/
    fnr.request.editretailProductPrice = function(params,options){ return fnr.ajaxJson('api/local/core/sale/seller_price/update',params,options); };
    /*上下架零售产品*/
    fnr.request.unShelveRetailProduct = function(params,options){ return fnr.ajaxJson('api/local/core/sale/seller_price/update/status',params,options); };

    //线路产品
    /*添加线路主产品*/
    fnr.request.addItineraryProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/create',params,options); };
    /*添加线路主产品*/
    fnr.request.itineraryProdList = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/select/list',params,options); };
    /*添加线路子产品*/
    fnr.request.addItinerarySubProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/sub/add',params,options); };
    /*添加线路销售计划*/
    fnr.request.addItinerarySalesPlan = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/batch/add',params,options); };
    /*修改线路销售计划*/
    fnr.request.editItinerarySalesPlan = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/batch/update',params,options); };
    /*添加线路销售计划*/
    fnr.request.ItinerarySalesList = function(params,options){ return fnr.ajaxJson('api/local/core/itinerary/sale/list',params,options); };
    /*查询线路主产品详情*/
    fnr.request.queryItineraryProdView = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/select/id',params,options); };
    /*修改线路主产品*/
    fnr.request.editItineraryProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/update',params,options); };
    /*修改线路子产品*/
    fnr.request.editItinerarySubProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/sub/update',params,options); };
    /*删除线路主产品*/
    fnr.request.delItineraryProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/delete',params,options); };
    /*删除线路子产品*/
    fnr.request.delItinerarySubProd = function(params,options){ return fnr.ajaxJson('api/local/core/product/itinerary/sub/delete',params,options); };
    /* 线路产品可售列表 */
    fnr.request.itineraryList = function(params,options) { return fnr.ajaxJson('api/local/core/product/itinerary/can_sale/list',params,options);};
    /* 线路产品预定详情 */
    fnr.request.itineraryShow = function(params,options) {return fnr.ajaxJson('api/local/client/Product/itinerary_info',params,options);};
=======
    fnr.request.prodList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/self/list',params,options); };
    /*他供产品列表*/
    fnr.request.procurementList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/other/list',params,options); };
    /*他供产品ById不含利润*/
    fnr.request.procurementProdNoPrice = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/other/price',params,options); };
    /*他供产品ById含利润*/
    fnr.request.procurementProdPrice = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/other/profit',params,options); };
    /*新增主产品*/
    fnr.request.addProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/add',params,options); };
    /*修改主产品*/
    fnr.request.editProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/update',params,options); };
    /*查询主产品ById*/
    fnr.request.queryProdById = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/scenery',params,options); };
    /*新增子产品*/
    fnr.request.addProdSub = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/sub/add',params,options); };
    /*修改子产品*/
    fnr.request.editProdSub = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/sub/update',params,options); };
    /*/!*查询子产品ById*!/
    fnr.request.queryProdSub = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/sub/save',params,options); };*/
    /*设置销售计划*/
    fnr.request.addSalp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/plan/config',params,options); };
    /*修改库存*/
    fnr.request.editStock = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/plan/update',params,options); };
    /*删除所有销售计划*/
    fnr.request.delSalp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/plan/delete',params,options); };
    /*可分销产品列表*/
    fnr.request.saledProdList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/sale/list',params,options); };
    /*查询下游平台商列表*/
    fnr.request.queryPlatfromList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/list/down',params,options); };
    /*新增退货规则*/
    fnr.request.addRefundRule = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/refund_rule/add',params,options); };
    /*退货规则列表*/
    fnr.request.refundRuleList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/refund_rule/list',params,options); };
    /*退货规则详情*/
    fnr.request.refundRuleInfo = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/refund_rule/info',params,options); };
    /*修改退货规则*/
    fnr.request.editRefundRule = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/refund_rule/update',params,options); };
    /*查询分销商*/
    fnr.request.querySalesList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/sale/ep/list',params,options); };
    /*查询分组*/
    fnr.request.queryGroupList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/sale/group/list',params,options); };
    /*产品分销给企业*/
    fnr.request.saleEp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/plan/sale/ep',params,options); };
    /*给平台批量上架产品*/
    fnr.request.saleProduct = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/plan/on_sale/platform_ep',params,options); };
    /*给平台批量下架产品*/
    fnr.request.productDisable = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/plan/off_sale/platform_ep',params,options); };
    /*产品分销给组*/
    fnr.request.saleGroup = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/plan/sale/group',params,options); };
    /*产品企业撤销*/
    fnr.request.epDisable = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/plan/off_sale/ep',params,options); };
    /*产品分组撤销*/
    fnr.request.groupDisable = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/plan/off_sale/group',params,options); };
    /*查询平台商*/
    fnr.request.queryPlatform = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/sale/platform_ep/list',params,options); };
    /*查询已分销未分销产品根据平台商ID*/
    fnr.request.queryProuct = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/platform/distribution',params,options); };
    /* 产品预订信息展现 */
    fnr.request.productView = function(params,options) { return fnr.ajaxJson('/voucher/api/local/client/Product/viewSubProd',params,options);};
    /* 子产品预定信息展现 */
    fnr.request.subProductView = function(params,options) { return fnr.ajaxJson('/voucher/api/local/client/Product/viewSubProd',params,options);};
    /* 可售产品列表 */
    fnr.request.lstProduct = function(params,options) { return fnr.ajaxJson('/voucher/api/local/core/plan/can_sale/sub/list',params,options);};
    /* 查询销售计划 */
    fnr.request.getSalePlane = function(params,options) { return fnr.ajaxJson('/voucher/api/local/core/plan/sale/calendar/list',params,options);};
    /* 主产品查询 */
    fnr.request.viewProduct = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/product/scenery',params,options);};
    /* 子产品查询 */
    fnr.request.viewSubProduct = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/product/sub',params,options);};
    /* 凭证查询 */
    fnr.request.queryMaList = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/voucher/merchant/list',params,options);};
    /*查询下级供应商*/
    fnr.request.querySupplier = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/ep/selectDownSupplier',params,options);};
    /*他供产品上架*/
    fnr.request.procurementSale = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/plan/sale/ep/creator',params,options);};
    /*修改他供产品利润*/
    fnr.request.editProcurementSale = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/sale/update',params,options);};
    /*查询票务商品列表*/
    fnr.request.queryMaProductList = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/voucher/product/list',params,options);};
    /*查询下级供应商*/
    fnr.request.querySupplierList = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/ep/select_down_supplier',params,options);};
    /*查询景点类型*/
    fnr.request.lstProductEvlType = function(params,options) {return fnr.ajaxJson('/voucher/api/local/client/Param/params',params,options);};
    /*退订退款审核配置*/
    fnr.request.refundConfiguration = function(params,options) {return fnr.ajaxJson('/voucher/api/local/client/product/refundConfiguration',params,options);};
    /*企业未上架产品*/
    fnr.request.queryEpSaleProduct = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/product/ep_distribute/list/all',params,options);};
    /*给企业上架产品*/
    fnr.request.saleEpProduct = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/plan/on_sale/products/ep',params,options);};
    /*企业已上架产品*/
    fnr.request.queryEpUnshelveProduct = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/product/ep_distributed/list/all',params,options);};
    /*给企业下架产品*/
    fnr.request.epProductDisable = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/plan/off_sale/products/ep',params,options);};
    /*分组未上架产品*/
    fnr.request.queryGroupSaleProduct = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/product/group_distribute/list',params,options);};
    /*给组上架产品*/
    fnr.request.saleGroupProduct = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/plan/sale_batch/group',params,options);};
    /*分组已上架产品*/
    fnr.request.queryGroupUnshelveProduct = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/product/group_distributed/list/all',params,options);};
    /*给组下架产品*/
    fnr.request.groupProductDisable = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/plan/off_sale_batch/group',params,options);};
    /*产品退订退款审核设置查询*/
    fnr.request.auditProdList = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/product/audit/list',params,options);};

    /*我的酒店列表*/
    fnr.request.hotelList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/select_hotel_name',params,options); };
    /*添加酒店*/
    fnr.request.addHotelProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/create',params,options); };
    /*修改酒店*/
    fnr.request.editHotelProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/update',params,options); };
    /*新增房型*/
    fnr.request.addHotelSub = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sub/add',params,options); };
    /*修改房型*/
    fnr.request.editHotelSub = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sub/update',params,options); };
    /*设置酒店销售计划*/
    fnr.request.addHotelSalp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/batch/create',params,options); };
    /*修改酒店销售计划*/
    fnr.request.editHotelSalp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/batch/update',params,options); };
    /*销售计划列表*/
    fnr.request.hotelSaleList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sub/select_hotel_sub',params,options); };
    /*查询销售计划详情*/
    fnr.request.saleView = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/batch/select_hotel_batch_summary',params,options); };
    /*销售计划上架*/
    fnr.request.hotelSaleShelves = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/batch/update_up',params,options); };
    /*销售计划下架*/
    fnr.request.hotelUnSaleShelves = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/down',params,options); };
    /*删除销售计划*/
    fnr.request.deleteHotelSalp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/batch/delete_hotel_batch',params,options); };
    /*可分销酒店产品列表*/
    fnr.request.saledHotelProdList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/sale/list/hotel',params,options); };
    /*查询分销商*/
    fnr.request.queryHotelSalesList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/select/not_sale',params,options); };
    /*查询可分销组*/
    fnr.request.queryUpHotelGroupList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/batch/group/up_list',params,options); };
    /*查询可下架组*/
    fnr.request.queryDownHotelGroupList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/batch/group/down_list',params,options); };
    /*酒店产品销售商上架*/
    fnr.request.saleHotelEp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/batch/up',params,options); };
    /* 酒店主产品查询 */
    fnr.request.viewHotelProduct = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/product/hotel/select/id',params,options);};
    /*销售商下架*/
    fnr.request.saleDisable = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/batch/down',params,options); };
    /*查询酒店子产品详情*/
    fnr.request.summaryView = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sub/select_hotel_sub_summary',params,options); };
    /*酒店产品上架列表*/
    fnr.request.upPlatform = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/select/platform_up_list',params,options); };
    /*酒店产品下架列表*/
    fnr.request.downPlatform = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/select/platform_down_list',params,options); };
    /*酒店产品分组上架*/
    fnr.request.saleHotelGroup = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/batch/group/up',params,options); };
    /*酒店产品分组下架*/
    fnr.request.hotelGroupDisable = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/sale/batch/group/down',params,options); };
    /*产品上架*/
    fnr.request.up = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/batch/update_up',params,options); };
    /*产品下架*/
    fnr.request.down = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/hotel/batch/update_down',params,options); };

    /*第三方供应主产品*/
    fnr.request.thirdPartySupplyProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/other/park_list',params,options); };
    /*第三方供应子产品*/
    fnr.request.thirdPartySupplyProdSub = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/other/ticket_list',params,options); };
    /*第三方供应产品列表*/
    fnr.request.thirdPartySupplyProdList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/other/ticket_all',params,options); };
    /*获取第三方平台*/
    fnr.request.getThirdParty = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/voucher/provider/list',params,options); };
    /*获取第三方平台*/
    fnr.request.getEpSMSDeployList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/balance_threshold/select/threshold/list',params,options); };
    /*开启或关闭销售商短信通知配置*/
    fnr.request.oddOrOnEpSMSDeploy = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/balance_threshold/create/update',params,options); };

    /*查询零售产品*/
    fnr.request.retailProductList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/sale/reatil/price/list',params,options); };
    /*修改零售产品价格*/
    fnr.request.editretailProductPrice = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/sale/seller_price/update',params,options); };
    /*上下架零售产品*/
    fnr.request.unShelveRetailProduct = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/sale/seller_price/update/status',params,options); };

    //线路产品
    /*添加线路主产品*/
    fnr.request.addItineraryProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/create',params,options); };
    /*添加线路主产品*/
    fnr.request.itineraryProdList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/select/list',params,options); };
    /*添加线路子产品*/
    fnr.request.addItinerarySubProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/sub/add',params,options); };
    /*添加线路销售计划*/
    fnr.request.addItinerarySalesPlan = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/batch/add',params,options); };
    /*修改线路销售计划*/
    fnr.request.editItinerarySalesPlan = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/batch/update',params,options); };
    /*添加线路销售计划*/
    fnr.request.ItinerarySalesList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/itinerary/sale/list',params,options); };
    /*查询线路主产品详情*/
    fnr.request.queryItineraryProdView = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/select/id',params,options); };
    /*修改线路主产品*/
    fnr.request.editItineraryProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/update',params,options); };
    /*修改线路子产品*/
    fnr.request.editItinerarySubProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/sub/update',params,options); };
    /*删除线路主产品*/
    fnr.request.delItineraryProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/delete',params,options); };
    /*删除线路子产品*/
    fnr.request.delItinerarySubProd = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/sub/delete',params,options); };
    /* 线路产品可售列表 */
    fnr.request.itineraryList = function(params,options) { return fnr.ajaxJson('/voucher/api/local/core/product/itinerary/can_sale/list',params,options);};
    /* 线路产品预定详情 */
    fnr.request.itineraryShow = function(params,options) {return fnr.ajaxJson('/voucher/api/local/client/Product/itinerary_info',params,options);};
>>>>>>> fix_master

});
