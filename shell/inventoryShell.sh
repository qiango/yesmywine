#!/bin/bash


##### host config ######
inventoryHost=http://88.88.88.211:8191

warehouseCode=$1
skuId=$2
token=$3


##############################
echo start `date`
##############################



echo  '\n仓库管理接口——查询仓库列表'
curl -X GET   ${inventoryHost}/warehouse/index

echo  '\n仓库管理接口——创建仓库'
curl -X POST  --data "contactId=100000007&warehouseCode=${warehouseCode}&warehouseName=天津分仓库&warehouseProvince=天津&warehouseCity=天津&warehouseAddress=天津&type=1&contactName=张三&telephone=02022334332&phone=1898887776" ${inventoryHost}/warehouse/save

echo  '\n仓库管理接口——修改仓库'
curl -X PUT  --data "warehouseId=5&warehouseName=天津22分仓库&warehouseProvince=天22津&warehouseCity=天22津&warehouseAddress=天11津&type=1&contactName=张11三&telephone=02022334332&phone=1898887776" ${inventoryHost}/warehouse/update

echo  '\n仓库管理接口——查看仓库信息'
curl -X GET  ${inventoryHost}/warehouse/show/3

echo  '\n仓库管理接口——查看仓库中所有商品'
curl -X GET  ${inventoryHost}/warehouse/getGoods/test1

echo  '\n仓库管理接口——查看商品存于那些仓库'
curl -X GET  ${inventoryHost}/warehouse/getGoodswarehouse/23i3887fj47



echo  '\n库存管理接口——创建新商品,新增库存'
curl -X GET   ${inventoryHost}/inventory/create?${skuId}=100&WCc988363=99

echo  '\n库存管理接口——查看商品库存（列表形式）'
curl -X GET  ${inventoryHost}/inventory/index?goodsSKUIds=${skuId},WT00987657

echo  '\n库存管理接口——给商品分配库存'
curl -X POST --data "jsonData={\"skuId\":\"${skuId}\",\"token\":\"${token}\",,\"goodsName\":\"HongXingErGuoTou\",\"warehouses\":[{\"warehouseId\":34,\"warehouseCode\":\"test1\",\"count\":40,\"freezeCount\":10},{\"warehouseId\":36,\"warehouseCode\":\"test2\",\"count\":30,\"freezeCount\":20}]}"  ${inventoryHost}/inventory/save

echo  '\n库存管理接口——商品库存修改  type 类型 0-正常进货   1-调仓  2-调拨出库   3-调拨进货 '
curl -X PUT --data "jsonData={\"skuId\":\"${skuId}\",\"token\":\"${token}\",,\"type\":0,\"warehouses\":[{\"warehouseId\":34,\"warehouseCode\":\"test1\",\"targetWarehouseCode\":\"test2\",\"changeQuantity\":1},{\"warehouseId\":36,\"warehouseCode\":\"test2\",\"targetWarehouseCode\":\"test1\",\"changeQuantity\":1}]}"  ${inventoryHost}/inventory/update

echo  '\n库存管理接口——根据SKUID查看库存状况'
curl -X GET  ${inventoryHost}/inventory/goodsInventory/${skuId}



echo  '\n福袋库存管理接口——创建福袋后，冻结库存'
curl -X POST  --data "luckyBagJson={\"luckyBagId\":5,\"goods\":[{\"goodsSKUID\":\"${skuId}\",\"warehouseCode\":\"test1\",\"warehouseId\":34,\"freezeCount\":5},{\"goodsSKUID\":\"${skuId}\",\"warehouseCode\":\"test1\",\"warehouseId\":34,\"freezeCount\":5}]}"  ${inventoryHost}/luckyBagInventory/save

echo  '\n福袋库存管理接口——销售福袋，减冻结库存'
curl -X PUT --data "luckyBagJson={\"goodsSKUID\":\"${skuId}\",\"warehouseCode\":\"test2\",\"orderNo\":\"DD8483737\",\"luckyBagId\":3,\"freezeCount\":1,\"comment\":\"备注\"}"   ${inventoryHost}/luckyBagInventory/saleLuckyBag

echo  '\n福袋库存管理接口——查看福袋剩余商品状况（详情）'
curl -X GET    ${inventoryHost}/luckyBagInventory/show/5

echo  '\n福袋库存管理接口——下架福袋,冻结库存归入可用库存'
curl -X GET    ${inventoryHost}/luckyBagInventory/soldOut/5





echo  '\n线下渠道字典管理接口——创建渠道'
curl -X POST --data  "channelsName=99TeMai"    ${inventoryHost}/channels/save

echo  '\n线下渠道字典管理接口——修改渠道'
curl -X PUT --data  "channelsName=99TeMai2222"    ${inventoryHost}/channels/update

echo  '\n线下渠道字典管理接口——查看渠道'
curl -X GET  ${inventoryHost}/channels/show/3

echo  '\n线下渠道字典管理接口——查看渠道列表'
curl -X GET  ${inventoryHost}/channels/index

echo  '\n线下渠道字典管理接口——删除渠道'
curl -X DELETE  ${inventoryHost}/channels/delete/2




echo  '\n线下库存管理接口——线下商品库存列表'
curl -X GET  ${inventoryHost}/offlineInventory/index

echo  '\n线下库存管理接口——查看商品线下库存详情'
curl -X GET  ${inventoryHost}/offlineInventory/show/23i3887fj47

echo  '\n线下库存管理接口——线下库存新增'
curl -X POST  --data "goodsSKUID=${skuId}&channelsId=1&channelsName=JD&count=100"  ${inventoryHost}/offlineInventory/save

echo  '\n线下库存管理接口——线下库存修改'
curl -X PUT  --data "offlineId=1&count=12"  ${inventoryHost}/offlineInventory/update





echo  '\n订单库存管理接口——下订单后更改库存'
curl -X PUT --data "jsonData={\"orderNo\":\"DD39372\",\"goodsInfo\":[{\"goodsSKUID\":\"23i3887fj47\",\"warehouseCode\":\"test1\",\"count\":1,\"comment\":\"beizhu\",\"type\":0}]}"  ${inventoryHost}/orderInventory/saleGoods

echo  '\n订单库存管理接口——取消订单或订单超时支付，减掉订单冻结库存'
curl -X GET   ${inventoryHost}/orderInventory/restore?orderNo=DD39372







echo  '\n调拨管理接口——发起调拨申请'
curl -X POST -d "goodsSKUID=WT00987657&count=1&importWarehouseCode=test1&sponsorId=100000001&sponsor=Name"   ${inventoryHost}/allot/allotApply

echo  '\n调拨管理接口——调拨申请驳回'
curl -X GET   ${inventoryHost}/allot/allotApply?id=1&exportWarehouseCode=test2&token=${token}

echo  '\n调拨管理接口——查看调拨记录详情'
curl -X GET   ${inventoryHost}/allot/show/1

echo  '\n调拨管理接口——调拨申请通过'
curl -X GET   ${inventoryHost}/allot/allotAudit?id=1&token=${token}&exportWarehouseCode=test1,test2

echo  '\n调拨管理接口——调拨申请确认收货'
curl -X GET   ${inventoryHost}/allot/confirmationReceipt?id=1&token=${token}






echo  '\n用户基本信息接口——查询用户基本信息'
curl -X GET ${userserviceHost}/userInfo/show/${token}

echo  '\n用户基本信息接口——用户列表查询'
curl -X  GET ${userserviceHost}/userInfo/index



echo  '\n用户黑名单接口——查询用户是否是黑名单用户'
curl -X GET ${userserviceHost}/userBlack/100000007

echo  '\n用户黑名单接口——加入黑名单'
curl -X POST --data "userId=100000007" ${userserviceHost}/userBlack/disable

echo  '\n用户黑名单接口——用户从黑名单中还原'
curl -X GET  ${userserviceHost}/userBlack/disable/recover/100000007






echo  '\n礼品卡种类管理——礼品卡种类新增'
curl -X POST  "GCardName=金卡&amounts=5000&batch=RT393438&comment="备注"  ${goodsHost}/giftcard/save

echo  '\n礼品卡种类管理——查看详情'
curl -X GET   ${goodsHost}/giftcard/show/1

echo  '\n礼品卡种类管理——礼品卡种类列表查询'
curl -X GET   ${goodsHost}/giftcard/index

echo  '\n礼品卡种类管理——将礼品卡发布为商品'
curl -X GET   ${goodsHost}/giftcard/pushGoods?ids=1





echo  '\n礼品卡管理——消费礼品卡'
curl -X GET  ${goodsHost}/giftcardorder/cardExpense?cardNos=WQ0001,WQ0002,WQ0003,WQ0004&monetary=3000&orderId=11111

echo  '\n礼品卡管理——销售后的礼品卡列表查询'
curl -X GET  ${goodsHost}/giftCardOrder/index








####################

echo '\n' the end  `date`
