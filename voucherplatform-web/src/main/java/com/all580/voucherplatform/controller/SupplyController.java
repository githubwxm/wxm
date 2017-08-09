package com.all580.voucherplatform.controller;

import com.all580.voucherplatform.api.service.SupplyService;
import com.all580.voucherplatform.manager.SupplyValidateManager;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.vo.PageRecord;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Linv2 on 2017-06-26.
 */
@Controller
@RequestMapping(value = "api/supply")
public class SupplyController {
    @Autowired
    private SupplyService supplyService;
    @Autowired
    private SupplyValidateManager supplyValidateManager;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Result create(@RequestBody Map map) {
        ParamsMapValidate.validate(map, supplyValidateManager.createValidate());
        return supplyService.create(map);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Map map) {
        ParamsMapValidate.validate(map, supplyValidateManager.updateValidate());
        return supplyService.update(map);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public Result get(Integer id) {
        return supplyService.getSupply(id);
    }

    @RequestMapping(value = "updateConf", method = RequestMethod.POST)
    @ResponseBody
    public Result updateConf(@RequestBody Map map) {

        ParamsMapValidate.validate(map, supplyValidateManager.updateConfValidate());
        return supplyService.updateConf(map);
    }

    @RequestMapping(value = "updateTicketSys", method = RequestMethod.POST)
    @ResponseBody
    public Result updateTicketSys(@RequestBody Map map) {

        ParamsMapValidate.validate(map, supplyValidateManager.updateTicketSysValidate());
        return supplyService.updateTicketSys(map);
    }

    @RequestMapping(value = "updateSignType", method = RequestMethod.POST)
    @ResponseBody
    public Result updateSignType(@RequestBody Map map) {
        ParamsMapValidate.validate(map, supplyValidateManager.updateSignTypeValidate());
        return supplyService.updateSignType(map);

    }

    @RequestMapping(value = "selectSupply", method = RequestMethod.GET)
    @ResponseBody
    public Result selectSupply(@RequestParam(value = "supplyId") Integer supplyId) {
        return supplyService.selectSupply(supplyId);
    }

    @RequestMapping(value = "selectSupplyList", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> selectSupplyList(String name,
                                      @RequestParam(value = "record_start", defaultValue = "0") Integer recordStart,
                                      @RequestParam(value = "record_count", defaultValue = "15") Integer recordCount) {
        Result<PageRecord<Map>> result = supplyService.selectSupplyList(name, recordStart, recordCount);
        return result;
    }


    @RequestMapping(value = "selectSupplyProdList", method = RequestMethod.GET)
    @ResponseBody
    public Result<PageRecord<Map>> selectSupplyProdList(Integer supplyId,
                                                        String prodCode,
                                                        @RequestParam(value = "record_Start", defaultValue = "0") Integer recordStart,
                                                        @RequestParam(value = "record_Count", defaultValue = "15") Integer recordCount) {
        return supplyService.selectSupplyProdList(supplyId, prodCode, recordStart, recordCount);
    }


    @RequestMapping(value = "getProd", method = RequestMethod.GET)
    @ResponseBody
    public Result getProd(@RequestParam(value = "supplyId") Integer supplyId,
                          @RequestParam(value = "prodId") String prodId) {
        Map map = supplyService.getProd(supplyId, prodId);
        Result result = new Result(true);
        result.put(map);
        return result;
    }

    @RequestMapping(value = "setProd", method = RequestMethod.POST)
    @ResponseBody
    public Result setProd(@RequestBody Map map) {
        ParamsMapValidate.validate(map, supplyValidateManager.setProdValidate());
        Integer supplyId = CommonUtil.objectParseInteger(map.get("supplyId"));
        if (!supplyService.checkProdPower(supplyId)) {
            return new Result(false, "该模式下不能手动维护产品");
        }
        return supplyService.setProd(supplyId, map);
    }

    @RequestMapping(value = "syncProd", method = RequestMethod.GET)
    @ResponseBody
    public Result syncProd(@RequestParam(value = "supplyId") int supplyId) {
        return supplyService.syncProd(supplyId);
    }

    @RequestMapping(value = "downConf", method = RequestMethod.GET)
    @ResponseBody
    public void downConf(@RequestParam(value = "supplyId") int supplyId,
                         HttpServletResponse response) throws IOException {
        Map map = supplyService.getConfFile(supplyId);
        response.setContentType("APPLICATION/OCTET-STREAM");
        Result result = supplyService.selectSupply(supplyId);
        Map supplyMap = (Map) result.get();
        String fileName = String.format("%s.zip", supplyMap.get("name"));
        /* 根据request的locale 得出可能的编码，中文操作系统通常是gb2312 */
        fileName = new String(fileName.getBytes("GB2312"), "ISO_8859_1");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
        for (Object key : map.keySet()) {
            String name = String.valueOf(key);
            String content = String.valueOf(map.get(name));
            ZipEntry zipEntry = new ZipEntry(name);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(content.getBytes());
            zipOutputStream.flush();
        }
        response.flushBuffer();
        zipOutputStream.close();
    }

    @RequestMapping(value = "getSignType", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getSignType() {
        Result result = new Result(true);
        result.put(supplyService.getSignType());
        return result;
    }
}
