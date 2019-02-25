#!/bin/bash


##### host config ######
ordersHost=http://88.88.88.211:8185
token=$1
##############################
echo start `date`
##############################

echo  '\n插入订单'
curl -X POST --data "channelId=1&orderType=1&token=${token}&totalGoodsAmount=33&totalNum=4&invoiceNeedFlag=NO&comment=32323&json= [ {"goodsId":"1" ,"skuId':"2" ,"goodsName":"Doe","unitPrice":"44" ,"orderNum":"2","cartDetailId":"12"},{ "goodsId":"2" ,"skuId':"2" , "goodsName":"Smith", "unitPrice":"55" ,"orderNum":"4", "cartDetailId":"3"} ]&beanAmount=20&couponId=1&totalGoodsAmount=23&couponAmount=32.8&cashBackAmount=55.9&giveGiftCardId=4&giveGiftCardAmount=77&baughtGiftCardAmount=55&accountAmount=33 &taxAmount=54&totalPromoteAmount=1223&totalCostAmount=223&deliverType=0&receiver=王千&areaId=12&address=上海长宁&phone=12344&postCode=2442232&freight=22324&promoteFreight=0"  ${ordersHost}/order


####################

echo '\n' the end  `date`
