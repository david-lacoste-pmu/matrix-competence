package fr.pmu.matrix.competence.domain;

import java.util.List;
import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    private String matricule;
    private List<Habilitation> habilitations = new ArrayList<>();
}