package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.CompetenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetenceRepository extends JpaRepository<CompetenceEntity, String> {
    // String est le type de l'identifiant (libelle)
}