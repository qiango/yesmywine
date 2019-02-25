#!/bin/bash


##### host sso ######
inventoryHost=http://88.88.88.211:8191
activityHost=http://88.88.88.211:8188
adverHost=88.88.88.211:8195
cmsHost=http://88.88.88.211:8198
evaluation=http://88.88.88.211:8190
goodsHost=http://88.88.88.211:8184
userserviceHost=http://88.88.88.211:8187
ssoHost=http://88.88.88.211:8186

token=jkfdkaf
username=jfkda
password=jkfda





##############################
echo start `date`
##############################

# 注册
echo  '\n注册'
curl -X POST --data "password=123456&email=1155528899@qq.com&param=1234&phoneNumber=1343383299971"  ${userserviceHost}/user/doRegister


# 登陆
echo  '\n登陆'
curl -X POST --data "param=1343383299971&type=1&password=123456"  ${ssoHost}/user/doLogin




##############################
echo end `date`
##############################
