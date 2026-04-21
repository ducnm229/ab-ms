FROM openjdk:17-jdk-slim

LABEL "org.opencontainers.image.authors"="aidanadrian@email.com"

COPY target/customer-0.0.1-SNAPSHOT.jar customer-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","customer-0.0.1-SNAPSHOT.jar"]