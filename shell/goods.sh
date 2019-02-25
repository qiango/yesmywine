#!/bin/bash


##### host config ######
goodsHost=http://88.88.88.211:8184





##############################
echo start `date`
##############################

echo  '\n查看该商品是否为预售商品'
curl -X GET  ${goodsHost}/goods/showPersell/6

echo  '\n设置为预售商品'
curl -X POST --data "goodsId=2&shiftTime=2017-01-09 17:03:44&endTime=2017-02-10 17:01:36"  ${goodsHost}/goods/setPersell

echo  '\n查看类型'
curl -X GET  ${goodsHost}/category/page

echo  '\n新增类型'
curl -X POST --data "categoryName=酒&code=bmj&parentId=1"  ${goodsHost}/category/create

echo  '\n修改类型加载'
curl -X GET   ${goodsHost}/category/3

echo  '\n修改类型'
curl -X PUT  --data "categoryName=测试酒&parentId=1&code=sss&isShow=yes" ${goodsHost}/category/9

echo  '\n删除类型'
curl -X POST --data "categoryId=9"  ${goodsHost}/category/delete

echo  '\n查看商品'
curl -X GET  ${goodsHost}/goodsBiz/page

echo  '\n根据商品id获取skuId'
curl -X GET   ${goodsHost}/goodsBiz/getSku/8

echo  '\n删除商品'
curl -X POST --data "goodsId=8"  ${goodsHost}/goodsBiz/delete

echo  '\n根据分类id获取skuId'
curl -X GET  ${goodsHost}/properties/getSku/3

echo  '\n查看商品属性'
curl -X GET  ${goodsHost}/properties/page

echo  '\n新增属性'
curl -X POST --data  "categoryId=3&isSku=0&canSearch=0&cnName=品种&enName=varieties&value=黑葡萄&code=h" ${goodsHost}/properties/create

echo  '\n通过分类id找可查询的属性和值'
curl -X GET   ${goodsHost}/properties/getValue/3

echo  '\n通过分类id找属性值的组合'
curl -X GET   ${goodsHost}/properties/getMeth/3
####################

echo '\n' the end  `date`
