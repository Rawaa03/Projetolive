package com.example.demo.controller;

import com.example.demo.dto.dashboard.*;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get Agriculteur (Farmer) Dashboard
     * Only accessible to authenticated users with AGRICULTEUR role
     */
    @GetMapping("/agriculteur")
    @PreAuthorize("hasRole('AGRICULTEUR')")
    public ResponseEntity<AgriculteurDashboardDTO> getAgriculteurDashboard(Authentication authentication) {
        String userId = authentication.getName(); // Get authenticated user ID
        AgriculteurDashboardDTO dashboard = dashboardService.getAgriculteurDashboard(userId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get Responsable (Supervisor) Dashboard
     * Only accessible to authenticated users with RESPONSABLE role
     */
    @GetMapping("/responsable")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public ResponseEntity<ResponsableDashboardDTO> getResponsableDashboard(Authentication authentication) {
        String userId = authentication.getName(); // Get authenticated user ID
        ResponsableDashboardDTO dashboard = dashboardService.getResponsableDashboard(userId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get Admin Dashboard
     * Only accessible to authenticated users with ADMIN role
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDTO> getAdminDashboard() {
        AdminDashboardDTO dashboard = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Fallback endpoint to get dashboard based on user role
     * Used if frontend doesn't know the exact role
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUserDashboard(Authentication authentication) {
        String userId = authentication.getName();
        
        // Get user roles
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AGRICULTEUR"))) {
            return ResponseEntity.ok(dashboardService.getAgriculteurDashboard(userId));
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_RESPONSABLE"))) {
            return ResponseEntity.ok(dashboardService.getResponsableDashboard(userId));
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(dashboardService.getAdminDashboard());
        } else {
            return ResponseEntity.status(403).body("User role not recognized for dashboard access");
        }
    }
}
