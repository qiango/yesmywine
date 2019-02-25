#!/bin/bash


##### host config ######
inventoryHost=http://88.88.88.211:8191
activityHost=http://88.88.88.211:8188
adverHost=88.88.88.211:8195
cmsHost=http://88.88.88.211:8198
evaluation=http://88.88.88.211:8190
goodsHost=http://88.88.88.211:8184
userserviceHost=http://88.88.88.211:8187
ssoHost=http://88.88.88.211:8186
cartHost=http://88.88.88.211:8183
ordersHost=http://88.88.88.211:8185

token=$1






##############################
echo start `date`
##############################



# 商品列表

echo  '\n查看商品'
curl -X GET  ${goodsHost}/goodsBiz/page

echo  '\n根据商品id获取skuId'
curl -X GET   ${goodsHost}/goodsBiz/getSku/8

echo  '\n根据分类id获取skuId'
curl -X GET  ${goodsHost}/properties/getSku/3

echo  '\n查看商品属性'
curl -X GET  ${goodsHost}/properties/page

echo  '\n通过分类id找可查询的属性和值'
curl -X GET   ${goodsHost}/properties/getValue/3

echo  '\n通过分类id找属性值的组合'
curl -X GET   ${goodsHost}/properties/getMeth/3

# 广告

echo  '\n广告位接口——根据广告位ID查询该广告位下的所有广告素材列表'
curl -X GET ${adverHost}/position/getAdvers/1


# 加入购物车

echo  '\n购物车接口——商品加入购物车'
curl -X POST  --data "goodsId=3&goodsAmount=5&token=${token}" ${cartHost}/cart/create

echo  '\n购物车接口——查询购物车列表'
curl -X GET ${cartHost}/cart/queryCartGoodsList?token=${token}

echo  '\n购物车接口——删除购物车中的一个商品'
curl -X DELETE -G --data "token=${token}&goodsId=3" ${cartHost}/cart/delete

echo  '\n购物车接口——清空购物车'
curl -X DELETE -G --data "token=${token}" ${cartHost}/cart/deleteBatch








# 商品列表

echo  '\n查看商品'
curl -X GET  ${goodsHost}/goodsBiz/page

echo  '\n根据商品id获取skuId'
curl -X GET   ${goodsHost}/goodsBiz/getSku/8

echo  '\n根据分类id获取skuId'
curl -X GET  ${goodsHost}/properties/getSku/3

echo  '\n查看商品属性'
curl -X GET  ${goodsHost}/properties/page

echo  '\n通过分类id找可查询的属性和值'
curl -X GET   ${goodsHost}/properties/getValue/3

echo  '\n通过分类id找属性值的组合'
curl -X GET   ${goodsHost}/properties/getMeth/3

# 广告

echo  '\n广告位接口——根据广告位ID查询该广告位下的所有广告素材列表'
curl -X GET ${adverHost}/position/getAdvers/1


# 加入购物车

echo  '\n购物车接口——商品加入购物车'
curl -X POST  --data "goodsId=3&goodsAmount=5&token=${token}" ${cartHost}/cart/create

echo  '\n购物车接口——查询购物车列表'
curl -X GET ${cartHost}/cart/queryCartGoodsList?token=${token}

echo  '\n购物车接口——删除购物车中的一个商品'
curl -X DELETE -G --data "token=${token}&goodsId=3" ${cartHost}/cart/delete

echo  '\n购物车接口——清空购物车'
curl -X DELETE -G --data "token=${token}" ${cartHost}/cart/deleteBatch






# 库存

echo  '\n库存管理接口——根据SKUID查看库存状况'
curl -X GET  ${inventoryHost}/inventory/goodsInventory/23i3887fj47

echo  '\n订单库存管理接口——下订单后更改库存'
curl -X PUT --data "jsonData={\"orderId\":1,\"goodsInfo\":[{\"goodsSKUID\":\"23i3887fj47\",\"warehouseCode\":\"test1\",\"count\":1,\"comment\":\"备注\",\"type\":0}]}"  ${inventoryHost}/orderInventory/saleGoods


# 积分

echo  '\购买商品获积分测试'
curl -X POST    ${userserviceHost}/buyGoods/${token}/50.00/123/2


# 评论

echo  '\n评论插入'
curl -X POST --data "goodId=11100011&image[0]=dddddddddd&image[1]=jjjjjjjjjjjjjjjj&orderNo=66666666&goodScore=4&token=${token}"  ${evaluation}/evaluation

echo  '\n评论查询'
curl -X GET   ${evaluation}/evaluation

echo  '\n咨询类型查询'
curl -X GET   ${evaluation}/adviceType

echo  '\n咨询插入'
curl -X POST --data "goodId=11100011&image[0]=dddddddddd&image[1]=jjjjjjjjjjjjjjjj&token=${token}"  ${evaluation}/advice

echo  '\n咨询查询'
curl -X GET   ${evaluation}/advice








####################

echo '\n' the end  `date`
