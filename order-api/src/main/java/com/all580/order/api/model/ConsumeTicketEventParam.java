package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/9 11:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumeTicketEventParam implements Serializable {
    private static final long serialVersionUID = -7077891918041486927L;
    private int itemId;
    private int serialId;
}
