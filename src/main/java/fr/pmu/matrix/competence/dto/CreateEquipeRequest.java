package fr.pmu.matrix.competence.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateEquipeRequest {
    private String code;
    private String nom;
    private String description;
    private String groupementCode;
    private List<CompetenceRequiseDto> profilRecherche;
}