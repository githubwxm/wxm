package com.all580.order.manager;

import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dto.AccountDataDto;
import com.all580.order.dto.GenerateAccountDto;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemAccount;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.JobClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/8 19:07
 */
@Component
@Transactional(rollbackFor = {Exception.class, RuntimeException.class}, readOnly = true)
public class BaseOrderManager {
    @Autowired
    private EpService epService;
    @Autowired
    private BalancePayService balancePayService;

    @Autowired
    private JobClient jobClient;

    @Value("${task.tracker}")
    private String taskTracker;

    @Value("${task.maxRetryTimes}")
    private Integer maxRetryTimes;

    /**
     * 获取利润
     * @param salesInfoList 销售链
     * @param epId 企业
     * @return
     */
    public AccountDataDto getProfit(List<EpSalesInfo> salesInfoList, int epId) {
        int salePrice = 0;
        int buyPrice = 0;
        for (EpSalesInfo info : salesInfoList) {
            if (info.getSaleEpId() == epId) {
                salePrice = info.getPrice();
            }
            if (info.getBuyEpId() == epId) {
                buyPrice = info.getPrice();
            }
        }
        return new AccountDataDto(salePrice, buyPrice, salePrice - buyPrice, null, null);
    }

    /**
     * 获取进货结构
     * @param epSalesInfos 销售链
     * @param buyEpId 进货企业ID
     * @return
     */
    public EpSalesInfo getBuyingPrice(List<EpSalesInfo> epSalesInfos, Integer buyEpId) {
        for (EpSalesInfo info : epSalesInfos) {
            if (info.getBuyEpId() == buyEpId) {
                return info;
            }
        }
        return null;
    }

    /**
     * 判断企业状态
     * @param result 企业状态返回
     * @param status 需要的状态
     * @return
     */
    public boolean isEpStatus(Result<Integer> result, int status) {
        if (result != null && result.get() == status) {
            return true;
        }
        return false;
    }

    /**
     * 获取平台商ID
     * @param result
     * @return
     */
    public Integer getCoreEpId(Result<Integer> result) {
        if (result == null || result.isNULL()) {
            return null;
        }
        return result.get();
    }

    public Result<Integer> getCoreEpId(int epId) {
        return epService.selectPlatformId(epId);
    }

    public Integer getCoreEp(Map<Integer, Integer> coreEpMap, Integer epId) {
        Integer buyCoreEpId = coreEpMap.get(epId);
        if (buyCoreEpId == null) {
            buyCoreEpId = getCoreEpId(getCoreEpId(epId));
            if (buyCoreEpId == null) {
                throw new ApiException("企业平台商不存在");
            }
            coreEpMap.put(epId, buyCoreEpId);
        }
        return buyCoreEpId;
    }

    /**
     * 把子订单转换为产品参数
     * @param orderItem 子订单
     * @return
     */
    public ProductSearchParams parseParams(OrderItem orderItem) {
        ProductSearchParams params = new ProductSearchParams();
        params.setSubProductId(orderItem.getProSubId());
        params.setStartDate(orderItem.getStart());
        params.setDays(orderItem.getDays());
        params.setQuantity(orderItem.getQuantity());
        params.setSubProductId(orderItem.getId());
        return params;
    }

    /**
     * 添加任务
     * @param action 任务执行器
     * @param params 参数
     */
    public void addJob(String action, Map<String, String> params) {
        Job job = new Job();
        job.setTaskId("ORDER-JOB-" + UUIDGenerator.generateUUID());
        job.setParam("ACTION", action);
        job.setExtParams(params);
        job.setTaskTrackerNodeGroup(taskTracker);
        if (maxRetryTimes != null) {
            job.setMaxRetryTimes(maxRetryTimes);
        }
        job.setNeedFeedback(true);
        jobClient.submitJob(job);
    }

    /**
     * 余额变动
     * @param type 变动类型
     * @param sn 流水
     * @param infos 账户
     * @return
     */
    public Result<BalanceChangeRsp> changeBalances(int type, String sn, BalanceChangeInfo... infos) {
        return changeBalances(type, sn, Arrays.asList(infos));
    }

    /**
     * 余额变动
     * @param type 变动类型
     * @param sn 流水
     * @param infos 账户
     * @return
     */
    public Result<BalanceChangeRsp> changeBalances(int type, String sn, List<BalanceChangeInfo> infos) {
        return balancePayService.changeBalances(infos, type, sn);
    }
}
