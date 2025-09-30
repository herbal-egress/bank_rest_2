FROM maven:3.9.9-amazoncorretto-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package
FROM tomcat:10.0-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]