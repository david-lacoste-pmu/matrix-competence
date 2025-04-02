package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.domain.CompetenceRequise;
import fr.pmu.matrix.competence.domain.Demande;
import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.dto.CompetenceRequiseCreationDto;
import fr.pmu.matrix.competence.dto.CompetenceRequiseUpdateDto;
import fr.pmu.matrix.competence.service.CompetenceService;
import fr.pmu.matrix.competence.service.DemandeService;
import fr.pmu.matrix.competence.service.NoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/demandes/{demandeId}/competences-requises")
public class CompetenceRequiseController {

    private final DemandeService demandeService;
    private final CompetenceService competenceService;
    private final NoteService noteService;

    @Autowired
    public CompetenceRequiseController(DemandeService demandeService,
                                       CompetenceService competenceService,
                                       NoteService noteService) {
        this.demandeService = demandeService;
        this.competenceService = competenceService;
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<CompetenceRequise>> getCompetencesRequisesByDemandeId(@PathVariable String demandeId) {
        try {
            Demande demande = demandeService.getDemandeById(demandeId);
            return ResponseEntity.ok(demande.getCompetencesRecherchees());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Demande non trouvée avec l'ID: " + demandeId);
        }
    }

    @PostMapping
    public ResponseEntity<CompetenceRequise> addCompetenceRequise(
            @PathVariable String demandeId,
            @RequestBody CompetenceRequiseCreationDto creationDto) {
        
        try {
            Demande demande = demandeService.getDemandeById(demandeId);
            
            // Vérifier si la compétence existe déjà dans la demande
            boolean competenceExisteDeja = demande.getCompetencesRecherchees().stream()
                    .anyMatch(cr -> cr.getCompetence().getLibelle().equals(creationDto.getCompetenceLibelle()));
            
            if (competenceExisteDeja) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "La compétence est déjà présente dans cette demande");
            }
            
            // Récupérer la compétence et la note requise
            Competence competence = competenceService.getCompetenceByLibelle(creationDto.getCompetenceLibelle());
            Note note = noteService.getNoteByValeur(creationDto.getNoteValeur());
            
            // Créer et ajouter la compétence requise
            CompetenceRequise competenceRequise = new CompetenceRequise(competence, note);
            demande.getCompetencesRecherchees().add(competenceRequise);
            
            // Mettre à jour la demande
            Demande updatedDemande = demandeService.updateDemande(demandeId, demande);
            
            // Récupérer la compétence requise mise à jour
            Optional<CompetenceRequise> updatedCompetenceRequise = updatedDemande.getCompetencesRecherchees().stream()
                    .filter(cr -> cr.getCompetence().getLibelle().equals(competence.getLibelle()))
                    .findFirst();
            
            return new ResponseEntity<>(updatedCompetenceRequise.get(), HttpStatus.CREATED);
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("trouvée")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
    }

    @GetMapping("/{competenceId}/{noteValeur}")
    public ResponseEntity<CompetenceRequise> getCompetenceRequise(
            @PathVariable String demandeId,
            @PathVariable String competenceId,
            @PathVariable int noteValeur) {
        
        try {
            Demande demande = demandeService.getDemandeById(demandeId);
            
            Optional<CompetenceRequise> competenceRequise = demande.getCompetencesRecherchees().stream()
                    .filter(cr -> cr.getCompetence().getLibelle().equals(competenceId) &&
                                  cr.getNoteRequise().getValeur() == noteValeur)
                    .findFirst();
            
            if (competenceRequise.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Compétence requise non trouvée dans la demande");
            }
            
            return ResponseEntity.ok(competenceRequise.get());
            
        } catch (RuntimeException e) {
            if (e instanceof ResponseStatusException) {
                throw e;
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Demande non trouvée avec l'ID: " + demandeId);
        }
    }

    @PutMapping("/{competenceId}/{noteValeur}")
    public ResponseEntity<CompetenceRequise> updateCompetenceRequise(
            @PathVariable String demandeId,
            @PathVariable String competenceId,
            @PathVariable int noteValeur,
            @RequestBody CompetenceRequiseUpdateDto updateDto) {
        
        try {
            Demande demande = demandeService.getDemandeById(demandeId);
            
            // Vérifier que la compétence existe dans la demande
            Optional<CompetenceRequise> competenceRequiseOptional = demande.getCompetencesRecherchees().stream()
                    .filter(cr -> cr.getCompetence().getLibelle().equals(competenceId) &&
                                  cr.getNoteRequise().getValeur() == noteValeur)
                    .findFirst();
            
            if (competenceRequiseOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Compétence requise non trouvée dans la demande");
            }
            
            // Récupérer la nouvelle note
            Note nouvelleNote = noteService.getNoteByValeur(updateDto.getNouvelleNoteValeur());
            
            // Mettre à jour la compétence requise
            CompetenceRequise competenceRequise = competenceRequiseOptional.get();
            demande.getCompetencesRecherchees().remove(competenceRequise);
            
            CompetenceRequise nouvelleCompetenceRequise = new CompetenceRequise(
                    competenceRequise.getCompetence(), nouvelleNote);
            demande.getCompetencesRecherchees().add(nouvelleCompetenceRequise);
            
            // Mettre à jour la demande
            Demande updatedDemande = demandeService.updateDemande(demandeId, demande);
            
            // Récupérer la compétence requise mise à jour
            Optional<CompetenceRequise> updatedCompetenceRequise = updatedDemande.getCompetencesRecherchees().stream()
                    .filter(cr -> cr.getCompetence().getLibelle().equals(competenceId) &&
                                  cr.getNoteRequise().getValeur() == updateDto.getNouvelleNoteValeur())
                    .findFirst();
            
            return ResponseEntity.ok(updatedCompetenceRequise.get());
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("trouvée")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
    }

    @DeleteMapping("/{competenceId}/{noteValeur}")
    public ResponseEntity<Void> deleteCompetenceRequise(
            @PathVariable String demandeId,
            @PathVariable String competenceId,
            @PathVariable int noteValeur) {
        
        try {
            Demande demande = demandeService.getDemandeById(demandeId);
            
            // Vérifier que la compétence existe dans la demande
            Optional<CompetenceRequise> competenceRequiseOptional = demande.getCompetencesRecherchees().stream()
                    .filter(cr -> cr.getCompetence().getLibelle().equals(competenceId) &&
                                  cr.getNoteRequise().getValeur() == noteValeur)
                    .findFirst();
            
            if (competenceRequiseOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Compétence requise non trouvée dans la demande");
            }
            
            // Supprimer la compétence requise
            demande.getCompetencesRecherchees().remove(competenceRequiseOptional.get());
            
            // Mettre à jour la demande
            demandeService.updateDemande(demandeId, demande);
            
            return ResponseEntity.noContent().build();
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("trouvée")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
    }

    @GetMapping("/by-competence/{competenceId}")
    public ResponseEntity<CompetenceRequise> getCompetenceRequiseByCompetence(
            @PathVariable String demandeId,
            @PathVariable String competenceId) {
        
        try {
            Demande demande = demandeService.getDemandeById(demandeId);
            
            Optional<CompetenceRequise> competenceRequise = demande.getCompetencesRecherchees().stream()
                    .filter(cr -> cr.getCompetence().getLibelle().equals(competenceId))
                    .findFirst();
            
            if (competenceRequise.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Compétence requise non trouvée dans la demande");
            }
            
            return ResponseEntity.ok(competenceRequise.get());
            
        } catch (RuntimeException e) {
            if (e instanceof ResponseStatusException) {
                throw e;
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Demande non trouvée avec l'ID: " + demandeId);
        }
    }
}