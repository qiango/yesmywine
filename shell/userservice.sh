#!/bin/bash


##### host config ######
userserviceHost=http://88.88.88.211:8187
niqu=$1




##############################
echo start `date`
##############################



echo  '\n插入测试'
curl -X POST --data "title=第二次测试&type=测试用&content=不教胡马度阴山&url=www.baidu.com&userId=12344&vipLevels"  ${userserviceHost}/message

echo  '\n删除测试'
curl -X DELETE -G --data "idList=${niqu}"  ${userserviceHost}/message

echo  '\n查询测试'
curl -X GET  ${userserviceHost}/message/list

echo  '\n阅读测试'
curl -X GET --data "id=75" ${userserviceHost}/message/list

echo  '\n类型增加测试'
curl -X POST --data "typeName=古道西风瘦马" ${userserviceHost}/message/type

#echo  '\n类型删除测试'
#curl -X DELETE --data "typeIds=1,2,3" ${userserviceHost}/message/type

echo  '\n类型查询测试'
curl -X GET --data "typeId=" ${userserviceHost}/message/type






####################

echo '\n' the end  `date`
