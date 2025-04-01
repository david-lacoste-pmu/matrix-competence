package fr.pmu.matrix.competence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipe {
    private String code;
    private String nom;
    private String description;
    private Groupement groupement;
    private List<Personne> membres = new ArrayList<>();
}