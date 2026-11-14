# A dockerfile mesterséges intelligencia segítségével készült
# --- 1. ÉPÍTÉSI FÁZIS (Build stage) ---
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Másoljuk át a Maven wrapper fájlokat és a pom.xml-t a függőségek letöltéséhez
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# FONTOS: Biztosítjuk, hogy az mvnw futtatható legyen
RUN chmod +x mvnw

# Futtassuk le a függőségek letöltését (ez gyorsítja a későbbi buildeket, ha nem változott a pom.xml)
RUN ./mvnw dependency:go-offline

# Másoljuk át a teljes forráskódot
COPY src ./src

# Lefordítjuk az alkalmazást tesztek kihagyásával
RUN ./mvnw clean package -DskipTests

# --- 2. FUTTATÁSI FÁZIS (Run stage) ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Átmásoljuk az előző fázisban lefordított jar fájlt a target mappából
COPY --from=build /app/target/*.jar app.jar

# A Render vagy más platformok a PORT környezeti változót adják át (alapértelmezetten 8080)
ENV PORT=8080
EXPOSE 8080

# Az alkalmazás indítása
ENTRYPOINT ["java", "-Xmx350m", "-jar", "app.jar"]