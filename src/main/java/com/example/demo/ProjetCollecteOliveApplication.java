package com.example.demo;

import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Date;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class ProjetCollecteOliveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetCollecteOliveApplication.class, args);
        System.out.println("========================================");
        System.out.println("🌿 Projet Collecte Olives démarré !");
        System.out.println("📍 http://localhost:8080");
        System.out.println("========================================");
    }

    @Bean
    public CommandLineRunner initData(UtilisateurRepository utilisateurRepository) {
        return args -> {
            if (utilisateurRepository.findByEmail("responsable@cooperative.com").isEmpty()) {
                
                Utilisateur responsable = new Utilisateur();
                responsable.setEmail("responsable@cooperative.com");
                responsable.setPrenom("Faiza");
                responsable.setNom("Ghozzi");
                responsable.setTelephone("+216 98 765 432");
                responsable.setRole("responsable");
                responsable.setAdresse("Sfax, Tunisie");
                responsable.setEstActif(true);
                responsable.setDateCreation(new Date());
                
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                responsable.setMotDePasse(encoder.encode("admin123"));
                
                utilisateurRepository.save(responsable);
                
                System.out.println("✅ Utilisateur responsable créé avec succès !");
                System.out.println("   Email: responsable@cooperative.com");
                System.out.println("   Mot de passe: admin123");
            } else {
                System.out.println("ℹ️ Utilisateur responsable existe déjà");
            }
        };
    }
}