package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.Travailleur;
import com.example.demo.model.Utilisateur;

public interface TravailleurRepository extends MongoRepository<Travailleur, String>{

}
