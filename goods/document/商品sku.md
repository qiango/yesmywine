接口清单：
- [查看sku](#查看sku)
- [新建sku](#新建sku)
- [删除sku](#删除sku)
- [排列sku](#排列sku)
- [同步sku](#同步sku)


#### 查看sku

##### url: `http://47.89.18.26:8184/goods/sku`
请求方式 : `GET`

参数名(参数名后追加“_l”为模糊查询)    | 含义    | 是否必须
-------|--------|-----
skuId| id |N
showField |显示需要查询的字段（例如：showField=skuName,code）｜N
pageNo|  分页页号 |N
pageSize| 分页大小 |N
sku_l |sku名字｜N（参数后加l为模糊查询）
code_l |SKU编码｜N
categoryId |分类Id｜N
supplierId |供应商Id｜N
all| 是否显示全部数据 （true-是，false-否，默认为false） |N

###  返回值

参数名  | 含义
-------------|-------------
List<Sku>  |sku实体
```json
{
  "code": "200",
  "msg": "success",
  "data": {
    "page": 1,
    "totalPages": 1,
    "pageSize": 10,
    "totalRows": 2,
    "content": [
      {
        "code": "123123rLMwin",
        "skuName": "红酒sku",
        "isUse": "yes",
        "costPrice": null,
        "property": "{度数:9度, 品牌:9度}",
        "sku": "红酒sku 9度 9度",
        "supplier": {
          "supplierName": "fddaf",
          "supplierCode": "rLM",
          "supplierType": "consignment",
          "provinceId": 11,
          "province": "北京",
          "cityId": 1102,
          "city": "北京县辖",
          "areaId": 110226,
          "area": "平谷县",
          "address": "fdsf",
          "postCode": "fasf",
          "contact": "dsaf",
          "telephone": "safsa",
          "mobilePhone": "asfsf",
          "fax": null,
          "mailbox": "sfsadf",
          "grade": null,
          "accountNumber": null,
          "credit": null,
          "procurementCycl": "fsasf",
          "paymentType": "saf",
          "invoiceCompany": "fsaf",
          "primarySupplier": null,
          "merchant": "140",
          "productManager": "fsasdf",
          "bank": null,
          "bankAccount": null,
          "dutyParagraph": null,
          "paymentDays": "Jan 21, 2016 12:09:00 AM",
          "deleteEnum": "NOT_DELETE",
          "id": 1,
          "createTime": "Apr 28, 2017 11:09:20 AM"
        },
        "category": {
          "categoryName": "红酒",
          "code": "win",
          "deleteEnum": "NOT_DELETE",
          "isShow": "no",
          "image": null,
          "parentName": {
            "categoryName": "酒",
            "code": "win",
            "deleteEnum": "NOT_DELETE",
            "isShow": "no",
            "image": null,
            "parentName": null,
            "id": 1,
            "createTime": "Apr 26, 2017 10:29:27 AM"
          },
          "id": 2,
          "createTime": "Apr 27, 2017 6:07:26 PM"
        },
        "id": 1,
        "createTime": "Apr 28, 2017 11:10:30 AM"
      },
      {
        "code": "123234rLMwin",
        "skuName": "红酒sku",
        "isUse": "no",
        "costPrice": null,
        "property": "{\"2\":\"1\",\"3\":\"2\"}",
        "sku": "红酒sku 9度 10度",
        "supplier": {
          "supplierName": "fddaf",
          "supplierCode": "rLM",
          "supplierType": "consignment",
          "provinceId": 11,
          "province": "北京",
          "cityId": 1102,
          "city": "北京县辖",
          "areaId": 110226,
          "area": "平谷县",
          "address": "fdsf",
          "postCode": "fasf",
          "contact": "dsaf",
          "telephone": "safsa",
          "mobilePhone": "asfsf",
          "fax": null,
          "mailbox": "sfsadf",
          "grade": null,
          "accountNumber": null,
          "credit": null,
          "procurementCycl": "fsasf",
          "paymentType": "saf",
          "invoiceCompany": "fsaf",
          "primarySupplier": null,
          "merchant": "140",
          "productManager": "fsasdf",
          "bank": null,
          "bankAccount": null,
          "dutyParagraph": null,
          "paymentDays": "Jan 21, 2016 12:09:00 AM",
          "deleteEnum": "NOT_DELETE",
          "id": 1,
          "createTime": "Apr 28, 2017 11:09:20 AM"
        },
        "category": {
          "categoryName": "红酒",
          "code": "win",
          "deleteEnum": "NOT_DELETE",
          "isShow": "no",
          "image": null,
          "parentName": {
            "categoryName": "酒",
            "code": "win",
            "deleteEnum": "NOT_DELETE",
            "isShow": "no",
            "image": null,
            "parentName": null,
            "id": 1,
            "createTime": "Apr 26, 2017 10:29:27 AM"
          },
          "id": 2,
          "createTime": "Apr 27, 2017 6:07:26 PM"
        },
        "id": 2,
        "createTime": "Apr 28, 2017 11:10:30 AM"
      }
    ],
    "hasPrevPage": true,
    "hasNextPage": false,
    "url": null,
    "conditionJson": null,
    "fields": null
  }
}
```

----------------------------------------

#### 新建sku

##### url: `http://47.89.18.26:8184/goods/sku`
请求方式 : `POST`

参数名    | 含义    | 是否必须
-------|--------|-----
suppierId|渠道id|Y
skuName|sku名字|Y
skuJsonArray|skuJson(例如：[{"19":"46"},{"20":"48"}],一条json为一个sku全部的属性及值，前者为属性id,后者为属性值id)|Y
categoryId|分类id|Y
###  返回值

参数名  | 含义
-------------|-------------
success|成功
```json
{
"code":"201",
"msg":"success",
"data":"success"
}
```

----------------------------------------
#### 删除sku

##### url: `http://47.89.18.26:8184/goods/sku`
请求方式 : `DELETE`

参数名    | 含义    | 是否必须
-------|--------|-----
skuId|skuId|Y
###  返回值

参数名  | 含义
-------------|-------------
success|成功
```json
{
"code":"204",
"msg":"success",
"data":"success"
}
```
----------------------------------------


#### 排列sku

##### url: `http://47.89.18.26:8184/goods/sku/rank`
请求方式 : `GET`

参数名    | 含义    | 是否必须
-------|--------|-----
valueJson|属性和属性值id字符串（例如：[{"id":"1","valueId":"1,2"},{"id":"2","valueId":"2,3"}]）|Y
###  返回值

参数名  | 含义
-------------|-------------
success|成功
```json
{
  "code": "200",
  "msg": "success",
  "data": [
    {
      "label": {
        "2": "3"
      },
      "value": "甜 苦"
    },
    {
      "label": {
        "2": "3"
      },
      "value": "甜 苦"
    }
  ]
}
```
----------------------------------------


#### 同步sku

##### url: 
请求方式 : 

参数名    | 含义
-------|--------
code|SKU编码
skuName|名称
property|属性
supplier|渠道
category|分类
###  返回值

参数名  | 含义
-------------|-------------

----------------------------------------
