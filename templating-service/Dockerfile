FROM openjdk:8
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} app.jar
#RUN wget https://uc1edefe33e0c01538ceaf25fff2.dl.dropboxusercontent.com/cd/0/get/BEzzLLne3eBc6gLXcAisZu2fG0eWldXSq4WBdTMhVW8zXC-uc_TCG01woioU4r7kIeAv5B4U_d1MIMiHmWhaITMvbFCthXL0nnRUpZiRfR6xsHei24s5hWZurf7Gi2cIpcU/file?dl=1# -O glowroot.jar
#COPY ../glowroot.jar glowroot.jar
#ENV JAVA_OPTS="-Xmx264m -Xms128m  -javaagent:glowroot.jar -Dglowroot.agent.id=audit-service -Dglowroot.collector.address=192.168.50.134:8181"
#ENV JAVA_OPTS="-Xmx1g -Xms512m"
ENV SPRING_PROFILES_ACTIVE="staging,resilience,redis-staging,sentry-staging"
#ENV CONFIG_SERVER_URL="http://config-server:8888/"
ENV TZ Asia/Kathmandu
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
ENTRYPOINT [ "sh", "-c", "java -jar /app.jar"]
