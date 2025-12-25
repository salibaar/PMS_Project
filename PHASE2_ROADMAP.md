# Phase 2: Advanced Features - Implementation Roadmap

## ✅ COMPLETED (Step 1)

### API Documentation
- **Swagger/OpenAPI 3.0** fully integrated
- All 24 endpoints documented with Vietnamese descriptions
- Interactive API testing available at `http://localhost:8080/swagger-ui.html`
- Complete with request/response examples

### User Management Foundation
- **User Entity**: Includes roles (USER, MANAGER, ADMIN), soft delete, audit fields
- **UserRepository**: CRUD operations with username/email lookups
- **JWT Token Provider**: Token generation and validation using HS512
- **Authentication DTOs**: LoginRequest, RegisterRequest, UserDTO, JwtResponse
- **Dependencies**: Spring Security 2.7.x, JJWT 0.11.5

---

## 🚧 IN PROGRESS (Steps 2-5)

### Step 2: Complete Authentication System (Next Priority)

#### Backend Tasks:
1. **UserDetailsService Implementation**
   ```java
   // Load user from database for authentication
   @Service
   public class CustomUserDetailsService implements UserDetailsService
   ```

2. **JWT Authentication Filter**
   ```java
   // Extract and validate JWT tokens from requests
   public class JwtAuthenticationFilter extends OncePerRequestFilter
   ```

3. **Security Configuration**
   ```java
   @Configuration
   @EnableWebSecurity
   public class SecurityConfig extends WebSecurityConfigurerAdapter
   ```
   - Public endpoints: `/api/v1/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`
   - Protected endpoints: All CRUD operations
   - CORS configuration for frontend
   - Password encryption with BCrypt

4. **Authentication Controller**
   ```java
   @RestController
   @RequestMapping("/api/v1/auth")
   public class AuthController {
       POST /register - Register new user
       POST /login - Login and get JWT token
       GET /me - Get current user info
       POST /logout - Logout (optional, client-side)
   }
   ```

5. **User Service**
   ```java
   @Service
   public class UserService {
       createUser(RegisterRequest)
       updateUser(UUID id, UserDTO)
       deleteUser(UUID id)
       getUserById(UUID id)
       getAllUsers(Pageable)
       assignRole(UUID userId, Role role)
   }
   ```

#### Frontend Tasks:
1. **Authentication Context**
   ```javascript
   // React Context for managing auth state
   const AuthContext = createContext();
   ```

2. **Login Page**
   - Form with username/email and password
   - Error handling for failed login
   - Redirect to dashboard on success

3. **Register Page**
   - Form with username, email, password, full name
   - Password confirmation
   - Terms acceptance

4. **Protected Routes**
   - Wrap private routes with authentication check
   - Redirect to login if not authenticated

5. **User Profile Page**
   - Display current user information
   - Edit profile functionality
   - Change password feature

---

### Step 3: Comments & Evaluations System

#### Backend:
1. **Comment Entity**
   ```java
   @Entity
   public class Comment {
       UUID id;
       UUID objectiveId;  // Comment on objective
       UUID userId;       // Who commented
       String content;
       UUID parentId;     // For nested comments
       LocalDateTime createdAt;
   }
   ```

2. **Evaluation Entity**
   ```java
   @Entity
   public class Evaluation {
       UUID id;
       UUID objectiveId;
       UUID userId;
       Integer rating;     // 1-5 stars
       String feedback;
       EvaluationStatus status; // PENDING, APPROVED, REJECTED
       LocalDateTime createdAt;
   }
   ```

3. **Comment/Evaluation Services & Controllers**
   - CRUD operations
   - List comments by objective
   - Calculate average ratings
   - Notification on new comments

#### Frontend:
1. **Comment Section Component**
   - Display comments list
   - Add new comment form
   - Reply to comments (nested)
   - Edit/Delete own comments

2. **Evaluation Component**
   - Star rating widget
   - Feedback textarea
   - Submit evaluation
   - Display evaluation history

3. **Activity Timeline**
   - Show recent comments and evaluations
   - Filter by user/objective
   - Real-time updates (optional)

---

### Step 4: Advanced Search & Filtering

#### Backend:
1. **Full-Text Search**
   ```java
   // Using PostgreSQL full-text search or Hibernate Search
   @Query("SELECT p FROM Plan p WHERE " +
          "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
   Page<Plan> fullTextSearch(String keyword, Pageable pageable);
   ```

2. **Advanced Filtering API**
   ```java
   GET /api/v1/plans/filter?year=2025&status=ACTIVE&hasBreakthrough=true
   GET /api/v1/objectives/filter?progress>50&status=IN_PROGRESS
   ```

3. **Export Functionality**
   ```java
   @Service
   public class ExportService {
       byte[] exportToExcel(List<Plan> plans)
       byte[] exportToPDF(Plan plan)
   }
   
   GET /api/v1/plans/export/excel
   GET /api/v1/plans/{id}/export/pdf
   ```

#### Frontend:
1. **Advanced Search Component**
   - Search bar with autocomplete
   - Filter dropdowns (year, status, etc.)
   - Date range picker
   - Save search filters

2. **Export Buttons**
   - Download as Excel button
   - Download as PDF button
   - Custom report configuration

---

### Step 5: Notifications & Progress Tracking

#### Backend:
1. **Notification Entity**
   ```java
   @Entity
   public class Notification {
       UUID id;
       UUID userId;
       String title;
       String message;
       NotificationType type; // COMMENT, EVALUATION, DEADLINE
       Boolean read;
       LocalDateTime createdAt;
   }
   ```

2. **Notification Service**
   - Create notifications on events
   - Mark as read
   - Get unread count
   - Get notification history

3. **Progress Calculation**
   - Auto-calculate objective progress from key results
   - Update plan progress from objectives
   - Status workflow automation

#### Frontend:
1. **Notification Bell**
   - Icon with unread count badge
   - Dropdown with recent notifications
   - Mark all as read button

2. **Progress Dashboard**
   - Progress charts (Chart.js or Recharts)
   - Completion statistics
   - Trend analysis
   - KPI cards

---

## 📊 Implementation Timeline

| Step | Feature | Estimated Time | Status |
|------|---------|---------------|---------|
| 1 | API Documentation & User Foundation | 2-3 days | ✅ DONE |
| 2 | Complete Authentication System | 3-4 days | 🚧 Next |
| 3 | Comments & Evaluations | 4-5 days | ⏳ Pending |
| 4 | Advanced Search & Export | 3-4 days | ⏳ Pending |
| 5 | Notifications & Progress | 3-4 days | ⏳ Pending |

**Total Estimated Time:** 15-20 days (2-3 weeks)

---

## 🔧 Technical Stack Additions

### Backend:
- ✅ Spring Security 2.7.x
- ✅ JJWT 0.11.5 (JWT tokens)
- ✅ SpringDoc OpenAPI 1.7.0
- 🚧 Apache POI (Excel export)
- 🚧 iText/Flying Saucer (PDF export)
- 🚧 Hibernate Search (full-text search, optional)

### Frontend:
- 🚧 JWT token storage (localStorage/sessionStorage)
- 🚧 Protected route wrapper
- 🚧 Chart.js or Recharts (for dashboards)
- 🚧 React Context API (auth state)
- 🚧 File download handling

---

## 🎯 Success Criteria for Phase 2

- ✅ API documentation accessible and complete
- [ ] Users can register and login
- [ ] JWT authentication works on all endpoints
- [ ] Users can comment on objectives
- [ ] Users can rate/evaluate objectives
- [ ] Advanced search returns accurate results
- [ ] Export to Excel/PDF works correctly
- [ ] Notifications are sent on relevant events
- [ ] Progress is calculated automatically
- [ ] Frontend has login/register pages
- [ ] Frontend displays user info
- [ ] Frontend shows comments and evaluations

---

## 📝 Next Immediate Actions

1. **Complete UserDetailsService** - Load users for authentication
2. **Create JwtAuthenticationFilter** - Extract JWT from requests
3. **Configure Security** - Define public/protected endpoints
4. **Create AuthController** - Login and register endpoints
5. **Test authentication** - Verify JWT flow works end-to-end
6. **Create Login UI** - Frontend login page
7. **Update existing API calls** - Add JWT token to headers

---

## 💡 Notes

- **Backward Compatibility**: Phase 1 endpoints remain accessible for testing
- **Migration**: Existing Plans/Objectives need `created_by` user assignment (can default to admin user)
- **Security**: All sensitive endpoints protected after Step 2 completion
- **Testing**: Manual testing recommended after each step
- **Documentation**: Swagger UI updated automatically with new endpoints

---

**Status:** Phase 2 Step 1 Complete - Continuing with Step 2
**Last Updated:** December 25, 2025
