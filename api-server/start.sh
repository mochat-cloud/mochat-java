#!/bin/bash
########################################################
############### Mochat 部署脚本 ##############
########################################################

echo 'Mochat 部署脚本启动 >>>>>>>>> '

cd "$PWD"

echo '构建代码 >>>>>>>>> '
"$PWD/"mvnw clean install
echo '构建完成 >>>>>>>>>'

#echo '启动 docker-compose 部署项目 >>>>>>>>>>> '
#docker-compose stop
#docker-compose rm
#docker-compose build
#docker-compose up -d
#echo '项目部署完成 >>>>>>>>>>> '