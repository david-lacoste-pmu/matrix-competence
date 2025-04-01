package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Habilitation;
import fr.pmu.matrix.competence.service.HabilitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Contrôleur pour la gestion des habilitations
 * Correspond à l'API définie dans le fichier habilitations-api.yml
 */
@RestController
@RequestMapping("/habilitations")
public class HabilitationController {

    private final HabilitationService habilitationService;

    @Autowired
    public HabilitationController(HabilitationService habilitationService) {
        this.habilitationService = habilitationService;
    }

    /**
     * Récupère toutes les habilitations (GET /habilitations)
     *
     * @return Liste des habilitations
     */
    @GetMapping
    public ResponseEntity<List<Habilitation>> getAllHabilitations() {
        try {
            List<Habilitation> habilitations = habilitationService.getAllHabilitations();
            return ResponseEntity.ok(habilitations);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des habilitations", e);
        }
    }

    /**
     * Récupère une habilitation par son code (GET /habilitations/{code})
     *
     * @param code Code unique de l'habilitation
     * @return Habilitation correspondante au code
     */
    @GetMapping("/{code}")
    public ResponseEntity<Habilitation> getHabilitationByCode(@PathVariable String code) {
        try {
            Habilitation habilitation = habilitationService.getHabilitationByCode(code);
            return ResponseEntity.ok(habilitation);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération de l'habilitation", e);
        }
    }

    /**
     * Crée une nouvelle habilitation (POST /habilitations)
     *
     * @param request Requête contenant les données de l'habilitation à créer
     * @return Habilitation créée
     */
    @PostMapping
    public ResponseEntity<Habilitation> createHabilitation(@RequestBody CreateHabilitationRequest request) {
        try {
            Habilitation habilitation = new Habilitation();
            habilitation.setCode(request.getCode());
            habilitation.setDescription(request.getDescription());

            Habilitation createdHabilitation = habilitationService.createHabilitation(habilitation);
            return new ResponseEntity<>(createdHabilitation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de l'habilitation", e);
        }
    }

    /**
     * Met à jour une habilitation existante (PUT /habilitations/{code})
     *
     * @param code Code de l'habilitation à mettre à jour
     * @param request Requête contenant la nouvelle description
     * @return Habilitation mise à jour
     */
    @PutMapping("/{code}")
    public ResponseEntity<Habilitation> updateHabilitation(
            @PathVariable String code,
            @RequestBody UpdateHabilitationRequest request) {
        try {
            Habilitation habilitation = new Habilitation();
            habilitation.setCode(code); // Le code ne change pas
            habilitation.setDescription(request.getDescription());

            Habilitation updatedHabilitation = habilitationService.updateHabilitation(code, habilitation);
            return ResponseEntity.ok(updatedHabilitation);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour de l'habilitation", e);
        }
    }

    /**
     * Supprime une habilitation (DELETE /habilitations/{code})
     *
     * @param code Code de l'habilitation à supprimer
     * @return Réponse sans contenu (204)
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteHabilitation(@PathVariable String code) {
        try {
            habilitationService.deleteHabilitation(code);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression de l'habilitation", e);
        }
    }

    /**
     * Classe pour la requête de création d'une habilitation
     * Correspond au schema CreateHabilitationRequest dans l'API
     */
    public static class CreateHabilitationRequest {
        private String code;
        private String description;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * Classe pour la requête de mise à jour d'une habilitation
     * Correspond au schema UpdateHabilitationRequest dans l'API
     */
    public static class UpdateHabilitationRequest {
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}