# MaintSync — Maintenance Management Web App (Spring Boot + React + MySQL)

*(Node.js server removed – backend is Spring Boot)*

A maintenance management system with:
- **Authentication**: Email/password signup & login with JWT + Google & GitHub OAuth2
- **Equipment Inventory**: Equipment tracking with technician & employee assignment
- **Teams**: Maintenance teams with technician member mapping
- **Maintenance Requests**: Workflow tickets with stages (`New Request`, `In Progress`, `Repaired`, `Scrap`), priorities, and schedules
- **Calendar**: Visual scheduling timeline
- **Dashboard**: Live statistics and activity stream with WebSocket real-time updates

---

## Tech Stack

- **Backend (`server-springboot/`)**: Java 17+, Spring Boot 3.2.5, Spring Security 6, JJWT, Spring Data JPA, MySQL, Maven
- **Frontend (`client/`)**: React 18, Vite, Tailwind CSS, Lucide Icons, Axios

---

## Prerequisites

1. **Java JDK 17+** (JDK 21 or JDK 25 also supported)
2. **Maven 3.9+** (e.g. `C:\Program Files\Apache\maven\apache-maven-3.9.16`)
3. **MySQL Server 8.0+**
4. **Node.js 18+**

---

## Project Setup

### 1) MySQL Database Setup

In MySQL Workbench / MySQL CLI:

```sql
CREATE DATABASE maintenance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

*(Optional: You can manually run `server-springboot/src/main/resources/schema-mysql.sql`, or let Spring Boot automatically create/update tables via JPA).*

---

### 2) Backend Configuration (`.env`)

In `server-springboot/`, create or edit `.env` (a template is provided in `server-springboot/.env.example`):

```dotenv
# Server
PORT=5000
CLIENT_ORIGIN=http://localhost:5173

# MySQL Database
DB_HOST=localhost
DB_PORT=3306
DB_DATABASE=maintenance_db
DB_USER=root
DB_PASSWORD=your_mysql_password_here

# JWT Security
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_MS=604800000

# OAuth2 Google (Optional)
GOOGLE_CLIENT_ID=your_google_client_id_here
GOOGLE_CLIENT_SECRET=your_google_client_secret_here

# OAuth2 GitHub (Optional)
GITHUB_CLIENT_ID=your_github_client_id_here
GITHUB_CLIENT_SECRET=your_github_client_secret_here

# OAuth2 Client Redirect
OAUTH2_REDIRECT_URI=http://localhost:5173/login
```

---

### 3) Run the Backend (Spring Boot)

```powershell
cd server-springboot
mvn clean spring-boot:run
```
*Or build and run the packaged JAR:*
```powershell
mvn clean package -DskipTests
java -jar target/maintsync-api-1.0.0.jar
```

Backend will start on: **http://localhost:5000**

---

### 4) Run the Frontend (React + Vite)

```powershell
cd client
npm install
npm run dev
```

Frontend will run on: **http://localhost:5173**

---

## API Endpoints Overview

### Auth:
- `POST /api/auth/signup` — Create a new account
- `POST /api/auth/login` — Login & receive JWT token
- `GET /oauth2/authorization/google` — Trigger Google OAuth2 login
- `GET /oauth2/authorization/github` — Trigger GitHub OAuth2 login

### Users:
- `GET /api/users?role=technician&q=...` — Search users (Admin)
- `PATCH /api/users/:id/role` — Update user role (Admin)

### Teams:
- `GET /api/teams` — List teams and assigned technicians
- `GET /api/team-options` — List teams for request creation
- `POST /api/teams` — Create team (Admin)
- `POST /api/teams/:id/members` — Add technician to team (Admin)
- `DELETE /api/teams/:id/members/:userId` — Remove technician from team (Admin)

### Equipment:
- `GET /api/equipment` — List equipment (role-filtered)
- `GET /api/equipment/:id` — Get equipment details
- `POST /api/equipment` — Add equipment (Admin)
- `PUT /api/equipment/:id` — Update equipment (Admin)
- `DELETE /api/equipment/:id` — Delete equipment (Admin)

### Maintenance Requests:
- `GET /api/requests` — List requests (role & date filtered)
- `GET /api/requests/:id` — Get request details
- `POST /api/requests` — Create maintenance request (Employee / Admin)
- `PUT /api/requests/:id` — Update request stage / details (Technician / Admin)
- `DELETE /api/requests/:id` — Delete request (Admin)

### Dashboard & Metrics:
- `GET /api/dashboard` — Live dashboard metrics and recent activities
- `GET /api/stats` — Quick stats summary
- `WS /ws` — WebSocket real-time subscription for live dashboard stats
