# WCAG 2.1 Webes akadálymentességi elemző és javító rendszer - Backend

A rendszer Spring Boot (Java 21) alapú REST API backend szolgáltatása, amely ellátja a weboldalak automatizált
akadálymentességi elemzését (Axe-Core, Selenium), a felhasználókezelést, valamint az AI-alapú kódjavítások generálását
(Spring AI / OpenAI API).

## Technológiai leírás

* **Nyelv & Keretrendszer:** Java 21 LTS, Spring Boot 3.x
* **Adatbázis:** PostgreSQL, Spring Data JPA, Hibernate ORM
* **Elemző motorok:** Selenium WebDriver (Headless Chrome), Deque Axe-Core
* **AI Integráció:** Spring AI (OpenAI API)
* **Build tool:** Apache Maven

## Előfeltételek (Prerequisites)

A projekt futtatásához az alábbiak szükségesek a gépeden:

* **JDK 21** vagy újabb
* **Apache Maven 3.8+**
* **PostgreSQL 15+** (a futó adatbázis-szolgáltatással)
* **Google Chrome** (a headless Selenium böngészéshez)

## Beállítás és Futtatás

### 1. Adatbázis előkészítése

Hozz létre egy adatbázist PostgreSQL-ben (például pgAdmin 4 segítségével):

```sql
CREATE DATABASE wcag_db;
```

### 2. Konfiguráció (`application.properties`)

Módosítsd vagy hozd létre a `src/main/resources/application.properties` fájlt az alábbi beállításokkal:

```properties
# PostgreSQL Adatbázis Kapcsolat
spring.datasource.url=jdbc:postgresql://localhost:5432/wcag_db
spring.datasource.username=postgres
spring.datasource.password=A_TE_POSTGRES_JELSZÓD

# JPA / Hibernate Beállítások
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Spring AI / OpenAI API
spring.ai.openai.api-key=A_TE_OPENAI_API_KULCSOD
```

### 3. Alkalmazás indítása

A projekt gyökér könyvtárából add ki a következő parancsokat:

```bash
mvn clean install
mvn spring-boot:run
```

## 🔗 Kapcsolódó Repo

* **Frontend UI (Angular):** [wcag-frontend](https://github.com/FLBence/wcag-frontend.git)