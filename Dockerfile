FROM openjdk:8-jre-slim

RUN mkdir /app

WORKDIR /app

ADD ./api/target/account-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD java -jar account-api-1.0.0-SNAPSHOT.jar