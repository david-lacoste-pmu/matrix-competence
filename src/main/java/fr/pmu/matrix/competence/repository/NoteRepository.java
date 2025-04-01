package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Integer> {
    // Integer est le type de l'identifiant (valeur)
}