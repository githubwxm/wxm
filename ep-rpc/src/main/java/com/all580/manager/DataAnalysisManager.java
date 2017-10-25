package com.all580.manager;

import com.all580.ep.dao.EpMapper;
import com.all580.report.api.service.AnalysisService;
import com.framework.common.mns.OssStoreManager;
import com.framework.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2017/10/24 0024.
 */
@Component
public class DataAnalysisManager {
    @Autowired
    AnalysisService analysisService;
    @Autowired
    EpMapper epMapper;
    @Autowired
    OssStoreManager analysisStoreManager;
    public void channelForAnalysis(String operation,String name,Integer ep_id){
        List<Map> list =  analysisService.selectSpotEpList().get();
        //Integer core_ep_id= epMapper.selectPlatformId(ep_id);
        for(Map map : list){
          int spot =   CommonUtil.objectParseInteger(map.get("spot_id"));
            analysisStoreManager.dimensionForAnalysis(spot, "CHANNEL", operation, ep_id, name);
            //Integer spot_ep_id = CommonUtil.objectParseInteger(map.get("ep_id"));

//           if(core_ep_id.equals(epMapper.selectPlatformId(spot_ep_id))){
//               analysisStoreEpManager.dimensionForAnalysis(spot, "CHANNEL", operation, ep_id, name);
//           }else{
//               analysisStoreEpManager.dimensionForAnalysis(spot, "CHANNEL", operation, core_ep_id,
//                       CommonUtil.objectParseString(epMapper.selectId(core_ep_id).get("name")) );
//           }     // 跨平台传 平台名称 平台id

       }

    }

}
