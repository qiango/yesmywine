#!/bin/bash

gradle build

dockerImageName=paas-sms
dockerContainerName=paas-sms-c
dockerContainerPort=8106


docker stop $dockerContainerName
docker rm $dockerContainerName
docker rmi $dockerImageName

docker build -t $dockerImageName .

docker run -e JAVA_OPTS='-Xmx512m' --name $dockerContainerName -it -p ${dockerContainerPort}:8080 -d $dockerImageName
