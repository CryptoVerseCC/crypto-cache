FROM openjdk:8 as builder

RUN mkdir /workspace
WORKDIR /workspace
COPY . /workspace

RUN ./gradlew build

FROM openjdk:8

COPY --from=builder /workspace/build/libs/app.jar .
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
