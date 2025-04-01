package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.UtilisateurEntity;
import fr.pmu.matrix.competence.entity.HabilitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UtilisateurRepository extends JpaRepository<UtilisateurEntity, String> {
    // String est le type de l'identifiant (matricule)
    
    List<UtilisateurEntity> findByHabilitationsContaining(HabilitationEntity habilitation);
}