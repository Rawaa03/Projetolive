package com.example.demo.service;

import com.example.demo.dto.dashboard.*;
import com.example.demo.model.Utilisateur;

public interface DashboardService {
    
    // Agriculteur Dashboard
    AgriculteurDashboardDTO getAgriculteurDashboard(String userId);
    
    // Responsable Dashboard
    ResponsableDashboardDTO getResponsableDashboard(String userId);
    
    // Admin Dashboard
    AdminDashboardDTO getAdminDashboard();
}
