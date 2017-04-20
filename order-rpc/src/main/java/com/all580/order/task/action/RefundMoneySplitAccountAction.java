package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.RefundAccountMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.Order;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.service.event.BasicSyncDataEvent;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退款后分账任务
 * @date 2016/10/21 15:11
 */
@Component(OrderConstant.Actions.REFUND_MONEY_SPLIT_ACCOUNT)
@Slf4j
public class RefundMoneySplitAccountAction extends BasicSyncDataEvent implements JobRunner {
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private RefundAccountMapper refundAccountMapper;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);
        String refundId = params.get("refundId");
        if (refundId == null) {
            log.warn("退款后分账任务,退订订单号为空");
            throw new Exception("退订订单号为空");
        }

        Integer id = CommonUtil.objectParseInteger(refundId);
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(id);
        refundOrderManager.refundSplitAccount(refundOrder);
        // 同步数据
        Order order = orderMapper.selectByRefundSn(refundOrder.getNumber());
        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_refund_account", refundAccountMapper.selectByRefundId(id));
        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("退款后分账任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "refundId"
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        ParamsMapValidate.validate(params, rules);
    }
}
