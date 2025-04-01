package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Utilisateur;
import fr.pmu.matrix.competence.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Contrôleur pour la gestion des utilisateurs
 * Correspond à l'API définie dans le fichier utilisateurs-api.yml
 */
@RestController
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Récupère tous les utilisateurs (GET /utilisateurs)
     * Opération: getAllUtilisateurs
     *
     * @return Liste des utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
            return ResponseEntity.ok(utilisateurs);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des utilisateurs", e);
        }
    }

    /**
     * Récupère un utilisateur par son matricule (GET /utilisateurs/{matricule})
     * Opération: getUtilisateurByMatricule
     *
     * @param matricule Matricule de l'utilisateur
     * @return Utilisateur correspondant au matricule
     */
    @GetMapping("/{matricule}")
    public ResponseEntity<Utilisateur> getUtilisateurByMatricule(@PathVariable String matricule) {
        try {
            Utilisateur utilisateur = utilisateurService.getUtilisateurByMatricule(matricule);
            return ResponseEntity.ok(utilisateur);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération de l'utilisateur", e);
        }
    }

    /**
     * Crée un nouvel utilisateur (POST /utilisateurs)
     * Opération: createUtilisateur
     *
     * @param request Requête de création d'utilisateur contenant le matricule et les IDs d'habilitations
     * @return Utilisateur créé avec code 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Utilisateur> createUtilisateur(@RequestBody CreateUtilisateurRequest request) {
        try {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setMatricule(request.getMatricule());

            Utilisateur createdUtilisateur = utilisateurService.createUtilisateur(utilisateur, request.getHabilitationsIds());
            return new ResponseEntity<>(createdUtilisateur, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de l'utilisateur", e);
        }
    }

    /**
     * Met à jour les habilitations d'un utilisateur (PUT /utilisateurs/{matricule})
     * Opération: updateUtilisateurHabilitations
     * Cette opération permet uniquement de modifier la liste des habilitations, pas le matricule
     *
     * @param matricule Matricule de l'utilisateur
     * @param request Requête de mise à jour contenant les nouveaux IDs d'habilitations
     * @return Utilisateur mis à jour
     */
    @PutMapping("/{matricule}")
    public ResponseEntity<Utilisateur> updateUtilisateurHabilitations(
            @PathVariable String matricule,
            @RequestBody UpdateUtilisateurRequest request) {
        try {
            Utilisateur updatedUtilisateur = utilisateurService.updateUtilisateurHabilitations(matricule, request.getHabilitationsIds());
            return ResponseEntity.ok(updatedUtilisateur);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour de l'utilisateur", e);
        }
    }

    /**
     * Supprime un utilisateur (DELETE /utilisateurs/{matricule})
     * Opération: deleteUtilisateur
     *
     * @param matricule Matricule de l'utilisateur à supprimer
     * @return Réponse sans contenu (204 No Content)
     */
    @DeleteMapping("/{matricule}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable String matricule) {
        try {
            utilisateurService.deleteUtilisateur(matricule);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression de l'utilisateur", e);
        }
    }

    /**
     * Classe pour la requête de création d'un utilisateur
     * Correspond au schéma CreateUtilisateurRequest dans l'API
     */
    public static class CreateUtilisateurRequest {
        private String matricule;
        private List<String> habilitationsIds;

        public String getMatricule() {
            return matricule;
        }

        public void setMatricule(String matricule) {
            this.matricule = matricule;
        }

        public List<String> getHabilitationsIds() {
            return habilitationsIds;
        }

        public void setHabilitationsIds(List<String> habilitationsIds) {
            this.habilitationsIds = habilitationsIds;
        }
    }

    /**
     * Classe pour la requête de mise à jour d'un utilisateur
     * Correspond au schéma UpdateUtilisateurRequest dans l'API
     */
    public static class UpdateUtilisateurRequest {
        private List<String> habilitationsIds;

        public List<String> getHabilitationsIds() {
            return habilitationsIds;
        }

        public void setHabilitationsIds(List<String> habilitationsIds) {
            this.habilitationsIds = habilitationsIds;
        }
    }
}