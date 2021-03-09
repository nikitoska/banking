#FROM adoptopenjdk/openjdk11:alpine-jre
#COPY build/libs/transaction-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

FROM gradle:jdk11 as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build

FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]