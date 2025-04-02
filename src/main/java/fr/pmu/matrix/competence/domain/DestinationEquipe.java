package fr.pmu.matrix.competence.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Classe représentant une Equipe comme destination pour une demande
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DestinationEquipe extends Destination {
    
    /**
     * L'équipe associée à cette destination
     */
    private Equipe equipe;
    
    /**
     * Constructeur à partir d'une équipe existante
     * @param equipe L'équipe à utiliser comme destination
     */
    public DestinationEquipe(Equipe equipe) {
        this.equipe = equipe;
        setCode(equipe.getCode());
        setNom(equipe.getNom());
    }
}