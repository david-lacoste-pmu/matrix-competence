package fr.pmu.matrix.competence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Personne {
    private String identifiant;
    private String nom;
    private String prenom;
    private String poste;
    private Equipe equipe;
}