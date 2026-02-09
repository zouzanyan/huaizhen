# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Huaizhen (怀真) is a full-stack admin system with a Java Spring Boot backend and Vue 3 frontend. It implements a Role-Based Access Control (RBAC) system for user management, roles, permissions, and menus.

## Technology Stack

### Backend
- **Framework**: Spring Boot 4.0.2
- **Language**: Java 25
- **ORM**: MyBatis-Plus 3.5.15
- **Database**: MySQL (remote instance)
- **Build Tool**: Maven
- **View Engine**: FreeMarker
- **Native Support**: Configured for GraalVM native compilation

### Frontend
- **Framework**: Vue 3.4.0
- **UI Library**: Element Plus 2.5.0
- **Routing**: Vue Router 4.2.0
- **Build Tool**: Vite 5.0.0
- **Type Support**: Files use .js extension but support TypeScript
- **HTTP Client**: Axios

### Authentication
- **JWT**: Used for stateless authentication
- **Password Hashing**: BCrypt
- **Token Storage**: localStorage (frontend)
- **Token Validation**: OncePerRequestFilter (backend)
- **Authentication Flow**: Bearer token via Authorization header

## Development Commands

### Backend (Maven)
```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Build Docker image (native)
./mvnw spring-boot:build-image -Pnative

# Create native executable
./mvnw native:compile -Pnative
```

### Frontend (npm)
```bash
# Install dependencies
npm install

# Development server (auto-opens browser on port 3000)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## Architecture Overview

### Authentication System

The system uses JWT (JSON Web Tokens) for stateless authentication:

**Backend Components**:
- `JwtUtil`: JWT generation and validation utility
- `JwtUser`: User details implementation with roles and permissions
- `UserDetailsServiceImpl`: Loads user information from database
- `JwtAuthenticationFilter`: OncePerRequestFilter for token validation
- `JwtAuthenticationEntryPoint`: Handles 401 unauthorized responses
- `SecurityConfig`: Spring Security configuration with JWT integration
- `LoginController`: Authentication endpoints (/api/auth/login, /api/auth/logout)

**Frontend Components**:
- `src/services/auth.js`: Authentication service with token management
- `src/utils/request.js`: Axios instance with request/response interceptors
- `src/views/Login.vue`: Login page component
- Router navigation guard for authentication checks

**Authentication Flow**:
1. User submits credentials to `/api/auth/login`
2. Backend validates credentials with BCrypt
3. JWT token generated with username and userId claims
4. Frontend stores token in localStorage
5. Subsequent requests include `Authorization: Bearer {token}` header
6. JwtAuthenticationFilter validates token on each request
7. 401 responses trigger token clear and redirect to login

### Backend Structure
- Standard Spring Boot MVC architecture
- Controllers in `src/main/java/org/qinfeng/backend/controller/`
- Entities follow JPA conventions with MyBatis-Plus
- Services contain business logic
- Database connection configured for remote MySQL instance

### Frontend Structure
- Vue 3 Composition API
- Main layout components: Layout, Sidebar, Header
- Views: Dashboard, UserManagement, RoleManagement, AccessLog, OperationLog
- Router configured in `src/router/index.js`
- Uses Element Plus UI components throughout

### Database Schema
The system implements a complete RBAC model with these core tables:
- `sys_user` - User accounts
- `sys_role` - Role definitions
- `sys_permission` - Permission codes
- `sys_menu` - Menu hierarchy
- `sys_user_role` - User-role assignments
- `sys_role_permission` - Role-permission assignments
- `sys_role_menu` - Role-menu assignments

## Key Configuration

### JWT Configuration
- **Secret Key**: Configured in `application.yml` (jwt.secret)
- **Token Expiration**: Configurable in milliseconds (jwt.expiration, default: 3600000)
- **Login Endpoint**: `/api/auth/login`
- **Logout Endpoint**: `/api/auth/logout`

### Database Configuration
- **Host**: 
- **Database**: huaizhen
- **Username**: root
- **Password**: 

### Application Ports
- **Frontend Dev Server**: 3000 (Vite)
- **Backend Server**: 8080 (Spring Boot default)

### Project Structure
- Parent POM at root coordinates both modules
- Frontend uses `@` alias for `src/` directory
- Backend follows standard Maven directory structure

## Development Notes

- The system uses MySQL with auto-incrementing BIGINT primary keys
- All tables include `created_at` and `updated_at` timestamps
- User passwords are stored as BCrypt hashes (not plaintext)
- Status fields use TINYINT (0 = inactive, 1 = active)
- Unique constraints on username and role code fields
- Menu supports hierarchical structure with parent_id field
- JWT tokens are stateless - no Redis or session storage required
- User roles and permissions are loaded on each authentication
- All protected API endpoints require valid JWT token in Authorization header

## Database Setup

The initial database schema is defined in `db/mysql.sql`. Before running the application:
1. Create the MySQL database
2. Execute the schema script
3. Ensure the database user has appropriate privileges