package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.MatriceCompetence;
import fr.pmu.matrix.competence.dto.CreateMatriceCompetenceRequest;
import fr.pmu.matrix.competence.dto.UpdateMatriceCompetenceRequest;
import fr.pmu.matrix.competence.service.MatriceCompetenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matrices-competences")
public class MatriceCompetenceController {

    private final MatriceCompetenceService matriceCompetenceService;

    @Autowired
    public MatriceCompetenceController(MatriceCompetenceService matriceCompetenceService) {
        this.matriceCompetenceService = matriceCompetenceService;
    }

    /**
     * Récupère toutes les matrices de compétences
     * 
     * @return Liste des matrices de compétences
     */
    @GetMapping
    public ResponseEntity<List<MatriceCompetence>> getAllMatricesCompetences() {
        try {
            List<MatriceCompetence> matrices = matriceCompetenceService.getAllMatricesCompetences();
            return ResponseEntity.ok(matrices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crée une nouvelle matrice de compétence
     * 
     * @param request La requête contenant les informations de la matrice à créer
     * @return La matrice de compétence créée
     */
    @PostMapping
    public ResponseEntity<MatriceCompetence> createMatriceCompetence(@RequestBody CreateMatriceCompetenceRequest request) {
        try {
            MatriceCompetence matrice = matriceCompetenceService.createMatriceCompetence(
                    request.getPersonneId(), 
                    request.getCompetenceId(), 
                    request.getNoteValeur());
            
            return new ResponseEntity<>(matrice, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else if (e.getMessage().contains("non trouvée")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère une matrice de compétence spécifique
     * 
     * @param personneId Identifiant de la personne
     * @param competenceId Identifiant de la compétence
     * @return La matrice de compétence correspondante
     */
    @GetMapping("/personnes/{personneId}/competences/{competenceId}")
    public ResponseEntity<MatriceCompetence> getMatriceCompetence(
            @PathVariable String personneId, 
            @PathVariable String competenceId) {
        try {
            MatriceCompetence matrice = matriceCompetenceService.getMatriceCompetence(personneId, competenceId);
            return ResponseEntity.ok(matrice);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvée")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Met à jour une matrice de compétence existante
     * 
     * @param personneId Identifiant de la personne
     * @param competenceId Identifiant de la compétence
     * @param request La requête contenant les nouvelles informations
     * @return La matrice de compétence mise à jour
     */
    @PutMapping("/personnes/{personneId}/competences/{competenceId}")
    public ResponseEntity<MatriceCompetence> updateMatriceCompetence(
            @PathVariable String personneId, 
            @PathVariable String competenceId, 
            @RequestBody UpdateMatriceCompetenceRequest request) {
        try {
            MatriceCompetence matrice = matriceCompetenceService.updateMatriceCompetence(
                    personneId, 
                    competenceId, 
                    request.getNoteValeur());
            
            return ResponseEntity.ok(matrice);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvée")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getMessage().contains("Note non trouvée")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprime une matrice de compétence
     * 
     * @param personneId Identifiant de la personne
     * @param competenceId Identifiant de la compétence
     * @return Réponse sans contenu
     */
    @DeleteMapping("/personnes/{personneId}/competences/{competenceId}")
    public ResponseEntity<Void> deleteMatriceCompetence(
            @PathVariable String personneId, 
            @PathVariable String competenceId) {
        try {
            matriceCompetenceService.deleteMatriceCompetence(personneId, competenceId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvée")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère toutes les compétences d'une personne
     * 
     * @param personneId Identifiant de la personne
     * @return Liste des matrices de compétences pour cette personne
     */
    @GetMapping("/personnes/{personneId}")
    public ResponseEntity<List<MatriceCompetence>> getCompetencesByPersonne(@PathVariable String personneId) {
        try {
            List<MatriceCompetence> matrices = matriceCompetenceService.getCompetencesByPersonne(personneId);
            return ResponseEntity.ok(matrices);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Personne non trouvée")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère toutes les personnes possédant une compétence donnée
     * 
     * @param competenceId Identifiant de la compétence
     * @return Liste des matrices de compétences pour cette compétence
     */
    @GetMapping("/competences/{competenceId}")
    public ResponseEntity<List<MatriceCompetence>> getPersonnesByCompetence(@PathVariable String competenceId) {
        try {
            List<MatriceCompetence> matrices = matriceCompetenceService.getPersonnesByCompetence(competenceId);
            return ResponseEntity.ok(matrices);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Compétence non trouvée")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}