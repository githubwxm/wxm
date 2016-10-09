package com.all580.order.manager;

import com.all580.order.api.OrderConstant;
import com.all580.order.dto.GenerateAccountDto;
import com.all580.order.entity.OrderItemAccount;
import com.all580.product.api.model.EpSalesInfo;
import com.framework.common.Result;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2016/10/8 19:07
 */
@Component
public class BaseOrderManager {

    /**
     * 生成分账（平账）
     * @param dto
     * @return
     */
    public List<OrderItemAccount> generateAccount(GenerateAccountDto dto) {
        List<OrderItemAccount> accounts = new ArrayList<>();
        OrderItemAccount subtractAccount = new OrderItemAccount();
        subtractAccount.setEpId(dto.getSubtractEpId());
        subtractAccount.setCoreEpId(dto.getSubtractCoreId());
        subtractAccount.setOrderItemId(dto.getOrderItemId());
        subtractAccount.setMoney(-dto.getMoney());
        subtractAccount.setProfit(dto.getSubtractProfit());
        subtractAccount.setSettledMoney(0);
        subtractAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
        accounts.add(subtractAccount);

        OrderItemAccount addAccount = new OrderItemAccount();
        addAccount.setEpId(dto.getAddEpId());
        addAccount.setCoreEpId(dto.getAddCoreId());
        addAccount.setOrderItemId(dto.getOrderItemId());
        addAccount.setMoney(dto.getMoney());
        addAccount.setProfit(dto.getAddProfit());
        addAccount.setSettledMoney(0);
        addAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
        accounts.add(addAccount);

        return accounts;
    }

    /**
     * 获取利润
     * @param salesInfoList 销售链
     * @param epId 企业
     * @return
     */
    public int getProfit(List<EpSalesInfo> salesInfoList, int epId) {
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
        return salePrice - buyPrice;
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
}
