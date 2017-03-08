package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.RefundOrderMapper;
import com.all580.order.entity.RefundOrder;
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
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退票成功/退款成功还库存
 * @date 2016/10/10 20:41
 */
@Component(OrderConstant.Actions.REFUND_STOCK)
@Slf4j
public class RefundStockAction implements JobRunner {
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private RefundOrderManager refundOrderManager;

    public RefundStockAction() {
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        Integer refundId = CommonUtil.objectParseInteger(params.get("refundId"));
        Assert.notNull(refundId);
        RefundOrder refundOrder = refundOrderMapper.selectByPrimaryKey(refundId);
        Assert.notNull(refundOrder);

        refundOrderManager.refundStock(refundOrder);
        return new Result(Action.EXECUTE_SUCCESS, String.valueOf(refundOrder.getQuantity()));
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("还库存任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "refundId" // 退订订单ID
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        ParamsMapValidate.validate(params, rules);
    }
}
