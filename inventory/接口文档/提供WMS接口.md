接口清单：
- [wms推送出库凭证](#wms推送出库凭证)
- [wms推送入库凭证](#wms推送入库凭证)

--------------------------------
##### wms推送出库凭证
##### url `http://api.hzbuvi.com/inventory/wms/outOrder`

请求方式  `POST`

参数名 | 含义    | 是否必须
-------|--------|-----
jsonData | json格式字符串 | Y

jsonData参数说明

参数名 | 含义    | 是否必须
-------|--------|-----
certificateNum | 出库凭证号 | Y
allotCode | 调拨指令编码 | Y
orderNum | 订单号 | Y
orderType | 订单类型 | Y
count | 出库数量 | Y
如：http://api.hzbuvi.com/inventory/wms/outOrder?jsonData=[{"certificateNum":"out_order_1","allotCode":"879.4100306158864","orderNum":"num_1","orderType":"sale","count":"15"}]

###  返回值

参数名  | 含义
-------------|-------------
```json
{"code":"201","msg":"success","data":"SUCCESS"}
```

--------------------------------
##### wms推送入库凭证
##### url `http://api.hzbuvi.com/inventory/wms/inOrder`

请求方式  `POST`

参数名 | 含义    | 是否必须
-------|--------|-----
jsonData | json格式字符串 | Y

jsonData参数说明

参数名 | 含义    | 是否必须
-------|--------|-----
certificateNum | 入库凭证号 | Y
allotCode | 调拨指令编码 | Y
orderNum | 订单号 | Y
orderType | 订单类型 | Y
count | 出库数量 | Y

如：http://api.hzbuvi.com/inventory/wms/inOrder?jsonData=[{"certificateNum":"in_order_1","allotCode":"879.4100306158864","orderNum":"num_1","orderType":"stock","count":"15"}]

###  返回值

参数名  | 含义
-------------|-------------
```json
{"code":"201","msg":"success","data":"SUCCESS"}
```
