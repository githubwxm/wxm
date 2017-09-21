define(['fnr'],function(fnr){
    /*添加团队*/
    fnr.request.addGroup = function(params,options){ return fnr.ajaxJson('api/local/core/group/add',params,options); };
    /*团队列表*/
    fnr.request.groupList = function(params,options){ return fnr.ajaxJson('api/local/client/group/queryGroupList',params,options); };
    /*团队详情*/
    fnr.request.queryGroupById = function(params,options){ return fnr.ajaxJson('api/local/client/group/queryGroupById',params,options); };
    /*修改团队*/
    fnr.request.editGroup = function(params,options){ return fnr.ajaxJson('api/local/core/group/update',params,options); };
    /*添加导游*/
    fnr.request.addGuide = function(params,options){ return fnr.ajaxJson('api/local/core/group/guide/add',params,options); };
    /*导游列表*/
    fnr.request.guideList = function(params,options){ return fnr.ajaxJson('api/local/client/group/queryGuideList',params,options); };
    /*导游信息*/
    fnr.request.queryGuideById = function(params,options){ return fnr.ajaxJson('api/local/client/group/queryGuideById',params,options); };
    /*修改导游*/
    fnr.request.editGuide = function(params,options){ return fnr.ajaxJson('api/local/core/group/guide/update',params,options); };
    /*删除导游*/
    fnr.request.deleteGuide = function(params,options){ return fnr.ajaxJson('api/local/core/group/guide/delete',params,options); };
    /*删除团队*/
    fnr.request.deleteGroup = function(params,options){ return fnr.ajaxJson('api/local/core/group/delete',params,options); };
    /*新增成员*/
    fnr.request.addVisitor = function(params,options){ return fnr.ajaxJson('api/local/core/group/member/add',params,options); };
    /*团员列表*/
    fnr.request.VisitorList = function(params,options){ return fnr.ajaxJson('api/local/client/group/queryVisitorList',params,options); };
    /*团员列表不分页*/
    fnr.request.VisitorListNoPage = function(params,options){ return fnr.ajaxJson('api/local/client/group/queryVisitorListNoPage',params,options); };
    /*删除团员*/
    fnr.request.deleteVisitor = function(params,options){ return fnr.ajaxJson('api/local/core/group/member/delete',params,options); };
    /*团队下单*/
    fnr.request.buyGroupTicked = function(params,options){ return fnr.ajaxJson('api/local/core/order/group/create',params,options); };
    /*团队订单团员信息*/
    fnr.request.ticketVisitorList = function(params,options){ return fnr.ajaxJson('api/local/client/Order/ticketVisitorList',params,options); };
    /*团队订单导游信息*/
    fnr.request.ticketGuideList = function(params,options){ return fnr.ajaxJson('api/local/client/Order/ticketGuideList',params,options); };
    /*修改团队订单信息*/
    fnr.request.editGroupTicket = function(params,options){ return fnr.ajaxJson('api/local/core/order/modify/group/ticket',params,options); };
});
