package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.CompetenceRequise;
import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.dto.CompetenceRequiseDto;
import fr.pmu.matrix.competence.dto.CreateEquipeRequest;
import fr.pmu.matrix.competence.dto.UpdateEquipeRequest;
import fr.pmu.matrix.competence.service.CompetenceService;
import fr.pmu.matrix.competence.service.EquipeService;
import fr.pmu.matrix.competence.service.NoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur pour la gestion des équipes
 * Correspond à l'API définie dans le fichier equipesapi.yml
 */
@RestController
@RequestMapping("/equipes")
public class EquipeController {

    private final EquipeService equipeService;
    private final CompetenceService competenceService;
    private final NoteService noteService;

    @Autowired
    public EquipeController(EquipeService equipeService, 
                           CompetenceService competenceService,
                           NoteService noteService) {
        this.equipeService = equipeService;
        this.competenceService = competenceService;
        this.noteService = noteService;
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
            
            // Traitement du profil de recherche si présent
            if (request.getProfilRecherche() != null && !request.getProfilRecherche().isEmpty()) {
                List<CompetenceRequise> competencesRequises = new ArrayList<>();
                for (CompetenceRequiseDto dto : request.getProfilRecherche()) {
                    var competence = competenceService.getCompetenceByLibelle(dto.getCompetenceLibelle());
                    var note = noteService.getNoteByValeur(dto.getNoteValeur());
                    competencesRequises.add(new CompetenceRequise(competence, note));
                }
                equipe.setProfilRecherche(competencesRequises);
            }

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
            
            // Traitement du profil de recherche si présent
            if (request.getProfilRecherche() != null) {
                List<CompetenceRequise> competencesRequises = new ArrayList<>();
                for (CompetenceRequiseDto dto : request.getProfilRecherche()) {
                    var competence = competenceService.getCompetenceByLibelle(dto.getCompetenceLibelle());
                    var note = noteService.getNoteByValeur(dto.getNoteValeur());
                    competencesRequises.add(new CompetenceRequise(competence, note));
                }
                equipe.setProfilRecherche(competencesRequises);
            }

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
     * Récupère le profil de recherche d'une équipe
     * 
     * @param code Code de l'équipe
     * @return Liste des compétences requises
     */
    @GetMapping("/{code}/profil-recherche")
    public ResponseEntity<List<CompetenceRequise>> getProfilRecherche(@PathVariable String code) {
        try {
            List<CompetenceRequise> profil = equipeService.getProfilRecherche(code);
            return ResponseEntity.ok(profil);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération du profil", e);
        }
    }
    
    /**
     * Met à jour le profil de recherche d'une équipe
     * 
     * @param code Code de l'équipe
     * @param dtos Liste des compétences requises en DTO
     * @return Équipe mise à jour
     */
    @PutMapping("/{code}/profil-recherche")
    public ResponseEntity<Equipe> updateProfilRecherche(
            @PathVariable String code,
            @RequestBody List<CompetenceRequiseDto> dtos) {
        try {
            List<CompetenceRequise> competencesRequises = new ArrayList<>();
            for (CompetenceRequiseDto dto : dtos) {
                var competence = competenceService.getCompetenceByLibelle(dto.getCompetenceLibelle());
                var note = noteService.getNoteByValeur(dto.getNoteValeur());
                competencesRequises.add(new CompetenceRequise(competence, note));
            }
            
            Equipe updatedEquipe = equipeService.updateProfilRecherche(code, competencesRequises);
            return ResponseEntity.ok(updatedEquipe);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("trouvée")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du profil", e);
        }
    }
    
    /**
     * Ajoute une compétence requise au profil de recherche
     * 
     * @param code Code de l'équipe
     * @param dto DTO de la compétence requise
     * @return Équipe mise à jour
     */
    @PostMapping("/{code}/profil-recherche")
    public ResponseEntity<Equipe> addCompetenceRequiseToProfilRecherche(
            @PathVariable String code,
            @RequestBody CompetenceRequiseDto dto) {
        try {
            var competence = competenceService.getCompetenceByLibelle(dto.getCompetenceLibelle());
            var note = noteService.getNoteByValeur(dto.getNoteValeur());
            CompetenceRequise competenceRequise = new CompetenceRequise(competence, note);
            
            Equipe updatedEquipe = equipeService.addCompetenceRequiseToProfilRecherche(code, competenceRequise);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedEquipe);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("trouvée")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            } else if (e.getMessage().contains("déjà partie")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'ajout au profil", e);
        }
    }
    
    /**
     * Supprime une compétence requise du profil de recherche
     * 
     * @param code Code de l'équipe
     * @param competenceLibelle Libellé de la compétence
     * @return Équipe mise à jour
     */
    @DeleteMapping("/{code}/profil-recherche/{competenceLibelle}")
    public ResponseEntity<Equipe> removeCompetenceRequiseFromProfilRecherche(
            @PathVariable String code,
            @PathVariable String competenceLibelle) {
        try {
            Equipe updatedEquipe = equipeService.removeCompetenceRequiseFromProfilRecherche(code, competenceLibelle);
            return ResponseEntity.ok(updatedEquipe);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("trouvée")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression du profil", e);
        }
    }
}