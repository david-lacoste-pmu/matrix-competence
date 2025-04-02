package fr.pmu.matrix.competence.dto;

import fr.pmu.matrix.competence.domain.Nature;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class DemandeCreationRequest {
    private String matriculeDemandeur;
    private String description;
    private Nature nature;
    private Date dateDebut;
    private Date dateFin;
    private DestinationRequest destination;
    private List<CompetenceRequiseRequest> competencesRecherchees;
}