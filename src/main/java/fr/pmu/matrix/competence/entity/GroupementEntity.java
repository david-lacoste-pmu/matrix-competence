package fr.pmu.matrix.competence.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "groupements")
@Getter
@Setter
public class GroupementEntity {
    
    @Id
    @Column(name = "code", nullable = false)
    private String code;
    
    @Column(name = "libelle")
    private String libelle;
    
    @Column(name = "direction")
    private String direction;
}