package com.all580.voucherplatform.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by Linv2 on 2017/5/12.
 */
public interface TicketSysService {

    /**
     * 添加一个票务对接
     * @param map {name:xx,version:xx,implPacket:xx}
     *              name - String - 名字
     *              version - String - 版本号
     *              implPacket - String - 实现的包名
     * @return
     */
    Result create(Map map);


    /**
     * 修改一个票务对接
     * @param map {id:xx,name:xx,version:xx,implPacket:xx}
     *              id - int - id
     *              name - String - 名字
     *              version - String - 版本号
     *              implPacket - String - 实现的包名
     * @return
     */
    Result update(Map map);

    /**
     * 删除一条记录
     * @param id id编号
     * @return
     */
    Result delete(int id);

    Result list(int pageSize, int pageIndex);
}
