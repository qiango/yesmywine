#!/bin/bash


##### host config ######
cmsHost=http://localhost:8080


id=$1
id1=$2


##############################
echo start `date`
##############################



echo  '\文章增加接口测试'
curl -X POST --data "columnsId=20&title=范围&articleContent=<htmldfdhgffffffffffffffdffdddeywr77ew7yeyryretwtrvcvx bxcvd123yggy/html>"  ${cmsHost}/article

echo  '\根据文章id查询测试'
curl -X GET   ${cmsHost}/article/44

echo  '\文章修改接口测试'
curl -X PUT --data "columnsId=5&title=购物流程&articleContent=<html dfdhgffffffffffffffdffdddeywr77ew7yeyryretwtrvcvx bxcvd123yggy/html>"  ${cmsHost}/article/update/{44}

echo  '\文章删除测试'
curl -X DELETE   ${testHost}/article/delete/${id}

echo  '\文章查询测试'
curl -X GET   ${cmsHost}/article/index/20

echo  '\栏目增加接口口测试'
curl -X POST --data "columnsName=新手指南&pId=1"  ${cmsHost}/column

echo  '\栏目修改接口测试'
curl -X PUT --data "columnsName=购物指南&pId=1"  ${cmsHost}/column/update/4

echo  '\根据栏目id查询接口测试'
curl -X GET    ${cmsHost}/column/3

echo  '\栏目查询接口测试'
curl -X GET    ${cmsHost}/column/index

echo  '\栏目删除接口测试'
curl -X DELETE    ${testHost}/column/delete/${id1}






####################

echo '\n' the end  `date`
