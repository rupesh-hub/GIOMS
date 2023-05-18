FROM openjdk:8
ARG JAR_FILE=./target/*.jar
RUN mkdir ./upload
RUN mkdir config-server
RUN mkdir config-server/config-repo
COPY ./config-repo/. ./config-server/config-repo/.
COPY ${JAR_FILE} app.jar

#ENV JAVA_OPTS="-Xmx264m -Xms128m"
#ENV SPRING_PROFILES_ACTIVE="docker,chaos-monkey"
ENV TZ Asia/Kathmandu
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
ENTRYPOINT [ "sh", "-c", "java -jar /app.jar"]
