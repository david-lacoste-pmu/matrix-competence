package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.AbsenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<AbsenceEntity, Long> {
    // Méthode pour trouver les absences par identifiant de personne
    List<AbsenceEntity> findByPersonneIdentifiant(String personneIdentifiant);
    
    // Méthode pour trouver les absences entre deux dates
    List<AbsenceEntity> findByDateDebutLessThanEqualAndDateFinGreaterThanEqual(Date dateFin, Date dateDebut);
    
    // Méthode pour trouver les absences par identifiant de personne et période
    List<AbsenceEntity> findByPersonneIdentifiantAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
        String personneIdentifiant, Date dateFin, Date dateDebut);
}