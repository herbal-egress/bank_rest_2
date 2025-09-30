# добавленный код: Этап build для сборки WAR с Maven (SOLID: SRP для build).
FROM maven:3.9.9-amazoncorretto-17 AS build
# добавленный код: Копирование файлов проекта.
COPY . /app
# добавленный код: Рабочая директория.
WORKDIR /app
# добавленный код: Сборка WAR (mvn clean package; spring-boot-maven-plugin repackages as WAR).
RUN mvn clean package

# добавленный код: Этап runtime с Tomcat base image (OWASP: official tomcat image with jdk17).
FROM tomcat:10.0-jdk17
# добавленный код: Удаление default webapps для чистоты (OWASP: minimize attack surface).
RUN rm -rf /usr/local/tomcat/webapps/*
# добавленный код: Копирование WAR из build и rename в ROOT.war для default context.
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
# добавленный код: Открытие порта Tomcat.
EXPOSE 8080
# добавленный код: Запуск Tomcat (ООП: инкапсуляция запуска).
CMD ["catalina.sh", "run"]