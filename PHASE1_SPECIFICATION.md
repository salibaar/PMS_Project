# 📋 Phase 1 Technical Specification

## Foundation & Core CRUD Implementation

**Duration:** 2-3 tuần  
**Priority:** 🔴 Critical  
**Status:** 📝 Planning

---

## 🎯 Objectives

Phase 1 xây dựng nền tảng vững chắc cho hệ thống PMS với đầy đủ CRUD operations và cấu trúc dữ liệu phân cấp.

### Key Results:

1. ✅ Complete CRUD API cho Plans, Objectives, KeyResults
2. ✅ Database schema với relationships
3. ✅ Frontend với danh sách, chi tiết, và form
4. ✅ Pagination, search, filter cơ bản
5. ✅ Documentation đầy đủ

---

## 🗄️ Database Schema Design

### Entity Relationship Diagram

```
┌─────────────────┐
│     Users       │
│─────────────────│
│ id (PK)         │
│ username        │
│ email           │
│ password_hash   │
│ role            │
│ created_at      │
└─────────────────┘
        │ 1
        │ creates
        │ *
┌─────────────────┐
│     Plans       │
│─────────────────│
│ id (PK)         │
│ year            │
│ title           │
│ description     │
│ status          │
│ created_by (FK) │
│ created_at      │
│ updated_at      │
│ deleted_at      │
└─────────────────┘
        │ 1
        │ has
        │ *
┌─────────────────┐
│   Objectives    │
│─────────────────│
│ id (PK)         │
│ plan_id (FK)    │
│ parent_id (FK)  │◄─┐
│ content         │  │
│ is_breakthrough │  │
│ order_index     │  │
│ status          │  │
│ progress        │  │
│ created_by (FK) │  │
│ created_at      │  │
│ updated_at      │  │
│ deleted_at      │  │
└─────────────────┘  │
        │ 1          │
        │ has        │self-referencing
        │ *          │for parent-child
┌─────────────────┐  │
│   KeyResults    │  │
│─────────────────│  │
│ id (PK)         │  │
│ objective_id(FK)│──┘
│ description     │
│ target_value    │
│ current_value   │
│ unit            │
│ status          │
│ created_at      │
│ updated_at      │
└─────────────────┘
```

### SQL Schema (PostgreSQL)

```sql
-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'MANAGER', 'USER'))
);

-- Plans table
CREATE TABLE plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    year INTEGER NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT plans_status_check CHECK (status IN ('DRAFT', 'ACTIVE', 'COMPLETED', 'ARCHIVED')),
    CONSTRAINT plans_year_check CHECK (year >= 2020 AND year <= 2100)
);

-- Objectives table (with self-referencing for hierarchy)
CREATE TABLE objectives (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    plan_id UUID NOT NULL REFERENCES plans(id) ON DELETE CASCADE,
    parent_id UUID NULL REFERENCES objectives(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    is_breakthrough BOOLEAN NOT NULL DEFAULT FALSE,
    order_index INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    progress INTEGER DEFAULT 0,
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT objectives_content_length CHECK (LENGTH(content) >= 10 AND LENGTH(content) <= 500),
    CONSTRAINT objectives_status_check CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'BLOCKED')),
    CONSTRAINT objectives_progress_check CHECK (progress >= 0 AND progress <= 100)
);

-- KeyResults table
CREATE TABLE key_results (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    objective_id UUID NOT NULL REFERENCES objectives(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    target_value DECIMAL(10,2) NOT NULL,
    current_value DECIMAL(10,2) DEFAULT 0,
    unit VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT key_results_description_length CHECK (LENGTH(description) >= 5 AND LENGTH(description) <= 300),
    CONSTRAINT key_results_status_check CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED'))
);

-- Indexes for performance
CREATE INDEX idx_plans_year ON plans(year);
CREATE INDEX idx_plans_status ON plans(status);
CREATE INDEX idx_plans_created_by ON plans(created_by);
CREATE INDEX idx_plans_deleted_at ON plans(deleted_at);

CREATE INDEX idx_objectives_plan_id ON objectives(plan_id);
CREATE INDEX idx_objectives_parent_id ON objectives(parent_id);
CREATE INDEX idx_objectives_status ON objectives(status);
CREATE INDEX idx_objectives_created_by ON objectives(created_by);
CREATE INDEX idx_objectives_deleted_at ON objectives(deleted_at);

CREATE INDEX idx_key_results_objective_id ON key_results(objective_id);
CREATE INDEX idx_key_results_status ON key_results(status);

-- Trigger to update updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_plans_updated_at BEFORE UPDATE ON plans
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_objectives_updated_at BEFORE UPDATE ON objectives
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_key_results_updated_at BEFORE UPDATE ON key_results
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

---

## 🏗️ Backend Architecture

### 1. Entity Models

#### Plan.java
```java
package com.gov.pms.entity;

import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "plans")
@Data
@SQLDelete(sql = "UPDATE plans SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Plan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status = PlanStatus.DRAFT;
    
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Objective> objectives = new ArrayList<>();
    
    public enum PlanStatus {
        DRAFT, ACTIVE, COMPLETED, ARCHIVED
    }
}
```

#### Objective.java
```java
package com.gov.pms.entity;

import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "objectives")
@Data
@SQLDelete(sql = "UPDATE objectives SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Objective {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Objective parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Objective> children = new ArrayList<>();
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "is_breakthrough", nullable = false)
    private Boolean isBreakthrough = false;
    
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjectiveStatus status = ObjectiveStatus.NOT_STARTED;
    
    @Column(nullable = false)
    private Integer progress = 0;
    
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @OneToMany(mappedBy = "objective", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeyResult> keyResults = new ArrayList<>();
    
    public enum ObjectiveStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED, BLOCKED
    }
}
```

#### KeyResult.java
```java
package com.gov.pms.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "key_results")
@Data
public class KeyResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_id", nullable = false)
    private Objective objective;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "target_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetValue;
    
    @Column(name = "current_value", precision = 10, scale = 2)
    private BigDecimal currentValue = BigDecimal.ZERO;
    
    @Column(length = 50)
    private String unit;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeyResultStatus status = KeyResultStatus.NOT_STARTED;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum KeyResultStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
}
```

### 2. Repository Layer

```java
package com.gov.pms.repository;

import com.gov.pms.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
    
    Page<Plan> findByYear(Integer year, Pageable pageable);
    
    Page<Plan> findByStatus(Plan.PlanStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Plan p WHERE p.year = ?1 AND p.status = ?2")
    List<Plan> findActivePlansByYear(Integer year, Plan.PlanStatus status);
    
    @Query("SELECT p FROM Plan p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Plan> searchByTitle(String keyword, Pageable pageable);
}
```

```java
package com.gov.pms.repository;

import com.gov.pms.entity.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, UUID> {
    
    List<Objective> findByPlanIdAndParentIsNull(UUID planId);
    
    List<Objective> findByParentId(UUID parentId);
    
    List<Objective> findByPlanIdAndIsBreakthroughTrue(UUID planId);
    
    @Query("SELECT o FROM Objective o LEFT JOIN FETCH o.keyResults WHERE o.id = ?1")
    Objective findByIdWithKeyResults(UUID id);
}
```

### 3. Service Layer

```java
package com.gov.pms.service;

import com.gov.pms.dto.PlanDTO;
import com.gov.pms.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PlanService {
    
    Plan createPlan(PlanDTO planDTO, UUID userId);
    
    Plan updatePlan(UUID id, PlanDTO planDTO);
    
    void deletePlan(UUID id);
    
    Plan getPlanById(UUID id);
    
    Page<Plan> getAllPlans(Pageable pageable);
    
    Page<Plan> getPlansByYear(Integer year, Pageable pageable);
    
    Page<Plan> searchPlans(String keyword, Pageable pageable);
}
```

---

## 🌐 REST API Endpoints

### Plans API

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/v1/plans` | Danh sách tất cả plans | - | Page<PlanDTO> |
| GET | `/api/v1/plans/{id}` | Chi tiết plan | - | PlanDTO |
| POST | `/api/v1/plans` | Tạo plan mới | CreatePlanRequest | PlanDTO |
| PUT | `/api/v1/plans/{id}` | Cập nhật plan | UpdatePlanRequest | PlanDTO |
| DELETE | `/api/v1/plans/{id}` | Xóa plan (soft delete) | - | 204 No Content |
| GET | `/api/v1/plans?year={year}` | Plans theo năm | - | Page<PlanDTO> |
| GET | `/api/v1/plans/search?q={keyword}` | Tìm kiếm plans | - | Page<PlanDTO> |

### Objectives API

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/v1/objectives` | Danh sách objectives | - | Page<ObjectiveDTO> |
| GET | `/api/v1/objectives/{id}` | Chi tiết objective | - | ObjectiveDTO |
| POST | `/api/v1/objectives` | Tạo objective | CreateObjectiveRequest | ObjectiveDTO |
| PUT | `/api/v1/objectives/{id}` | Cập nhật objective | UpdateObjectiveRequest | ObjectiveDTO |
| DELETE | `/api/v1/objectives/{id}` | Xóa objective | - | 204 No Content |
| GET | `/api/v1/plans/{planId}/objectives` | Objectives của plan | - | List<ObjectiveDTO> |
| GET | `/api/v1/objectives/{id}/children` | Child objectives | - | List<ObjectiveDTO> |

### KeyResults API

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/v1/key-results/{id}` | Chi tiết key result | - | KeyResultDTO |
| POST | `/api/v1/key-results` | Tạo key result | CreateKeyResultRequest | KeyResultDTO |
| PUT | `/api/v1/key-results/{id}` | Cập nhật key result | UpdateKeyResultRequest | KeyResultDTO |
| DELETE | `/api/v1/key-results/{id}` | Xóa key result | - | 204 No Content |
| GET | `/api/v1/objectives/{objectiveId}/key-results` | Key results của objective | - | List<KeyResultDTO> |

---

## 💻 Frontend Components

### Component Structure

```
frontend/src/
├── components/
│   ├── common/
│   │   ├── Button.js
│   │   ├── Input.js
│   │   ├── Modal.js
│   │   ├── Table.js
│   │   ├── Pagination.js
│   │   └── LoadingSpinner.js
│   ├── layout/
│   │   ├── Header.js
│   │   ├── Sidebar.js
│   │   ├── Footer.js
│   │   └── Layout.js
│   ├── plans/
│   │   ├── PlanList.js
│   │   ├── PlanCard.js
│   │   ├── PlanDetail.js
│   │   ├── CreatePlanForm.js
│   │   └── EditPlanForm.js
│   ├── objectives/
│   │   ├── ObjectiveList.js
│   │   ├── ObjectiveCard.js
│   │   ├── ObjectiveTree.js
│   │   ├── CreateObjectiveForm.js
│   │   └── EditObjectiveForm.js
│   └── keyresults/
│       ├── KeyResultList.js
│       ├── KeyResultCard.js
│       └── KeyResultForm.js
├── pages/
│   ├── Dashboard.js
│   ├── PlansPage.js
│   ├── PlanDetailPage.js
│   ├── CreatePlanPage.js
│   └── NotFoundPage.js
├── services/
│   ├── api.js
│   ├── planService.js
│   ├── objectiveService.js
│   └── keyResultService.js
├── context/
│   └── AppContext.js
├── hooks/
│   ├── usePlans.js
│   ├── useObjectives.js
│   └── useKeyResults.js
└── utils/
    ├── formatters.js
    ├── validators.js
    └── constants.js
```

### Key Components Examples

#### PlanList.js (Component chính)

```jsx
import React, { useState, useEffect } from 'react';
import { fetchPlans, deletePlan } from '../services/planService';
import PlanCard from './PlanCard';
import Pagination from '../common/Pagination';
import LoadingSpinner from '../common/LoadingSpinner';

const PlanList = () => {
    const [plans, setPlans] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        loadPlans();
    }, [page]);

    const loadPlans = async () => {
        setLoading(true);
        try {
            const response = await fetchPlans(page, 10);
            setPlans(response.content);
            setTotalPages(response.totalPages);
        } catch (error) {
            console.error('Error loading plans:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Bạn có chắc muốn xóa kế hoạch này?')) {
            try {
                await deletePlan(id);
                loadPlans();
            } catch (error) {
                console.error('Error deleting plan:', error);
            }
        }
    };

    if (loading) return <LoadingSpinner />;

    return (
        <div className="plan-list">
            <div className="header">
                <h2>📋 Danh Sách Kế Hoạch</h2>
                <button onClick={() => window.location.href = '/plans/create'}>
                    ➕ Tạo Kế Hoạch Mới
                </button>
            </div>
            
            <div className="plans-grid">
                {plans.map(plan => (
                    <PlanCard 
                        key={plan.id} 
                        plan={plan} 
                        onDelete={handleDelete}
                    />
                ))}
            </div>
            
            <Pagination 
                currentPage={page}
                totalPages={totalPages}
                onPageChange={setPage}
            />
        </div>
    );
};

export default PlanList;
```

---

## ✅ Acceptance Criteria

### Must Have (P0 - Critical)

- [ ] User có thể tạo, xem, sửa, xóa Plans
- [ ] User có thể tạo, xem, sửa, xóa Objectives
- [ ] User có thể tạo, xem, sửa, xóa Key Results
- [ ] Objectives có thể có parent-child relationship
- [ ] Pagination hoạt động trên tất cả list pages
- [ ] Form validation hoạt động đúng
- [ ] Soft delete cho Plans và Objectives
- [ ] API trả về proper error messages
- [ ] Loading states cho tất cả async operations
- [ ] Responsive design (desktop & mobile)

### Should Have (P1 - High)

- [ ] Search functionality cho Plans
- [ ] Filter by year, status
- [ ] Sort by date, title
- [ ] Breadcrumb navigation
- [ ] Confirmation modals cho delete actions
- [ ] Toast notifications cho success/error
- [ ] Empty states với helpful messages

### Nice to Have (P2 - Medium)

- [ ] Drag & drop để reorder objectives
- [ ] Keyboard shortcuts
- [ ] Undo/Redo functionality
- [ ] Bulk actions (delete multiple)
- [ ] Quick filters
- [ ] Recently viewed items

---

## 🧪 Testing Strategy

### Backend Tests

```java
@SpringBootTest
class PlanServiceTest {
    
    @Autowired
    private PlanService planService;
    
    @Test
    void testCreatePlan() {
        PlanDTO dto = new PlanDTO();
        dto.setYear(2026);
        dto.setTitle("Test Plan");
        dto.setDescription("Test Description");
        
        Plan plan = planService.createPlan(dto, UUID.randomUUID());
        
        assertNotNull(plan.getId());
        assertEquals(2026, plan.getYear());
        assertEquals("Test Plan", plan.getTitle());
    }
    
    @Test
    void testSoftDelete() {
        Plan plan = createTestPlan();
        planService.deletePlan(plan.getId());
        
        assertThrows(EntityNotFoundException.class, () -> {
            planService.getPlanById(plan.getId());
        });
    }
}
```

### Frontend Tests

```jsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import PlanList from './PlanList';
import * as planService from '../services/planService';

jest.mock('../services/planService');

describe('PlanList', () => {
    test('renders plan list', async () => {
        planService.fetchPlans.mockResolvedValue({
            content: [
                { id: '1', title: 'Plan 2026', year: 2026 }
            ],
            totalPages: 1
        });

        render(<PlanList />);
        
        await waitFor(() => {
            expect(screen.getByText('Plan 2026')).toBeInTheDocument();
        });
    });

    test('deletes plan on confirmation', async () => {
        // Test implementation
    });
});
```

---

## 📦 Deliverables Checklist

### Backend Deliverables:

- [ ] Database migration scripts
- [ ] Entity models với relationships
- [ ] Repository interfaces
- [ ] Service implementations
- [ ] REST Controllers
- [ ] DTO classes
- [ ] Exception handling
- [ ] Validation annotations
- [ ] Unit tests (>70% coverage)
- [ ] Integration tests
- [ ] API documentation (Swagger)

### Frontend Deliverables:

- [ ] Component library
- [ ] Page components
- [ ] API service layer
- [ ] Context/State management
- [ ] Routing setup
- [ ] Forms với validation
- [ ] Loading & error states
- [ ] Responsive CSS
- [ ] Component tests
- [ ] E2E tests (critical paths)

### Documentation:

- [ ] API endpoint documentation
- [ ] Database schema diagram
- [ ] Component documentation
- [ ] Setup instructions
- [ ] Deployment guide
- [ ] Changelog

---

## 🚀 Deployment Plan

### Development Environment:

```bash
# Start development servers
cd backend && mvn spring-boot:run
cd frontend && npm start
```

### Staging/Production:

```bash
# Build & deploy with Docker
docker-compose -f docker-compose.prod.yml up --build -d
```

---

## 📞 Support

Nếu gặp khó khăn trong quá trình implement Phase 1:

1. Xem lại specification này
2. Check DEVELOPMENT_ROADMAP.md
3. Tạo issue với label `phase-1`
4. Tag @copilot trong PR comments

---

**Version:** 1.0  
**Last Updated:** December 2024  
**Status:** 🟡 Ready for Implementation
