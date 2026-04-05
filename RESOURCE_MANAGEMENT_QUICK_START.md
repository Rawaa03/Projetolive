# Resource Management - Quick Start Guide
## Zitouna Olive Collection Management

---

## Quick Overview

The Resource Management system manages two types of resources:
- **Bennes** (🛢️) - Containers/bins for olive collection
- **Tracteurs** (🚜) - Tractors for transport

All operations are secured with JWT authentication and require **ADMIN** or **RESPONSABLE** role.

---

## Base URL

```
http://localhost:8080/api/ressources
```

---

## Quick Commands

### 1. Create a Benne

```bash
curl -X POST http://localhost:8080/api/ressources/bennes \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Benne-2024-001",
    "immatriculation": "BEN-001",
    "capaciteKg": 5000
  }'
```

**Key Fields:**
- `nom` - Resource name (required)
- `immatriculation` - License plate (required)
- `capaciteKg` - Capacity in kg (required for bennes)

---

### 2. Create a Tractor

```bash
curl -X POST http://localhost:8080/api/ressources/tracteurs \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Tracteur-2024-001",
    "immatriculation": "TRAC-001",
    "puissance": "120 CV",
    "carburant": "Diesel",
    "consommationHoraire": 18.0,
    "aRemorque": true,
    "kilometrage": 0
  }'
```

**Key Fields:**
- `puissance` - Engine power (required)
- `carburant` - Fuel type (required)
- `consommationHoraire` - Hourly consumption
- `aRemorque` - Has trailer (yes/no)
- `kilometrage` - Current mileage

---

### 3. Load a Benne

```bash
curl -X POST "http://localhost:8080/api/ressources/bennes/{benneId}/charger?quantite=2500" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Result:** Adds 2500 kg to the benne

---

### 4. Empty a Benne

```bash
curl -X POST http://localhost:8080/api/ressources/bennes/{benneId}/vider \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Result:** Empties the benne completely

---

### 5. Check Benne Capacity

```bash
curl -X GET http://localhost:8080/api/ressources/bennes/{benneId}/capacite \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Returns:**
```json
{
  "benneId": "abc123",
  "capaciteMax": 5000,
  "quantiteActuelle": 2500,
  "tauxRemplissage": 50.0,
  "estPleine": false,
  "capaciteDisponible": 2500
}
```

---

### 6. Assign Tractor to Benne

```bash
curl -X POST "http://localhost:8080/api/ressources/bennes/{benneId}/assign-tracteur/{tracteurId}" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 7. Assign Driver to Tractor

```bash
curl -X POST "http://localhost:8080/api/ressources/tracteurs/{tracteurId}/assign-driver/{driverId}" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 8. Assign Resource to Tour

```bash
curl -X POST "http://localhost:8080/api/ressources/{resourceId}/assign-tour/{tourneeId}" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 9. Check Resource Availability

```bash
curl -X GET "http://localhost:8080/api/ressources/{resourceId}/available?startDate=2024-01-15T08:00:00&endDate=2024-01-15T18:00:00" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Returns:**
```json
{
  "ressourceId": "abc123",
  "disponible": true,
  "dateDebut": "2024-01-15T08:00:00",
  "dateFin": "2024-01-15T18:00:00"
}
```

---

### 10. Get Resource Status

```bash
curl -X GET http://localhost:8080/api/ressources/{resourceId}/status \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Returns:**
```json
{
  "id": "abc123",
  "nom": "Benne-2024-001",
  "type": "BENNE",
  "statut": "DISPONIBLE",
  "disponible": true,
  "enTournee": false,
  "nombreTournees": 0
}
```

---

### 11. List All Available Bennes

```bash
curl -X GET http://localhost:8080/api/ressources/bennes/available \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 12. List All Available Tractors

```bash
curl -X GET http://localhost:8080/api/ressources/tracteurs/available \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 13. Record Maintenance

```bash
curl -X POST "http://localhost:8080/api/ressources/bennes/{benneId}/maintenance?description=Oil+change&cout=150" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 14. End Maintenance

```bash
curl -X POST http://localhost:8080/api/ressources/bennes/{benneId}/maintenance/end \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 15. Update Tractor Mileage

```bash
curl -X PUT "http://localhost:8080/api/ressources/tracteurs/{tracteurId}/update-mileage?mileage=46000" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Common Statuses

| Status | Meaning |
|--------|---------|
| `DISPONIBLE` | Available for use |
| `OCCUPE` | Assigned/in use |
| `MAINTENANCE` | Under maintenance |
| `EN_USE` | Currently being used |
| `HORS_SERVICE` | Out of service |

---

## Key Endpoints by Purpose

### Listing Resources
```
GET /api/ressources                              # All resources
GET /api/ressources/bennes                       # All bennes
GET /api/ressources/tracteurs                    # All tractors
GET /api/ressources/available                    # Available resources
GET /api/ressources/bennes/available             # Available bennes
GET /api/ressources/tracteurs/available          # Available tractors
```

### Filtering
```
GET /api/ressources/type/BENNE                   # By type
GET /api/ressources/statut/DISPONIBLE            # By status
GET /api/ressources/type/BENNE/statut/DISPONIBLE # Combined filter
```

### Resource Operations
```
POST /api/ressources/{id}/assign-tour/{tourId}   # Assign to tour
DELETE /api/ressources/{id}/unassign-tour/{tourId} # Remove from tour
GET /api/ressources/{id}/status                  # Get status
GET /api/ressources/{id}/available               # Check availability
```

### Benne Operations
```
POST /api/ressources/bennes                      # Create
GET /api/ressources/bennes/{id}                  # Get
PUT /api/ressources/bennes/{id}                  # Update
DELETE /api/ressources/bennes/{id}               # Delete
POST /api/ressources/bennes/{id}/charger         # Add load
POST /api/ressources/bennes/{id}/vider           # Empty
GET /api/ressources/bennes/{id}/capacite         # Check capacity
GET /api/ressources/bennes/{id}/stats            # Get stats
POST /api/ressources/bennes/{id}/assign-tracteur/{tractorId} # Link tractor
```

### Tractor Operations
```
POST /api/ressources/tracteurs                   # Create
GET /api/ressources/tracteurs/{id}               # Get
PUT /api/ressources/tracteurs/{id}               # Update
DELETE /api/ressources/tracteurs/{id}            # Delete
GET /api/ressources/tracteurs/{id}/specs         # Get specs
GET /api/ressources/tracteurs/{id}/stats         # Get stats
PUT /api/ressources/tracteurs/{id}/update-mileage # Update mileage
POST /api/ressources/tracteurs/{id}/assign-driver/{driverId} # Assign driver
POST /api/ressources/tracteurs/{id}/consumption-estimate # Estimate consumption
```

### Maintenance
```
POST /api/ressources/bennes/{id}/maintenance     # Record benne maintenance
POST /api/ressources/bennes/{id}/maintenance/end # End benne maintenance
GET /api/ressources/bennes/maintenance-list      # List bennes in maintenance
POST /api/ressources/tracteurs/{id}/maintenance  # Record tractor maintenance
POST /api/ressources/tracteurs/{id}/maintenance/end # End tractor maintenance
GET /api/ressources/tracteurs/maintenance-list   # List tractors in maintenance
```

---

## Typical Workflow

### 1️⃣ Setup Phase
```
1. Create Benne with capacity
2. Create Tractor with specifications
3. Link Tractor to Benne
4. Assign Driver to Tractor
```

### 2️⃣ Collection Phase
```
1. Check resource availability
2. Assign resources to tour
3. Add loads to bennes
4. Track fill percentages
5. Monitor current tour
```

### 3️⃣ Transport Phase
```
1. Update tractor mileage
2. Track fuel consumption
3. Monitor tour progress
4. Maintain resource status
```

### 4️⃣ Completion Phase
```
1. Empty bennes
2. Unassign from tour
3. Return to available status
4. Record maintenance if needed
```

---

## Error Handling

### 400 Bad Request
Usually means validation failed. Check:
- Required fields are provided
- Date formats are correct
- Capacity constraints
- Resource exists

### 404 Not Found
Resource doesn't exist. Check:
- Correct resource ID
- Resource hasn't been deleted
- Type matches (benne vs tracteur)

### 403 Forbidden
User lacks permission. Need:
- Valid JWT token
- ADMIN or RESPONSABLE role

### 500 Server Error
Unexpected error. Check:
- Server logs
- Database connection
- Request format

---

## Tips & Best Practices

✅ **Always check availability** before assigning to a tour  
✅ **Empty bennes after use** to prepare for next collection  
✅ **Record maintenance** when issues arise  
✅ **Monitor fill levels** to prevent overfilling  
✅ **Track mileage** for maintenance planning  
✅ **Assign drivers** to improve tracking  
✅ **Use status filtering** to find appropriate resources  
✅ **Plan maintenance** during low-usage periods  

---

## Complete Documentation

For detailed information, see:
- `RESOURCE_MANAGEMENT_API.md` - Full API reference
- `RESOURCE_MANAGEMENT_IMPLEMENTATION.md` - Technical details

---

## Need Help?

1. Check the full API documentation
2. Review error message details
3. Verify JWT token is valid
4. Confirm user has ADMIN or RESPONSABLE role
5. Check resource exists and is correct type

---

**Happy Resource Managing! 🚜🛢️**
