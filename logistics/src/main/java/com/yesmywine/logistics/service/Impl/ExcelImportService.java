package com.yesmywine.logistics.service.Impl;

import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;


@Service
public class ExcelImportService {

    public List<Map<String, Object>> importEx(String keyName, String keyType,String nullable,  FileInputStream inputStream, String origName) {
//        String keyName = "goodsNumber,categoryName,brandName,goodsName,goodsTitle,statusName";
//        String keyType = "string,string,string,string,string,string";
        Integer startCol = 1;
        Integer startRow = 2;
        List<Map<String, Object>> list = MSExcel.parseExcel(inputStream, origName, keyName, keyType,nullable, startRow, startCol);
        return list;
    }

}
