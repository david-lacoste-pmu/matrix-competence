package fr.pmu.matrix.competence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profil {
    private Personne personne;
    private String rapporteur;
    private Date dateDebutDisponibilite;
    private Date dateFinDisponibilite;
}