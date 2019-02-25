接口清单：
- [查询sku成本价列表](#查询sku成本价列表)

##### 查询仓库列表
##### url `http://api.hzbuvi.com/paas/inventory/costPriceRecord`

请求方式  `GET`

参数名(字段名后加“_l”表示模糊查询) | 含义    | 是否必须
-------|--------|-----
showFields | 可显示的字段（eg:showFields=warehouseCode,warehouseName）,默认全部显示 | N
pageNo|  页码 | N
pageSize|  每页条数 | N
all | 是否显示全部数据 （true-是，false-否，默认为false） | N
year | 年   |   N
mounth | 月   |   N
skuCode | sku编码 |N
skuName | sku名称 |N
skuId | skuID |N

如：http://api.hzbuvi.com/paas/inventory/costPriceRecord

###  返回值

参数名  | 含义
-------------|-------------
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
        "id": 45,
        "skuId": 1,
        "skuCode": "s1",
        "skuName": "name1",
        "year": 2017,
        "mounth": 4,
        "mounthInitCount": 101,
        "costPrice": 18.34,
        "totalCount": 924,
        "totalPrice": 44983.68,
        "synStatus": null,
        "createTime": "Apr 18, 2017 4:51:59 PM"
      },
      {
        "id": 46,
        "skuId": 2,
        "skuCode": "s2",
        "skuName": "name2",
        "year": 2017,
        "mounth": 4,
        "mounthInitCount": 102,
        "costPrice": 20.34,
        "totalCount": 306,
        "totalPrice": 6224.04,
        "synStatus": null,
        "createTime": "Apr 18, 2017 4:52:00 PM"
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