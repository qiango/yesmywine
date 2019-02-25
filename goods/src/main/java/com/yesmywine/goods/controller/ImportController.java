
package com.yesmywine.goods.controller;

import com.yesmywine.goods.service.ImportService;
import com.yesmywine.goods.util.ExcelImportService;
import com.yesmywine.util.basic.Threads;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.error.yesmywineException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by hz on 2016/12/9.
 */
@RestController
@RequestMapping("/goods/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    // 允许上传的格式
    private static final String[] IMAGE_TYPE = new String[] { ".xlsx", ".xls"};
    @RequestMapping(method = RequestMethod.POST)
    public String imports(@RequestParam("uploadFiles")MultipartFile uploadFiles) {
        String origName = uploadFiles.getOriginalFilename();
        String name=StringUtils.substringBefore(origName,".");
        String result=null;
        try {
            switch (name) {
                case "prop":
                    result = uploadProp(uploadFiles);
                    break;
                case "sku":
                    result = uploadSku(uploadFiles);
                    break;
                case "goods":
                    result = uploadGoods(uploadFiles);
                    break;
                case "category":
                    result = uploadCategory(uploadFiles);
                    break;
                case "supplier":
                    result = uploadSupplier(uploadFiles);
                    break;
                default:ValueUtil.isError("文件名或格式不对");
            }
            return result;
        }catch (yesmywineException e){
            Threads.createExceptionFile("goods",e.getMessage());
            return ValueUtil.toError(e.getCode(),e.getMessage());
        }
    }

//    @RequestMapping(value = "/prop", method = RequestMethod.POST)
    public  String uploadProp(@RequestParam("uploadFiles")MultipartFile uploadFiles)
             {

        String origName = uploadFiles.getOriginalFilename();


        // 校验格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(origName, type)) {
                isLegal = true;
                break;
            }
        }
        if(!isLegal){
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "文件格式不正确");
        }

                 InputStream inputStream = null;
                 try {
                     inputStream = uploadFiles.getInputStream();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 ExcelImportService excelImportService = new ExcelImportService();
        String keyName = "cnName,code,isSku,canSearch,entryMode,propValue";
        String keyType = "string,string,string,string,string,String";
        String nullable = "false,false,false,false,true,true";

        List<Map<String, Object>> list = excelImportService.importEx(keyName, keyType,nullable, (FileInputStream) inputStream, origName);
        List<Map<String, Object>> ReList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(ValueUtil.notEmpity(list.get(i).get("erro"))){
                Map<String, Object> map = new HashMap<>();
                map = list.get(i);
                ReList.add(map);
            }
        }
        if(ReList.size()!=0){
            return ValueUtil.toJson(500, ReList);
        }
        List<Map<String, Object>> listRe = importService.importPropAndPropValue(list);
        if(listRe.size()==0){
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "success");
        }else {
            for(Map<String, Object> map: listRe){
                ReList.add(map);
            }
            return ValueUtil.toError("500","erro", ReList);
        }
    }




//    @RequestMapping(value = "/supplier", method = RequestMethod.POST)
    public String uploadSupplier(@RequestParam("uploadFiles")MultipartFile uploadFiles)
             {
                 try{
        String origName = uploadFiles.getOriginalFilename();


        // 校验格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(origName, type)) {
                isLegal = true;
                break;
            }
        }
        if(!isLegal){
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "文件格式不正确");
        }

                 InputStream inputStream = null;
                 try {
                     inputStream = uploadFiles.getInputStream();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 ExcelImportService excelImportService = new ExcelImportService();
        String keyName = "supplierName,supplierType,province,provinceId,city,cityId,area,areaId,address,postCode" +
                ",contact,telephone,mobilePhone,fax,mailbox,grade,accountNumber,credit,procurementCycl,paymentType" +
                ",invoiceCompany,primarySupplier,merchantIdentification,productManager" +
                ",bank,bankAccount,dutyParagraph,paymentDays,supplierCode";
        String keyType = "string,string,string,string,string,string,string,string,string,string,string,string,string" +
                ",string,string,string,string,string,string,string,string,string,string,string,string,string,string,string,String";
        String nullable = "false,false,false,true,false,true,false,true,false,false,false,false,false,true" +
                ",false,true,true,true,false,false,false,false,false,false,true,true,true,false,false";

        List<Map<String, Object>> list = excelImportService.importEx(keyName, keyType,nullable, (FileInputStream) inputStream, origName);
        List<Map<String, Object>> ReList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(ValueUtil.notEmpity(list.get(i).get("erro"))){
                Map<String, Object> map = new HashMap<>();
                map = list.get(i);
                ReList.add(map);
            }
        }
        if(ReList.size()!=0){
            return ValueUtil.toJson(500, ReList);
        }
        List<Map<String, Object>> listRe = this.importService.importSupplier(list);
        if(listRe.size()==0){
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "success");
        }else {
            for(Map<String, Object> map: listRe){
                ReList.add(map);
            }
            return ValueUtil.toError("500","erro", ReList);
        }
             }catch (yesmywineException e){
                     Threads.createExceptionFile("goods",e.getMessage());
        return ValueUtil.toError(e.getCode(),e.getMessage());
    }
    }



//    @RequestMapping(value = "/sku", method = RequestMethod.POST)
    public String uploadSku(@RequestParam("uploadFiles")MultipartFile uploadFiles) {
        try{
        String origName = uploadFiles.getOriginalFilename();


        // 校验格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(origName, type)) {
                isLegal = true;
                break;
            }
        }
        if(!isLegal){
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "文件格式不正确");
        }

                 InputStream inputStream = null;
                 try {
                     inputStream = uploadFiles.getInputStream();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 ExcelImportService excelImportService = new ExcelImportService();
        String keyName = "categoryName,supplierName,skuName,type,propJson";
        String keyType = "string,string,string,String,String";
        String nullable = "false,false,false,false,false";

        List<Map<String, Object>> list = excelImportService.importEx(keyName, keyType,nullable, (FileInputStream) inputStream, origName);
        List<Map<String, Object>> ReList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(ValueUtil.notEmpity(list.get(i).get("erro"))){
                Map<String, Object> map = new HashMap<>();
                map = list.get(i);
                ReList.add(map);
            }
        }
        if(ReList.size()!=0){
            return ValueUtil.toJson(500, ReList);
        }
        List<Map<String, Object>> listRe = this.importService.importSku(list);
        if(listRe.size()==0){
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "success");
        }else {
            for(Map<String, Object> map: listRe){
                ReList.add(map);
            }
            return ValueUtil.toError("500","erro", ReList);
        }
             }catch (yesmywineException e){
            Threads.createExceptionFile("goods",e.getMessage());
        return ValueUtil.toError(e.getCode(),e.getMessage());
    }
    }



//    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public String uploadGoods(@RequestParam("uploadFiles")MultipartFile uploadFiles)
             {

        String origName = uploadFiles.getOriginalFilename();


        // 校验格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(origName, type)) {
                isLegal = true;
                break;
            }
        }
        if(!isLegal){
            return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "文件格式不正确");
        }

                 InputStream inputStream = null;
                 try {
                     inputStream = uploadFiles.getInputStream();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 ExcelImportService excelImportService = new ExcelImportService();
        String keyName = "goodsName,categoryName,price,skuCode";
        String keyType = "string,string,string,string";
        String nullable = "false,false,false,false";

        List<Map<String, Object>> list = excelImportService.importEx(keyName, keyType,nullable, (FileInputStream) inputStream, origName);
        List<Map<String, Object>> ReList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(ValueUtil.notEmpity(list.get(i).get("erro"))){
                Map<String, Object> map = new HashMap<>();
                map = list.get(i);
                ReList.add(map);
            }
        }
        if(ReList.size()!=0){
            return ValueUtil.toJson(500, ReList);
        }
        List<Map<String, Object>> listRe = this.importService.importGoods(list);
        if(listRe.size()==0){
            return ValueUtil.toJson(HttpStatus.SC_CREATED, "导入成功");
        }else {
            for(Map<String, Object> map: listRe){
                ReList.add(map);
            }
            return ValueUtil.toError("500","erro", ReList);
        }
    }


//    @RequestMapping(value = "/category", method = RequestMethod.POST)
    public String uploadCategory(@RequestParam("uploadFiles")MultipartFile uploadFiles) {
       try {
           String origName = uploadFiles.getOriginalFilename();
           // 校验格式
           boolean isLegal = false;
           for (String type : IMAGE_TYPE) {
               if (StringUtils.endsWithIgnoreCase(origName, type)) {
                   isLegal = true;
                   break;
               }
           }
           if (!isLegal) {
               return ValueUtil.toError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "文件格式不正确");
           }

           InputStream inputStream = null;
           try {
               inputStream = uploadFiles.getInputStream();
           } catch (IOException e) {
               e.printStackTrace();
           }
           ExcelImportService excelImportService = new ExcelImportService();
           String keyName = "categoryName,parentId,code,isShow,prop";
           String keyType = "string,string,string,string,string";
           String nullable = "false,true,false,false,false";

           List<Map<String, Object>> list = excelImportService.importEx(keyName, keyType, nullable, (FileInputStream) inputStream, origName);
           List<Map<String, Object>> ReList = new ArrayList<>();
           for (int i = 0; i < list.size(); i++) {
               if (ValueUtil.notEmpity(list.get(i).get("erro"))) {
                   Map<String, Object> map = new HashMap<>();
                   map = list.get(i);
                   ReList.add(map);
               }
           }
           if (ReList.size() != 0) {
               return ValueUtil.toJson(500, ReList);
           }
           List<Map<String, Object>> listRe = this.importService.importCategory(list);

           if (listRe.size() == 0) {
               return ValueUtil.toJson(HttpStatus.SC_CREATED, "success");
           } else {
               for (Map<String, Object> map : listRe) {
                   ReList.add(map);
               }
               return ValueUtil.toError("500", "erro", ReList);
           }
       }catch (yesmywineException e){
           Threads.createExceptionFile("goods",e.getMessage());
           return ValueUtil.toError(e.getCode(),e.getMessage());
       }

    }




}