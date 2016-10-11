package com.all580.ep.service;

import com.all580.ep.api.EpConstant;
import com.all580.ep.api.entity.CoreEpAccess;
import com.all580.ep.api.entity.Ep;
import com.all580.ep.api.entity.PlatformEp;
import com.all580.ep.api.service.CoreEpAccessService;
import com.all580.ep.api.service.CoreEpPaymentConfService;
import com.all580.ep.api.service.EpService;
import com.all580.ep.com.Common;
import com.all580.ep.dao.EpMapper;

import com.framework.common.Result;
import com.framework.common.exception.ParamsMapValidationException;
import com.framework.common.validate.ParamsMapValidate;
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
    private CoreEpPaymentConfService coreEpPaymentConfService;

    /**
     * // 创建平台商
     * @param map
     * @return
     */
    @Override
    public Result<CoreEpAccess> createPlatform(Map map) {
        map.put("status", EpConstant.EpStatus.UNINITIAL);// 状态默认未初始化
        map.put("status_bak",EpConstant.EpStatus.UNINITIAL);// 状态默认未初始化
        map.put("ep_type",EpConstant.EpType.PLATFORM);
        map.put("access_id", Common.getAccessId());
        map.put("access_key", Common.getAccessKey());
            try {
                ParamsMapValidate.validate(map, generateCreateEpValidate());
            } catch (ParamsMapValidationException e) {
                log.warn("平台商参数错误");
                return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
            }
        int epId= epMapper.create(map);//返回刚添加的平台商
        CoreEpAccess access=null;
        Result<CoreEpAccess> result = new Result<CoreEpAccess>();
        if(epId>0){// 添加平台商t_core_ep_access
            access = new CoreEpAccess(epId,Common.getAccessId(),Common.getAccessKey());
            Map accessMap = new HashMap();
            accessMap.put("id",map.get("id"));
            accessMap.put("access_id",access.getAccess_id() );
            accessMap.put("access_key",access.getAccess_key() );
            accessMap.put("link", "");//TODO 待完善
            try {
                coreEpAccessService.create(accessMap);
                result.put(access);
                result.setSuccess();
            } catch (Exception e) {
                result.setFail();
                result.setError(Result.DB_FAIL, "添加验证地址错误");
            }
        }else{
            log.warn("添加平台商错误");
            return new Result<>(false, Result.PARAMS_ERROR, "添加平台商错误");
        }
       // map.put("epId",epId);
       // epMapper.update(map);//TODO  所属平台商企业id为平台商时是否指定
        map.put("core_ep_id",map.get("id"));
        map.put("conf_data","");
        map.put("payment_type",EpConstant.PaymentType.BALANCE);//默认方式余额
        coreEpPaymentConfService.create(map);//余额支付配置
        //TODO  Insert t_capital(添加通道费率, 余额 )
        return result;
    }

    @Override
    public Result<Map> createEp(Map map) {
        map.put("status", EpConstant.EpStatus.UNINITIAL);// 状态默认未初始化
        map.put("status_bak",EpConstant.EpStatus.UNINITIAL);// 状态默认未初始化
        map.put("access_id", Common.getAccessId());
        map.put("access_key", Common.getAccessKey());
        try {
            String ep_type=(String)map.get("ep_type");//企业类型
            String creator_ep_id =(String) map.get("creator_ep_id");//上级企业id
            String group_id=(String)map.get("group_id");
            String ep_class = (String) map.get("ep_class");//企业分类
            if(!Common.isTrue(creator_ep_id,"\\d+")){
                throw new  ParamsMapValidationException("上级企业类型错误");
            }
            if(!Common.isTrue(ep_class,"\\d+")){
                throw new  ParamsMapValidationException("企业分类错误");
            }
            if(!(null==map.get("group_id")||Common.isTrue(group_id,"\\d+"))){
                throw new  ParamsMapValidationException("分组id只能为空或是数字");
            }
            if(!Common.isTrue(ep_type,"\\d+")){//供应商添加企业    销售商自营商与OTA创建的都是销售商
                Map tempMap = new HashMap();
                tempMap.put("id",creator_ep_id);
                Integer epType=  select(tempMap).get().get(0).getEp_type();// 没有传企业类型获取创建企业类型

                if(EpConstant.EpType.SUPPLIER.equals(epType)){
                    map.put("ep_type",epType);
                }else if(EpConstant.EpType.SELLER.equals(epType)||EpConstant.EpType.OTA.equals(epType)||
                        EpConstant.EpType.DEALER.equals(epType)){
                    map.put("ep_type",EpConstant.EpType.SELLER);
                }else{
                throw new  ParamsMapValidationException("企业类型错误");}
            }

            ParamsMapValidate.validate(map, generateCreateEpValidate());
            map.put("core_ep_id",selectPlatformId(Integer.parseInt(creator_ep_id ) ).get());
        } catch (ParamsMapValidationException e) {
            log.warn("企业商参数错误");
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        Map resultMap= new HashMap();
        Result<Map> result = new Result<Map>();
        try {
            epMapper.create(map);//添加企业信息
            //Todo 添加余额
            resultMap.put("ep_info",map);
            result.put(resultMap);// 添加企业信息map
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "添加企业。出错原因："+e.getMessage());
        }
        return result;

    }

    @Override
    public Result<Integer>  selectPlatformId(Integer epId){
        Result<Integer> result = new Result<Integer>();
        if(epId==null){
            result.setFail();
            result.setError(Result.PARAMS_ERROR, "参数错误");
        }
        Integer core_ep_id=null;
        Map map = new HashMap();
        map.put("id",epId);
        Result<List<Ep>>  list= select(map);
        if(null!=list){
            if(!list.isEmpty()){
                core_ep_id =  list.get().get(0).getCore_ep_id();
                if(null==core_ep_id){
                    core_ep_id=epId;
                }
                result.put(core_ep_id);
                result.setSuccess();
            }
        }
        return result;
    }

    @Override
    public Result<Integer> updateStatus(Map params) {
        try {
            ParamsMapValidate.validate(params, generateCreateStatusValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("修改企业状态参数错误");
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }
        Result<Integer>  result= new Result<Integer>() ;
        try {
            result.put(epMapper.updateStatus(params));
            result.setSuccess();
        }catch (Exception e){
            result.setFail();
            result.setError(Result.DB_FAIL, "更新异常");
        }
        return result;
    }

    @Override
    public Result<Integer> freeze(Map params) {
        params.put("status",EpConstant.EpStatus.FREEZE);
        params.put("statusActive",EpConstant.EpStatus.ACTIVE);
        return updateStatus(params);
    }

    @Override
    public Result<Integer> disable(Map params) {
        params.put("status",EpConstant.EpStatus.STOP);
        params.put("statusActive",EpConstant.EpStatus.ACTIVE);
        return updateStatus(params);
    }

    @Override
    public Result<Integer> enable(Map params) {
        params.put("status",EpConstant.EpStatus.ACTIVE);
        return updateStatus(params);
    }

    @Override
    public Result<Map> updateEp(Map map) {
        try {
            String ep_class= (String)map.get("ep_class");
            String id=map.get("id").toString();
            if(!Common.isTrue(id,"\\d+")){
                throw new  ParamsMapValidationException("企业id错误");
            }
            if(!Common.isTrue(ep_class,"\\d+")){
                throw new  ParamsMapValidationException("企业分类错误");
            }
            ParamsMapValidate.validate(map, generateCreateEpValidate());
        } catch (ParamsMapValidationException e) {
            log.warn("平台商参数错误");
            return new Result<>(false, Result.PARAMS_ERROR, e.getMessage());
        }

        Result<Map> result = new Result<>();
        try {
            epMapper.update(map);
            result.put(map);
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库更新错误。出错原因："+e.getMessage());
        }
        return result;
    }

    /**
     * 查询企业接口，列表单个
     * @param map
     * @return
     */
    @Override
    public Result< List<Ep>> select(Map map) {
        Result<List<Ep>> result = new Result<List<Ep>>();
        try {
            result.put(epMapper.select(map));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库查询出错。出错原因："+e.getMessage());
        }
        return result;
    }

    /**
     * 查询平台商下所有的企业接口
     * @param params
     * @return
     */
    @Override
    public Result<List<Ep>> all(Map params) {
     Result<List<Ep>> result = new Result<List<Ep>>();
        try {
            result.put(epMapper.all(params));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库查询出错。出错原因："+e.getMessage());
        }
        return result;
    }

    /**
     * 验证平台商
     * @param params
     * @return
     */
    @Override
    public Result<PlatformEp> validate(Map params) {
        Result<PlatformEp> result = new Result<PlatformEp>();
        CoreEpAccess access=  coreEpAccessService.select(params).get();
        if(null!=access){
            PlatformEp platformEp  = new PlatformEp();
            params.clear();
            params.put("id",access.getId());
            try {
                platformEp.setEp_info( epMapper.select(params).get(0));
                platformEp.setPayment(coreEpPaymentConfService.findById(access.getId()));
                platformEp.setCapital(null);//TODO   t_capital(余额)
                result.put(platformEp);
                result.setSuccess();
            } catch (Exception e) {
                result.setFail();
                result.setError(Result.DB_FAIL, "平台商校验失败");
            }
            return result;
        }else{
            result.setFail();
            result.setError(Result.DB_FAIL, "授权ID校验失败");
            return result;
        }
    }

    /**
     * 获取企业状态（包括上级企业）
     *0-未初始化1-正常\n2-已冻结\n3-已停用
     * @param id
     * @return
     */
    @Override
    public Result<Integer> getEpStatus(Integer id){
        Result<Integer> result = new Result<Integer>();
        int status=-1;
        Map map = new HashMap();
        while(true){
               map.put("id",id);
            List<Ep> list=  epMapper.select(map);
            if(list==null){
                result.setFail();
                result.setError(Result.DB_FAIL, "查找企业出错");
                break;
            }
            Ep ep = list.get(0);
             id= ep.getCreator_ep_id();//上级企业id
             status = ep.getStatus();
            if(status!=EpConstant.EpStatus.ACTIVE||ep.getEp_type()==EpConstant.EpType.PLATFORM) {//查到平台商 或者不是正常的就直接返回
                result.put(status);
                break;
            }
        }
        return result;
    }
    /**
     * 获取企业基本信息接口
     * @param epids
     * @param field
     * @return
     */
    @Override
    public Result<List<Ep>> getEp(Integer[] epids, String[] field) {
        Map map = new HashMap();
        map.put("epids",epids);
        map.put("field",field);
        Result<List<Ep>> result = new Result<List<Ep>>();
        try {
            result.put(epMapper.getEp(map));
            result.setSuccess();
        } catch (Exception e) {
            result.setFail();
            result.setError(Result.DB_FAIL, "数据库查询出错。出错原因：***************");
        }
        return result;

    }


    private Map<String[], ValidRule[]> generateCreateEpValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name.", // 企业中文名
                "code", // '企业组织机构代码',
                "logo_pic", // '企业组织机构代码',
                "license", // 营业证编号
                "linkman", // 企业联系人姓名
                "link_phone", // 企业联系人电话
                "province", // 企业省
                "city", // 市
                "area", // 区
                "address", // 详细地址
        }, new ValidRule[]{new ValidRule.NotNull()});
//
//        // 校验整数
        rules.put(new String[]{
                "province", // 企业省
                "city", // 市
                "area", // 区
        }, new ValidRule[]{new ValidRule.Digits()});

//        // 校验身份证
//        rules.put(new String[]{
//                "items.visitor.sid" // 订单游客身份证号码
//        }, new ValidRule[]{new ValidRule.IdCard()});
        // 校验手机号码
        rules.put(new String[]{
                "link_phone", // 订单联系人手机号码
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});
        return rules;
    }
    private Map<String[], ValidRule[]> generateCreateStatusValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id.", //
                "access_id"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id" // 企业id
        }, new ValidRule[]{new ValidRule.Digits()});
        return rules;
    }
}
