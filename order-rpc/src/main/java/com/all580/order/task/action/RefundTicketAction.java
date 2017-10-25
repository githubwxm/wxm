package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.service.event.BasicSyncDataEvent;
import com.all580.voucher.api.model.RefundTicketParams;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.lang.JsonUtils;
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

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 退票任务
 * @date 2016/10/25 11:30
 */
@Component(OrderConstant.Actions.REFUND_TICKET)
@Slf4j
public class RefundTicketAction extends BasicSyncDataEvent implements JobRunner {
    @Autowired
    private VoucherRPCService voucherRPCService;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);
        RefundTicketParams ticketParams = JsonUtils.map2obj(params, RefundTicketParams.class);
        int epMaId = Integer.parseInt(params.get("ep_ma_id"));
        com.framework.common.Result result = voucherRPCService.refundTicket(epMaId, ticketParams);
        if (!result.isSuccess()) {
            throw new ApiException(result.getError());
        }
        return new Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("出票任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "orderSn",
                "quantity",
                "visitorId",
                "refundSn",
                "voucher",
                "ep_ma_id"
        }, new ValidRule[]{new ValidRule.NotNull()});
        rules.put(new String[]{
                "orderSn",
                "quantity",
                "visitorId",
                "ep_ma_id"
        }, new ValidRule[]{new ValidRule.Digits()});

        ParamsMapValidate.validate(params, rules);
    }
}
