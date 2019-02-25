#!/bin/bash

gradle build

dockerImageName=paas-user
dockerContainerName=paas-user-c
dockerContainerPort=8107


docker stop $dockerContainerName
docker rm $dockerContainerName
docker rmi $dockerImageName

docker build -t $dockerImageName .

docker run -e JAVA_OPTS='-Xmx512m'  --name $dockerContainerName -it -p ${dockerContainerPort}:8080 -d $dockerImageName
