package com.all580.order.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemAccountMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.dto.AccountDataDto;
import com.all580.order.dto.GenerateAccountDto;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemAccount;
import com.all580.order.entity.OrderItemDetail;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.framework.common.Result;
import com.framework.common.exception.ApiException;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.JobClient;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BaseOrderManager {
    @Autowired
    private EpService epService;
    @Autowired
    private BalancePayService balancePayService;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;

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
        params.setSubOrderId(orderItem.getId());
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

    /**
     * 获取某天详情
     * @param detailList 天数据
     * @param day 某一天
     * @return
     */
    public OrderItemDetail getDetailByDay(List<OrderItemDetail> detailList, Date day) {
        if (detailList == null || day == null) {
            return null;
        }
        for (OrderItemDetail detail : detailList) {
            if (detail.getDay().equals(day)) {
                return detail;
            }
        }
        return null;
    }

    /**
     * 获取某天详情
     * @param daysData 天数据
     * @param day 某一天
     * @return
     */
    public JSONObject getAccountDataByDay(JSONArray daysData, String day) {
        if (daysData == null || day == null) {
            return null;
        }
        for (Object o : daysData) {
            JSONObject json = (JSONObject) o;
            String d = json.getString("day");
            if (day.equals(d)) {
                return json;
            }
        }
        return null;
    }

    /**
     * 根据每天的利润获取消费金额
     * @param consumeQuantity 消费张数
     * @param account 分账数据
     * @param coreEpId
     * @param dayData 消费日期利润
     * @return
     */
    private int getMoney(int consumeQuantity, OrderItemAccount account, int coreEpId, JSONObject dayData) {
        int money = 0;
        // 平台内部分账->利润
        if (coreEpId == account.getCoreEpId()) {
            int profit = dayData.getIntValue("profit");
            money = consumeQuantity * profit;
        } else {
            // 平台之间分账->进货价
            int inPrice = dayData.getIntValue("inPrice");
            money = consumeQuantity * inPrice;
        }
        return money;
    }

    /**
     * 核销反核销分账
     * @param orderItem 子订单
     * @param day 日期
     * @param consumeQuantity 张数
     * @param sn 流水
     * @param consume 是否核销
     */
    @Transactional
    public void consumeOrReConsumeSplitAccount(OrderItem orderItem, Date day, int consumeQuantity, String sn, boolean consume) {
        // 获取子订单的分账记录
        List<OrderItemAccount> accounts = orderItemAccountMapper.selectByOrderItem(orderItem.getId());
        Map<Integer, Integer> coreEpIdMap = new HashMap<>();
        // 存储调用余额分账的数据
        List<BalanceChangeInfo> balanceChangeInfoList = new ArrayList<>();
        // 预付
        if (orderItem.getPaymentFlag() == ProductConstants.PayType.PREPAY) {
            for (OrderItemAccount account : accounts) {
                int coreEpId = getCoreEp(coreEpIdMap, account.getEpId());
                // 每天的单价利润数据
                String data = account.getData();
                JSONArray daysData = JSONArray.parseArray(data);
                // 获取核销日期的单价利润
                JSONObject dayData = getAccountDataByDay(daysData, DateFormatUtils.parseDateToDatetimeString(day));
                BalanceChangeInfo changeInfo = new BalanceChangeInfo();
                changeInfo.setEpId(account.getEpId());
                changeInfo.setCoreEpId(account.getCoreEpId());
                int money = getMoney(consumeQuantity, account, coreEpId, dayData);
                if (money == 0) {
                    continue;
                }
                // 核销分账可提现金额
                changeInfo.setCanCash(account.getMoney() > 0 ? (consume ? money : -money) : (consume ? -money : money));
                balanceChangeInfoList.add(changeInfo);
                account.setSettledMoney(consume ? account.getSettledMoney() + money : account.getSettledMoney() - money); // 设置已结算金额
                orderItemAccountMapper.updateByPrimaryKey(account);
            }
        }
        // 调用分账
        Result<BalanceChangeRsp> result = changeBalances(PaymentConstant.BalanceChangeType.CONSUME_SPLIT, sn, balanceChangeInfoList);
        if (result.hasError()) {
            log.warn("核销OR反核销:{},分账失败:{}", consume, result.get());
            throw new ApiException(result.getError());
        }
    }
}
