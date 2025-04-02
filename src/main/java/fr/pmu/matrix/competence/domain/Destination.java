package fr.pmu.matrix.competence.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe abstraite représentant une destination pour une demande
 * (peut être soit un Groupement, soit une Equipe)
 */
@Data
@NoArgsConstructor
public abstract class Destination {
    /**
     * Code identifiant la destination
     */
    private String code;
    
    /**
     * Nom de la destination
     */
    private String nom;
}