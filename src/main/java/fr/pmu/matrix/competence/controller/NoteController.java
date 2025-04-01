package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Contrôleur pour la gestion des notes d'évaluation
 * Correspond à l'API définie dans le fichier notes-api.yml
 */
@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Récupère toutes les notes (GET /notes)
     * Opération: getAllNotes
     *
     * @return Liste des notes
     */
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        try {
            List<Note> notes = noteService.getAllNotes();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des notes", e);
        }
    }

    /**
     * Récupère une note par sa valeur (GET /notes/{valeur})
     * Opération: getNoteByValeur
     *
     * @param valeur Valeur numérique de la note
     * @return Note correspondante
     */
    @GetMapping("/{valeur}")
    public ResponseEntity<Note> getNoteByValeur(@PathVariable int valeur) {
        try {
            Note note = noteService.getNoteByValeur(valeur);
            return ResponseEntity.ok(note);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération de la note", e);
        }
    }

    /**
     * Crée une nouvelle note (POST /notes)
     * Opération: createNote
     *
     * @param request Requête de création de note
     * @return Note créée avec code 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody CreateNoteRequest request) {
        try {
            Note note = new Note();
            note.setValeur(request.getValeur());
            note.setLibelle(request.getLibelle());

            Note createdNote = noteService.createNote(note);
            return new ResponseEntity<>(createdNote, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de la note", e);
        }
    }

    /**
     * Met à jour une note existante (PUT /notes/{valeur})
     * Opération: updateNote
     *
     * @param valeur Valeur de la note à mettre à jour
     * @param request Requête de mise à jour
     * @return Note mise à jour
     */
    @PutMapping("/{valeur}")
    public ResponseEntity<Note> updateNote(
            @PathVariable int valeur,
            @RequestBody UpdateNoteRequest request) {
        try {
            Note note = new Note();
            note.setLibelle(request.getLibelle());

            Note updatedNote = noteService.updateNote(valeur, note);
            return ResponseEntity.ok(updatedNote);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour de la note", e);
        }
    }

    /**
     * Supprime une note (DELETE /notes/{valeur})
     * Opération: deleteNote
     *
     * @param valeur Valeur de la note à supprimer
     * @return Réponse sans contenu (204 No Content)
     */
    @DeleteMapping("/{valeur}")
    public ResponseEntity<Void> deleteNote(@PathVariable int valeur) {
        try {
            noteService.deleteNote(valeur);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression de la note", e);
        }
    }

    /**
     * Classe pour la requête de création d'une note
     * Correspond au schéma CreateNoteRequest dans l'API
     */
    public static class CreateNoteRequest {
        private Integer valeur;
        private String libelle;

        public Integer getValeur() {
            return valeur;
        }

        public void setValeur(Integer valeur) {
            this.valeur = valeur;
        }

        public String getLibelle() {
            return libelle;
        }

        public void setLibelle(String libelle) {
            this.libelle = libelle;
        }
    }

    /**
     * Classe pour la requête de mise à jour d'une note
     * Correspond au schéma UpdateNoteRequest dans l'API
     */
    public static class UpdateNoteRequest {
        private String libelle;

        public String getLibelle() {
            return libelle;
        }

        public void setLibelle(String libelle) {
            this.libelle = libelle;
        }
    }
}