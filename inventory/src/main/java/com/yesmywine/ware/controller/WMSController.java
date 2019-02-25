package com.yesmywine.ware.controller;

import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.service.ChannelsInventoryService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by SJQ on 2017/4/5.
 */
@RestController
@RequestMapping("/inventory/wms")
public class WMSController {
    @Autowired
    private ChannelsInventoryService channelsInventoryService;

    /*
    *@Author Gavin
    *@Description wms出库凭证,调拨仓库减冻结库存，目标仓库加在途库存
    *@Date 2017/3/31 16:58
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/outOrder", method = RequestMethod.POST)
    public String wmsOutOrder(String jsonData) {
        try {
            String result = channelsInventoryService.wmsOutOrder(jsonData);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }


    /*
    *@Author Gavin
    *@Description wms入库凭证
    *@Date 2017/3/31 16:58
    *@Email gavinsjq@sina.com
    *@Params
    */
    @RequestMapping(value = "/inOrder", method = RequestMethod.POST)
    public String wmsInOrder(String jsonData) {
        try {
            String result = channelsInventoryService.wmsInOrder(jsonData);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, result);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }
}
