package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.entity.NoteEntity;
import fr.pmu.matrix.competence.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des notes d'évaluation
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Récupère toutes les notes
     *
     * @return Liste des notes
     */
    public List<Note> getAllNotes() {
        return noteRepository.findAll()
                .stream()
                .map(this::convertToNote)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une note par sa valeur
     *
     * @param valeur Valeur numérique de la note
     * @return Note correspondante
     * @throws RuntimeException si la note n'existe pas
     */
    public Note getNoteByValeur(int valeur) {
        NoteEntity noteEntity = noteRepository.findById(valeur)
                .orElseThrow(() -> new RuntimeException("Note non trouvée avec la valeur: " + valeur));
        return convertToNote(noteEntity);
    }

    /**
     * Crée une nouvelle note
     *
     * @param note Note à créer
     * @return Note créée
     * @throws RuntimeException si une note avec la même valeur existe déjà
     */
    @Transactional
    public Note createNote(Note note) {
        if (noteRepository.existsById(note.getValeur())) {
            throw new RuntimeException("Une note avec la valeur " + note.getValeur() + " existe déjà");
        }

        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setValeur(note.getValeur());
        noteEntity.setLibelle(note.getLibelle());

        NoteEntity savedEntity = noteRepository.save(noteEntity);
        return convertToNote(savedEntity);
    }

    /**
     * Met à jour une note existante
     *
     * @param valeur Valeur de la note à mettre à jour
     * @param note Nouvelles données de la note
     * @return Note mise à jour
     * @throws RuntimeException si la note n'existe pas
     */
    @Transactional
    public Note updateNote(int valeur, Note note) {
        NoteEntity noteEntity = noteRepository.findById(valeur)
                .orElseThrow(() -> new RuntimeException("Note non trouvée avec la valeur: " + valeur));

        // Mise à jour du libellé
        if (note.getLibelle() != null) {
            noteEntity.setLibelle(note.getLibelle());
        }

        NoteEntity updatedEntity = noteRepository.save(noteEntity);
        return convertToNote(updatedEntity);
    }

    /**
     * Supprime une note
     *
     * @param valeur Valeur de la note à supprimer
     * @throws RuntimeException si la note n'existe pas
     */
    @Transactional
    public void deleteNote(int valeur) {
        if (!noteRepository.existsById(valeur)) {
            throw new RuntimeException("Note non trouvée avec la valeur: " + valeur);
        }
        noteRepository.deleteById(valeur);
    }

    /**
     * Convertit une entité note en objet de domaine
     *
     * @param entity Entité note
     * @return Objet de domaine note
     */
    private Note convertToNote(NoteEntity entity) {
        Note note = new Note();
        note.setValeur(entity.getValeur());
        note.setLibelle(entity.getLibelle());
        return note;
    }
}