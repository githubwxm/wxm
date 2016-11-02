package com.all580.ep.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.api.service.EpService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.CoreEpAccessMapper;
import com.all580.ep.dao.EpMapper;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.BalancePayService;
import com.all580.payment.api.service.EpPaymentConfService;
import com.all580.product.api.service.PlanGroupRPCService;
import com.framework.common.Result;
import com.framework.common.synchronize.SynchronizeDataManager;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import javax.lang.exception.ParamsMapValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class EpServiceImple implements EpService {


    @Autowired
    private EpMapper epMapper;

    @Autowired
    private CoreEpAccessService coreEpAccessService;

    @Autowired
    private EpPaymentConfService epPaymentConfService;

    @Autowired
    private BalancePayService balancePayService;

    @Autowired
    private PlanGroupRPCService planGroupRPCService;

    @Autowired
    private EpBalanceThresholdService epBalanceThresholdService;

    @Autowired
    private SynchronizeDataManager synchronizeDataManager;

    @Autowired
    private CoreEpAccessMapper coreEpAccessMapper;//ddd

    /**
     * // 创建平台商
     *
     * @param map
     * @return CoreEpAccess
     */
    @Override
    public Result<Map<String,Object>> createPlatform(Map<String,Object> map) {
        try {
            checkNamePhone(map);//检查电话与名字是否存在 ，存在抛出异常
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }
        map.put("status", EpConstant.EpStatus.ACTIVE);// 状态默认未初始化
        map.put("status_bak", EpConstant.EpStatus.ACTIVE);// 状态默认未初始化
        map.put("ep_type", EpConstant.EpType.PLATFORM);
        map.put("access_id", Common.getAccessId());
        // CommonUtil.formtAddress(map);
        int epId = 0;
        try {
            epId = epMapper.create(map);//添加成功
        } catch (Exception e) {
            log.error("添加平台商异常", e);
            throw new ApiException("添加平台商异常", e);
        }
        map.put("access_key", Common.getAccessKey());

        Integer coreEpId = null;//企业id 平台商id
        Result<Map<String,Object>> result = new Result<>();
        if (epId > 0) {
            coreEpId = Integer.parseInt(map.get("id").toString());
            Map<String,Object> accessMap = new HashMap();
            accessMap.put("id", coreEpId);
            accessMap.put("access_id", Common.getAccessId());// 添加平台商t_core_ep_access
            accessMap.put("access_key", Common.getAccessKey());
            accessMap.put("link", "");//TODO 待完善


            map.put("coreEpId", coreEpId);
            map.put("confData", "");
            map.put("paymentType", PaymentConstant.PaymentType.BALANCE);//默认方式余额

            try {
                epMapper.updateCoreEpId(map);//TODO  所属平台商企业id为平台商时是否指定
                coreEpAccessService.create(accessMap);
                //epPaymentConfService.create(map);//添加支付方式
                balancePayService.createBalanceAccount(coreEpId, coreEpId);//添加余额d
                result.put(accessMap);
                result.setSuccess();
            } catch (Exception e) {
                 log.error("添加平台商未成功", e);
                throw new ApiException("添加平台商异常", e);
            }
        } else {
            log.warn("添加平台商未成功");
            throw new ApiException("添加平台商未成功");
        }
        return result;
    }

    @Override
    public Result<Map<String,Object>> selectPlatform() {
        Result<Map<String,Object>> result = new Result<>(true);
        try {
            Map<String,Object> map = new HashMap();
            map.put("list", epMapper.selectPlatform());
            result.setCode(200);
            result.put(map);
            return result;
        } catch (Exception e) {
            log.error("查询数据库错误", e);
            throw new ApiException("查询数据库错误", e);
        }
    }

    /**
     * 验证平台商
     *
     * @param params
     * @return PlatformEp
     */
    @Override
    public Result<Map<String,Object>> validate(Map<String,Object> params) {
        Result<Map<String,Object>> result = new Result<>();
        Map access;
        try {
            access = coreEpAccessService.select(params).get().get(0);
        } catch (Exception e) {
             log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }

        Map<String,Object> map = new HashMap();
        if (null != access) {
            params.clear();
            Integer coreEpId = Common.objectParseInteger(access.get("id"));
            params.put("id", coreEpId);
            try {
                map.put("ep_info", epMapper.select(params).get(0));
                // 查询支付方式，刚添加的应只有个余额支付的方式
               // List<Map<String, String>> listPayment =epPaymentConfService.listByEpId(coreEpId).get();
                //map.put("payment", listPayment.get(0));//校验成功同步数据
                map.put("capital", balancePayService.getBalanceAccountInfo(coreEpId, coreEpId).get());
                result.put(map);
                result.setSuccess();


                //syncEpData(coreEpId,"t_core_ep_payment_conf",listPayment);//同步支付方式 默认余额
            } catch (Exception e) {
                 log.error("添加平台商未成功", e);
                throw new ApiException("添加平台商异常", e);
            }
            return result;
        } else {
             log.error("授权ID校验失败未找到");
            throw new ApiException("授权ID校验失败未找到");
        }
    }

    @Override
    public Result<Map<String,Object>> createEp(Map<String,Object> map) {
        try {
            checkNamePhone(map);//检查电话与名字是否存在 ，存在抛出异常
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }
        boolean flag = false;//是否添加余额阀值标识
        map.put("status", EpConstant.EpStatus.ACTIVE);// 状态默认未初始化
        map.put("status_bak", EpConstant.EpStatus.ACTIVE);// 状态默认未初始化
        map.put("access_id", Common.getAccessId());
        map.put("access_key", Common.getAccessKey());
        // CommonUtil.formtAddress(map);
        //address  // TODO: 2016/10/22 0022
        String ep_type = (String) map.get("ep_type");//企业类型
        String creator_ep_id = (String) map.get("creator_ep_id");//上级企业id
        Integer group_id = CommonUtil.objectParseInteger(map.get("group_id"));
        String ep_class = (String) map.get("ep_class");//企业分类
        try {
            Object groupName = planGroupRPCService.searchPlanGroupById(group_id).get().get("name");
            // String groupName="固定分组";
            if (null == groupName) {
                throw new ParamsMapValidationException("企业分组错误");
            }
            map.put("group_name", groupName);
            if (!Common.isTrue(creator_ep_id, "\\d+")) {
                throw new ParamsMapValidationException("上级企业类型错误");
            }
            if (!Common.isTrue(ep_class, "\\d+")) {
                throw new ParamsMapValidationException("企业分类错误");
            }

            if (!Common.isTrue(ep_type, "\\d+")) {//供应商添加企业    销售商自营商与OTA创建的都是销售商
                Map<String,Object> tempMap = new HashMap();
                tempMap.put("id", creator_ep_id);
                Integer epType = Common.objectParseInteger(epMapper.select(tempMap).get(0).get("ep_type"));// 没有传企业类型获取创建企业类型

                if (EpConstant.EpType.SUPPLIER.equals(epType)) {
                    map.put("ep_type", epType);
                } else if (EpConstant.EpType.SELLER.equals(epType) || EpConstant.EpType.OTA.equals(epType) ||
                        EpConstant.EpType.DEALER.equals(epType)) {
                    //todo 企业余额提醒值
                    flag = true;
                    map.put("ep_type", EpConstant.EpType.SELLER);
                } else {
                    throw new ParamsMapValidationException("企业类型错误");
                }
            }

        } catch (ParamsMapValidationException e) {
            log.error("企业商参数错误");
            throw new ParamsMapValidationException("企业类型错误");

        } catch (Exception e) {
            log.error("企业商参数错误", e);
            throw new ApiException("企业商参数错误", e);
        }
        Map<String,Object> resultMap = new HashMap();
        Result<Map<String,Object>> result = new Result<>();
        try {
            epMapper.create(map);//添加企业信息
            Integer epId = Common.objectParseInteger(map.get("id"));
            Integer core_ep_id = selectPlatformId(Integer.parseInt(creator_ep_id)).get();
            balancePayService.createBalanceAccount(epId, core_ep_id); //创建余额账户
            if (flag) {//如果是分销商默认加载余额阀值为1000
                Map<String,Object> epBalanceThresholdMap = new HashMap();
                epBalanceThresholdMap.put("ep_id", epId);
                epBalanceThresholdMap.put("core_ep_id", core_ep_id);
                Object threshold= map.get("threshold");
                if(null==threshold){
                    threshold=1000;
                    epBalanceThresholdMap.put("threshold",threshold);
                }
                epBalanceThresholdService.createOrUpdate(epBalanceThresholdMap);
            }
            resultMap.put("ep_info", map);
            resultMap.put("capital", balancePayService.getBalanceAccountInfo(epId, core_ep_id).get());
            result.put(resultMap);// 添加企业信息map
            result.setSuccess();
            result.setCode(200);
        } catch (Exception e) {
             log.error("添加企业出错", e);
            throw new ApiException("添加企业出错", e);
        }

        return result;

    }

    @Override
    public Result<Integer> selectPlatformId(Integer epId) {
        Result<Integer> result = new Result<>();
        if (epId == null) {
            result.setFail();
            result.setError(Result.PARAMS_ERROR, "参数错误");
        }
        Integer core_ep_id ;
        Map<String,Object> map = new HashMap<>();
        map.put("id", epId);
        try {
            List<Map<String,Object>> list = epMapper.select(map);
            if (null != list) {
                if (!list.isEmpty()) {
                    core_ep_id = Common.objectParseInteger(list.get(0).get("core_ep_id"));//list.get().get(0).getCore_ep_id();
                    if (null == core_ep_id) {
                        core_ep_id = epId;
                    }
                    result.put(core_ep_id);
                    result.setSuccess();
                }
            }
        } catch (Exception e) {
             log.error("查询平台商出错", e);
            throw new ApiException("查询平台商出错", e);
        }
        return result;
    }

    /**
     * 冻结停用企业
     * @param params
     * @return
     */
    public Result<Integer> updateStatus(Map<String,Object> params) {
        Object nextStatus = params.remove("status");//查询时先移除掉状态
        Result<Integer> result = new Result<Integer>();
        try {
            List<Map<String,Object>> list=  epMapper.select(params);
            if(list.isEmpty()){
                log.warn("未找到该企业");
                throw new ApiException("未找到该企业");
            }else{
                Integer currentStatus = CommonUtil.objectParseInteger(list.get(0).get("status"));
                if(EpConstant.EpStatus.ACTIVE.equals(nextStatus)){
                     if(EpConstant.EpStatus.ACTIVE.equals(currentStatus)){
                         log.warn("企业本身就是激活状态不用激活");
                         throw new ApiException("企业本身就是激活状态不用激活");
                     }  //这里的话是激活，激活不用判断状态
                }else {
                    if (!EpConstant.EpStatus.ACTIVE.equals(currentStatus)) {
                        log.warn("企业在未激活状态不能执行该操作");
                        throw new ApiException("企业在未激活状态不能执行该操作");
                    }
                }
                params.put("status",nextStatus);
            }
            int ref= epMapper.updateStatus(params);
           if(ref>0){
               List<Map<String,String>>  listMap =   epMapper.selectSingleTable(params);
               syncEpData(params.get("ep_id"),EpConstant.Table.T_EP,listMap);
               result.put(ref);
               result.setSuccess();
           }else{
               result.setError("数据不用更新");
           }
        } catch (Exception e) {
             log.error("冻结停用企业异常", e);
            throw new ApiException("冻结停用企业异常", e);
        }
        return result;
    }

    @Override
    public Result<Integer> freeze(Map<String,Object> map) {
        Map<String,Object> params= new HashMap();//  同一个查询方法，按map参数查找，所以重构
        params.put("id",map.get("id"));
        params.put(EpConstant.EpKey.CORE_EP_ID,map.get("ep_id"));
        params.put("status", EpConstant.EpStatus.FREEZE);
        params.put("statusActive", EpConstant.EpStatus.ACTIVE);
        try {
            return updateStatus(params);
        } catch (ApiException e) {
             log.error("更新异常", e);
            throw new ApiException("更新异常", e);
        }
    }

    @Override
    public Result<Integer> disable(Map<String,Object> map) {
        Map<String,Object> params= new HashMap();//  同一个查询方法，按map参数查找，所以重构
        params.put("id",map.get("id"));
        params.put(EpConstant.EpKey.CORE_EP_ID,map.get("ep_id"));
        params.put("status", EpConstant.EpStatus.STOP);
        params.put("statusActive", EpConstant.EpStatus.ACTIVE);
        try {
            return updateStatus(params);
        } catch (ApiException e) {
            log.error("更新异常", e);
            throw new ApiException("更新异常", e);
        }
    }

    /**
     * 激活企业
     * @param map
     * @return
     */
    @Override
    public Result<Integer> enable(Map<String,Object> map) {
        Map<String,Object> params= new HashMap<>();//  同一个查询方法，按map参数查找，所以重构
        params.put("id",map.get("id"));
        params.put(EpConstant.EpKey.CORE_EP_ID,map.get("ep_id"));
        params.put("status", EpConstant.EpStatus.ACTIVE);
        try {
            Result<Integer> result = new Result<Integer>(true);
            Integer ref =updateStatus(params).get();
            if(ref>0){//是否有更新
                List<Map<String,String>>  listMap =   epMapper.selectSingleTable(params);
                syncEpData(map.get("ep_id"),EpConstant.Table.T_EP,listMap);
                result.setSuccess();
            }else{
                result.setError("企业已经激活");
            }
            return result;
        } catch (ApiException e) {
             log.error("激活企业更新异常", e);
            throw new ApiException("激活企业更新异常", e);
        }

    }

    @Override
    public Result<Integer> platformFreeze(Map<String,Object> map) {
        Map<String,Object> params= new HashMap();//  同一个查询方法，按map参数查找，所以重构
        params.put("id",map.get("id"));
        //params.put(EpConstant.EpKey.CORE_EP_ID,map.get("ep_id"));
        params.put("statusActive", EpConstant.EpStatus.ACTIVE);
        params.put("status", EpConstant.EpStatus.FREEZE);
        try {
            return updatePlatfrom(params);
        } catch (ApiException e) {
             log.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }
    }

    @Override
    public Result<Integer> platformDisable(Map<String,Object> map) {
        Map<String,Object> params= new HashMap();
        params.put("id",map.get("id"));
       // params.put(EpConstant.EpKey.CORE_EP_ID,map.get("ep_id"));
        params.put("statusActive", EpConstant.EpStatus.ACTIVE);
        params.put("status", EpConstant.EpStatus.STOP);
        try {
            return updatePlatfrom(params);
        } catch (ApiException e) {
             log.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }
    }

    @Override
    public Result<Integer> platformEnable(Map<String,Object> map) {
        Map<String,Object> params= new HashMap();
        params.put("id",map.get("id"));
        //params.put(EpConstant.EpKey.CORE_EP_ID,map.get("ep_id"));
        params.put("status", EpConstant.EpStatus.ACTIVE);
        Result<Integer> result = new Result<>();
        try {
            result.put(epMapper.platformEnable(params));//更新平台商的状态激活
            epMapper.epEnable(params);       //更新平台商企业下的状态为  之前状态
            result.setSuccess();
            Map<String,Object> paramsMap = new HashMap<>();//数据同步条件
            paramsMap.put("core_ep_id",map.get("id"));
            List<Map<String,String>>  listMap =   epMapper.selectSingleTable(paramsMap);
            syncEpData(map.get("id"),EpConstant.Table.T_EP,listMap);
        } catch (Exception e) {
            log.error("更新平台商状态异常", e);
            throw new ApiException("更新平台商状态异常", e);
        }
        return result;
    }

    /**
     * 平台商停用冻结
     *
     * @param map
     * @return
     */
    private Result<Integer> updatePlatfrom(Map<String,Object> map) {
        Result<Integer> result = new Result<>();
        Object nextStatus=map.remove("status");//同一个查询方法这个状态是非激活的 所以先移除掉不然查不到数据
        try {
            List<Map<String,Object>> list= epMapper.select(map);
            if(list.isEmpty()){
                throw new ApiException("未找到该平台商");
            }else{
                 Integer currentStatus=CommonUtil.objectParseInteger(list.get(0).get("status")) ;
                if(!EpConstant.EpStatus.ACTIVE.equals(currentStatus)){
                    throw new ApiException("平台商在未激活状态不能执行该操作");
                }else{
                    map.put("status",nextStatus);
                }
            }
            result.put(epMapper.updatePlatfromStatus(map));
            result.setSuccess();
            Map<String,Object> paramsMap = new HashMap<>();
            paramsMap.put("core_ep_id",map.get("id"));
            List<Map<String,String>>  listMap =   epMapper.selectSingleTable(paramsMap);
            syncEpData(map.get("id"),EpConstant.Table.T_EP,listMap);
        } catch(ApiException e){
            log.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }catch (Exception e) {
             log.error("平台商停用冻结平台商停用冻结", e);
            throw new ApiException("平台商停用冻结平台商停用冻结", e);
        }
        return result;
    }


    @Override
    public Result<Map<String,Object>> updateEp(Map<String,Object> map) {
        Result<Map<String,Object>> result = new Result<>();
        try {
            //CommonUtil.formtAddress(map);
            Integer group_id = CommonUtil.objectParseInteger(map.get("group_id"));
            if (null != group_id) {
                Object groupName = planGroupRPCService.searchPlanGroupById(group_id).get().get("name");
                map.put("group_name", groupName);
                log.error("查询企业分组错误");
            }
            // String groupName="固定分组";
          int ref=  epMapper.update(map);
            if(ref>0){
                List<Map<String,String>>  listMap =   epMapper.selectSingleTable(map);
                syncEpData(map.get(EpConstant.EpKey.CORE_EP_ID),EpConstant.Table.T_EP,listMap);
                result.put(map);
                result.setSuccess();
            }

        } catch (Exception e) {
            log.error("数据库更新错误", e);
            throw new ApiException("数据库更新错误", e);
        }
        return result;
    }

    @Override
    public Result<Map<String,Object>> platformListDown(Map<String,Object> map) {
        map.put("ep_type", EpConstant.EpType.PLATFORM);
        Result<Map<String,Object>> result = new Result<>();
        Map<String,Object> resultMap = new HashMap<>();
        try {
            Common.checkPage(map);
            resultMap.put("list", epMapper.platformListDown(map));
            resultMap.put("totalCount", epMapper.platformListDownCount(map));
            result.put(resultMap);
            result.setSuccess();
        } catch (Exception e) {
            log.error("数据库查询错误", e);
            throw new ApiException("数据库查询错误", e);
        }
        return result;
    }

    @Override
    public Result<Map<String,Object>> platformListUp(Map<String,Object> map) {
        map.put("ep_type", EpConstant.EpType.PLATFORM);
        Result<Map<String,Object>> result = new Result<>();
        Map resultMap = new HashMap();
        try {
            Common.checkPage(map);
            List<Map<String,Object>> list = epMapper.platformListUp(map);
            if (list.isEmpty()) {
                result.setError("未查询到数据");
            } else {
                resultMap.put("list", list);
                resultMap.put("totalCount", epMapper.platformListUpCount(map));
                result.put(resultMap);
                result.setSuccess();
            }
        } catch (Exception e) {
            log.error("数据库查询错误", e);
            throw new ApiException("数据库查询错误", e);
        }
        return result;
    }

    @Override
    public Result<List<Map<String,Object>>> selectSeller(Integer id) {
        Result<List<Map<String,Object>>> result = new Result<>();
        if (null == id) {
            result.setError(result.PARAMS_ERROR, "企业id不能为空");
            return result;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("creator_ep_id", id);
        map.put("ep_type", EpConstant.EpType.SELLER);
        try {
            Common.isEmpty(result, epMapper.select(map));
        } catch (Exception e) {
            log.error("查询企业下级销售商异常", e);
            result.setError(result.DB_FAIL, "查询企业下级销售商异常");
            //throw new ApiException("更新异常", e);
        }
        return result;
    }

    @Override
    public Result<Integer> selectCreatorEpId(Integer id) {
        Map<String,Object> map = new HashMap<>();
        Result<Integer> result = new Result<>();
        try {
            if (null != id) {
                map.put("id", id);
                List<Map<String,Object>> list = epMapper.select(map);
                if (list.isEmpty()) {
                    result.put(-1);
                    result.setFail();
                } else {
                    result.put(Common.objectParseInteger(list.get(0).get("creator_ep_id")));
                    result.setSuccess();
                }
            } else {
                return new Result<>(false, Result.PARAMS_ERROR, "参数不合法");
            }
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }
        return result;
    }

    @Override
    public Result<Boolean> checkNamePhone(Map<String,Object> map) {
        List<Map<String,Object>> list = epMapper.checkNamePhone(map);
        Result<Boolean> result = new Result<>();
        String message = "";
        try {
            if (list.isEmpty()) {
                result.put(true);
                result.setSuccess();
            } else if (list.size() == 2) {
                message = "企业名字与电话重复";
                throw new ApiException(message);
            } else if (list.size() == 1) {
                Map mapResult = list.get(0);
                String name = mapResult.get("name").toString();
                String link_phone = mapResult.get("link_phone").toString();
                if (name.equals(map.get("name"))) {
                    message = "企业名字重复 ";
                }
                if (link_phone.equals(map.get("link_phone"))) {
                    message += "号码已经存在 ";
                }
                throw new ApiException(message);
            }
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException(message, e);
        }
        return result;
    }

    /**
     * 查询企业列表
     *
     * @param map
     * @return Ep  Map
     */
    @Override
    public Result<Map<String,Object>> select(Map map) {
        Result<Map<String,Object>> result = new Result<>(true);
        try {
            Map<String,Object> resultMap = new HashMap<>();
            CommonUtil.checkPage(map);
            List<Map<String,Object>> list = epMapper.select(map);
            if (list.isEmpty()) {
                result.setError("未查询到数据");
            } else {
                resultMap.put("list", list);
                resultMap.put("totalCount", epMapper.selectCount(map));
                result.put(resultMap);
            }
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }
        return result;
    }

    /**
     * 查询平台商下所有的企业
     *
     * @param params
     * @return
     */
    @Override
    public Result<List<Map<String,Object>>> all(Map<String,Object> params) {
        Result<List<Map<String,Object>>> result = new Result<>();
        try {
            CommonUtil.checkPage(params);

            Common.isEmpty(result, epMapper.all(params));
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }
        return result;
    }

    /**
     * 获取企业状态（包括上级企业）
     *
     * @param  id
     * @return
     */
    @Override
    public Result<Integer> getEpStatus(Integer id) {
        Result<Integer> result = new Result<>();

        if (!Common.objectIsNumber(id)) {
            return new Result<>(false, Result.PARAMS_ERROR, "参数不合法");
        }
        Map<String,Object> map = new HashMap<>();
        try {
            while (true) {
                map.put("id", id);
                List<Map<String,Object>> list = epMapper.select(map);
                if (list == null) {
                    result = new Result<>(false, Result.PARAMS_ERROR, "参数不合法");
                    break;
                }
                Map<String,Object> ep = list.get(0);
                id = CommonUtil.objectParseInteger(ep.get("creator_ep_id"));  //ep.getCreator_ep_id();//上级企业id
             int   status = CommonUtil.objectParseInteger(ep.get("status"));  //ep.getStatus();

                if (status != EpConstant.EpStatus.ACTIVE ||
                        EpConstant.EpType.PLATFORM.equals(CommonUtil.objectParseInteger(ep.get("ep_type")))) {//查到平台商 或者不是正常的就直接返回
                    result.put(status);
                    result.setSuccess();
                    break;
                }
            }
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }
        return result;
    }

    @Override
    public Result<Map<String,Object>> selectId(Map<String,Object> params) {
        Result<Map<String,Object>> result = new Result<>();
        try {
            CommonUtil.checkPage(params);
            List<Map<String,Object>> resuleMap = epMapper.select(params);
            if (resuleMap.isEmpty()) {
                result.setError("未查询到数据");
            } else {
                result.put(resuleMap.get(0));
                result.setSuccess();
            }

        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }
        return result;
    }

    /**
     * 获取企业基本信息接口
     *
     * @param epids 企业id
     * @param field 企业列    所传的值必在一下列里面
     * @return
     */
    @Override
    public Result<List<Map<String,Object>>> getEp(Integer[] epids, String[] field) {
        Map<String,Object> map = new HashMap<>();
        map.put("epids", epids);
        map.put("field", field);
        Result<List<Map<String,Object>>> result = new Result<>();
        try {
            Common.isEmpty(result, epMapper.getEp(map));
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }
        return result;

    }
    /**
     * 同步数据
     * @param
     */
    public void syncEpData(Object coreEpId,String table,List<Map<String,String>> data) {
        if(!CommonUtil.objectIsNumber(coreEpId)){
            log.error("同步数据平台商错误 {} {}",table,data );
        }
        if(data.isEmpty()){
            log.error("没有要同步的数据");
        }
        Map<String,Object> tempMap = new HashMap<>();
        tempMap.put("id",coreEpId);
        List<Map<String,Object>> keyList =coreEpAccessMapper.select(tempMap);
        String key ="";
        if(keyList.isEmpty()){
            log.error("查询平台商key数据错误" );
            throw new ApiException("查询平台商key数据错误");
        }else{
            key=CommonUtil.objectParseString( keyList.get(0).get(EpConstant.EpKey.ACCESS_KEY));
        }

        synchronizeDataManager.generate(key)
                .put(table,data)
                .sync();
    }
}
