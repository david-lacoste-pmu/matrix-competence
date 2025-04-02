package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.entity.CompetenceRequiseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenceRequiseRepository extends JpaRepository<CompetenceRequiseEntity, Long> {
    
    /**
     * Recherche les compétences requises pour une demande spécifique
     * 
     * @param demandeId L'identifiant de la demande
     * @return Liste des compétences requises pour cette demande
     */
    List<CompetenceRequiseEntity> findByDemandeId(String demandeId);
    
    /**
     * Recherche les compétences requises pour une compétence spécifique
     * 
     * @param competenceLibelle Le libellé de la compétence
     * @return Liste des compétences requises pour cette compétence
     */
    List<CompetenceRequiseEntity> findByCompetenceLibelle(String competenceLibelle);
    
    /**
     * Recherche les compétences requises avec un niveau de note minimum
     * 
     * @param noteValeur La valeur minimum de la note
     * @return Liste des compétences requises avec une note supérieure ou égale
     */
    @Query("SELECT cr FROM CompetenceRequiseEntity cr WHERE cr.noteRequise.valeur >= :noteValeur")
    List<CompetenceRequiseEntity> findByNoteRequiseValeurMinimum(@Param("noteValeur") int noteValeur);
    
    /**
     * Recherche les compétences requises pour une demande et une compétence spécifique
     * 
     * @param demandeId L'identifiant de la demande
     * @param competenceLibelle Le libellé de la compétence
     * @return La compétence requise correspondante, s'il existe
     */
    CompetenceRequiseEntity findByDemandeIdAndCompetenceLibelle(String demandeId, String competenceLibelle);
}