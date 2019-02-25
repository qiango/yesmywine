package com.yesmywine.ware;

import com.yesmywine.util.basic.ExcelHelper;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.ware.controller.OMSController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Action;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

/**
 * Created by Administrator on 2017/7/13 0013.
 */
@RestController
@RequestMapping("/itf/import")
public class ImpotExcel {

    @Autowired
    private OMSController omsController;


}
