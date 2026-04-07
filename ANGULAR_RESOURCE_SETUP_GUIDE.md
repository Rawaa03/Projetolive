# Angular Resource Management Module - Setup Guide

## Overview
Complete Angular resource management system with TypeScript services, models, and standalone components for managing Bennes (bins) and Tracteurs (tractors).

## Files Created

### Models
1. **src/app/models/benne.model.ts** - Benne interface and types
2. **src/app/models/tracteur.model.ts** - Tracteur interface and types

### Services
1. **src/app/services/benne.service.ts** - HTTP service for benne operations
2. **src/app/services/tracteur.service.ts** - HTTP service for tracteur operations

### Components

#### Benne Components
- **src/app/ressources/bennes/liste-bennes/liste-bennes.ts** - List all bennes with filtering
- **src/app/ressources/bennes/ajouter-benne/ajouter-benne.ts** - Create new benne
- **src/app/ressources/bennes/modifier-benne/modifier-benne.ts** - Edit existing benne

#### Tracteur Components
- **src/app/ressources/tracteurs/liste-tracteurs/liste-tracteurs.ts** - List all tracteurs with filtering
- **src/app/ressources/tracteurs/ajouter-tracteur/ajouter-tracteur.ts** - Create new tracteur
- **src/app/ressources/tracteurs/modifier-tracteur/modifier-tracteur.ts** - Edit existing tracteur

### Sidebar
- **src/app/components/sidebar/sidebar-responsable.ts** - Navigation component with resources submenu

### Routing
- **src/app/app.routes.ts** - Complete application routing configuration

## API Integration

### Base URL
```typescript
private apiUrl = 'http://localhost:8080/api/ressources';
```

### Benne Service Endpoints
- `GET /bennes` - Get all bennes
- `GET /bennes/:id` - Get single benne
- `GET /bennes/type/:type` - Get bennes by type
- `GET /bennes/statut/:statut` - Get bennes by status
- `GET /bennes/available?startDate=...&endDate=...` - Get available bennes
- `POST /bennes` - Create new benne
- `PUT /bennes/:id` - Update benne
- `DELETE /bennes/:id` - Delete benne
- `POST /bennes/:id/charger` - Load benne with quantity
- `POST /bennes/:id/vider` - Empty benne
- `POST /bennes/:id/maintenance/start` - Start maintenance
- `POST /bennes/:id/maintenance/end` - End maintenance
- `POST /bennes/:id/assign-tournee` - Assign to tour
- `POST /bennes/:id/unassign-tournee` - Unassign from tour
- `POST /bennes/:id/assign-tractor` - Assign tractor

### Tracteur Service Endpoints
- `GET /tracteurs` - Get all tracteurs
- `GET /tracteurs/:id` - Get single tracteur
- `GET /tracteurs/type/:type` - Get tracteurs by type
- `GET /tracteurs/statut/:statut` - Get tracteurs by status
- `GET /tracteurs/carburant/:carburant` - Get tracteurs by fuel type
- `GET /tracteurs/available?startDate=...&endDate=...` - Get available tracteurs
- `POST /tracteurs` - Create new tracteur
- `PUT /tracteurs/:id` - Update tracteur
- `DELETE /tracteurs/:id` - Delete tracteur
- `POST /tracteurs/:id/kilometrage` - Update mileage
- `POST /tracteurs/:id/estimate-consumption` - Estimate fuel consumption
- `POST /tracteurs/:id/maintenance/start` - Start maintenance
- `POST /tracteurs/:id/maintenance/end` - End maintenance
- `POST /tracteurs/:id/assign-operator` - Assign operator/driver
- `POST /tracteurs/:id/unassign-operator` - Unassign operator
- `POST /tracteurs/:id/assign-tournee` - Assign to tour
- `POST /tracteurs/:id/unassign-tournee` - Unassign from tour

## Installation Steps

### 1. Copy Files to Your Frontend Project
Copy all generated TypeScript files to your Angular frontend directory maintaining the folder structure.

### 2. Update HttpClientModule
Ensure your `app.config.ts` includes HttpClientModule:

```typescript
import { HttpClientModule } from '@angular/common/http';
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    HttpClientModule
  ]
};
```

### 3. Configure CORS (if needed)
If your backend is on a different port, configure CORS headers in Angular's HttpClient:

```typescript
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  req = req.clone({
    setHeaders: {
      Authorization: `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    }
  });
  return next(req);
};
```

### 4. Add Guards
Ensure `AuthGuard` and `roleGuard` are implemented:

```typescript
// guards/auth-guard.ts
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CanActivateFn } from '@angular/router';

export const AuthGuard: CanActivateFn = (route, state) => {
  const token = localStorage.getItem('token');
  if (token) {
    return true;
  }
  // Redirect to login
  return false;
};

// guards/role-guard.ts
export const roleGuard: CanActivateFn = (route, state) => {
  const requiredRoles = route.data?.['role'];
  const userRole = JSON.parse(localStorage.getItem('currentUser') || '{}').role;
  return requiredRoles?.includes(userRole) || false;
};
```

## Component Features

### Liste-Bennes Component
- Display all bennes in a table
- Filter by status (DISPONIBLE, EN_USE, MAINTENANCE, HORS_SERVICE)
- Filter by availability
- Load benne with quantity
- Empty benne
- Start/end maintenance
- Delete benne
- Navigate to add/edit forms

### Ajouter-Benne Component
- Form for creating new benne
- Fields: capaciteMax, typeMateriau, usure, couleur, description
- Validation: Min capacity 100kg, usure 0-100%
- Success/error messages

### Modifier-Benne Component
- Form for editing existing benne
- Pre-fill form with current data
- Maintenance status toggle
- Update and cancel options

### Liste-Tracteurs Component
- Display all tracteurs
- Filter by status, availability, fuel type
- Update mileage
- Estimate fuel consumption
- Start/end maintenance
- Toggle trailer attachment
- Delete tracteur
- Navigate to add/edit forms

### Ajouter-Tracteur Component
- Form for creating new tracteur
- Fields: puissanceMoteur, consommationCarburant, kilometrage, typeCarburant, remorqueAttachee, marque, modele, annee, couleur, description
- Validation: Engine power >1, consumption >0.1, year 1990-current
- Success/error messages

### Modifier-Tracteur Component
- Form for editing existing tracteur
- Pre-fill with current data
- Maintenance status toggle
- Update and cancel options

### Sidebar Component
- Navigation menu with resources submenu
- Expandable/collapsible menus
- User profile display
- Role-based menu filtering
- Responsive design (collapsible on small screens)
- Icon support for visual clarity

## Routes

```
/ressources
  /bennes
    /          (list bennes)
    /ajouter   (create benne)
    /modifier/:id (edit benne)
  /tracteurs
    /          (list tracteurs)
    /ajouter   (create tracteur)
    /modifier/:id (edit tracteur)
```

## Authentication

All endpoints are protected with:
- `AuthGuard` - Verifies user is logged in
- `roleGuard` - Verifies user has ADMIN or RESPONSABLE role

Token is stored in `localStorage.getItem('token')`
User info is stored in `localStorage.getItem('currentUser')`

## Error Handling

All components include:
- Loading state management
- Error message display
- HTTP error handling
- User-friendly error messages

## Notes

- All components are standalone (no module needed)
- Uses reactive forms for validation
- Implements proper TypeScript typing
- Supports both French and English interfaces
- API base URL configurable in services
- Ready for Material UI or Bootstrap integration

## Next Steps

1. Create HTML templates for each component (liste-bennes.html, ajouter-benne.html, etc.)
2. Create CSS stylesheets for each component
3. Implement error interceptors
4. Add loading animations
5. Integrate with your authentication system
6. Configure proper API URLs
7. Add form validation messages
8. Implement success notifications
