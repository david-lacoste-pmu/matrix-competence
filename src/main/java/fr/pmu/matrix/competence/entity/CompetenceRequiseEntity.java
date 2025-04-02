package fr.pmu.matrix.competence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité JPA représentant une compétence requise dans une demande
 */
@Entity
@Table(name = "competence_requise")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceRequiseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandeEntity demande;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "competence_libelle", nullable = false)
    private CompetenceEntity competence;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "note_valeur", nullable = false)
    private NoteEntity noteRequise;
}