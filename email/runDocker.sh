#!/bin/bash

gradle build

dockerImageName=paas-email
dockerContainerName=paas-email-c
dockerContainerPort=8102


docker stop $dockerContainerName
docker rm $dockerContainerName
docker rmi $dockerImageName

docker build -t $dockerImageName .

docker run -e JAVA_OPTS='-Xmx512m' --name $dockerContainerName -it -p ${dockerContainerPort}:8080 -d $dockerImageName
