FROM openjdk:8
ARG JAR_FILE=./target/*.jar
RUN mkdir ./upload
COPY ${JAR_FILE} app.jar

#ENV JAVA_OPTS="-Xmx264m -Xms128m"
ENV SPRING_PROFILES_ACTIVE="staging"
#ENV CONFIG_SERVER_URL="http://config-server:8888/"
ENV TZ Asia/Kathmandu
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
ENTRYPOINT [ "sh", "-c", "java -jar /app.jar"]
