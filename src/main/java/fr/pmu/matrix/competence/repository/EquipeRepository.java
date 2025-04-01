package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.EquipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipeRepository extends JpaRepository<EquipeEntity, String> {
    // String est le type de l'identifiant (code)
}