
FROM gradle:8.10.2-jdk17 AS builder

# Копируем только нужные для сборки файлы
COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle src ./src

# Собираем jar
RUN gradle clean bootJar

# Финальный образ — только JRE
FROM eclipse-temurin:17-jre-alpine

# Создаём директорию для файлов пользователей
RUN mkdir /uploads && chown -R 1000:1000 /uploads
VOLUME /uploads

# Копируем готовый jar из сборочного образа
COPY --from=builder /home/gradle/build/libs/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app.jar"]