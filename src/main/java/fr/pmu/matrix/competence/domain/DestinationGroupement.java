package fr.pmu.matrix.competence.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Classe représentant un Groupement comme destination pour une demande
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DestinationGroupement extends Destination {
    
    /**
     * Le groupement associé à cette destination
     */
    private Groupement groupement;
    
    /**
     * Constructeur à partir d'un groupement existant
     * @param groupement Le groupement à utiliser comme destination
     */
    public DestinationGroupement(Groupement groupement) {
        this.groupement = groupement;
        setCode(groupement.getCode());
        setNom(groupement.getLibelle());
    }
}