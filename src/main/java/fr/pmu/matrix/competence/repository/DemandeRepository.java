package fr.pmu.matrix.competence.repository;

import fr.pmu.matrix.competence.domain.Nature;
import fr.pmu.matrix.competence.entity.DemandeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<DemandeEntity, String> {
    
    /**
     * Recherche les demandes par matricule du demandeur
     * 
     * @param matriculeDemandeur Le matricule du demandeur
     * @return Liste des demandes correspondantes
     */
    List<DemandeEntity> findByMatriculeDemandeur(String matriculeDemandeur);
    
    /**
     * Recherche les demandes par nature
     * 
     * @param nature La nature de la demande
     * @return Liste des demandes correspondantes
     */
    List<DemandeEntity> findByNature(Nature nature);
    
    /**
     * Recherche les demandes pour une équipe spécifique
     * 
     * @param destinationCode Le code de l'équipe
     * @param estGroupement false pour rechercher des équipes
     * @return Liste des demandes pour cette équipe
     */
    List<DemandeEntity> findByDestinationCodeAndEstGroupement(String destinationCode, Boolean estGroupement);
    
    /**
     * Recherche les demandes pour un groupement spécifique
     * 
     * @param destinationCode Le code du groupement
     * @param estGroupement true pour rechercher des groupements
     * @return Liste des demandes pour ce groupement
     */
    default List<DemandeEntity> findByGroupement(String destinationCode) {
        return findByDestinationCodeAndEstGroupement(destinationCode, true);
    }
    
    /**
     * Recherche les demandes pour une équipe spécifique
     * 
     * @param destinationCode Le code de l'équipe
     * @return Liste des demandes pour cette équipe
     */
    default List<DemandeEntity> findByEquipe(String destinationCode) {
        return findByDestinationCodeAndEstGroupement(destinationCode, false);
    }
    
    /**
     * Recherche les demandes dont la date de début est supérieure ou égale à la date spécifiée
     * 
     * @param date La date de référence
     * @return Liste des demandes correspondantes
     */
    List<DemandeEntity> findByDateDebutGreaterThanEqual(Date date);
    
    /**
     * Recherche les demandes dont la date de fin est inférieure ou égale à la date spécifiée
     * 
     * @param date La date de référence
     * @return Liste des demandes correspondantes
     */
    List<DemandeEntity> findByDateFinLessThanEqual(Date date);
    
    /**
     * Recherche les demandes actives à une date donnée
     * 
     * @param date La date de référence
     * @return Liste des demandes actives à cette date
     */
    @Query("SELECT d FROM DemandeEntity d WHERE d.dateDebut <= :date AND (d.dateFin IS NULL OR d.dateFin >= :date)")
    List<DemandeEntity> findActiveAtDate(@Param("date") Date date);
    
    /**
     * Recherche les demandes qui requièrent une compétence spécifique
     * 
     * @param competenceLibelle Le libellé de la compétence
     * @return Liste des demandes qui requièrent cette compétence
     */
    @Query("SELECT d FROM DemandeEntity d JOIN d.competencesRequises cr WHERE cr.competence.libelle = :competenceLibelle")
    List<DemandeEntity> findByCompetenceRequise(@Param("competenceLibelle") String competenceLibelle);
    
    /**
     * Recherche les demandes qui requièrent au moins une des compétences spécifiées
     * 
     * @param competenceLibelles Liste des libellés de compétences
     * @return Liste des demandes qui requièrent au moins une de ces compétences
     */
    @Query("SELECT DISTINCT d FROM DemandeEntity d JOIN d.competencesRequises cr WHERE cr.competence.libelle IN :competenceLibelles")
    List<DemandeEntity> findByCompetencesRequises(@Param("competenceLibelles") Collection<String> competenceLibelles);
    
    /**
     * Recherche les demandes qui requièrent au moins une des compétences spécifiées avec au moins une des notes spécifiées
     * 
     * @param competenceLibelles Liste des libellés de compétences
     * @param noteValeurs Liste des valeurs de notes
     * @return Liste des demandes correspondantes
     */
    @Query("SELECT DISTINCT d FROM DemandeEntity d JOIN d.competencesRequises cr " +
           "WHERE cr.competence.libelle IN :competenceLibelles AND cr.noteRequise.valeur IN :noteValeurs")
    List<DemandeEntity> findByCompetencesRequisesAndNotes(
            @Param("competenceLibelles") Collection<String> competenceLibelles,
            @Param("noteValeurs") Collection<Integer> noteValeurs);
    
    /**
     * Recherche les demandes qui requièrent des compétences avec au moins une des notes spécifiées
     * 
     * @param noteValeurs Liste des valeurs de notes
     * @return Liste des demandes correspondantes
     */
    @Query("SELECT DISTINCT d FROM DemandeEntity d JOIN d.competencesRequises cr " +
           "WHERE cr.noteRequise.valeur IN :noteValeurs")
    List<DemandeEntity> findByNotes(@Param("noteValeurs") Collection<Integer> noteValeurs);
}