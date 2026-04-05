# Gestion des Ressources - API Documentation
## Zitouna - Intelligent Olive Collection Management

---

## Overview

This document provides comprehensive documentation for the Resource Management API in the Zitouna backend. The system manages two types of resources:
- **Bennes** (Bins/Containers) - for collecting and storing olives
- **Tracteurs** (Tractors) - for transporting resources and towing bennes

All endpoints require authentication and are restricted to **ADMIN** and **RESPONSABLE** roles.

---

## Base URL

```
http://localhost:8080/api/ressources
```

## Authentication

All requests must include a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <jwt_token>
```

---

## Base CRUD Operations

### Create Resource
**POST** `/api/ressources`

Creates a new resource (Benne or Tracteur).

**Request Body:**
```json
{
  "nom": "Benne001",
  "type": "BENNE",
  "statut": "DISPONIBLE",
  "immatriculation": "BEN-001",
  "capaciteKg": 5000,
  "quantiteChargeeActuelle": 0
}
```

**Response (200):**
```json
{
  "message": "Ressource créée avec succès",
  "ressource": { ... }
}
```

---

### Get Resource by ID
**GET** `/api/ressources/{id}`

Retrieves a specific resource.

**Response (200):**
```json
{
  "id": "123abc",
  "nom": "Benne001",
  "type": "BENNE",
  "statut": "DISPONIBLE",
  "capaciteKg": 5000,
  "quantiteChargeeActuelle": 2500,
  "tauxRemplissage": 50.0
}
```

---

### List All Resources
**GET** `/api/ressources`

Retrieves all resources in the system.

**Response (200):**
```json
[
  { ... },
  { ... }
]
```

---

### Update Resource
**PUT** `/api/ressources/{id}`

Updates an existing resource.

**Request Body:**
```json
{
  "nom": "Benne001-Updated",
  "statut": "DISPONIBLE",
  "capaciteKg": 6000
}
```

**Response (200):**
```json
{
  "message": "Ressource modifiée avec succès",
  "ressource": { ... }
}
```

---

### Delete Resource
**DELETE** `/api/ressources/{id}`

Deletes a resource.

**Response (200):**
```json
{
  "message": "Ressource supprimée avec succès"
}
```

---

## Search & Filter Operations

### Filter by Type
**GET** `/api/ressources/type/{type}`

Get all resources of a specific type (BENNE, TRACTEUR, TRAVAILLEUR).

**Example:**
```
GET /api/ressources/type/BENNE
```

---

### Filter by Status
**GET** `/api/ressources/statut/{statut}`

Get resources by status (DISPONIBLE, OCCUPE, MAINTENANCE, EN_USE, HORS_SERVICE).

**Example:**
```
GET /api/ressources/statut/DISPONIBLE
```

---

### Filter by Type and Status
**GET** `/api/ressources/type/{type}/statut/{statut}`

Get resources filtered by both type and status.

**Example:**
```
GET /api/ressources/type/BENNE/statut/DISPONIBLE
```

---

### Get Available Resources
**GET** `/api/ressources/available`

Lists all available resources.

---

### Get Resources Under Maintenance
**GET** `/api/ressources/maintenance`

Lists all resources currently in maintenance.

---

## Availability Operations

### Check Availability for Period
**GET** `/api/ressources/{id}/available`

Check if a resource is available for a specific date range.

**Query Parameters:**
- `startDate` (required): Start date in format `yyyy-MM-dd'T'HH:mm:ss`
- `endDate` (required): End date in format `yyyy-MM-dd'T'HH:mm:ss`

**Example:**
```
GET /api/ressources/123abc/available?startDate=2024-01-15T08:00:00&endDate=2024-01-15T18:00:00
```

**Response (200):**
```json
{
  "ressourceId": "123abc",
  "disponible": true,
  "dateDebut": "2024-01-15T08:00:00",
  "dateFin": "2024-01-15T18:00:00"
}
```

---

### Get Available Resources for Period
**GET** `/api/ressources/available/period`

List all available resources for a date range.

**Query Parameters:**
- `startDate` (required)
- `endDate` (required)

---

### Get Available Resources by Type for Period
**GET** `/api/ressources/available/{type}/period`

List available resources of a specific type for a date range.

**Example:**
```
GET /api/ressources/available/BENNE/period?startDate=2024-01-15T08:00:00&endDate=2024-01-15T18:00:00
```

---

## Tour Assignment Operations

### Assign Resource to Tour
**POST** `/api/ressources/{ressourceId}/assign-tour/{tourneeId}`

Assign a resource to a specific tour.

**Response (200):**
```json
{
  "message": "Ressource assignée à la tournée avec succès",
  "ressource": { ... }
}
```

---

### Unassign Resource from Tour
**DELETE** `/api/ressources/{ressourceId}/unassign-tour/{tourneeId}`

Remove a resource from a tour.

---

### Get Resource Status
**GET** `/api/ressources/{id}/status`

Get detailed status information about a resource.

**Response (200):**
```json
{
  "id": "123abc",
  "nom": "Benne001",
  "type": "BENNE",
  "statut": "OCCUPE",
  "disponible": false,
  "enTournee": true,
  "tourneeActuelleId": "tour123",
  "nombreTournees": 3
}
```

---

## Benne (Bin) Endpoints

### Create Benne
**POST** `/api/ressources/bennes`

**Request Body:**
```json
{
  "nom": "Benne001",
  "immatriculation": "BEN-001",
  "capaciteKg": 5000
}
```

---

### List All Bennes
**GET** `/api/ressources/bennes`

---

### List Available Bennes
**GET** `/api/ressources/bennes/available`

---

### Get Benne by ID
**GET** `/api/ressources/bennes/{id}`

---

### Update Benne
**PUT** `/api/ressources/bennes/{id}`

---

### Delete Benne
**DELETE** `/api/ressources/bennes/{id}`

---

## Benne Load Management

### Add Load to Benne
**POST** `/api/ressources/bennes/{id}/charger`

Add a quantity of olives to a benne.

**Query Parameters:**
- `quantite` (required): Quantity in kg

**Example:**
```
POST /api/ressources/bennes/123abc/charger?quantite=500
```

**Response (200):**
```json
{
  "message": "Charge ajoutée avec succès",
  "benne": {
    "id": "123abc",
    "quantiteChargeeActuelle": 2500,
    "tauxRemplissage": 50.0,
    "estPleine": false
  }
}
```

---

### Empty Benne
**POST** `/api/ressources/bennes/{id}/vider`

Empty a benne completely.

**Response (200):**
```json
{
  "message": "Benne vidée avec succès",
  "benne": {
    "quantiteChargeeActuelle": 0,
    "tauxRemplissage": 0.0,
    "estPleine": false
  }
}
```

---

### Get Capacity Info
**GET** `/api/ressources/bennes/{id}/capacite`

Get detailed capacity information.

**Response (200):**
```json
{
  "benneId": "123abc",
  "nom": "Benne001",
  "capaciteMax": 5000,
  "quantiteActuelle": 2500,
  "tauxRemplissage": 50.0,
  "estPleine": false,
  "capaciteDisponible": 2500
}
```

---

### List Full Bennes
**GET** `/api/ressources/bennes/full`

Get all bennes that are completely full.

---

### Get Benne Statistics
**GET** `/api/ressources/bennes/{id}/stats`

Get comprehensive statistics for a benne.

**Response (200):**
```json
{
  "benneId": "123abc",
  "nom": "Benne001",
  "type": "BENNE",
  "statut": "OCCUPE",
  "capaciteMax": 5000,
  "quantiteActuelle": 2500,
  "tauxRemplissage": 50.0,
  "estPleine": false,
  "tracteurAssigne": "trac456",
  "nombreTournees": 3,
  "enTournee": true,
  "quantiteTotalCollectee": 7500
}
```

---

## Benne-Tractor Assignment

### Assign Tractor to Benne
**POST** `/api/ressources/bennes/{benneId}/assign-tracteur/{tracteurId}`

Link a tractor to a benne.

---

### Unassign Tractor from Benne
**DELETE** `/api/ressources/bennes/{benneId}/unassign-tracteur`

Remove tractor assignment from a benne.

---

## Benne Maintenance

### Record Benne Maintenance
**POST** `/api/ressources/bennes/{id}/maintenance`

Record a maintenance operation.

**Query Parameters:**
- `description` (required): Description of maintenance
- `cout` (optional): Cost of maintenance

**Example:**
```
POST /api/ressources/bennes/123abc/maintenance?description=Oil+change&cout=150
```

---

### End Benne Maintenance
**POST** `/api/ressources/bennes/{id}/maintenance/end`

Mark maintenance as complete and make benne available again.

---

### List Bennes Under Maintenance
**GET** `/api/ressources/bennes/maintenance-list`

---

## Tractor (Tracteur) Endpoints

### Create Tractor
**POST** `/api/ressources/tracteurs`

**Request Body:**
```json
{
  "nom": "Tracteur001",
  "immatriculation": "TRAC-001",
  "puissance": "150 CV",
  "carburant": "Diesel",
  "consommationHoraire": 15.5,
  "aRemorque": true,
  "kilometrage": 0
}
```

---

### List All Tractors
**GET** `/api/ressources/tracteurs`

---

### List Available Tractors
**GET** `/api/ressources/tracteurs/available`

---

### Get Tractor by ID
**GET** `/api/ressources/tracteurs/{id}`

---

### Update Tractor
**PUT** `/api/ressources/tracteurs/{id}`

---

### Delete Tractor
**DELETE** `/api/ressources/tracteurs/{id}`

---

## Tractor Specifications

### Get Tractor Specs
**GET** `/api/ressources/tracteurs/{id}/specs`

Get engine specifications and technical details.

**Response (200):**
```json
{
  "tracteurId": "456def",
  "nom": "Tracteur001",
  "immatriculation": "TRAC-001",
  "puissance": "150 CV",
  "carburant": "Diesel",
  "consommationHoraire": 15.5,
  "aRemorque": true
}
```

---

### Get Tractor Statistics
**GET** `/api/ressources/tracteurs/{id}/stats`

Get comprehensive statistics for a tractor.

**Response (200):**
```json
{
  "tracteurId": "456def",
  "nom": "Tracteur001",
  "type": "TRACTEUR",
  "statut": "DISPONIBLE",
  "puissance": "150 CV",
  "carburant": "Diesel",
  "consommationHoraire": 15.5,
  "kilometrage": 45000,
  "aRemorque": true,
  "conducteurId": "user123",
  "nombreTournees": 5,
  "enTournee": false
}
```

---

## Tractor Mileage & Fuel

### Update Mileage
**PUT** `/api/ressources/tracteurs/{id}/update-mileage`

Update the tractor's mileage.

**Query Parameters:**
- `mileage` (required): New mileage in km

**Example:**
```
PUT /api/ressources/tracteurs/456def/update-mileage?mileage=46000
```

---

### Estimate Fuel Consumption
**POST** `/api/ressources/tracteurs/{id}/consumption-estimate`

Calculate estimated fuel consumption for a distance.

**Query Parameters:**
- `distance` (required): Distance in km

**Response (200):**
```json
{
  "distance": 100,
  "consommationHoraire": 15.5,
  "consommationEstimee": 15.5
}
```

---

## Tractor Driver Assignment

### Assign Driver
**POST** `/api/ressources/tracteurs/{id}/assign-driver/{driverId}`

Assign a driver to the tractor.

---

### Unassign Driver
**DELETE** `/api/ressources/tracteurs/{id}/unassign-driver`

Remove the driver assignment.

---

## Tractor Maintenance

### Record Tractor Maintenance
**POST** `/api/ressources/tracteurs/{id}/maintenance`

Record a maintenance operation.

**Query Parameters:**
- `description` (required)
- `cout` (optional)

---

### End Tractor Maintenance
**POST** `/api/ressources/tracteurs/{id}/maintenance/end`

Mark maintenance as complete.

---

### List Tractors Under Maintenance
**GET** `/api/ressources/tracteurs/maintenance-list`

---

### List Tractors with Trailer
**GET** `/api/ressources/tracteurs/with-trailer`

Get all tractors that have a trailer attached.

---

## Resource Status Codes

### Resource Status Values
- `DISPONIBLE` - Available for use
- `OCCUPE` - Currently in use/assigned to a tour
- `MAINTENANCE` - Under maintenance
- `EN_USE` - Currently being used
- `HORS_SERVICE` - Out of service

### Benne-Specific Status
- `DISPONIBLE` - Available for loading
- `PLEINE` - Full and ready for transport
- `EN_ROUTE` - Currently being transported
- `MAINTENANCE` - Under maintenance

### Tractor-Specific Status
- `DISPONIBLE` - Available for assignment
- `EN_ROUTE` - Currently on a tour
- `MAINTENANCE` - Under maintenance
- `HORS_SERVICE` - Out of service

---

## Error Responses

### 400 Bad Request
```json
{
  "error": "Invalid request parameters"
}
```

### 404 Not Found
Resource not found or does not exist.

### 403 Forbidden
User does not have permission to perform this action (requires ADMIN or RESPONSABLE role).

### 500 Internal Server Error
```json
{
  "error": "Unexpected server error"
}
```

---

## Examples

### Example 1: Create a Benne and Load It

1. **Create Benne:**
```bash
curl -X POST http://localhost:8080/api/ressources/bennes \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Benne-Collecte-001",
    "immatriculation": "BEN-2024-001",
    "capaciteKg": 5000
  }'
```

2. **Add Load:**
```bash
curl -X POST http://localhost:8080/api/ressources/bennes/{benneId}/charger?quantite=2500 \
  -H "Authorization: Bearer <token>"
```

3. **Get Capacity Info:**
```bash
curl -X GET http://localhost:8080/api/ressources/bennes/{benneId}/capacite \
  -H "Authorization: Bearer <token>"
```

---

### Example 2: Create Tractor and Assign Driver

1. **Create Tractor:**
```bash
curl -X POST http://localhost:8080/api/ressources/tracteurs \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Tracteur-Transport-001",
    "immatriculation": "TRAC-2024-001",
    "puissance": "120 CV",
    "carburant": "Diesel",
    "consommationHoraire": 18.0,
    "aRemorque": true
  }'
```

2. **Assign Driver:**
```bash
curl -X POST http://localhost:8080/api/ressources/tracteurs/{tracteurId}/assign-driver/{driverId} \
  -H "Authorization: Bearer <token>"
```

---

### Example 3: Check Availability and Assign to Tour

1. **Check Availability:**
```bash
curl -X GET 'http://localhost:8080/api/ressources/123abc/available?startDate=2024-01-15T08:00:00&endDate=2024-01-15T18:00:00' \
  -H "Authorization: Bearer <token>"
```

2. **Assign to Tour:**
```bash
curl -X POST http://localhost:8080/api/ressources/123abc/assign-tour/{tourneeId} \
  -H "Authorization: Bearer <token>"
```

---

## Rate Limiting & Best Practices

- All requests are authenticated and role-based
- Maintain proper status transitions (don't assign a resource in maintenance to a tour)
- Always check availability before assigning resources
- Empty bennes after each collection
- Record maintenance operations to track resource health

---

## Changelog

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024 | Initial release - Complete CRUD, search, and assignment operations |

---

## Support

For issues or questions regarding the Resource Management API, please contact the development team or open an issue in the project repository.
