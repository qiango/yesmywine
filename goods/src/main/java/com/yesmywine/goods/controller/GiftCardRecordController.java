package com.yesmywine.goods.controller;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.bean.PageModel;
import com.yesmywine.goods.bean.DeleteEnum;
import com.yesmywine.goods.bean.SupplierTypeEnum;
import com.yesmywine.goods.dao.GiftCardDao;
import com.yesmywine.goods.entity.GiftCard;
import com.yesmywine.goods.entity.GiftCardRecord;
import com.yesmywine.goods.service.GiftCardRecordService;
import com.yesmywine.util.basic.MapUtil;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/12/22.
 */
@RestController
@RequestMapping("/goods/giftCardRecord")
public class GiftCardRecordController {

    @Autowired
    private GiftCardRecordService giftCardRecordService;
    @Autowired
    private GiftCardDao giftCardDao;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@RequestParam Map<String, String> param) {//新增礼品卡
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardRecordService.addGiftCard(param));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@RequestParam Map<String, String> param) {//修改保存礼品卡生成记录
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardRecordService.updateSave(param));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public String delete(Long id) {//删除存礼品卡生成记录
        try {
            return ValueUtil.toError(HttpStatus.SC_NO_CONTENT, giftCardRecordService.delete(id));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(@RequestParam Map<String, Object> params, Integer pageNo, Integer pageSize, Long id) throws Exception {
        MapUtil.cleanNull(params);
        if (id != null) {//查看礼品卡生成记录详情
            return ValueUtil.toJson(HttpStatus.SC_OK, giftCardRecordService.updateLoad(id));
        }
            params.put("deleteEnum", DeleteEnum.NOT_DELETE);
        if (params.get("supplierType") != null) {
            if (ValueUtil.notEmpity(params.get("supplierType"))) {
                String supplierType = params.get("supplierType").toString();
                params.remove(params.remove("supplierType").toString());
                switch (supplierType) {
                    case "distribution":
                        params.put("supplierType", SupplierTypeEnum.distribution);
                        break;
                    case "consignment":
                        params.put("supplierType", SupplierTypeEnum.consignment);
                        break;
                    case "seaAmoy":
                        params.put("supplierType", SupplierTypeEnum.seaAmoy);
                        break;
                }
            }
        }
        if (null != params.get("all") && params.get("all").toString().equals("true")) {
            return ValueUtil.toJson(giftCardRecordService.findAll());
        } else if (null != params.get("all")) {
            params.remove(params.remove("all").toString());
        }
        PageModel pageModel = new PageModel(pageNo == null ? 1 : pageNo, pageSize == null ? 10 : pageSize);
        if (null != params.get("showFields")) {
            pageModel.setFields(params.remove("showFields").toString());
        }
        if (pageNo != null) params.remove(params.remove("pageNo").toString());
        if (pageSize != null) params.remove(params.remove("pageSize").toString());
        pageModel.addCondition(params);
        pageModel = giftCardRecordService.findAll(pageModel);
        return ValueUtil.toJson(HttpStatus.SC_OK, pageModel);
    }

    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public String audit(Long id,String reason,Integer status) {//审核礼品卡生成记录
        try {
            return ValueUtil.toJson(HttpStatus.SC_CREATED, giftCardRecordService.audit(id,reason,status));
        } catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
    }

    @RequestMapping(value = "/exportExcel/itf", method = RequestMethod.GET)
    public String exportExcel(Long id, HttpServletResponse response) {//审核礼品卡生成记录
        try {
            GiftCardRecord giftCardRecord = giftCardRecordService.findOne(id);
            if(giftCardRecord.getStatus()==0||giftCardRecord.getType()==0){
                ValueUtil.isError("只有实体礼品卡审核通过才能导出!");
//                return "只有实体礼品卡审核通过才能导出";
            }
            List<GiftCard> cardList = giftCardDao.findByBatchNumber(giftCardRecord.getBatchNumber());
            String fileName = "CardNoList.xls";
            fileName = new String(fileName.getBytes("GBK"), "iso8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);// 指定下载的文件名
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            OutputStream output = response.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);

            JSONObject jo = new JSONObject();
            Workbook[] wbs = new Workbook[] { new XSSFWorkbook() };
            for (int i = 0; i < wbs.length; i++) {
                Workbook wb = wbs[i];
                CreationHelper createHelper = wb.getCreationHelper();
                // create a new sheet
                Sheet s = wb.createSheet();
                // declare a row object reference
                Row r = null;
                // declare a cell object reference
                Cell c = null;
                // create 2 cell styles
                CellStyle cs = wb.createCellStyle();
                CellStyle cs2 = wb.createCellStyle();
                DataFormat df = wb.createDataFormat();

                // create 2 fonts objects
                Font f = wb.createFont();
                Font f2 = wb.createFont();

                // Set font 1 to 12 point type, blue and bold
                f.setFontHeightInPoints((short) 12);
                f.setColor(IndexedColors.RED.getIndex());
                f.setBoldweight(Font.BOLDWEIGHT_BOLD);

                // Set font 2 to 10 point type, red and bold
                f2.setFontHeightInPoints((short) 10);
                f2.setColor(IndexedColors.RED.getIndex());
                f2.setBoldweight(Font.BOLDWEIGHT_BOLD);

                // Set cell style and formatting
                cs.setFont(f);
                cs.setDataFormat(df.getFormat("#,##0.0"));

                // Set the other cell style and formatting
                cs2.setBorderBottom(cs2.BORDER_THIN);
                cs2.setDataFormat(df.getFormat("text"));
                cs2.setFont(f2);

                Row r0 = s.createRow(0);

                Cell c0 = r0.createCell(0);
                c0.setCellValue(createHelper.createRichTextString("序号"));

                Cell c1 = r0.createCell(1);
                c1.setCellValue(createHelper.createRichTextString("卡号"));

                Cell c2 = r0.createCell(2);
                c2.setCellValue(createHelper.createRichTextString("密码"));

                // Define a few rows
                int j=1;
                for(GiftCard card:cardList){
                    String cardNum = card.getCardNumber().toString();
                    String password = card.getPassword();

                    Row row = s.createRow(j);

                    Cell cells1 = row.createCell(0);
                    // cells1.setCellStyle();
                    cells1.setCellValue(createHelper.createRichTextString(String.valueOf(j)));

                    Cell cells2 = row.createCell(1);
                    // cells2.setCellStyle(cellStyleTitle);
                    cells2.setCellValue(createHelper.createRichTextString(cardNum));

                    Cell cells3 = row.createCell(2);
                    // cells3.setCellStyle(cellStyleTitle);
                    cells3.setCellValue(createHelper.createRichTextString(password));
                    j++;
                }
                bufferedOutPut.flush();
                wb.write(bufferedOutPut);
                bufferedOutPut.close();
            }
            if(true){
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (yesmywineException e) {
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(), e.getMessage());
        }
        return null;
    }



}