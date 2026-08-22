# Deployment Guide for **Odoo‑Adani**

This document contains everything you need to take the local React + Spring‑Boot application and run it in the cloud, using a **single `.env` file** to store all credentials (database connection, JWT secret, API URLs, etc.).

---

## 📋 Prerequisites
- **Git** installed and a GitHub repository containing the project.
- **Node 16+** (for building the React client).
- **Docker** (required for the backend Docker image).
- An account on a cloud‑hosted MySQL provider (PlanetScale is recommended).
- A PaaS that can run Docker containers (Render, Railway, Fly.io, or Heroku).
- A static‑site host for the React build (Vercel, Netlify, or similar).

---

## 1️⃣ Provision a Managed MySQL Database
### Using PlanetScale (free tier)
1. Sign‑up at <https://planetscale.com> and create a new database, e.g. `maintsync-db`.
2. In the **Connect** tab, copy the **Host**, **Username**, **Password**, and **Database** values.
3. Run your existing schema against the new DB (you already have `schema‑mysql.sql`):
   ```bash
   mysql -h <HOST> -u <USERNAME> -p<PASSWORD> <DATABASE> < server-springboot/src/main/resources/schema-mysql.sql
   ```
   This creates tables such as `team`, `maintenance_request`, etc.
4. PlanetScale forces TLS, so you do not need any additional SSL configuration.

---

## 2️⃣ Prepare the Spring‑Boot Backend
### 2.1 Dockerfile (add to `server‑springboot/`)
```dockerfile
# ======== Build stage ========
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -Dmaven.test.skip=true clean package

# ======== Runtime stage ========
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

### 2.2 Externalise secrets in `application.properties`
Replace any hard‑coded values with placeholders that will be read from the environment (populated via `.env`):
```properties
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=true&requireSSL=true
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.jpa.hibernate.ddl-auto=none

jwt.secret=${JWT_SECRET}
jwt.access-token-expiration-minutes=15
jwt.refresh-token-expiration-days=7
```
Commit the updated file (it now only contains placeholders).

### 2.3 CORS configuration (optional, but needed for the frontend)
Add a `WebConfig` class:
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://<YOUR_FRONTEND_DOMAIN>.vercel.app")
                        .allowedMethods("*")
                        .allowCredentials(true);
            }
        };
    }
}
```
Replace `<YOUR_FRONTEND_DOMAIN>` with the actual Vercel domain.

---

## 3️⃣ Deploy the Backend
### Using Render (free tier) – **read credentials from `.env`**
1. Create a Render account: <https://render.com>.
2. **New → Web Service** → connect the GitHub repo.
3. Choose **Docker** as the build method (Render detects the Dockerfile automatically).
4. Set **Start Command** to `docker run -p 8080:8080 $IMAGE`.
5. **Add a `.env` file** at the root of the repository (outside version‑control) containing:
   ```
   DB_HOST=<PlanetScale host>
   DB_PORT=3306
   DB_NAME=<database name>
   DB_USER=<username>
   DB_PASS=<password>
   JWT_SECRET=<your‑jwt‑secret>
   SERVER_PORT=8080   # optional, default is 8080
   ```
   Render will automatically import these variables when you deploy (you can also copy‑paste them into Render’s UI under “Environment”).
6. Click **Create Web Service**. Render builds the Docker image, starts a container, and gives you a URL such as `https://maintsync-backend.onrender.com`.

### Alternative providers (Railway, Fly.io, Heroku)
The same `.env` file can be used – simply upload or set the variables in the provider’s dashboard.

---

## 4️⃣ Build & Deploy the React Frontend
### 4.1 Store the backend URL in `.env` (client side)
Create a `.env` file inside the `client/` folder with the following line:
```bash
VITE_API_BASE_URL=https://<YOUR_BACKEND_URL>
```
Replace `<YOUR_BACKEND_URL>` with the URL you obtained from Render (or your chosen provider).
The Vite build process automatically injects any `VITE_` prefixed variables from `.env`.

### 4.2 Vercel deployment (free tier)
1. Sign‑in at <https://vercel.com> → **New Project** → import the GitHub repo.
2. Set **Root Directory** to `client`.
3. **Build Command**: `npm install && npm run build`.
4. **Output Directory**: `dist`.
5. Vercel automatically reads the `.env` file (you may need to enable “Environment Variables” in the project settings and point to the same `VITE_API_BASE_URL`).
6. Deploy – you’ll receive a preview URL and a production URL like `https://maintsync-frontend.vercel.app`.

### 4.3 Netlify (if you prefer)
Same steps: **Build command** `npm install && npm run build`, **Publish directory** `dist`, and ensure the `.env` file with `VITE_API_BASE_URL` is present.

---

## 5️⃣ Verify the Deployment
| ✅ Step | Command / Check |
|--------|-----------------|
| Backend health | `curl https://<BACKEND_URL>/actuator/health` → should return `{"status":"UP"}` |
| DB connectivity | Health endpoint should list `db: UP` |
| Frontend loads | Open the Vercel URL in a browser – the login page appears |
| API calls succeed | Log in; the UI should show data (teams, requests) without errors |
| HTTPS everywhere | Both URLs start with `https://` |
| No secrets in repo | `.gitignore` blocks `.env`; all secrets reside only in the local `.env` and are injected into the platform |

---

## 6️⃣ Quick Cheat‑Sheet (one‑liner commands)
```bash
# ---------- Database ----------
pscale connect maintsync-db --port 3306   # optional local tunnel for testing
mysql -h <HOST> -u <USER> -p<PASS> <DB> < server-springboot/src/main/resources/schema-mysql.sql

# ---------- Backend ----------
cd server-springboot
docker build -t maintsync .   # local build test
# Push repo → Render (or Railway) – `.env` will be read automatically

# ---------- Frontend ----------
cd client
npm install && npm run build   # test locally
# Push repo → Vercel – `.env` provides VITE_API_BASE_URL
```

---

## 7️⃣ Reference Code Snippet (SecurityConfig)
For completeness, the part of `SecurityConfig.java` that defines the `AuthenticationManager` bean and the `PasswordEncoder` bean is included below:
```java
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
```
This snippet is also saved as a separate artifact (`SecurityConfigSnippet.md`).

---

### 🎉 You’re done!
All credentials now live in a single `.env` file that is **never committed** to the repository. Follow the steps, and your **React + Spring‑Boot** application will be live, using a secure, cloud‑hosted MySQL database.
