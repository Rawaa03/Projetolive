# Role-Based Dashboard System - Complete Implementation Guide

## 📋 Overview

A comprehensive, role-based dashboard system for the Olive Collection Management Application with three distinct user interfaces for **ADMIN**, **RESPONSABLE** (Supervisor), and **AGRICULTEUR** (Farmer) roles.

## 🏗️ Architecture

### Backend (Spring Boot)

**Files Created:**
- **Dashboard DTOs** (`/src/main/java/com/example/demo/dto/dashboard/`)
  - `AgriculteurDashboardDTO.java` - Farmer's dashboard data structure
  - `ResponsableDashboardDTO.java` - Supervisor's dashboard data structure
  - `AdminDashboardDTO.java` - Admin's dashboard data structure
  - Supporting DTOs for cards, charts, and metrics

- **Dashboard Service** (`/src/main/java/com/example/demo/service/`)
  - `DashboardService.java` - Interface
  - `DashboardServiceImpl.java` - Implementation with data aggregation logic

- **Dashboard Controller** (`/src/main/java/com/example/demo/controller/`)
  - `DashboardController.java` - RESTful API endpoints

### Frontend (Angular)

**Directory Structure:**
```
frontend/src/app/dashboard/
├── layout/
│   ├── dashboard-layout.component.ts
│   ├── dashboard-layout.component.html
│   └── dashboard-layout.component.css
│
├── shared/
│   ├── services/
│   │   └── dashboard.service.ts
│   └── components/
│       ├── dashboard-card/
│       ├── stat-chart/
│       ├── progress-bar/
│       ├── data-table/
│       ├── alert-badge/
│       ├── verger-card/
│       └── tournee-card/
│
├── agriculteur-dashboard/
│   ├── agriculteur-dashboard.component.ts
│   ├── agriculteur-dashboard.component.html
│   └── agriculteur-dashboard.component.css
│
├── responsable-dashboard/
│   ├── responsable-dashboard.component.ts
│   ├── responsable-dashboard.component.html
│   └── responsable-dashboard.component.css
│
├── admin-dashboard/
│   ├── admin-dashboard.component.ts
│   ├── admin-dashboard.component.html
│   └── admin-dashboard.component.css
│
├── dashboard.module.ts
└── dashboard-routing.module.ts
```

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api/dashboard
```

### Endpoints

#### 1. Agriculteur Dashboard
```
GET /api/dashboard/agriculteur
Authorization: Bearer {token}
@PreAuthorize("hasRole('AGRICULTEUR')")
```

**Response:** `AgriculteurDashboardDTO`
- Vergers owned by the farmer
- Total production and progress
- Active collection tours
- Alerts and tasks

---

#### 2. Responsable Dashboard
```
GET /api/dashboard/responsable
Authorization: Bearer {token}
@PreAuthorize("hasRole('RESPONSABLE')")
```

**Response:** `ResponsableDashboardDTO`
- Assigned vergers and status
- Worker management data
- Collection progress across vergers
- Tour/Tournee status
- Production vs targets
- Efficiency metrics

---

#### 3. Admin Dashboard
```
GET /api/dashboard/admin
Authorization: Bearer {token}
@PreAuthorize("hasRole('ADMIN')")
```

**Response:** `AdminDashboardDTO`
- System-wide user statistics
- All vergers overview
- Resource utilization
- System alerts
- Performance benchmarking

---

#### 4. Current User Dashboard (Role-Aware)
```
GET /api/dashboard/current
Authorization: Bearer {token}
```

**Response:** Returns appropriate dashboard based on user role

## 🎯 Dashboard Features

### AGRICULTEUR (Farmer) Dashboard

**Display Statistics:**
- ✅ Number of vergers (orchards) and their status
- ✅ Current harvest collection progress
- ✅ Total olive production for current season
- ✅ Active tours/tournees assigned
- ✅ Visual progress charts by verger
- ✅ Alerts and pending tasks

**Key Components:**
- Summary cards showing key metrics
- Verger cards with individual status
- Progress bars for each verger
- Active tours list
- Alert notifications

---

### RESPONSABLE (Supervisor) Dashboard

**Display Statistics:**
- ✅ All assigned vergers with status
- ✅ Team worker management
- ✅ Aggregate collection progress
- ✅ Tour status (planned, ongoing, completed)
- ✅ Production targets vs actual
- ✅ Worker efficiency and KPI metrics

**Key Components:**
- Summary cards with aggregate metrics
- Worker list with status and specialties
- Tour status breakdown (3-column)
- Verger production comparison
- Efficiency metrics dashboard

---

### ADMIN Dashboard

**Display Statistics:**
- ✅ User management overview (by role)
- ✅ Complete vergers inventory
- ✅ Resource utilization (vehicles, bins)
- ✅ System-wide alerts and issues
- ✅ Performance benchmarking
- ✅ Top performers and bottleneck analysis

**Key Components:**
- System-wide summary cards
- User role distribution
- Verger status distribution
- Resource utilization charts
- Top performing vergers
- System statistics summary

## 🔧 Installation & Setup

### Backend Setup

1. **Build the project:**
```bash
cd /vercel/share/v0-project
mvn clean install
```

2. **Run the Spring Boot application:**
```bash
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`

### Frontend Setup

1. **Create Angular project (if not exists):**
```bash
ng new frontend
cd frontend
```

2. **Install dependencies:**
```bash
npm install ng2-charts chart.js
```

3. **Copy dashboard module files** to your Angular project at:
```
src/app/dashboard/
```

4. **Import DashboardModule** in your main `app.module.ts`:
```typescript
import { DashboardModule } from './dashboard/dashboard.module';

@NgModule({
  imports: [
    // ... other imports
    DashboardModule
  ]
})
export class AppModule { }
```

5. **Add routing** to your `app-routing.module.ts`:
```typescript
const routes: Routes = [
  {
    path: 'dashboard',
    loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule)
  }
];
```

6. **Update API URL** in `dashboard.service.ts`:
```typescript
private apiUrl = 'http://localhost:8080/api/dashboard';
```

7. **Run the Angular development server:**
```bash
ng serve
```

The frontend will be available at `http://localhost:4200/dashboard`

## 🔐 Authentication & Authorization

### Backend Security
- All dashboard endpoints are protected by Spring Security
- Role-based access control using `@PreAuthorize` annotations
- User identity extracted from authenticated principal
- Data is filtered based on user ownership (e.g., agriculteur only sees their vergers)

### Frontend Guards (Ready to Implement)
- Route guards can be added to prevent unauthorized access
- User role is stored in localStorage as `userInfo`

### Token Management
Ensure your authentication system sets:
```
localStorage.setItem('userInfo', JSON.stringify({
  id: userId,
  email: userEmail,
  role: userRole,
  prenom: firstName,
  nom: lastName
}));
localStorage.setItem('token', jwtToken);
```

## 📊 Data Flow

```
┌─────────────────┐
│  Angular UI     │
└────────┬────────┘
         │ HTTP GET /api/dashboard/{role}
         │
┌────────▼────────────────┐
│  DashboardController    │
│  (Handles routing)      │
└────────┬─────────────────┘
         │
┌────────▼────────────────────┐
│  DashboardService           │
│  (Aggregates data)          │
└────────┬────────────────────┘
         │
┌────────┴──────────────────────────────────┐
│              Repositories                 │
├──────────────────────────────────────────┤
│ • UtilisateurRepository                  │
│ • VergerRepository                       │
│ • CollecteRepository                     │
│ • TourneeRepository                      │
│ • AlerteRepository                       │
│ • RessourceRepository                    │
└─────────────────────────────────────────┘
```

## 🎨 Styling & UI Components

### Color Scheme
- **Primary:** #2c3e50 (Dark Blue-Gray)
- **Accent:** #3498db (Sky Blue)
- **Success:** #27ae60 (Green)
- **Warning:** #f39c12 (Orange)
- **Danger:** #e74c3c (Red)
- **Neutral:** #95a5a6 (Gray)

### Responsive Design
- Desktop: Full layout with 2-column grid
- Tablet: Adjusted grid with 1-2 columns
- Mobile: Single column, collapsible sidebar

### Reusable Components
1. **DashboardCard** - Stat cards with trends
2. **ProgressBar** - Visual progress indicators
3. **StatChart** - Charts (pie, bar, line)
4. **DataTable** - Sortable data tables
5. **AlertBadge** - Alert notifications
6. **VergerCard** - Orchard details
7. **TourneeCard** - Tour information

## 📈 Data Models

### AgriculteurDashboardDTO
```json
{
  "utilisateurId": "string",
  "nom": "string",
  "prenom": "string",
  "vergers": [VergerCardDTO],
  "totalVergers": 5,
  "vergerActifs": 4,
  "totalProductionKg": 15000,
  "targetProductionKg": 20000,
  "productionPercentage": 75.0,
  "activeTournees": [TourneeCardDTO],
  "totalActiveTournees": 3,
  "vergerProgressData": [VergerProgressDTO],
  "alerts": [AlertDTO],
  "totalArbreCollecte": 2500
}
```

### ResponsableDashboardDTO
```json
{
  "utilisateurId": "string",
  "nom": "string",
  "prenom": "string",
  "assignedVergers": [VergerCardDTO],
  "totalAssignedVergers": 10,
  "workers": [WorkerCardDTO],
  "totalWorkers": 25,
  "activeWorkers": 22,
  "totalCollectionKg": 50000,
  "targetCollectionKg": 60000,
  "collectionPercentage": 83.3,
  "planifiedTournees": 5,
  "ongoingTournees": 2,
  "completedTournees": 15,
  "recentTournees": [TourneeCardDTO],
  "vergerProduction": [VergerProductionDTO],
  "averageKgPerWorker": 2272.7,
  "efficiencyRating": 0.833,
  "efficiencyMetrics": [EfficiencyMetricDTO]
}
```

### AdminDashboardDTO
```json
{
  "totalUsers": 50,
  "usersByRole": {
    "ADMIN": 2,
    "RESPONSABLE": 5,
    "AGRICULTEUR": 20,
    "TRAVAILLEUR": 23
  },
  "activeUsers": 45,
  "inactiveUsers": 5,
  "totalVergers": 100,
  "allVergers": [VergerCardDTO],
  "vergersByStatus": {
    "ACTIF": 85,
    "INACTIF": 15
  },
  "resourceUtilization": ResourceUtilizationDTO,
  "totalSystemProductionKg": 500000,
  "totalSystemCollectionTours": 200,
  "topPerformingVergers": [TopPerformerDTO],
  "systemAlerts": [AlertDTO],
  "totalAlerts": 5
}
```

## 🔄 Future Enhancements

1. **Real-time Updates** - WebSocket integration for live data
2. **Export Reports** - PDF/Excel export functionality
3. **Custom Filters** - Date range, status filters
4. **Role Customization** - Configurable dashboard widgets
5. **Performance Optimization** - Data caching, lazy loading
6. **Advanced Charts** - More chart types and interactivity
7. **Mobile App** - Native mobile applications
8. **Notifications** - Push notifications for alerts
9. **Audit Logging** - Track dashboard access and data changes
10. **Multi-language** - Internationalization (i18n)

## 🐛 Troubleshooting

### CORS Issues
If you encounter CORS errors, ensure your Spring Boot `application.properties` has:
```properties
server.servlet.context-path=/
spring.web.cors.allowed-origins=http://localhost:4200
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
```

### 401 Unauthorized
- Verify JWT token is valid and not expired
- Check `Authorization` header format: `Bearer {token}`
- Ensure user role in token matches endpoint requirements

### Data Not Loading
- Check browser console for errors
- Verify API endpoint URL in `dashboard.service.ts`
- Ensure backend is running on port 8080
- Check network tab for failed requests

## 📝 Notes

- **No backend logic changes** - Uses existing repositories and services
- **Fully responsive** - Works on desktop, tablet, and mobile
- **Modern Angular** - Uses Angular 17+ features
- **Clean architecture** - Separated concerns with services and components
- **Reusable components** - Can be extended for other features

## 📚 References

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Angular Documentation](https://angular.io/docs)
- [Chart.js](https://www.chartjs.org/)
- [Material Design](https://material.io/design)

---

**Implementation Date:** April 23, 2026
**Status:** ✅ Complete and Ready for Integration
