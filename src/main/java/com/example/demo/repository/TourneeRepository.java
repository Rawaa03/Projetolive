
package com.example.demo.repository;

import com.example.demo.model.StatutTournee;
import com.example.demo.model.Tournee;
import com.example.demo.model.Verger;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourneeRepository extends MongoRepository<Tournee, String> {

    Optional<Tournee> findByCode(String code);

    List<Tournee> findByStatut(StatutTournee statut);

    List<Tournee> findByVerger(Verger verger);

    @Query("{ 'verger.$id': { $oid: ?0 } }")
    List<Tournee> findByVergerId(String vergerId);

    @Query("{ 'verger.$id': { $oid: ?0 }, 'statut': 'TERMINEE' }")
    List<Tournee> findTermineesByVergerId(String vergerId);

    @Query("{ 'travailleurs.$id': { $oid: ?0 } }")
    List<Tournee> findByTravailleurId(String travailleurId);

    @Query("{ 'benne.$id': { $oid: ?0 } }")
    List<Tournee> findByBenneId(String benneId);

    @Query("{ 'tracteur.$id': { $oid: ?0 } }")
    List<Tournee> findByTracteurId(String tracteurId);

    @Query("{ 'statut': { $in: ['PLANIFIEE', 'EN_COURS'] } }")
    List<Tournee> findActive();

    boolean existsByCode(String code);

    // ── Availability overlap queries ───────────────────────────────
    // A conflict exists when an active tournée overlaps [dateDebut, dateFin]:
    //   existing.dateDebut < requested.dateFin
    //   AND existing.dateFin > requested.dateDebut
    // We exclude the tournée being edited (excludeId) so update works correctly.

    /**
     * Active tournées using this benne that overlap the given window.
     * excludeId: pass the current tournée ID when updating (pass a dummy value like "NONE" when creating).
     */
    @Query("{ " +
            "  'benne.$id': { $oid: ?0 }, " +
            "  'statut': { $in: ['PLANIFIEE', 'EN_COURS'] }, " +
            "  '_id': { $ne: { $oid: ?3 } }, " +
            "  'dateDebut': { $lt: ?2 }, " +
            "  'dateFin':   { $gt: ?1 } " +
            "}")
    List<Tournee> findConflictsByBenne(String benneId, Date debut, Date fin, String excludeId);

    /**
     * Active tournées using this tracteur that overlap the given window.
     */
    @Query("{ " +
            "  'tracteur.$id': { $oid: ?0 }, " +
            "  'statut': { $in: ['PLANIFIEE', 'EN_COURS'] }, " +
            "  '_id': { $ne: { $oid: ?3 } }, " +
            "  'dateDebut': { $lt: ?2 }, " +
            "  'dateFin':   { $gt: ?1 } " +
            "}")
    List<Tournee> findConflictsByTracteur(String tracteurId, Date debut, Date fin, String excludeId);

    /**
     * Active tournées where this travailleur is assigned that overlap the given window.
     */
    @Query("{ " +
            "  'travailleurs.$id': { $oid: ?0 }, " +
            "  'statut': { $in: ['PLANIFIEE', 'EN_COURS'] }, " +
            "  '_id': { $ne: { $oid: ?3 } }, " +
            "  'dateDebut': { $lt: ?2 }, " +
            "  'dateFin':   { $gt: ?1 } " +
            "}")
    List<Tournee> findConflictsByTravailleur(String travailleurId, Date debut, Date fin, String excludeId);
}