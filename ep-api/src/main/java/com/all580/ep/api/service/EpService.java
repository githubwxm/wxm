package com.all580.ep.api.service;



import com.framework.common.Result;

import java.util.List;
import java.util.Map;

public interface EpService {



    /**
     * 创建平台商
     * @param ep
     * id,name 企业名称,en_name 英文名,ep_type 企业类型id,linkman 联系人,link_phone 联系人电话,address 街道信息,
     * code 企业组织机构代码,license 营业执照,logo_pic 图片地址,
     * status '100初始化101-正常\n102-已冻结\n103-已停用',access_id,access_key,creator_ep_id  上级企业id,
     * core_ep_id 平台商企业id,province,city,area,  省市区
     * group_id 组id,group_name 组名称,ep_class 景区酒店等,pic_address   省市区 合并地址
     * @return  id  access_id  access_key  link
     */
    Result<Map> createPlatform(Map ep);

    /**
     *查询平台商通道
     * @return   ｛list:{id:1 平台商id,name:String 平台商名称}｝
     */
    Result<Map> selectPlatform();
    /**
     * 创建企业
     * @param map   企业信息
     * @return  ep_info 企业信息map  capital  余额信息map
     */
    Result<Map> createEp(Map map);

    /**
     *
     * @param map ep.`id`=#{id}
    and ep.name like  CONCAT('%',#{name},'%') ep_type status
                  province city  省市
    link_phone  电话  core_ep_id  creator_ep_id 创建企业id
     * @return    list  企业信息listMap totalCount  总数
     */
    Result<Map> select (Map map);


    /**
     *  查询企业信息
     * @param map   id，
     * @return  返回一条企业信息  map 列 getEp 借口 field 对应
     */
    Result<Map> selectId (Map map);
    /**
     * 获取企业基本信息接口
     *
     * @param epids    企业id
     * @param field    企业列    所传的值必须在一下列里面
     * @return  map 的key  为 field    key对应在最下面 Map
     */
    Result<List<Map>>  getEp(Integer [] epids, String[] field);

    Result<List<Map>> all(Map params);

    /**
     * 验证平台商
     * @param params
     * @return
     */
    Result<Map> validate(Map params);
    /**
     * 获取企业状态（包括上级企业）
     *100-未初始化101-正常\n102-已冻结\n103-已停用
     * @param id
     * @return
     */
    Result<Integer> getEpStatus(Integer id);

    /**
     * 根据企业id查询平台商id
     * @param epId
     * @return {
     *     id,
     * }
     */
    Result<Integer>  selectPlatformId(Integer epId);



    /**
     * 冻结停用企业
     * @param params
     * @return
     */
    Result<Integer> freeze(Map params);
    Result<Integer> disable(Map params);
    Result<Integer> enable(Map params);

    Result<Integer> platformFreeze(Map params);
    Result<Integer> platformDisable(Map params);
    Result<Integer> platformEnable(Map params);
    //    Result<List<Ep>> selectEp(Map map);
    Result<Map> updateEp(Map map);

    /**
     * 下游平台商列表接口
     * @param map
     * @return
     */
    Result<Map> platformListDown(Map map);

    /**
     * 下游
     * @param map
     * @return
     */
    Result<Map> platformListUp(Map map);

    /**
     * 查询企业下的下级销售商
     * @param id  企业id
     * @return  id, name,
     */
    Result<List<Map>> selectSeller(Integer id);
    /**
     * 查找创建企业id
     * @param id
     * @return   未找到 -1  平台商0   上级企业id
     */
    Result<Integer> selectCreatorEpId(Integer id);

    /**
     * 检出啊名字是否存在
     * @param map
     * @return
     */
    Result<Boolean>  checkNamePhone(Map map);


    /**
     Map {id  企业id
     name  企业名称
     en_name  企业英文名
     ep_type   10000-畅旅平台商10001平台商10002供应商10003销售商10004自营商10005OTA
     linkman    联系人
     link_phone  联系电话
     address   地址
     code   企业组织机构代码
     license  营业执照
     logo_pic  企业logo
     status  100初始化101-正常\n102-已冻结\n103-已停用
     access_id   运营平台接口访问标识
     access_key  运营平台接口访问密钥
     creator_ep_id    上级企业
     core_ep_id   所属平台商企业id
     add_time
     status_bak    ' 冻结/停用平台商操作时企业当前的状态
     province  省
     city  市
     area  区
     group_id  组ID
     group_name  组名称
     ep_class   10010;//景区10011;//酒店10012;//旅行社10013;//其他
     pic_address   企业  省市区   湖南省长沙市岳麓区
     }
     * */

}
