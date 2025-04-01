package fr.pmu.matrix.competence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMatriceCompetenceRequest {
    private String personneId;
    private String competenceId;
    private int noteValeur;
}