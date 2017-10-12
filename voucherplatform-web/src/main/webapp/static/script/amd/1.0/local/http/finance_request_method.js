/**
 * Created by ShaneWei on 2016/11/14.
 */
define(['fnr'],function(fnr){
    fnr.request.getAccountInfo = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/getAccountInfo',params,options); };
    fnr.request.manageEpAccount = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/credit/get_account_info_list',params,options); };
    fnr.request.addBalance = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/finance/addBalance',params,options); };
    fnr.request.lstBalance = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/lstBalance',params,options); };
    fnr.request.lstCash = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/lstBalance',params,options); };
    fnr.request.lstBalanceAll = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/lstBalanceAll',params,options); };
    fnr.request.lstBalanceAllByEpId = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/lstBalanceAllByEpId',params,options); };
    fnr.request.lstOwnerBalanceAll = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/lstOwnerBalanceAll',params,options); };
    fnr.request.setupCredit = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/setupCredit',params,options); };
    fnr.request.viewTradeDetail = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/viewTradeDetail',params,options); };
    fnr.request.viewwriteOffDetail = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/viewwriteOffDetail',params,options); };
    fnr.request.viewRefundDetail = function(params,options){ return fnr.ajaxJson('/voucher/api/local/client/Finance/viewRefundDetail',params,options); };

    fnr.request.lst_balance = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/lst_balance',params,options); };
    fnr.request.getAccount = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/account/info',params,options); };
    //获取交易订单流水名称列表
    fnr.request.getBalanceType = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/get/balance/type',params,options); };

    //授信列表
    fnr.request.creditList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/credit/platform/list',params,options); };
    //销售平台账户列表
    fnr.request.sellerPlatfromList = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/get_seller_platfrom_accunt_info',params,options); };

    //授信历史信息
    fnr.request.hostory_credit = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/credit/hostory_credit',params,options); };

    //授信
    fnr.request.setCredit = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/credit/set',params,options); };

    //获取平台总资金
    fnr.request.queryTotalFunds = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/select_platfrom_fund',params,options); };

    //平台总资金列表
    fnr.request.select_fund_serial = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/select_fund_serial',params,options); };

    //查询企业对公账户
    fnr.request.getBankInfo = function(params,options){ return fnr.ajaxJson('/voucher/api/local/core/finance/select/bank',params,options); };
});
