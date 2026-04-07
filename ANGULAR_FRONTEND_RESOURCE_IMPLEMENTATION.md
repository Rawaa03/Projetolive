# Angular Frontend - Resource Management Implementation

## Overview
Complete Angular resource management system with full CRUD operations for Bennes (bins) and Tracteurs (tractors).

## Project Structure

```
src/app/
├── models/
│   ├── ressource.ts          # Base resource interface
│   ├── benne.ts              # Benne model & interfaces
│   └── tracteur.ts           # Tracteur model & interfaces
├── services/
│   ├── ressource.service.ts  # Base resource service
│   ├── benne.service.ts      # Benne operations
│   └── tracteur.service.ts   # Tracteur operations
└── resources/
    ├── bennes/
    │   ├── liste-bennes/     # List, filter, delete bennes
    │   ├── ajouter-benne/    # Create new benne
    │   └── modifier-benne/   # Edit existing benne
    └── tracteurs/
        ├── liste-tracteurs/  # List, filter, delete tracteurs
        ├── ajouter-tracteur/ # Create new tracteur
        └── modifier-tracteur/# Edit existing tracteur
```

## Components Created

### Bennes (Bins)

#### 1. **ListeBennesComponent**
- Displays all bennes in a data table
- Advanced filtering (search, status, material type)
- Delete functionality with confirmation
- Status color coding
- Responsive design with Material Design

**Features:**
- Real-time search filtering
- Status badge visualization
- Load percentage bar with color coding
- Edit and delete actions per row

#### 2. **AjouterBenneComponent**
- Form to create new bennes
- Form validation
- Required fields: Capacity, Material Type, Wear %
- Optional fields: Color, Description
- Success/error messages

#### 3. **ModifierBenneComponent**
- Edit existing bennes
- Pre-populated form with current data
- Update all benne properties
- Status management
- Confirmation on success

### Tracteurs (Tractors)

#### 1. **ListeTracteursComponent**
- Displays all tractors in a data table
- Advanced filtering (search, status, fuel type)
- Delete functionality with confirmation
- Fuel type icons
- Responsive Material Design table

**Features:**
- Real-time search filtering
- Fuel type visualization with icons
- Status badge color coding
- Edit and delete actions per row

#### 2. **AjouterTracteurComponent**
- Form to create new tractors
- Required fields: Brand, Model, Year, Power, Fuel Type, Consumption
- Optional fields: Mileage, Color, Description, Trailer attachment
- Form validation with error messages

#### 3. **ModifierTracteurComponent**
- Edit existing tractors
- Pre-populated form with current data
- Update all properties including status
- Trailer attachment toggle
- Success/error handling

## Services

### BenneService
**CRUD Operations:**
- `getAll()` - Retrieve all bennes
- `getById(id)` - Get specific benne
- `create(benne)` - Create new benne
- `update(id, benne)` - Update benne
- `delete(id)` - Delete benne

**Specialized Operations:**
- `chargerBenne(id, quantite)` - Load benne with olive quantity
- `viderBenne(id)` - Empty benne
- `assignTracteur(benneId, tracteurId)` - Assign tractor to benne
- `unassignTracteur(benneId)` - Remove tractor assignment
- `startMaintenance(id)` - Begin maintenance
- `endMaintenance(id)` - End maintenance
- `getFullBennes()` - Get all fully loaded bennes
- `getStats(id)` - Get benne statistics

### TracteurService
**CRUD Operations:**
- `getAll()` - Retrieve all tractors
- `getById(id)` - Get specific tractor
- `create(tracteur)` - Create new tractor
- `update(id, tracteur)` - Update tractor
- `delete(id)` - Delete tractor

**Specialized Operations:**
- `getAvailable()` - Get available tractors only
- `getSpecs(id)` - Get technical specifications
- `updateKilometrage(id, km)` - Update mileage
- `estimateConsumption(id, distance)` - Calculate fuel consumption
- `assignConducteur(tracteurId, driverId)` - Assign driver
- `unassignConducteur(tracteurId)` - Remove driver assignment
- `startMaintenance(id)` - Begin maintenance
- `endMaintenance(id)` - End maintenance
- `getWithTrailer()` - Get tractors with trailers
- `getStats(id)` - Get tractor statistics

### RessourceService
**General Operations:**
- `getAll()` - Get all resources
- `getById(id)` - Get specific resource
- `getByType(type)` - Filter by type (BENNE/TRACTEUR)
- `getByStatut(statut)` - Filter by status
- `getAvailable()` - Get available resources
- `checkAvailability(id, startDate, endDate)` - Check date range availability
- `assignToTournee(ressourceId, tourneeId)` - Assign to tour
- `unassignFromTournee(ressourceId)` - Remove from tour

## Models/Interfaces

### Ressource (Base)
```typescript
interface Ressource {
  id?: string;
  type: 'BENNE' | 'TRACTEUR';
  statut: 'DISPONIBLE' | 'EN_USE' | 'MAINTENANCE' | 'HORS_SERVICE';
  dateCreation: Date;
  dateDerniereMaintenance?: Date;
  tourneeId?: string;
}
```

### Benne
```typescript
interface Benne extends Ressource {
  capaciteMax: number;
  chargeActuelle: number;
  pourcentageRemplissage: number;
  typeMateriau: string;
  tracteurId?: string;
  usure: number;
  couleur?: string;
  description?: string;
}
```

### Tracteur
```typescript
interface Tracteur extends Ressource {
  puissanceMoteur: number;
  consommationCarburant: number;
  kilometrage: number;
  conducteurId?: string;
  typeCarburant: string;
  marque?: string;
  modele?: string;
  annee?: number;
  couleur?: string;
}
```

## Material Design Components Used

- **MatTableModule** - Data tables with sorting/pagination
- **MatFormFieldModule** - Form field styling
- **MatInputModule** - Text input fields
- **MatSelectModule** - Dropdown selections
- **MatButtonModule** - Action buttons
- **MatIconModule** - Material icons
- **MatChipsModule** - Status badges
- **MatProgressBarModule** - Loading indicators
- **MatCheckboxModule** - Toggle options

## Styling Features

### Color Scheme
- **Primary**: #007bff (Blue)
- **Success**: #28a745 (Green) - DISPONIBLE
- **Warning**: #ffc107 (Yellow) - EN_USE
- **Info**: #17a2b8 (Cyan) - MAINTENANCE
- **Danger**: #dc3545 (Red) - HORS_SERVICE

### Responsive Design
- Mobile-first approach
- Breakpoints: xs, sm, md, lg, xl
- Flex-based layouts
- Bootstrap grid system integration

## Error Handling

All components include:
- Try-catch error handling
- User-friendly error messages
- Validation feedback
- Loading states
- Success notifications
- API error logging

## Form Validation

### Benne Creation
- Capacity Min: 100 kg
- Material Type: Required
- Wear: 0-100%

### Tracteur Creation
- Year: Min 1950, Max Current+1
- Power: Minimum 1 CV
- Fuel Type: Required
- Consumption: Required, Min 0

## API Endpoints Expected

```
POST   /api/ressources/bennes           - Create benne
GET    /api/ressources/bennes           - List all bennes
GET    /api/ressources/bennes/{id}      - Get specific benne
PUT    /api/ressources/bennes/{id}      - Update benne
DELETE /api/ressources/bennes/{id}      - Delete benne

POST   /api/ressources/tracteurs        - Create tracteur
GET    /api/ressources/tracteurs        - List all tracteurs
GET    /api/ressources/tracteurs/{id}   - Get specific tracteur
PUT    /api/ressources/tracteurs/{id}   - Update tracteur
DELETE /api/ressources/tracteurs/{id}   - Delete tracteur
```

## Integration with Existing App

1. **Import Components** in app.routes.ts
2. **Add Routes** for resource management
3. **Include in Navigation** (sidebar menu)
4. **Configure API URL** in services
5. **Setup Authentication** headers if needed

## Next Steps

1. Integrate with existing authentication/authorization
2. Add Material Design theme
3. Setup API environment variables
4. Add unit tests
5. Implement file upload for images
6. Add batch operations
7. Create reports/analytics views

## Notes

- All services use Angular's HttpClient with RxJS
- Error handling with proper Observable patterns
- Reactive forms for validation
- Standalone components for modern Angular
- Material Design for consistent UI
