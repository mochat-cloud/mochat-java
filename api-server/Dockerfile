FROM ypwcode/mochat:bate-0.0.1

# 工作目录设置
WORKDIR /opt/www

# 设置JVM信息
ENV JAVA_OPTS=-Xmx512m

# 引入build阶段编译的war包
COPY target/mochat-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8400

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
