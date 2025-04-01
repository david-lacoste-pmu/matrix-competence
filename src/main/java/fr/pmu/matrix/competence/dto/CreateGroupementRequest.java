package fr.pmu.matrix.competence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupementRequest {
    private String code;
    private String libelle;
    private String direction;
}