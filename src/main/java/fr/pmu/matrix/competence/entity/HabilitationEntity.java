package fr.pmu.matrix.competence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "habilitation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabilitationEntity {
    @Id
    private String code;
    
    private String description;
    
    @ManyToMany(mappedBy = "habilitations")
    private List<UtilisateurEntity> utilisateurs = new ArrayList<>();
}