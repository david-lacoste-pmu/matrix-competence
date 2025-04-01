package fr.pmu.matrix.competence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriceCompetence {
    private Personne personne;
    private Competence competence;
    private Note note;
}