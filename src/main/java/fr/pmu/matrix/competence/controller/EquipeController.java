package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.service.EquipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Contrôleur pour la gestion des équipes
 * Correspond à l'API définie dans le fichier equipesapi.yml
 */
@RestController
@RequestMapping("/equipes")
public class EquipeController {

    private final EquipeService equipeService;

    @Autowired
    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    /**
     * Récupère toutes les équipes (GET /equipes)
     * Opération: getAllEquipes
     *
     * @return Liste des équipes
     */
    @GetMapping
    public ResponseEntity<List<Equipe>> getAllEquipes() {
        try {
            List<Equipe> equipes = equipeService.getAllEquipes();
            return ResponseEntity.ok(equipes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des équipes", e);
        }
    }

    /**
     * Récupère une équipe par son code (GET /equipes/{code})
     * Opération: getEquipeByCode
     *
     * @param code Code unique de l'équipe
     * @return Équipe correspondante au code
     */
    @GetMapping("/{code}")
    public ResponseEntity<Equipe> getEquipeByCode(@PathVariable String code) {
        try {
            Equipe equipe = equipeService.getEquipeByCode(code);
            return ResponseEntity.ok(equipe);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération de l'équipe", e);
        }
    }

    /**
     * Crée une nouvelle équipe (POST /equipes)
     * Opération: createEquipe
     *
     * @param request Requête de création d'équipe
     * @return Équipe créée avec code 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Equipe> createEquipe(@RequestBody CreateEquipeRequest request) {
        try {
            Equipe equipe = new Equipe();
            equipe.setCode(request.getCode());
            equipe.setNom(request.getNom());
            equipe.setDescription(request.getDescription());

            Equipe createdEquipe = equipeService.createEquipe(equipe, request.getGroupementCode());
            return new ResponseEntity<>(createdEquipe, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de l'équipe", e);
        }
    }

    /**
     * Met à jour une équipe existante (PUT /equipes/{code})
     * Opération: updateEquipe
     *
     * @param code Code de l'équipe à mettre à jour
     * @param request Requête de mise à jour
     * @return Équipe mise à jour
     */
    @PutMapping("/{code}")
    public ResponseEntity<Equipe> updateEquipe(
            @PathVariable String code,
            @RequestBody UpdateEquipeRequest request) {
        try {
            Equipe equipe = new Equipe();
            equipe.setCode(code);
            equipe.setNom(request.getNom());
            equipe.setDescription(request.getDescription());

            Equipe updatedEquipe = equipeService.updateEquipe(code, equipe, request.getGroupementCode());
            return ResponseEntity.ok(updatedEquipe);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour de l'équipe", e);
        }
    }

    /**
     * Supprime une équipe (DELETE /equipes/{code})
     * Opération: deleteEquipe
     *
     * @param code Code de l'équipe à supprimer
     * @return Réponse sans contenu (204 No Content)
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteEquipe(@PathVariable String code) {
        try {
            equipeService.deleteEquipe(code);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression de l'équipe", e);
        }
    }

    /**
     * Classe pour la requête de création d'une équipe
     * Correspond au schéma CreateEquipeRequest dans l'API
     */
    public static class CreateEquipeRequest {
        private String code;
        private String nom;
        private String description;
        private String groupementCode;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getGroupementCode() {
            return groupementCode;
        }

        public void setGroupementCode(String groupementCode) {
            this.groupementCode = groupementCode;
        }
    }

    /**
     * Classe pour la requête de mise à jour d'une équipe
     * Correspond au schéma UpdateEquipeRequest dans l'API
     */
    public static class UpdateEquipeRequest {
        private String nom;
        private String description;
        private String groupementCode;

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getGroupementCode() {
            return groupementCode;
        }

        public void setGroupementCode(String groupementCode) {
            this.groupementCode = groupementCode;
        }
    }
}