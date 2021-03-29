# Mvn base镜像
FROM gradle AS build

# 编译目录设置
WORKDIR /opt/code

# 代码拷贝
COPY . /opt/code

# 编译...
RUN gradle mc


# JAVA base镜像，JDK1.8
FROM mochat/mochat-java:latest

# 修改 deb 源, 安装 ffmpeg 媒体转换库
RUN set -ex \
    && sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list \
    && echo "deb http://archive.debian.org/debian jessie-backports main" > /etc/apt/sources.list.d/jessie-backports.list \
    && echo "deb http://www.deb-multimedia.org jessie main" >> /etc/apt/sources.list.d/multimedia.list \
    && apt-get -o Acquire::Check-Valid-Until=false update \
    && apt-get install -y --force-yes deb-multimedia-keyring \
    && apt-get install -y --force-yes ffmpeg

# 工作目录设置
WORKDIR /opt/www

# 设置JVM信息
ENV JAVA_OPTS=-Xmx512m

# 引入build阶段编译的war包
COPY --from=build /opt/code/build/libs/mochat-1.0.0.jar ./

# 暴露端口
EXPOSE 8400

# 启动命令
ENTRYPOINT ["java", "-jar", "/opt/www/mochat-1.0.0.jar"]
