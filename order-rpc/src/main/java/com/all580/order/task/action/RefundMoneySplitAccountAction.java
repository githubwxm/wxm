package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderClearanceSerialMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.RefundAccountMapper;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.OrderClearanceSerial;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.RefundAccount;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.RefundOrderManager;
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
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退款后分账任务
 * @date 2016/10/21 15:11
 */
@Component(OrderConstant.Actions.REFUND_MONEY_SPLIT_ACCOUNT)
@Slf4j
public class RefundMoneySplitAccountAction implements JobRunner {
    @Autowired
    private RefundOrderManager refundOrderManager;

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
        refundOrderManager.refundSplitAccount(id);
        // 同步数据
        refundOrderManager.syncRefundAccountData(id);
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
