package com.yesmywine.ware.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import com.yesmywine.ware.entity.Channels;
import com.yesmywine.ware.service.ChannelsService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by SJQ on 2007/1/5.
 *
 * @Description:渠道管理
 */
@RestController
@RequestMapping("/inventory/channels")
public class ChannelsController {
    @Autowired
    private ChannelsService channelsService;

    /*
    *@Author SJQ
    *@Description 渠道列表
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Integer id) {
        try {
            MapUtil.cleanNull(params);

            if (id != null) {
                Channels channels = channelsService.findOne(id);
                ValueUtil.verifyNotExist(channels, "无此渠道");
                return ValueUtil.toJson(channels);
            }

            if (null != params.get("all") && params.get("all").toString().equals("true")) {
                return ValueUtil.toJson(channelsService.findAll());
            } else if (null != params.get("all")) {
                params.remove(params.remove("all").toString());
            }

            if (null != params.get("parentChannelId") && !params.get("parentChannelId").toString().equals("") && !params.get("parentChannelId").toString().equals("null")) {
                Channels parentChannel = new Channels();
                parentChannel.setId(Integer.valueOf(params.get("parentChannelId").toString()));
                params.remove(params.remove("parentChannelId").toString());
                params.put("parentChannel", parentChannel);
            } else if (null != params.get("parentChannelId") && params.get("parentChannelId").toString().equals("null")) {
                return ValueUtil.toJson(channelsService.findByParentChannelIsNull());
            }
            PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
            if (null != params.get("showFields")) {
                pageModel.setFields(params.remove("showFields").toString());
            }
            if (pageNo != null) params.remove(params.remove("pageNo").toString());
            if (pageSize != null) params.remove(params.remove("pageSize").toString());
            pageModel.addCondition(params);
            pageModel = channelsService.findAll(pageModel);
            return ValueUtil.toJson(pageModel);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author SJQ
    *@Description 创建渠道
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.POST)
    public String create(Channels channels, @RequestParam Map<String, String> params) {
        try {
            Channels newChannels = channelsService.create(channels, params);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, newChannels);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }


    /*
    *@Author Gavin
    *@Description 向OMS与商城同步新增渠道
    *@Date 2007/3/14 16:27
    *@Email gavinsjq@sina.com
    *@Params
    */
    private String synchronizationAdd(Channels channels) {
        return null;
    }

    /*
    *@Author SJQ
    *@Description 修改渠道
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.PUT)
    public String update(Channels channels, @RequestParam Map<String, String> params, Integer id) {
        try {
            Channels newChannels = channelsService.update(channels, params, id);
            return ValueUtil.toJson(HttpStatus.SC_CREATED, channels);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }


    /*
    *@Author SJQ
    *@Description 渠道查看
    *@CreateTime
    *@Params
    */
    @RequestMapping(value = "/itf", method = RequestMethod.GET)
    public String show(Integer id) {
        try {
            ValueUtil.verify(id);
            Channels channels = channelsService.findOne(id);
//            ValueUtil.verifyNotExist(channels);
            return ValueUtil.toJson(channels);
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author SJQ
    *@Description 渠道删除
    *@CreateTime
    *@Params
    */
    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(Integer id) {
        try {
            String result = channelsService.deleteChannel(id);
            return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT, "channels");
        } catch (yesmywineException e) {
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    /*
    *@Author SJQ
    *@Description 渠道查重
    *@CreateTime
    *@Params
    */
    @RequestMapping(value = "/checkNameRepeat", method = RequestMethod.GET)
    public String checkNameRepeat(String channelName) {
        Boolean isExist = channelsService.checkNameRepeat(channelName);
        return ValueUtil.toJson(isExist);
    }
}
