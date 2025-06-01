FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests && ls -l target

EXPOSE 8080

CMD ["java", "-jar", "target/TIENDA_BRYAN_CASTANO_SANSEGUNDO-0.0.1-SNAPSHOT.jar"]
# 