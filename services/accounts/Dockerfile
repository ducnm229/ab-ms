FROM openjdk:17-jdk-slim

LABEL "org.opencontainers.image.authors"="aidanadrian@email.com"

COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","accounts-0.0.1-SNAPSHOT.jar"]