package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.PersonneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonneRepository extends JpaRepository<PersonneEntity, String> {
    // Ajoute la méthode manquante pour récupérer les personnes par code d'équipe
    List<PersonneEntity> findByEquipeCode(String equipeCode);
}