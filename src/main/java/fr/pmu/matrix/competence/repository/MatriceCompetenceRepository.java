package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.MatriceCompetenceEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.entity.CompetenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatriceCompetenceRepository extends JpaRepository<MatriceCompetenceEntity, MatriceCompetenceEntity.MatriceCompetenceId> {
    // MatriceCompetenceId est la classe ID composite
    
    List<MatriceCompetenceEntity> findByPersonne(PersonneEntity personne);
    
    List<MatriceCompetenceEntity> findByCompetence(CompetenceEntity competence);
    
    Optional<MatriceCompetenceEntity> findByPersonneAndCompetence(PersonneEntity personne, CompetenceEntity competence);
}