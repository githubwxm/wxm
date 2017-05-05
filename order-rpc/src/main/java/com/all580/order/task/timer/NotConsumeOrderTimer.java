package com.all580.order.task.timer;

import com.all580.order.api.model.ConsumeTicketInfo;
import com.all580.order.api.service.TicketCallbackService;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.entity.OrderItem;
import com.all580.voucher.api.service.VoucherRPCService;
import com.all580.voucher.api.service.third.ThirdProductService;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 未消费的订单定时器
 * @date 2016/10/13 16:26
 */
@Component
@Slf4j
public class NotConsumeOrderTimer {
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private TicketCallbackService ticketCallbackService;

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Autowired
    private VoucherRPCService voucherRPCService;
    @Autowired
    private ThirdProductService thirdProductService;
    @Value("${hld.ma.conf.id}")
    private Integer hldMaConfId;

    public static final String HLD_NOT_CONSUME_LOCK = "HLD_NOT_CONSUME_LOCK_TIMER";
    private AtomicBoolean hldRunning = new AtomicBoolean(false);

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void notConsumeByHld() {
        try {
            if (hldRunning.compareAndSet(false, true)) {
                DistributedReentrantLock lock = distributedLockTemplate.execute(HLD_NOT_CONSUME_LOCK, 60);
                try {
                    Result result = voucherRPCService.selectMaByVoucherPlatform(hldMaConfId);
                    List list = (List) result.get();
                    if (list == null || list.isEmpty()) {
                        return;
                    }
                    List<Integer> epMaIds = new ArrayList<>();
                    for (Object o : list) {
                        Map map = (Map) o;
                        epMaIds.add(Integer.parseInt(map.get("id").toString()));
                    }
                    notConsumeByHld(epMaIds, 0);
                } catch (Exception e) {
                    log.error("未核销订单检查异常", e);
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            log.error("未核销订单检查异常", e);
        } finally {
            hldRunning.set(false);
        }
    }

    private void notConsumeByHld(List<Integer> epMaIds, Integer current) {
        // 每次查询10条 做完再递归
        List<OrderItem> items = orderItemMapper.selectByNotConsumeAndEpMaId(epMaIds, current);
        if (items == null || items.isEmpty()) {
            return;
        }
        current = items.get(items.size() - 1).getId();
        for (OrderItem item : items) {
            Result statusResult = thirdProductService.checkOrderStatus(item.getEp_ma_id(), item.getNumber());
            String status = CommonUtil.objectParseString(statusResult.get());
            if ("T".equalsIgnoreCase(status)) {
                ConsumeTicketInfo consumeTicketInfo = new ConsumeTicketInfo();
                consumeTicketInfo.setConsumeQuantity(item.getQuantity() - item.getRefund_quantity() - item.getUsed_quantity());
                consumeTicketInfo.setTicketId(String.valueOf(item.getId()));
                consumeTicketInfo.setValidateSn(String.valueOf(item.getNumber()));
                consumeTicketInfo.setVoucherNumber(consumeTicketInfo.getValidateSn());
                ticketCallbackService.consumeTicket(item.getNumber(), consumeTicketInfo, new Date());
            }
        }
        notConsumeByHld(epMaIds, current);
    }
}
