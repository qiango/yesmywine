package com.yesmywine.goods.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.yesmywine.base.record.biz.BaseServiceImpl;
import com.yesmywine.goods.bean.DeleteEnum;
import com.yesmywine.goods.bean.SupplierTypeEnum;
import com.yesmywine.goods.dao.SkuDao;
import com.yesmywine.goods.dao.SupplierDao;
import com.yesmywine.goods.entity.Sku;
import com.yesmywine.goods.entityProperties.Supplier;
import com.yesmywine.goods.service.CommonService;
import com.yesmywine.goods.service.SupplierService;
import com.yesmywine.httpclient.bean.RequestMethod;
import com.yesmywine.util.basic.Dictionary;
import com.yesmywine.util.basic.SynchronizeUtils;
import com.yesmywine.util.basic.ValueUtil;
import com.yesmywine.util.date.DateUtil;
import com.yesmywine.util.error.yesmywineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yesmywine.goods.bean.SupplierTypeEnum.consignment;

/**
 * Created by wangdiandian on 2017/3/15.
 */
@Service
@Transactional
public class SupplierServiceImpl extends BaseServiceImpl<Supplier, Integer> implements SupplierService {

    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private CommonService<Supplier> commonService;

    public String addSupplier(Map<String, String> param) throws yesmywineException {//新增供应商
        ValueUtil.verify(param, new String[]{"supplierName", "supplierType","supplierCode",
                "province", "city", "area", "address", "postCode", "contact", "telephone", "mobilePhone",
                 "mailbox",  "procurementCycl", "paymentType",
                "invoiceCompany",  "merchantIdentification", "productManager"});

        Supplier supplier = new Supplier();
        supplier.setSupplierName(param.get("supplierName"));
//        String supplierCode = Encode.getSalt(3);//生成供应商编码
        String supplierCode= param.get("supplierCode");
        Supplier supplier1=supplierDao.findBySupplierCodeAndDeleteEnum(supplierCode, DeleteEnum.NOT_DELETE);
        if(supplier1!=null){
            ValueUtil.isError("供应商编码已存在");
        }
        supplier.setSupplierCode(supplierCode);
        String supplierType = param.get("supplierType");
        switch (supplierType) {
            case "distribution":
                supplier.setSupplierType(SupplierTypeEnum.distribution);
                break;
            case "consignment":
                supplier.setSupplierType(consignment);
                break;
            case "seaAmoy":
                supplier.setSupplierType(SupplierTypeEnum.seaAmoy);
                break;
        }
        supplier.setProvince(param.get("province"));
        supplier.setProvinceId(Integer.valueOf(param.get("provinceId")));
        supplier.setCity(param.get("city"));
        supplier.setCityId(Integer.valueOf(param.get("cityId")));
        supplier.setArea(param.get("area"));
        supplier.setAreaId(Integer.valueOf(param.get("areaId")));
        supplier.setAddress(param.get("address"));
        supplier.setPostCode(param.get("postCode"));
        supplier.setContact(param.get("contact"));
        supplier.setTelephone(param.get("telephone"));
        supplier.setMobilePhone(param.get("mobilePhone"));
        supplier.setFax(param.get("fax"));
        supplier.setMailbox(param.get("mailbox"));
        supplier.setGrade(param.get("grade"));
        supplier.setAccountNumber(param.get("accountNumber"));
        supplier.setCredit(param.get("credit"));
        supplier.setProcurementCycl(param.get("procurementCycl"));
        supplier.setPaymentType(param.get("paymentType"));
        supplier.setInvoiceCompany(param.get("invoiceCompany"));
        supplier.setPrimarySupplier(param.get("primarySupplier"));

        String merchantIdentification=param.get("merchantIdentification");
        supplier.setMerchantIdentification(merchantIdentification);
        supplier.setProductManager(param.get("productManager"));
        supplier.setBank(param.get("bank"));
        supplier.setBankAccount(param.get("bankAccount"));
        supplier.setDutyParagraph(param.get("dutyParagraph"));
        String paymentDays =param.get("paymentDays");
//        supplier.setMerchant(param.get("merchant"));
        if(paymentDays!=null) {
            supplier.setPaymentDays(DateUtil.toDate(paymentDays, "yyyy-mm-dd"));
        }
        supplier.setDeleteEnum(DeleteEnum.NOT_DELETE);


        supplierDao.save(supplier);

//        String merchant=param.get("merchant");
//        String[] strArray= merchantLogo.split(";"); //拆分字符为";" ,然后把结果交给数组strArray
//        HttpBean httpRequest = new HttpBean(ConstantData.fileUpload+ "/fileUpload/tempToFormal", RequestMethod.post);
//        httpRequest.addParameter("module","supplier");
//        httpRequest.addParameter("mId", supplier.getId());
//
//        String ids = "";
//        for (int i=0; i< strArray.length;i++) {
//            if(i==0){
//                ids = ids + strArray[i];
//            }else {
//                ids = ids +","+ strArray[i];
//            }
//            httpRequest.addParameter("id", ids);
//        }
//        httpRequest.run();
//        String temp = httpRequest.getResponseContent();
//
//        Map[] maps = new HashMap[strArray.length];
//        String result = ValueUtil.getFromJson(temp, "data");
//        JsonParser jsonParser = new JsonParser();
//        JsonArray image = jsonParser.parse(result).getAsJsonArray();
//        for(int f=0;f < image.size(); f ++){
//            String id = image.get(f).getAsJsonObject().get("id").getAsString();
//            String name = image.get(f).getAsJsonObject().get("name").getAsString();
//            Map<String ,String> map1 = new HashMap<>();
//            map1.put("id", id);
//            map1.put("name", name);
//            maps[f] = map1;
//        }
//        supplier.setMerchantLogo(maps);
//        supplierDao.save(supplier);

        param.put("supplierCode", supplierCode);
//        if(ValueUtil.notEmpity(supplier.getMerchantLogo()) || supplier.getMerchantLogo().length==0){
//            for (int i = 0; i < supplier.getMerchantLogo().length; i++) {
//                param.put("id" + i, supplier.getMerchantLogo()[i].get("id").toString());
//                param.put("name" + i, supplier.getMerchantLogo()[i].get("name").toString());
//            }
//            param.put("num", String.valueOf(supplier.getMerchantLogo().length));
//        }
//

        //向oms同步新增供应商
        sendToOMS(supplier,0);


        if(!this.commonService.synchronous(param, Dictionary.MALL_HOST+ "/goods/suppliers/synchronous", 0)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //商城后台新增失败，删除oms中的供应商
//            sendToOMS(supplier,2);
            return "erro";
        }

        return "success";
    }

    public Supplier updateLoad(Integer id) throws yesmywineException {//加载显示供应商
        ValueUtil.verify(id, "idNull");
        Supplier suppliers = supplierDao.findOne(id);
        return suppliers;
    }
    public String delete(Integer id) throws yesmywineException {//删除供应商
        ValueUtil.verify(id, "idNull");
//        查看供应商，被使用，不能删除
        Supplier supplier=new Supplier();
        supplier.setId(id);
        List<Sku> sku=skuDao.findBySupplier(supplier);
        if(sku.size()!=0){
            return "supplierUsed";
        }
        Supplier oldSupplier = supplierDao.findOne(id);
        oldSupplier.setDeleteEnum(DeleteEnum.DELETED);
        Map<String ,String> map = new HashMap<>();
        map.put("supplierCode", oldSupplier.getSupplierCode());

        //向OMS同步
        sendToOMS(oldSupplier,2);

        if(this.commonService.synchronous(map, Dictionary.MALL_HOST+ "/goods/suppliers/synchronous", 2)) {
            supplierDao.save(oldSupplier);
            return "success";
        }
        //商城删除失败，oms中的供应商需还原
        oldSupplier.setDeleteEnum(DeleteEnum.NOT_DELETE);
        sendToOMS(oldSupplier,0);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return "erro";
    }

    public String updateSave(Map<String, String> param) throws yesmywineException {//修改保存供应商
        Integer supplierId = Integer.parseInt(param.get("id"));
        Supplier supplier = supplierDao.findOne(supplierId);
        Supplier rollBack_supplier = supplierDao.findOne(supplierId);
        supplier.setSupplierName(param.get("supplierName"));
        String supplierCode= param.get("supplierCode");
        supplier.setSupplierCode(supplierCode);
        String supplierType = param.get("supplierType");
        switch (supplierType) {
            case "distribution":
                supplier.setSupplierType(SupplierTypeEnum.distribution);
                break;
            case "consignment":
                supplier.setSupplierType(consignment);
                break;
            case "seaAmoy":
                supplier.setSupplierType(SupplierTypeEnum.seaAmoy);
                break;
        }
        supplier.setProvince(param.get("province"));
        supplier.setProvinceId(Integer.valueOf(param.get("provinceId")));
        supplier.setCity(param.get("city"));
        supplier.setCityId(Integer.valueOf(param.get("cityId")));
        supplier.setArea(param.get("area"));
        supplier.setAreaId(Integer.valueOf(param.get("areaId")));
        supplier.setAddress(param.get("address"));
        supplier.setPostCode(param.get("postCode"));
        supplier.setContact(param.get("contact"));
        supplier.setTelephone(param.get("telephone"));
        supplier.setMobilePhone(param.get("mobilePhone"));
        supplier.setFax(param.get("fax"));
        supplier.setMailbox(param.get("mailbox"));
        supplier.setGrade(param.get("grade"));
        supplier.setAccountNumber(param.get("accountNumber"));
        supplier.setCredit(param.get("credit"));
        supplier.setProcurementCycl(param.get("procurementCycl"));
        supplier.setPaymentType(param.get("paymentType"));
        supplier.setInvoiceCompany(param.get("invoiceCompany"));
        supplier.setPrimarySupplier(param.get("primarySupplier"));
//        supplier1.setMerchantLogo(param.get("merchantLogo"));
        supplier.setProductManager(param.get("productManager"));
        supplier.setBank(param.get("bank"));
        supplier.setBankAccount(param.get("bankAccount"));
        supplier.setDutyParagraph(param.get("dutyParagraph"));
        String paymentDays =param.get("paymentDays");


        String merchantIdentification=param.get("merchantIdentification");
//        String[] strArray= merchantLogo.split(";"); //拆分字符为";" ,然后把结果交给数组strArray
//        HttpBean httpRequest = new HttpBean(ConstantData.fileUpload+ "/fileUpload/tempToFormal", RequestMethod.post);
//        httpRequest.addParameter("module","supplier");
//        httpRequest.addParameter("mId", supplierId);
//        String ids = "";
//        for (int i=0; i< strArray.length;i++) {
//            if(i==0){
//                ids = ids + strArray[i];
//            }else {
//                ids = ids +","+ strArray[i];
//            }
//            httpRequest.addParameter("id", ids);
//        }
//        httpRequest.run();
//        String temp = httpRequest.getResponseContent();
//
//        Map[] maps = new HashMap[strArray.length];
//        String result = ValueUtil.getFromJson(temp, "data");
//        JsonParser jsonParser = new JsonParser();
//        JsonArray image = jsonParser.parse(result).getAsJsonArray();
//        for(int f=0;f < image.size(); f ++){
//            String id = image.get(f).getAsJsonObject().get("id").getAsString();
//            String name = image.get(f).getAsJsonObject().get("name").getAsString();
//            Map<String ,String> map1 = new HashMap<>();
//            map1.put("id", id);
//            map1.put("name", name);
//            maps[f] = map1;
//        }

                supplier.setMerchantIdentification(merchantIdentification);

        if(paymentDays!=null) {
            supplier.setPaymentDays(DateUtil.toDate(paymentDays, "yyyy-mm-dd"));
        }

        //向oms同步供应商信息
        sendToOMS(supplier,1);

        if(!this.commonService.synchronous(param, Dictionary.MALL_HOST+ "/goods/suppliers/synchronous", 1)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //还原oms中的供应商信息
            sendToOMS(rollBack_supplier,1);
            return "向商城同步失败！";
        }

        supplierDao.save(supplier);
        return "success";

    }

    public void sendToOMS(Supplier supplier,Integer status) throws yesmywineException {//status 0-增  1-改  2-删
        JSONObject requestJson = new JSONObject();
        requestJson.put("function",status);
        JSONObject dataJson = new JSONObject();
        dataJson.put("customerCode",supplier.getSupplierCode());
        dataJson.put("customerName",supplier.getSupplierName());
        SupplierTypeEnum type = supplier.getSupplierType();
        switch (type){
            case consignment :
                dataJson.put("customerType","代销");
                break;
            case distribution:
                dataJson.put("customerType","经销");
                break;
            default:
                dataJson.put("customerType","海淘");
                break;
        }
        dataJson.put("contactProvince",supplier.getProvince());
        dataJson.put("contactCity",supplier.getCity());
        dataJson.put("contactDistrict",supplier.getArea());
        dataJson.put("contactAddress",supplier.getAddress());
        dataJson.put("contactPostCode",supplier.getPostCode());
        dataJson.put("contactperson",supplier.getContact());
        dataJson.put("contactTelephone",supplier.getTelephone());
        dataJson.put("contactMobile",supplier.getMobilePhone());
        dataJson.put("contactFax",supplier.getFax());
        dataJson.put("contactEmail",supplier.getMailbox());
        dataJson.put("grade",supplier.getGrade());
        dataJson.put("accountNumber",supplier.getAccountNumber());
        dataJson.put("creditDegree",supplier.getCredit());
        dataJson.put("purchasingCycle",supplier.getProcurementCycl());
        switch (supplier.getPaymentType()){
            case "1":
                dataJson.put("paymentMethod","银联");
                break;
            case "2":
                dataJson.put("paymentMethod","直联");
                break;
            case "3":
                dataJson.put("paymentMethod","货到付款");
                break;
            default:
                dataJson.put("paymentMethod","后结帐");
                break;
        }
        dataJson.put("invoiceCompanyName",supplier.getInvoiceCompany());
        dataJson.put("primarySupplier",supplier.getPrimarySupplier());
        dataJson.put("merchantLogo",supplier.getMerchantIdentification());
        dataJson.put("productManager",supplier.getProductManager());
        dataJson.put("depositBank",supplier.getBank());
        dataJson.put("bankAccount",supplier.getBankAccount());
        dataJson.put("dutyParagraph",supplier.getDutyParagraph());
        dataJson.put("accountPeriod",supplier.getPaymentDays());
        requestJson.put("data",dataJson);
        //向oms同步供应商信息
        String  result = SynchronizeUtils.getOmsResult(Dictionary.OMS_HOST,"/updateBaseCustomerSupplier",RequestMethod.post,"",requestJson.toJSONString());
        if(result!=null) {
            String respStatus = ValueUtil.getFromJson(result,"status");
            String message = ValueUtil.getFromJson(result,"message");
            if(!respStatus.equals("success")){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                ValueUtil.isError("向OMS同步供应商失败,原因："+message);
            }
        }else{
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ValueUtil.isError("向OMS同步供应商失败");
        }

    }

}
