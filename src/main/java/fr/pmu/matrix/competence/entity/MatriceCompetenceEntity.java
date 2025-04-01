package fr.pmu.matrix.competence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "matrice_competence")
@IdClass(MatriceCompetenceEntity.MatriceCompetenceId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriceCompetenceEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "personne_id")
    private PersonneEntity personne;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "competence_id")
    private CompetenceEntity competence;
    
    @ManyToOne
    @JoinColumn(name = "note_id")
    private NoteEntity note;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatriceCompetenceId implements Serializable {
        private static final long serialVersionUID = 1L;
        private String personne;
        private String competence;
    }
}