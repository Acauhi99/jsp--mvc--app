FROM maven:3.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY . .

# Create the .m2 directory to avoid permission issues
RUN mkdir -p /root/.m2

# Enable annotation processing explicitly and build
RUN mvn clean package -DskipTests -Dmaven.compiler.annotationProcessorPaths=org.projectlombok:lombok:1.18.30

FROM tomcat:10.1-jdk21
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080