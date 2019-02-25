#!/bin/bash

##### host config ######
adverHost=http://88.88.88.211:8195

##############################
echo start `date`
##############################

echo  '\n广告素材接口——创建广告素材'
curl -X POST  --data "adverName=1111&adverType=1111&url=1111&image=1111&remark=1111&positionId=2" ${adverHost}/adver/create

echo  '\n广告素材接口——修改广告素材'
curl -X PUT  --data "adverName=2222&adverType=2222&url=2222&image=2222&remark=2222&adverId=5" ${adverHost}/adver/update

echo  '\n广告素材接口——删除广告素材'
curl -X DELETE  -G --data "adverId=5&isDelete=1" ${adverHost}/adver/delete

echo  '\n广告素材接口——查询广告素材列表'
curl -X GET ${adverHost}/adver/index




echo  '\n广告位接口——创建广告位'
curl -X POST  --data "positionName=2222&positionType=2222&positionDesc=2222&width=2222&height=2222" ${adverHost}/position/create

echo  '\n广告位接口——修改广告位'
curl -X PUT  --data "positionId=3&positionName=3333&positionType=3333&positionDesc=3333&width=3333&height=3333&status=1" ${adverHost}/position/update

echo  '\n广告位接口——删除广告位'
curl -X DELETE  -G --data "isDelete=1" ${adverHost}/position/delete/3

echo  '\n广告位接口——根据广告位ID查询广告位'
curl -X GET ${adverHost}/position/query/3

echo  '\n广告位接口——查询广告位列表'
curl -X GET ${adverHost}/position/index

echo  '\n广告位接口——根据广告位ID查询该广告位下的所有广告素材列表'
curl -X GET ${adverHost}/position/getAdvers/1


####################

echo '\n' the end  `date`