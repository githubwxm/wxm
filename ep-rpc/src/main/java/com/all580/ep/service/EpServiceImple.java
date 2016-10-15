package com.all580.ep.service;

import com.all580.ep.api.conf.EpConstant;

import com.all580.ep.api.service.EpBalanceThresholdService;
import com.all580.ep.dao.EpMapper;
import com.all580.ep.entity.PlatformEp;
import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.api.service.EpService;
import com.all580.ep.com.Common;


import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.BalancePayService;
import com.all580.payment.api.service.EpPaymentConfService;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    private EpBalanceThresholdService epBalanceThresholdService;

    /**
     * // 创建平台商
     *
     * @param map
     * @return CoreEpAccess
     */
    @Override
    public Result<Map> createPlatform(Map map) {

        map.put("status", EpConstant.EpStatus.UNINITIAL);// 状态默认未初始化
        map.put("status_bak", EpConstant.EpStatus.UNINITIAL);// 状态默认未初始化
        map.put("ep_type", EpConstant.EpType.PLATFORM);
        map.put("access_id", Common.getAccessId());
        int epId = 0;
        try {
            epId = epMapper.create(map);//添加成功
        } catch (Exception e) {
            log.error("添加平台商异常", e);
            throw new ApiException("添加平台商异常", e);
        }
        map.put("access_key", Common.getAccessKey());

        Integer coreEpId = null;//企业id 平台商id
        Result<Map> result = new Result<>();
        if (epId > 0) {
            coreEpId = Integer.parseInt(map.get("id").toString());
            Map accessMap = new HashMap();
            accessMap.put("id", coreEpId);
            accessMap.put("access_id", Common.getAccessId());// 添加平台商t_core_ep_access
            accessMap.put("access_key", Common.getAccessKey());
            accessMap.put("link", "");//TODO 待完善

            //epMapper.update(map);//TODO  所属平台商企业id为平台商时是否指定
            map.put("coreEpId", coreEpId);
            map.put("confData", "");
            map.put("paymentType", PaymentConstant.PaymentType.BALANCE);//默认方式余额

            try {
                coreEpAccessService.create(accessMap);
                epPaymentConfService.create(map);//添加支付方式
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

    /**
     * 验证平台商
     *
     * @param params
     * @return PlatformEp
     */
    @Override
    public Result<Map> validate(Map params) {
        Result<Map> result = new Result<>();
        Map access;
        try {
            access = coreEpAccessService.select(params).get();
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }

        Map map = new HashMap();
        if (null != access) {
            params.clear();
            Integer coreEpId = Common.objectParseInteger(access.get("id"));
            params.put("id", coreEpId);
            try {
                map.put("ep_info", epMapper.select(params).get(0));
                // 查询支付方式，刚添加的应只有个余额支付的方式
                map.put("payment", epPaymentConfService.listByEpId(coreEpId).get().get(0));
                map.put("capital", balancePayService.getBalanceAccountInfo(coreEpId, coreEpId));
                result.put(map);
                result.setSuccess();
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
    public Result<Map> createEp(Map map) {
        boolean flag = false;//是否添加余额阀值标识
        map.put("status", EpConstant.EpStatus.UNINITIAL);// 状态默认未初始化
        map.put("status_bak", EpConstant.EpStatus.UNINITIAL);// 状态默认未初始化
        map.put("access_id", Common.getAccessId());
        map.put("access_key", Common.getAccessKey());
        String ep_type = (String) map.get("ep_type");//企业类型
        String creator_ep_id = (String) map.get("creator_ep_id");//上级企业id
        String group_id = (String) map.get("group_id");
        String ep_class = (String) map.get("ep_class");//企业分类
        try {
            if (!Common.isTrue(creator_ep_id, "\\d+")) {
                throw new ParamsMapValidationException("上级企业类型错误");
            }
            if (!Common.isTrue(ep_class, "\\d+")) {
                throw new ParamsMapValidationException("企业分类错误");
            }
            if (!(null == map.get("group_id") || Common.isTrue(group_id, "\\d+"))) {
                throw new ParamsMapValidationException("分组id只能为空或是数字");
            }
            if (!Common.isTrue(ep_type, "\\d+")) {//供应商添加企业    销售商自营商与OTA创建的都是销售商
                Map tempMap = new HashMap();
                tempMap.put("id", creator_ep_id);
                Integer epType = Common.objectParseInteger(select(tempMap).get().get(0).get("ep_type"));// 没有传企业类型获取创建企业类型

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
        Map resultMap = new HashMap();
        Result<Map> result = new Result<Map>();
        try {
            epMapper.create(map);//添加企业信息
            Integer epId = Common.objectParseInteger(map.get("id"));
            Integer core_ep_id = selectPlatformId(Integer.parseInt(creator_ep_id)).get();
            balancePayService.createBalanceAccount(epId, core_ep_id); //
            if (flag) {//如果是分销商默认加载余额阀值为1000
                Map epBalanceThresholdMap = new HashMap();
                epBalanceThresholdMap.put("ep_id", epId);
                epBalanceThresholdMap.put("core_ep_id", core_ep_id);
                epBalanceThresholdService.createOrUpdate(epBalanceThresholdMap);
            }
            resultMap.put("ep_info", map);
            resultMap.put("capital", balancePayService.getBalanceAccountInfo(epId, core_ep_id));
            result.put(resultMap);// 添加企业信息map
            result.setSuccess();
        } catch (Exception e) {
            log.error("添加企业出错", e);
            throw new ApiException("添加企业出错", e);
        }
        return result;

    }

    @Override
    public Result<Integer> selectPlatformId(Integer epId) {
        Result<Integer> result = new Result<Integer>();
        if (epId == null) {
            result.setFail();
            result.setError(Result.PARAMS_ERROR, "参数错误");
        }
        Integer core_ep_id = null;
        Map map = new HashMap();
        map.put("id", epId);
        try {
            Result<List<Map>> list = select(map);
            if (null != list) {
                if (!list.isEmpty()) {
                    core_ep_id = Common.objectParseInteger(list.get().get(0).get("core_ep_id"));//list.get().get(0).getCore_ep_id();
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

    public Result<Integer> updateStatus(Map params) {

        Result<Integer> result = new Result<Integer>();
        try {
            result.put(epMapper.updateStatus(params));
            result.setSuccess();
        } catch (Exception e) {
            log.error("更新异常", e);
            throw new ApiException("更新异常", e);
        }
        return result;
    }

    @Override
    public Result<Integer> freeze(Map params) {
        params.put("status", EpConstant.EpStatus.FREEZE);
        params.put("statusActive", EpConstant.EpStatus.ACTIVE);
        try {
            return updateStatus(params);
        }catch (ApiException e){
            log.error("更新异常", e);
            throw new ApiException("更新异常", e);
        }
    }

    @Override
    public Result<Integer> disable(Map params) {
        params.put("status", EpConstant.EpStatus.STOP);
        params.put("statusActive", EpConstant.EpStatus.ACTIVE);
        try {
            return updateStatus(params);
        }catch (ApiException e){
            log.error("更新异常", e);
            throw new ApiException("更新异常", e);
        }
    }

    @Override
    public Result<Integer> enable(Map params) {
        params.put("status", EpConstant.EpStatus.ACTIVE);
        try {
            return updateStatus(params);
        }catch (ApiException e){
            log.error("更新异常", e);
            throw new ApiException("更新异常", e);
        }

    }

    @Override
    public Result<Integer> platformFreeze(Map params) {
        params.put("status", EpConstant.EpStatus.FREEZE);
        try {
            return updatePlatfrom(params);
        }catch (ApiException e){
            log.error("更新异常", e);
            throw new ApiException("更新异常", e);
        }
    }

    @Override
    public Result<Integer> platformDisable(Map params) {
        params.put("status", EpConstant.EpStatus.STOP);
        try {
            return updatePlatfrom(params);
        }catch (ApiException e){
            log.error("更新异常", e);
            throw new ApiException("更新异常", e);
        }
    }

    /**
     * 平台商停用冻结
     *
     * @param map
     * @return
     */
    private Result<Integer> updatePlatfrom(Map map) {
        Result<Integer> result = new Result<Integer>();
        try {
            result.put(epMapper.updatePlatfromStatus(map));
            result.setSuccess();
        } catch (Exception e) {
            log.error("更新异常", e);
            throw new ApiException("更新异常", e);
        }
        return result;
    }


    @Override
    public Result<Integer> platformEnable(Map params) {
        params.put("status", EpConstant.EpStatus.ACTIVE);
        Result<Integer> result = new Result<Integer>();
        try {
            result.put(epMapper.platformEnable(params));
            result.setSuccess();
        } catch (Exception e) {
            log.error("更新平台商状态异常", e);
            throw new ApiException("更新平台商状态异常", e);
        }
        return result;
    }

    @Override
    public Result<Map> updateEp(Map map) {
        Result<Map> result = new Result<>();
        try {
            epMapper.update(map);
            result.put(map);
            result.setSuccess();
        } catch (Exception e) {
            log.error("数据库更新错误", e);
            throw new ApiException("数据库更新错误", e);
        }
        return result;
    }

    @Override
    public Result<Map> platformListDown(Map map) {
        map.put("ep_type", EpConstant.EpType.PLATFORM);
        Result<Map> result = new Result<>();
        Map resultMap = new HashMap();
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
    public Result<Map> platformListUp(Map map) {
        map.put("ep_type", EpConstant.EpType.PLATFORM);
        Result<Map> result = new Result<>();
        Map resultMap = new HashMap();
        try {
            Common.checkPage(map);
            resultMap.put("list", epMapper.platformListUp(map));
            resultMap.put("totalCount", epMapper.platformListUpCount(map));
            result.put(resultMap);
            result.setSuccess();
        } catch (Exception e) {
            log.error("数据库查询错误", e);
            throw new ApiException("数据库查询错误", e);
        }
        return result;
    }

    @Override
    public Result<Integer> selectCreatorEpId(Integer id) {
        Map map = new HashMap();
        Result<Integer> result = new Result<>();
        try {
            if (Common.objectIsNumber(id)) {
                map.put("id", id);
                List<Map> list = epMapper.select(map);
                if (list.isEmpty()) {
                    result.put(-1);
                    result.setFail();
                } else {
                    result.put(Common.objectParseInteger(list.get(0).get("creator_ep_id")));
                    result.setSuccess();
                }
            }else{
                return new Result<>(false, Result.PARAMS_ERROR, "参数不合法");
            }
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }
        return result;
    }

    /**
     * 查询企业接口，列表单个
     *
     * @param map
     * @return Ep  Map
     */
    @Override
    public Result<List<Map>> select(Map map) {
        Result<List<Map>> result = new Result<>(true);
        try {
            result.put(epMapper.select(map));
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
    public Result<List<Map>> all(Map params) {
        Result<List<Map>> result = new Result<>();
        try {
            result.put(epMapper.all(params));
            result.setSuccess();
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }
        return result;
    }


    /**
     * 获取企业状态（包括上级企业）
     *
     * @param id
     * @return
     */
    @Override
    public Result<Integer> getEpStatus(Integer id) {
        Result<Integer> result = new Result<>();
        int status = -1;
        if (!Common.objectIsNumber(id)){
            return new Result<>(false, Result.PARAMS_ERROR, "参数不合法");
        }
        Map map = new HashMap();
        try {
            while (true) {
                map.put("id", id);
                List<Map> list = epMapper.select(map);
                if (list == null) {
                    result= new Result<>(false, Result.PARAMS_ERROR, "参数不合法");
                    break;
                }
                Map ep = list.get(0);
                id = CommonUtil.objectParseInteger(ep.get("creator_ep_id"));  //ep.getCreator_ep_id();//上级企业id
                status = CommonUtil.objectParseInteger(ep.get("status"));  //ep.getStatus();
                if (status != EpConstant.EpStatus.ACTIVE || EpConstant.EpType.PLATFORM.equals(Integer.parseInt(ep.get("ep_type").toString()))) {//查到平台商 或者不是正常的就直接返回
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

    /**
     * 获取企业基本信息接口
     *
     * @param epids    企业id
     * @param field    企业列    所传的值必在一下列里面
     id
    name  企业名称
    en_name  企业英文名
    ep_type   10000-畅旅平台商10001平台商10002供应商10003销售商10004自营商10005OTA
    linkman    联系人
    link_phone  联系电话
    address   地址
    code   企业组织机构代码
    license  营业执照
    logo_pic  企业logo
    status  100初始化101-正常\n102-已冻结\n103-已停用
    access_id   运营平台接口访问标识
    access_key  运营平台接口访问密钥
    creator_ep_id    上级企业
    core_ep_id   所属平台商企业id
    add_time
    status_bak    ' 冻结/停用平台商操作时企业当前的状态
    province  省
    city  市
    area  区
    group_id  组ID
    group_name  组名称
    ep_class   10010;//景区10011;//酒店10012;//旅行社10013;//其他
     * @return
     */
    @Override
    public Result<List<Map>> getEp(Integer[] epids, String[] field) {
        Map map = new HashMap();
        map.put("epids", epids);
        map.put("field", field);
        Result<List<Map>> result = new Result<>();
        try {
            result.put(epMapper.getEp(map));
            result.setSuccess();
        } catch (Exception e) {
            log.error("查询数据库异常", e);
            throw new ApiException("查询数据库异常", e);
        }
        return result;

    }
}
