package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.PersonneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonneRepository extends JpaRepository<PersonneEntity, String> {
    // String est le type de l'identifiant (identifiant)
}