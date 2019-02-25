#!/bin/bash


##### host config ######
pointHost=http://localhost:8080
token=$1


##############################
echo start `date`
##############################



echo  '\签到获积分测试'
curl -X POST    ${pointHost}/sign/${token}/1

echo  '\推荐人获积分测试'
curl -X POST   ${pointHost}/recommend/${token}/123/5

echo  '\市场活动获积分测试'
curl -X POST   ${pointHost}/activity/${token}/3/2/30.00

echo  '\购买商品获积分测试'
curl -X POST    ${pointHost}/buyGoods/${token}/50.00/123/2

echo  '\积分消耗测试 '
curl -X PUT    ${pointHost}/beans/increase/${token}/5/8

echo  '\积分清空测试 '
curl -X PUT    ${pointHost}/clean/clearPoint

echo  '\按时间段查询积分历史记录测试'
curl -X GET   "pageSize=5"  ${pointHost}/pointRecord/2015-08-01/2016-12-29/1

echo  '\配置积分规则更新测试'
curl -X POST --data "name=签到&pointRules=2,10&subscribe=积分随机2到10"  ${pointHost}/configuration/15

echo  '\配置积分规则增加测试'
curl -X POST --data "name=签到规则&pointRules=2,20&subscribe=积分随机2到20"  ${pointHost}/configuration

echo  '\酒豆消费测试 '
curl -X PUT    ${pointHost}/beans/decrease/${token}/5

####################

echo '\n' the end  `date`
