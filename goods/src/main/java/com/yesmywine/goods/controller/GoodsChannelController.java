package com.yesmywine.goods.controller;

import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.goods.dao.ChannelDao;
import com.yesmywine.goods.entity.GoodsChannel;
import com.yesmywine.goods.service.GoodsChannelService;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by ${shuang} on 2017/3/16.
 */


@RestController
@RequestMapping("/goods/goodschannel")
public class GoodsChannelController {

    @Autowired
    GoodsChannelService goodsChannelService;
    @Autowired
    private ChannelDao channelDao;

    //查找
    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize,Integer id){
        MapUtil.cleanNull(params);
        if(id!=null){
            GoodsChannel one = goodsChannelService.findOne(id);
            one.setChannelName(this.channelDao.findOne(Integer.valueOf(one.getChannelId().toString())).getChannelName());
            Integer inventory = goodsChannelService.inventoryGoodsSku(one.getGoodsSku(), one.getChannelId(), one.getItem());
            one.setInventory(inventory);
            return ValueUtil.toJson(one);
        }

        if(null!=params.get("all")&&params.get("all").toString().equals("true")){
            List<GoodsChannel> all = goodsChannelService.findAll();
            for(int i=0; i< all.size(); i++){
                all.get(i).setChannelName(this.channelDao.findOne(Integer.valueOf(all.get(i).getChannelId().toString())).getChannelName());
                Integer inventory = goodsChannelService.inventoryGoodsSku(all.get(i).getGoodsSku(), all.get(i).getChannelId(), all.get(i).getItem());
                all.get(i).setInventory(inventory);
            }
            return ValueUtil.toJson(all);
        }else  if(null!=params.get("all")){
            params.remove(params.remove("all").toString());
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        if(ValueUtil.isEmpity(params.get("goodsName_l"))){
            params.remove("goodsName_l");
        }
        if(ValueUtil.isEmpity(params.get("channelName_l"))){
            params.remove("channelName_l");
        }
        pageModel.addCondition(params);
        pageModel = goodsChannelService.findAll(pageModel);

        List<GoodsChannel> conditions = pageModel.getContent();
        for(int i=0; i< pageModel.getContent().size(); i ++){
            conditions.get(i).setChannelName(this.channelDao.findOne(Integer.valueOf(conditions.get(i).getChannelId().toString())).getChannelName());
            Integer inventory = goodsChannelService.inventoryGoodsSku(conditions.get(i).getGoodsSku(), conditions.get(i).getChannelId(), conditions.get(i).getItem());
            conditions.get(i).setInventory(inventory);
        }
        pageModel.setContent(conditions);

        return ValueUtil.toJson(pageModel);
    }


    //插入
    @RequestMapping(method = RequestMethod.POST)
    public String create(Integer goodsId, String params, Integer operate){
        try {
            ValueUtil.verify(goodsId, "goodsId");
            ValueUtil.verify(params, "渠道");
            String[] split = params.split(",");
            List failedList =goodsChannelService.setGoodsChannel(goodsId, split, operate);
           if( failedList.size()==0){
               return ValueUtil.toJson(HttpStatus.SC_CREATED,"success");
           }else {
               ValueUtil.isError("该渠道已下发");
           }
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
            return null;
    }

    @RequestMapping(value = "/updateThirdCode",method = RequestMethod.PUT)
    public String updateThirdCode(Integer id,String thirdCode){
        try {
            String s = goodsChannelService.updateThirdCode(id, thirdCode);
            if("success".equals(s)){
                return ValueUtil.toJson(HttpStatus.SC_CREATED, s);
            }
            return ValueUtil.toError("500", s);
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toJson(e.getCode(),e.getMessage());
        }

    }


    //转换
    @RequestMapping(method = RequestMethod.PUT)
    public String exchange(Integer goodsId,Integer channelId){
        try {
        String  result=  goodsChannelService.exchange(goodsId,channelId);
            if("success".equals(result)){
                return ValueUtil.toJson(HttpStatus.SC_CREATED,"success");
            }else {
                return ValueUtil.toError("500",result);
            }
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toJson(e.getCode(),e.getMessage());
        }

    }
}
