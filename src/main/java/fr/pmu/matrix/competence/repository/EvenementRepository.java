package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.EvenementEntity;
import fr.pmu.matrix.competence.entity.EvenementEntity.Criticite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<EvenementEntity, Long> {
    // Méthode pour trouver les événements par criticité
    List<EvenementEntity> findByCriticite(Criticite criticite);
    
    // Méthode pour trouver les événements par type
    List<EvenementEntity> findByType(String type);
    
    // Méthode pour trouver les événements entre deux dates
    List<EvenementEntity> findByDateDebutGreaterThanEqualAndDateFinLessThanEqual(Date dateDebut, Date dateFin);
    
    // Méthode pour trouver les événements auxquels une personne participe
    @Query("SELECT e FROM EvenementEntity e JOIN e.personneParticipants p WHERE p.identifiant = :personneId")
    List<EvenementEntity> findByPersonneParticipant(@Param("personneId") String personneId);
    
    // Méthode pour trouver les événements auxquels une équipe participe
    @Query("SELECT e FROM EvenementEntity e JOIN e.equipeParticipants eq WHERE eq.code = :equipeCode")
    List<EvenementEntity> findByEquipeParticipant(@Param("equipeCode") String equipeCode);
}