define(['fnr'],function(fnr){
    ///service/local/core/refund_rule/list
<<<<<<< HEAD
    fnr.request.listAllByCore = function(params,options){ return fnr.ajaxJson('api/local/client/personnel/queryList',params,options); };
    fnr.request.listAllSale = function(params,options){ return fnr.ajaxJson('api/local/client/sale/queryList',params,options); };
    fnr.request.listAllEp = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/list',params,options); };
    fnr.request.statusFreeze = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/status/freeze',params,options); };
    fnr.request.statusDisable = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/status/disable',params,options); };
    fnr.request.statusEnable = function(params,options){ return fnr.ajaxJson('api/local/client/ep/platformEnable',params,options); };
    fnr.request.listAllPassageWay = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/channel/list',params,options); };
    fnr.request.voucherList = function(params,options){ return fnr.ajaxJson('api/local/core/voucher/list',params,options); };
    fnr.request.paymentList = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/payment/list_by_ep_id',params,options); };
    fnr.request.paymentList1 = function(params,options){ return fnr.ajaxJson('api/local/client/voucher/listByEpId',params,options); };

    fnr.request.applyList4core = function(params,options){ return fnr.ajaxJson('api/local/client/EpApply',params,options); };
    fnr.request.applyView = function(params,options){ return fnr.ajaxJson('api/local/client/EpApply/view',params,options); };
    fnr.request.applyPass = function(params,options){ return fnr.ajaxJson('api/local/client/EpApply/applyPass',params,options); };
    fnr.request.applyUnPass = function(params,options){ return fnr.ajaxJson('api/local/client/EpApply/applyUnPass',params,options); };

    fnr.request.queryEpList = function(params,options){ return fnr.ajaxJson('api/local/client/ep/getList',params,options); };
    fnr.request.epView = function(params,options){ return fnr.ajaxJson('api/local/client/ep/view',params,options); };
    fnr.request.getByid = function(params,options){ return fnr.ajaxJson('api/local/client/ep/getByid',params,options); };
    fnr.request.getEpByid = function(params,options){ return fnr.ajaxJson('api/local/client/ep/getEpByid',params,options); };
    fnr.request.selectPlatfromId = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/select_platfrom_id',params,options); };
    fnr.request.getMemberList = function(params,options){ return fnr.ajaxJson('api/local/client/ep/getMemberList',params,options); };
    fnr.request.getSubEnterpriseList = function(params,options){ return fnr.ajaxJson('api/local/client/ep/getSubEnterpriseList',params,options); };
    fnr.request.editMyEp = function(params,options){ return fnr.ajaxJson('api/local/client/ep/editMyEp',params,options); };
    fnr.request.checkNamePhone = function(params,options){ return fnr.ajaxJson('api/local/client/ep/checkNamePhone',params,options); };
    fnr.request.checkPhone = function(params,options){ return fnr.ajaxJson('api/local/client/ep/checkPhone',params,options); };

    fnr.request.disableEp = function(params,options){ return fnr.ajaxJson('api/local/client/ep/disable',params,options); };
    fnr.request.enableEp = function(params,options){ return fnr.ajaxJson('api/local/client/ep/enable',params,options); };
    fnr.request.freezeEp = function(params,options){ return fnr.ajaxJson('api/local/client/ep/freeze',params,options); };
    fnr.request.resetPassword = function(params,options){ return fnr.ajaxJson('api/local/client/ep/resetPassword',params,options); };

    fnr.request.groupList = function(params,options){ return fnr.ajaxJson('api/local/core/sale/group/list',params,options); };

    fnr.request.merchantList = function(params,options){ return fnr.ajaxJson('api/local/core/voucher/merchant/list',params,options); };

    fnr.request.queryPaymentByid = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/payment/select',params,options); };

    /*中心平台查所有通道费账单*/
    fnr.request.queryAllPassagewayBill = function(params,options){ return fnr.ajaxJson('api/local/core/order/list/channel/bill/supplier',params,options); };
    /*供应平台通道费账单*/
    fnr.request.querySupplierPassagewayBill = function(params,options){ return fnr.ajaxJson('api/local/core/order/list/channel/bill',params,options); };
    /*账单明细*/
    fnr.request.queryBillDeatiles = function(params,options){ return fnr.ajaxJson('api/local/core/order/list/channel/bill/detail',params,options); };
    /*供应平台*/
    fnr.request.querySupplierPlatform = function(params,options){ return fnr.ajaxJson('api/local/core/ep/platform/channel/supplier_core_ep_id_list',params,options); };

    fnr.request.queryDivisionInfo4Id = function(params,options){ return fnr.ajaxJson('api/local/client/personnel/queryDivisionInfo4Id',params,options); };

    fnr.request.checkName = function(params,options){ return fnr.ajaxJson('api/local/client/personnel/checkName',params,options); };

    fnr.request.insertDivision = function(params,options){ return fnr.ajaxJson('api/local/client/personnel/insertDivision',params,options); };

    fnr.request.updateDivision = function(params,options){ return fnr.ajaxJson('api/local/client/personnel/updateDivision',params,options); };

    /* 模糊查询企业名称（统计报表使用） 2017-03-22 by ShaneWei*/
    // fnr.request.queryEpName = function(params,options) {return fnr.ajaxJson('api/local/client/ep/queryEpNameByCoreEpId');};
    //
    // fnr.request.queryProductByEp = function(params,options) {return fnr.ajaxJson('api/local/core/product/select_ep_self_product');};
=======
    fnr.request.listAllByCore = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/personnel/queryList',params,options); };
    fnr.request.listAllSale = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/sale/queryList',params,options); };
    fnr.request.listAllEp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/list',params,options); };
    fnr.request.statusFreeze = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/status/freeze',params,options); };
    fnr.request.statusDisable = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/status/disable',params,options); };
    fnr.request.statusEnable = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/platformEnable',params,options); };
    fnr.request.listAllPassageWay = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/channel/list',params,options); };
    fnr.request.voucherList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/voucher/list',params,options); };
    fnr.request.paymentList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/payment/list_by_ep_id',params,options); };
    fnr.request.paymentList1 = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/voucher/listByEpId',params,options); };

    fnr.request.applyList4core = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/EpApply',params,options); };
    fnr.request.applyView = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/EpApply/view',params,options); };
    fnr.request.applyPass = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/EpApply/applyPass',params,options); };
    fnr.request.applyUnPass = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/EpApply/applyUnPass',params,options); };

    fnr.request.queryEpList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/getList',params,options); };
    fnr.request.epView = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/view',params,options); };
    fnr.request.getByid = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/getByid',params,options); };
    fnr.request.getEpByid = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/getEpByid',params,options); };
    fnr.request.selectPlatfromId = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/select_platfrom_id',params,options); };
    fnr.request.getMemberList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/getMemberList',params,options); };
    fnr.request.getSubEnterpriseList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/getSubEnterpriseList',params,options); };
    fnr.request.editMyEp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/editMyEp',params,options); };
    fnr.request.checkNamePhone = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/checkNamePhone',params,options); };
    fnr.request.checkPhone = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/checkPhone',params,options); };

    fnr.request.disableEp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/disable',params,options); };
    fnr.request.enableEp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/enable',params,options); };
    fnr.request.freezeEp = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/freeze',params,options); };
    fnr.request.resetPassword = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/ep/resetPassword',params,options); };

    fnr.request.groupList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/sale/group/list',params,options); };

    fnr.request.merchantList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/voucher/merchant/list',params,options); };

    fnr.request.queryPaymentByid = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/payment/select',params,options); };

    /*中心平台查所有通道费账单*/
    fnr.request.queryAllPassagewayBill = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/order/list/channel/bill/supplier',params,options); };
    /*供应平台通道费账单*/
    fnr.request.querySupplierPassagewayBill = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/order/list/channel/bill',params,options); };
    /*账单明细*/
    fnr.request.queryBillDeatiles = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/order/list/channel/bill/detail',params,options); };
    /*供应平台*/
    fnr.request.querySupplierPlatform = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/ep/platform/channel/supplier_core_ep_id_list',params,options); };

    fnr.request.queryDivisionInfo4Id = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/personnel/queryDivisionInfo4Id',params,options); };

    fnr.request.checkName = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/personnel/checkName',params,options); };

    fnr.request.insertDivision = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/personnel/insertDivision',params,options); };

    fnr.request.updateDivision = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/personnel/updateDivision',params,options); };

    /* 模糊查询企业名称（统计报表使用） 2017-03-22 by ShaneWei*/
    // fnr.request.queryEpName = function(params,options) {return fnr.ajaxJson('/voucher/api/local/client/ep/queryEpNameByCoreEpId');};
    //
    // fnr.request.queryProductByEp = function(params,options) {return fnr.ajaxJson('/voucher/api/local/core/product/select_ep_self_product');};
>>>>>>> fix_master
});
