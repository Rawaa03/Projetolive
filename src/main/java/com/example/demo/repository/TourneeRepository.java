package com.example.demo.repository;

import com.example.demo.model.StatutTournee;
import com.example.demo.model.Tournee;
import com.example.demo.model.Verger;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourneeRepository extends MongoRepository<Tournee, String> {

    Optional<Tournee> findByCode(String code);

    List<Tournee> findByVerger(Verger verger);

    List<Tournee> findByStatut(StatutTournee statut);

    @Query("{ 'verger' : ?0, 'statut' : 'TERMINEE' }")
    List<Tournee> findTermineesByVerger(Verger verger);

    @Query("{ 'verger.$id' : { $oid: ?0 }, 'statut' : 'TERMINEE' }")
    List<Tournee> findTermineesByVergerId(String vergerId);

    @Query("{ 'verger.$id' : { $oid: ?0 } }")
    List<Tournee> findByVergerId(String vergerId);

    @Query("{ 'travailleurIds' : ?0 }")
    List<Tournee> findByTravailleurId(String travailleurId);

    @Query("{ 'benneId' : ?0 }")
    List<Tournee> findByBenneId(String benneId);

    @Query("{ 'tracteurId' : ?0 }")
    List<Tournee> findByTracteurId(String tracteurId);

    @Query("{ 'statut' : { $in: ['PLANIFIEE', 'EN_COURS'] } }")
    List<Tournee> findActive();

    boolean existsByCode(String code);
}