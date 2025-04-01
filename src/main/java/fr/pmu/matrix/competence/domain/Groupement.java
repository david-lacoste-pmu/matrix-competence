package fr.pmu.matrix.competence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Groupement {
    private String code;
    private String libelle;
    private String direction;
}