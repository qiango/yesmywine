#!/bin/bash


##### host config ######
activityHost=http://88.88.88.211:8188



##############################
echo start `date`
##############################

echo  '\n插入活动测试'
curl -X POST --data "name=新年大促销&description=全场打折&status=current&userLevel=0&startTime=2017-01-10 15:13:10&endTime=2017-01-15 15:13:10&actionId=2&triggerId=1&configValue=[{"price":200,"percent":0.8},{"price":100,"percent":0.9}]&configKey=full&json=[{ "skuId":"2" , "type":"Sku", "ware":"Promoting"},{ "skuId":"2" , "type":"0", "ware":"0"}]"  ${activityHost}/activity/create

echo  '\n查询活动测试'
curl -X GET   ${activityHost}/activity/page

echo  '\n加载活动测试'
curl -X GET --data  ${activityHost}/activity/updateLoad/1

echo  '\n修改活动测试'
curl -X PUT --data "name=新年大促销&description=全场打折&status=current&userLevel=0&startTime=2017-01-10 15:13:10&endTime=2017-01-15 15:13:10&configValue=[{"price":200,"percent":0.8},{"price":100,"percent":0.9}]&configKey=full&json=[{ "skuId":6 , "type":"Sku", "ware":"Promoting"},{ "skuId":7 , "type":"0", "ware":"0"}]&createTime=2017-01-15 15:13:10&triggerId=1&actionId=2&discountId=1&dicsountName=dd&type=trigger&configKey=full&configValue=[{"price":200,"percent":0.8},{"price":100,"percent":0.9}]&isDelete=NOT_DELETE&activityId=1&iftttDiscountId=1&iftttConfigId=1&priority=0&userLevel=0&creator=ssd&createTime=2017-01-5 15:13:10&editorame=ewrr&discountCreateTime=2017-01-5 15:13:10&discountId=1&confihgCreateTime=2017-01-5 15:13:10"  ${activityHost}/activity/update

echo  '\n删除活动测试'
curl -X POST --data "activityId=1"  ${activityHost}/activity/delete


echo  '\n插入活动互享测试'
curl -X POST --data "code=xddd&json=[{"activityId":1},{"activityId":2},{"activityId":3}]&startTime=2017-01-10 15:13:10&endTime=2017-05- 15:13:10"  ${activityHost}/activityShare/create

echo  '\n查询活动互享测试'
curl -X GET   ${activityHost}/activityShare/page

echo  '\n加载活动互享测试'
curl -X GET --data   ${activityHost}/activityShare/updateLoad/6

echo  '\n修改活动互享测试'
curl -X PUT --data "code=xddd&json=[{"activityId":5},{"activityId":2},{"activityId":3}]&startTime=2017-01-10 15:13:10&endTime=2017-05- 15:13:10"  ${activityHost}/activityShare/update

echo  '\n删除活动互享测试'
curl -X POST --data "code=xddd"  ${activityHost}/activityShare/delete


####################

echo '\n' the end  `date`
