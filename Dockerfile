# Use the openjdk image to run the Java application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Update package lists and install necessary tools
RUN apt-get update && apt-get install -y vim curl net-tools && rm -rf /var/lib/apt/lists/*

# Copy the pre-built JAR file from your local machine
COPY ./target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar
COPY config/logback.xml /app/config/logback.xml
COPY src/main/resources/application-dev.properties /app/config/application-dev.properties
EXPOSE 9797
CMD ["sh", "-c", "java -Dlog4j.configurationFile=file:/app/config/logback.xml -jar /app/demo-0.0.1-SNAPSHOT.jar --spring.config.location=file:/app/config/application-dev.properties; tail -f /dev/null"]
