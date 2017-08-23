package com.all580.base.manager;

import com.all580.product.api.consts.ProductConstants;
import com.framework.common.validate.ValidRule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description:
 * @date 17-7-6 下午3:21
 */
@Component
public class PackageValidateManager {
    /**
     * 创建主产品验证
     * @return
     */
    public Map<String[], ValidRule[]> addValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name",
                "phone",
                "province",
                "city",
                "area",
                "pcastr",
                "address",
                "type",
                "blurb",
                "imgs"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "province",
                "city",
                "area",
                "type"
        }, new ValidRule[]{new ValidRule.Digits()});

        rules.put(new String[]{
                "type"
        }, new ValidRule[]{new ValidRule.Digits(new Long[]{
                (long) ProductConstants.PackageType.SCENERY,
                (long) ProductConstants.PackageType.SCENERY_HOTEL
        })});

        rules.put(new String[]{
                "phone"
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        return rules;
    }

    public Map<String[], ValidRule[]> updateValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "phone",
                "province",
                "city",
                "area",
                "pcastr",
                "address",
                "blurb",
                "imgs"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id",
                "province",
                "city",
                "area",
                "type"
        }, new ValidRule[]{new ValidRule.Digits()});

        rules.put(new String[]{
                "type"
        }, new ValidRule[]{new ValidRule.Digits(new Long[]{
                (long) ProductConstants.PackageType.SCENERY,
                (long) ProductConstants.PackageType.SCENERY_HOTEL
        })});

        rules.put(new String[]{
                "phone"
        }, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});

        return rules;
    }

    public Map<String[], ValidRule[]> deleteValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id"
        }, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }

    public Map<String[], ValidRule[]> addSubValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "name",
                "market_price",
                "min_sell_price",
                "max_buy_quantity",
                "product_id",
                "refund",
                "items",
                "items.quantity",
                "items.sub_id",
                "items.days"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "product_id",
                "market_price",
                "min_sell_price",
                "max_buy_quantity",
                "items.quantity",
                "items.sub_id",
                "items.seq",
                "items.days"
        }, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }

    public Map<String[], ValidRule[]> updateSubValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "market_price",
                "min_sell_price",
                "max_buy_quantity",
                "refund",
                "items",
                "items.quantity",
                "items.sub_id",
                "items.days"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "product_id",
                "id",
                "market_price",
                "min_sell_price",
                "max_buy_quantity",
                "items.quantity",
                "items.sub_id",
                "items.seq",
                "items.days"
        }, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }

    public Map<String[], ValidRule[]> updateShopPriceValidate() {
        Map<String[], ValidRule[]> rules = new HashMap<>();
        // 校验不为空的参数
        rules.put(new String[]{
                "id",
                "price"
        }, new ValidRule[]{new ValidRule.NotNull()});

        // 校验整数
        rules.put(new String[]{
                "id",
                "price"
        }, new ValidRule[]{new ValidRule.Digits()});

        return rules;
    }
}
