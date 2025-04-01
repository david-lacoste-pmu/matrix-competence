package fr.pmu.matrix.competence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePersonneRequest {
    private String nom;
    private String prenom;
    private String poste;
    private String equipeId;
}