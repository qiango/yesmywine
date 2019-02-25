package com.yesmywine.user.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.user.dao.ChannelsDao;
import com.yesmywine.user.dao.MonIntegraDao;
import com.yesmywine.user.entity.Channels;
import com.yesmywine.user.entity.MonIntegra;
import com.yesmywine.user.service.MonIntegraService;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by hz on 3/27/17.
 */
@RestController
@RequestMapping("/user/monIntegral")
public class MonIntegraController {
    @Autowired
    private MonIntegraService monIntegraService;
    @Autowired
    private ChannelsDao channelsDao;
    @Autowired
    private MonIntegraDao monIntegraDao;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String,String> map){
        try {
            ValueUtil.verify(map.get("proportion"));
            ValueUtil.verify(map.get("channelCode"));
            return ValueUtil.toJson(HttpStatus.SC_CREATED,monIntegraService.create(map));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)  //删除
    public String deleteMonItegra(String channelCode) {
        Channels channels=channelsDao.findByChannelCode(channelCode);
        monIntegraDao.delete(monIntegraDao.findByChannels(channels));
        return ValueUtil.toJson(HttpStatus.SC_NO_CONTENT,"success");
    }



    @RequestMapping(method = RequestMethod.PUT)
    public String updateSave(Integer id,String proportion){
        try {
            ValueUtil.verify(proportion);
            return ValueUtil.toJson(HttpStatus.SC_CREATED,monIntegraService.updateSave(id, proportion));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("user",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

   /* @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize,Integer monIntegraId) {   //查看
        if(monIntegraId!=null){
            MonIntegra monIntegra = monIntegraService.findOne(monIntegraId);
            monIntegra.setPro(monIntegra.getProportion().split(":")[1]);
            return ValueUtil.toJson(monIntegra);
        }
        MapUtil.cleanNull(params);

        if (params.get("channelId") != null ) {
            Integer channelId = Integer.valueOf(params.get("channelId").toString());
            Channels channels = new Channels();
            channels.setId(channelId);
            params.remove(params.remove("channelId").toString());
            params.put("channel", channels);
        }else if(params.get("channelId_ne") != null ){
            Integer channelId = Integer.valueOf(params.get("channelId_ne").toString());
            Channels channels = new Channels();
            channels.setId(channelId);
            params.remove(params.remove("channelId_ne").toString());
            params.put("channel_ne", channels);
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        return ValueUtil.toJson(monIntegraService.findAll(pageModel));
    }*/


    @RequestMapping(method = RequestMethod.GET)
    public Object index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize,Integer monIntegraId){
        MapUtil.cleanNull(params);
        if(monIntegraId!=null){
            MonIntegra monIntegra = monIntegraService.findOne(monIntegraId);
            monIntegra.setPro(monIntegra.getProportion().split(":")[1]);
            return ValueUtil.toJson(monIntegra);
        }
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        Channels channels = new Channels();
        if (params.get("channelId") != null ) {
//            if(ValueUtil.isEmpity(pageSize)){
//                pageSize=10;
//            }if(ValueUtil.isEmpity(pageNo)){
//                pageNo=0;
//            }
            Pageable pageable = new PageRequest(0, 10, sort);
            Integer channelId = Integer.valueOf(params.get("channelId").toString());
            channels.setId(channelId);
            Page<MonIntegra> pages=monIntegraDao.findByChannels(channels,pageable);
            pageModel.setContent(pages.getContent());
            pageModel.setTotalPages(pages.getTotalPages());
            pageModel.setTotalRows(pages.getTotalElements());
            pageModel.setPage(pages.getNumber()+1);
            return ValueUtil.toJson(pageModel);
//            return pages;
        }else {
            if (null != params.get("showFields")) {
                pageModel.setFields(params.remove("showFields").toString());
            }
            if (pageNo != null) params.remove(params.remove("pageNo").toString());
            if (pageSize != null) params.remove(params.remove("pageSize").toString());
            pageModel.addCondition(params);
            return ValueUtil.toJson(monIntegraService.findAll(pageModel));
        }


    }

}
