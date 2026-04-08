# Backend Implementation Guide - Resource Management System

## Problem Identified

When adding new resources (Bennes or Tracteurs), the new resource replaces the previous one in the database instead of creating a new record. This is because:

1. **No dedicated RessourceController exists** in the backend
2. **No RessourceService exists** to handle resource operations
3. **No RessourceRepository exists** for database queries

## Solution: Add Backend Java Files

### Files to Create/Add to Your Spring Boot Project

Add these files to your backend `src/main/java/com/example/demo/` directory:

---

### 1. RessourceRepository.java
**Location:** `src/main/java/com/example/demo/repository/RessourceRepository.java`

This file is provided in: `/backend/src/main/java/com/example/demo/repository/RessourceRepository.java`

**Key Features:**
- MongoDB repository extending `MongoRepository<Ressource, String>`
- Methods to find resources by type, status, and immatriculation
- Ensures each save creates a new document with unique ID

---

### 2. RessourceService.java
**Location:** `src/main/java/com/example/demo/service/RessourceService.java`

This file is provided in: `/backend/src/main/java/com/example/demo/service/RessourceService.java`

**Key Features:**
- `create(Ressource)` - Creates new resource with auto-generated ID
- `getAll()` - Retrieves all resources
- `getByType(TypeRessource)` - Filters by resource type
- `getById(String)` - Gets specific resource
- `update(String id, Ressource)` - Updates only provided fields
- `delete(String)` - Removes resource

**Critical Implementation:**
```java
// MongoDB auto-generates unique ID each time
ressource.setId(null);  // Always null on create
ressourceRepository.save(ressource);  // Creates NEW document
```

---

### 3. RessourceController.java
**Location:** `src/main/java/com/example/demo/controller/RessourceController.java`

This file is provided in: `/backend/src/main/java/com/example/demo/controller/RessourceController.java`

**Endpoints:**

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/ressources` | Create new resource |
| GET | `/api/ressources` | Get all resources |
| GET | `/api/ressources?type=BENNE` | Get resources by type |
| GET | `/api/ressources/{id}` | Get specific resource |
| PUT | `/api/ressources/{id}` | Update resource |
| DELETE | `/api/ressources/{id}` | Delete resource |
| POST | `/api/ressources/{id}/charger` | Load benne |
| POST | `/api/ressources/{id}/vider` | Empty benne |
| GET | `/api/ressources/status/{statut}` | Filter by status |

---

## Implementation Steps

1. **Copy the files from the backend directory:**
   - `backend/src/main/java/com/example/demo/repository/RessourceRepository.java`
   - `backend/src/main/java/com/example/demo/service/RessourceService.java`
   - `backend/src/main/java/com/example/demo/controller/RessourceController.java`

2. **Paste them into your Spring Boot project** in the respective directories:
   - `src/main/java/com/example/demo/repository/`
   - `src/main/java/com/example/demo/service/`
   - `src/main/java/com/example/demo/controller/`

3. **Restart your Spring Boot application**

4. **Test the endpoints:**
   ```bash
   # Create a benne
   curl -X POST http://localhost:8080/api/ressources \
     -H "Content-Type: application/json" \
     -d '{
       "type": "BENNE",
       "nom": "Benne 1",
       "capaciteKg": 1000,
       "statut": "DISPONIBLE"
     }'

   # Get all bennes
   curl http://localhost:8080/api/ressources?type=BENNE
   ```

---

## Why Multiple Resources Now Work

### Before (Problem):
- Creating a resource without proper backend would use the same ID
- `save()` with same ID = UPDATE, not INSERT
- Result: Only one resource in database

### After (Solution):
- `create()` always sets `id = null`
- MongoDB auto-generates unique ObjectId
- `save()` with new ID = INSERT
- Result: Multiple resources created with unique IDs

---

## Security Configuration

The controller includes `@PreAuthorize` annotations:
- **Create/Update/Delete**: Requires `ADMIN` or `RESPONSABLE` role
- **Read operations**: Open to authenticated users

Make sure your `SecurityConfig.java` authorizes `/api/ressources/**` endpoints.

---

## Frontend Already Updated

Your frontend code has been updated to:
1. Use the correct `/api/ressources` endpoint (not `/api/ressources/bennes`)
2. Properly map field names (capaciteMax → capaciteKg, etc.)
3. Handle filtering by type with query parameters
4. Display multiple resources correctly

---

## Testing Checklist

- [ ] Backend files copied to correct locations
- [ ] Spring Boot application restarts without errors
- [ ] POST `/api/ressources` creates new resource with unique ID
- [ ] Multiple resources persist in database
- [ ] GET `/api/ressources?type=BENNE` returns all bennes
- [ ] Frontend can add multiple resources
- [ ] Frontend list page displays all resources
- [ ] Sidebar submenu "Gérer ressources" expands/collapses correctly

---

## Troubleshooting

**Issue**: "RessourceController not found"
- **Solution**: Ensure files are in correct package structure (com.example.demo.controller)

**Issue**: "No bean of type RessourceService"
- **Solution**: Ensure RessourceService has @Service annotation

**Issue**: Still only one resource in database
- **Solution**: Clear MongoDB collection and restart backend

**Issue**: Sidebar submenu doesn't expand
- **Solution**: Check browser console for JavaScript errors

---

## Additional Notes

The Ressource model uses MongoDB's @Document annotation, which automatically:
- Creates collection named "ressources"
- Generates unique ObjectId for each document
- Handles polymorphic fields (benne-specific vs tracteur-specific)

This design allows a single Ressource entity to represent BENNE, TRACTEUR, and TRAVAILLEUR types with appropriate fields.
