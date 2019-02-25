#!/bin/bash


##### host evaluation ######
evaluation=http://88.88.88.211:8190
token=$1





##############################
echo start `date`
##############################



echo  '\n咨询类型插入测试'
curl -X POST --data "name=kucunpeisong"  ${evaluation}/adviceType

echo  '\n咨询类型查询测试'
curl -X GET   ${evaluation}/adviceType


echo  '\n评论插入测试'
curl -X POST --data "goodId=11100011&image[0]=dddddddddd&image[1]=jjjjjjjjjjjjjjjj&orderNo=66666666&goodScore=4&token=${token}"  ${evaluation}/evaluation

echo  '\n评论查询测试'
curl -X GET   ${evaluation}/evaluation


echo  '\n咨询插入测试'
curl -X POST --data "goodId=11100011&image[0]=dddddddddd&image[1]=jjjjjjjjjjjjjjjj&token=${token}"  ${evaluation}/advice

echo  '\n咨询查询测试'
curl -X GET   ${evaluation}/advice






####################

echo '\n' the end  `date`
