package fr.pmu.matrix.competence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {
    private Date dateDebut;
    private Date dateFin;
    private String type;
    private Criticite criticite;
    private List<Object> participants; // Cette liste peut contenir des objets de type Personne ou Equipe
    
    public enum Criticite {
        MINOR, CRITICAL, MAJOR
    }
}