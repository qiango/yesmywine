#!/bin/bash

##### host config ######
cartHost=http://88.88.88.211:8183
#####cartHost=http://localhost:8080
token=$1

##############################
echo start `date`
##############################

echo  '\n购物车接口——商品加入购物车'
curl -X POST  --data "goodsId=3&goodsAmount=5&token=${token}" ${cartHost}/cart/create

echo  '\n购物车接口——查询购物车列表'
curl -X GET ${cartHost}/cart/queryCartGoodsList?token=${token}

echo  '\n购物车接口——删除购物车中的一个商品'
curl -X DELETE -G --data "token=${token}&goodsId=3" ${cartHost}/cart/delete

echo  '\n购物车接口——清空购物车'
curl -X DELETE -G --data "token=${token}" ${cartHost}/cart/deleteBatch




####################

echo '\n' the end  `date`