package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.dto.CreateCompetenceRequest;
import fr.pmu.matrix.competence.dto.UpdateCompetenceRequest;
import fr.pmu.matrix.competence.service.CompetenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/competences")
public class CompetenceController {

    private final CompetenceService competenceService;

    @Autowired
    public CompetenceController(CompetenceService competenceService) {
        this.competenceService = competenceService;
    }

    /**
     * Récupère toutes les compétences
     * 
     * @return Liste des compétences
     */
    @GetMapping
    public ResponseEntity<List<Competence>> getAllCompetences() {
        try {
            List<Competence> competences = competenceService.getAllCompetences();
            return ResponseEntity.ok(competences);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crée une nouvelle compétence
     * 
     * @param request Les données de la compétence à créer
     * @return La compétence créée
     */
    @PostMapping
    public ResponseEntity<Competence> createCompetence(@RequestBody CreateCompetenceRequest request) {
        try {
            Competence competence = new Competence();
            competence.setLibelle(request.getLibelle());
            competence.setDescription(request.getDescription());
            
            Competence createdCompetence = competenceService.createCompetence(competence);
            return new ResponseEntity<>(createdCompetence, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (e.getMessage().contains("invalide") || request.getLibelle() == null || request.getLibelle().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère une compétence par son libellé
     * 
     * @param libelle Libellé de la compétence
     * @return La compétence correspondante
     */
    @GetMapping("/{libelle}")
    public ResponseEntity<Competence> getCompetenceByLibelle(@PathVariable String libelle) {
        try {
            Competence competence = competenceService.getCompetenceByLibelle(libelle);
            return ResponseEntity.ok(competence);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvée")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Met à jour une compétence
     * 
     * @param libelle Libellé de la compétence à mettre à jour
     * @param request Les nouvelles données de la compétence
     * @return La compétence mise à jour
     */
    @PutMapping("/{libelle}")
    public ResponseEntity<Competence> updateCompetence(
            @PathVariable String libelle,
            @RequestBody UpdateCompetenceRequest request) {
        try {
            Competence competence = new Competence();
            competence.setDescription(request.getDescription());
            
            Competence updatedCompetence = competenceService.updateCompetence(libelle, competence);
            return ResponseEntity.ok(updatedCompetence);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvée")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (request.getDescription() == null || request.getDescription().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprime une compétence
     * 
     * @param libelle Libellé de la compétence à supprimer
     * @return Réponse sans contenu
     */
    @DeleteMapping("/{libelle}")
    public ResponseEntity<Void> deleteCompetence(@PathVariable String libelle) {
        try {
            competenceService.deleteCompetence(libelle);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvée")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}