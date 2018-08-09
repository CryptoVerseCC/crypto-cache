FROM openjdk:8 as builder
EXPOSE 8080

RUN mkdir /workspace
WORKDIR /workspace
COPY . /workspace

RUN ./gradlew build

FROM openjdk:8

COPY --from=builder /workspace/build/libs/app.jar .

ENTRYPOINT ["java","-jar","app.jar"]
