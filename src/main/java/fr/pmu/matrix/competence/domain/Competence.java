package fr.pmu.matrix.competence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Competence {
    private String libelle;
    private String description;
}