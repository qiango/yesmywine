接口清单：
- [oms采购入库](#oms采购入库)
- [oms查询渠道库存](#oms查询渠道库存)
- [oms申请冻结库存接口](#oms申请冻结库存接口)
- [oms扣减库存通知](#oms扣减库存通知)
- [oms释放冻结库存通知](#oms释放冻结库存通知)
- [oms查询仓库库存](#oms查询仓库库存)

--------------------------------
##### oms采购入库
##### url `http://api.hzbuvi.com/inventory/oms/stock`

请求方式  `POST`

参数名 | 含义    | 是否必须
-------|--------|-----
jsonData | json格式字符串 | Y


如：http://api.hzbuvi.com/inventory/oms/stock?jsonData=[{"certificateNum":"cNum_1","skuId":"1","warehouseId":"1","channelId":"1","orderNum":"num001","orderType":"stock","count":"101","price":"18.34","comment":"备注1"}, {"certificateNum":"cNum_2","skuId":"2","warehouseId":"1","channelId":"1","orderNum":"num001","orderType":"stock","count":"102","price":"20.34","comment":"备注2"}, {"certificateNum":"cNum_3","skuId":"1","warehouseId":"2","channelId":"1","orderNum":"num001","orderType":"stock","count":"103","price":"28.34","comment":"备注3"}, {"certificateNum":"cNum_4","skuId":"1","warehouseId":"2","channelId":"2","orderNum":"num001","orderType":"stock","count":"104","price":"38.34","comment":"备注4"}]

###  返回值

参数名  | 含义
-------------|-------------
入库成功json
```json
{"code":"200","msg":"success","data":"200"}
```
入库失败json
```json
{
"code": "500",
"msg": "failed",
"data": ""
}
```

--------------------------------
##### oms查询渠道库存
##### url `http://api.hzbuvi.com/inventory/oms/cIndex`

请求方式  `GET`
参数名 | 含义    | 是否必须
-------|--------|-----
jsonData | 查询集合 | Y

如：http://api.hzbuvi.com/inventory/oms/cIndex?jsonData=[{"skuId":"1","channelId":"1"}]

###  返回值

参数名  | 含义
-------------|-------------
```json
{
  "code": "200",
  "msg": "success",
  "data": [
    {
      "channeId": 1,
      "count": 204,
      "skuId": 1,
      "skuCode": "s1",
      "channelCode": "TM"
    }
  ]
}
```

--------------------------------
##### oms申请冻结库存接口
##### url `http://api.hzbuvi.com/inventory/oms/freeze`

请求方式  `PUT`

参数名 | 含义    | 是否必须
-------|--------|-----
jsonData | 库存集合 | Y

如：http://api.hzbuvi.com/inventory/oms/freeze?jsonData=[{"skuId":"1","warehouseId":"1","channelId":"1","count":"101"}]

###  返回值

参数名  | 含义
-------------|-------------
冻结成功json
```json
{"code":"201","msg":"success","data":"SUCCESS"}
```
冻结失败json
```json
{
"code": "500",
"msg": "failed",
"data": ""
}
```

--------------------------------
##### oms扣减库存通知
##### url `http://api.hzbuvi.com/inventory/oms/subFreeze`

请求方式  `POST`

参数名 | 含义    | 是否必须
-------|--------|-----
jsonData | 库存集合 | Y

如：http://api.hzbuvi.com/inventory/oms/subFreeze?jsonData=[{"skuId":"1","warehouseId":"1","channelId":"1","count":"13"}]

###  返回值

参数名  | 含义
-------------|-------------
扣减成功json
```json
{"code":"200","msg":"success","data":"200"}
```
扣减失败json
```json
{
"code": "500",
"msg": "failed",
"data": ""
}
```

--------------------------------
##### oms释放冻结库存通知
##### url `http://api.hzbuvi.com/inventory/oms/releaseFreeze`

请求方式  `POST`

参数名 | 含义    | 是否必须
-------|--------|-----
jsonData | 库存集合 | Y

如：http://api.hzbuvi.com/inventory/oms/releaseFreeze?jsonData=[{"skuId":"1","warehouseId":"1","channelId":"1","count":"101"}]

###  返回值

参数名  | 含义
-------------|-------------
释放成功json
```json
{"code":"200","msg":"success","data":"200"}
```
释放失败json
```json
{
"code": "500",
"msg": "failed",
"data": ""
}
```

--------------------------------
##### oms查询仓库库存
##### url `http://api.hzbuvi.com/inventory/oms/cwIndex`

请求方式  `GET`
参数名 | 含义    | 是否必须
-------|--------|-----
jsonData | 查询集合 | Y

如：http://api.hzbuvi.com/inventory/oms/cwIndex?jsonData=[{"skuId":"1","channelId":"1","warehouseId":"1"}]

###  返回值

参数名  | 含义
-------------|-------------
```json
{
  "code": "200",
  "msg": "success",
  "data": [
    {
      "channeId": 1,
      "warehouseId": 1,
      "count": 102,
      "skuId": 1,
      "skuCode": "s1",
      "warehouseCode": "w1",
      "channelCode": "c1"
    }
  ]
}
```
