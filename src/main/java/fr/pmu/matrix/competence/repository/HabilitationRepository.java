package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.HabilitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabilitationRepository extends JpaRepository<HabilitationEntity, String> {
    // String est le type de l'identifiant (code)
}