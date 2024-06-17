# Base image
FROM openjdk:17-jdk-slim

# Gradle 설치
ARG GRADLE_VERSION=8.7
RUN apt-get update && apt-get install -y wget unzip curl vim python3 python3-pip \
    && wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION} /opt/gradle \
    && rm gradle-${GRADLE_VERSION}-bin.zip
ENV PATH=${PATH}:/opt/gradle/bin

# 작업 디렉토리 설정
WORKDIR /app

# 소스 코드 및 이미지 파일 복사
COPY . .

# 빌드 디렉토리 생성
RUN mkdir -p /app/userData /app/logs

# 애플리케이션 빌드
RUN gradle build -x test

# 포트 노출
EXPOSE 8080 5005

# 디버깅을 위한 환경 변수 설정
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar build/libs/demoboard-0.0.1-SNAPSHOT.jar"]