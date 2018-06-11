FROM openjdk:8 as builder

RUN mkdir /workspace
WORKDIR /workspace
COPY . /workspace

RUN ./gradlew build

FROM openjdk:8

COPY --from=builder /workspace/build/libs/app.jar .

ENTRYPOINT ["java","-jar","app.jar"]