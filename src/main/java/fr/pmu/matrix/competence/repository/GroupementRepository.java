package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.GroupementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupementRepository extends JpaRepository<GroupementEntity, String> {
    // String est le type de l'identifiant (code)
}