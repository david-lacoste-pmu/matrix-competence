package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.ProfilEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfilRepository extends JpaRepository<ProfilEntity, Long> {
    // Méthode pour trouver les profils par identifiant de personne
    List<ProfilEntity> findByPersonneIdentifiant(String personneIdentifiant);
}