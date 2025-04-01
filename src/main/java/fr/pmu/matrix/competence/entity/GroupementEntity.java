package fr.pmu.matrix.competence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "groupement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupementEntity {
    private String libelle;
    
    @Id
    private String code;
    
    private String direction;
}