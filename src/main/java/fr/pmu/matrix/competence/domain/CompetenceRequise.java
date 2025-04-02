package fr.pmu.matrix.competence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe représentant une compétence requise dans le cadre d'une demande
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceRequise {
    
    /**
     * La compétence requise
     */
    private Competence competence;
    
    /**
     * Le niveau minimal requis pour cette compétence
     */
    private Note noteRequise;
}