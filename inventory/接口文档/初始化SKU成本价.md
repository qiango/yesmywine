接口清单：
- [初始化成本价接口](#初始化成本价接口)
- [初始化sku期初数量接口](#初始化sku期初数量接口)

--------------------------------
##### 初始化成本价接口 
##### url `http://api.hzbuvi.com/paas/inventory/costPriceInit`

请求方式  `POST`

参数名 | 含义    | 是否必须
-------|--------|-----
jsonData | json集合 | Y

如：http://api.hzbuvi.com/paas/inventory/costPriceInit?jsonData=[{"year":"2017","mounth":"7","costPrice":"129.56","skuId":"1","skuCode":"code1","skuName":"name1"}]

###  返回值

参数名  | 含义
-------------|-------------
分配成功
```json
{"code":"201","msg":"success","data":"SUCCESS"}
```


--------------------------------
##### 初始化sku期初数量接口 
##### url `http://api.hzbuvi.com/paas/inventory/costPriceInit`

请求方式  `POST`

参数名 | 含义    | 是否必须
-------|--------|-----

如：http://api.hzbuvi.com/paas/inventory/costPriceInit

###  返回值

参数名  | 含义
-------------|-------------
分配成功
```json
{"code":"201","msg":"success","data":"success"}
```