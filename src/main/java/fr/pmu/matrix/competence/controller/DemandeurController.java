package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.MatriceCompetence;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.domain.Profil;
import fr.pmu.matrix.competence.service.DemandeurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/demandeurs")
public class DemandeurController {

    private final DemandeurService demandeurService;

    @Autowired
    public DemandeurController(DemandeurService demandeurService) {
        this.demandeurService = demandeurService;
    }

    /**
     * Récupère toutes les personnes disponibles sur le marché 
     * (sortantes ou cherchant un nouveau challenge)
     * 
     * @return Liste des personnes disponibles avec leurs profils
     */
    @GetMapping("/personnes-disponibles")
    public ResponseEntity<List<Profil>> getPersonnesDisponibles() {
        List<Profil> profils = demandeurService.getPersonnesDisponibles();
        return ResponseEntity.ok(profils);
    }

    /**
     * Récupère la matrice de compétence complète pour une personne disponible
     * 
     * @param personneId Identifiant de la personne
     * @return Liste des évaluations de compétence pour cette personne
     */
    @GetMapping("/personnes-disponibles/{personneId}/competences")
    public ResponseEntity<List<MatriceCompetence>> getMatriceCompetencePersonne(@PathVariable String personneId) {
        try {
            List<MatriceCompetence> matriceCompetences = demandeurService.getMatriceCompetencePersonneDisponible(personneId);
            return ResponseEntity.ok(matriceCompetences);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Filtre les personnes disponibles selon un niveau de note minimal pour certaines compétences
     * 
     * @param competences Liste des libellés de compétences
     * @param noteMinimale Note minimale requise pour chaque compétence
     * @return Liste des personnes répondant aux critères
     */
    @GetMapping("/filtrer-par-notes")
    public ResponseEntity<List<Personne>> filtrerPersonnesParNotes(
            @RequestParam List<String> competences,
            @RequestParam int noteMinimale) {
        List<Personne> personnes = demandeurService.filtrerPersonnesParNotes(competences, noteMinimale);
        return ResponseEntity.ok(personnes);
    }

    /**
     * Filtre les personnes disponibles selon des compétences spécifiques recherchées
     * 
     * @param competences Liste des libellés de compétences recherchées
     * @return Liste des personnes ayant ces compétences
     */
    @GetMapping("/filtrer-par-competences")
    public ResponseEntity<List<Personne>> filtrerPersonnesParCompetences(
            @RequestParam List<String> competences) {
        List<Personne> personnes = demandeurService.filtrerPersonnesParCompetences(competences);
        return ResponseEntity.ok(personnes);
    }
}