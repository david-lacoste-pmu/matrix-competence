package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.entity.NoteEntity;
import fr.pmu.matrix.competence.mapper.NoteMapper;
import fr.pmu.matrix.competence.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;
    
    @Spy
    private NoteMapper noteMapper = new NoteMapper();

    @InjectMocks
    private NoteService noteService;

    @Test
    void testGetAllNotes() {
        // Given
        NoteEntity entity1 = new NoteEntity();
        entity1.setValeur(1);
        entity1.setLibelle("Insuffisant");

        NoteEntity entity2 = new NoteEntity();
        entity2.setValeur(5);
        entity2.setLibelle("Excellent");

        when(noteRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        // When
        List<Note> result = noteService.getAllNotes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getValeur());
        assertEquals("Insuffisant", result.get(0).getLibelle());
        assertEquals(5, result.get(1).getValeur());
        assertEquals("Excellent", result.get(1).getLibelle());
        verify(noteRepository, times(1)).findAll();
        verify(noteMapper, times(2)).mapToNoteDomain(any(NoteEntity.class));
    }

    @Test
    void testGetNoteByValeur() {
        // Given
        NoteEntity entity = new NoteEntity();
        entity.setValeur(5);
        entity.setLibelle("Excellent");

        when(noteRepository.findById(5)).thenReturn(Optional.of(entity));

        // When
        Note result = noteService.getNoteByValeur(5);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getValeur());
        assertEquals("Excellent", result.getLibelle());
        verify(noteRepository, times(1)).findById(5);
        verify(noteMapper, times(1)).mapToNoteDomain(entity);
    }

    @Test
    void testGetNoteByValeur_NotFound() {
        // Given
        when(noteRepository.findById(999)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> noteService.getNoteByValeur(999));
        verify(noteRepository, times(1)).findById(999);
        verify(noteMapper, never()).mapToNoteDomain(any(NoteEntity.class));
    }

    @Test
    void testCreateNote() {
        // Given
        Note note = new Note();
        note.setValeur(5);
        note.setLibelle("Excellent");

        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setValeur(5);
        noteEntity.setLibelle("Excellent");

        NoteEntity savedEntity = new NoteEntity();
        savedEntity.setValeur(5);
        savedEntity.setLibelle("Excellent");

        when(noteRepository.existsById(5)).thenReturn(false);
        when(noteMapper.mapToNoteEntity(note)).thenReturn(noteEntity);
        when(noteRepository.save(noteEntity)).thenReturn(savedEntity);

        // When
        Note result = noteService.createNote(note);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getValeur());
        assertEquals("Excellent", result.getLibelle());
        verify(noteRepository, times(1)).existsById(5);
        verify(noteMapper, times(1)).mapToNoteEntity(note);
        verify(noteRepository, times(1)).save(noteEntity);
        verify(noteMapper, times(1)).mapToNoteDomain(savedEntity);
    }

    @Test
    void testCreateNote_AlreadyExists() {
        // Given
        Note note = new Note();
        note.setValeur(5);
        note.setLibelle("Excellent");

        when(noteRepository.existsById(5)).thenReturn(true);

        // When / Then
        assertThrows(RuntimeException.class, () -> noteService.createNote(note));
        verify(noteRepository, times(1)).existsById(5);
        verify(noteRepository, never()).save(any(NoteEntity.class));
        verify(noteMapper, never()).mapToNoteEntity(any(Note.class));
        verify(noteMapper, never()).mapToNoteDomain(any(NoteEntity.class));
    }

    @Test
    void testUpdateNote() {
        // Given
        NoteEntity existingEntity = new NoteEntity();
        existingEntity.setValeur(5);
        existingEntity.setLibelle("Excellent");

        NoteEntity updatedEntity = new NoteEntity();
        updatedEntity.setValeur(5);
        updatedEntity.setLibelle("Excellent - Performance exceptionnelle");

        Note updateData = new Note();
        updateData.setLibelle("Excellent - Performance exceptionnelle");

        when(noteRepository.findById(5)).thenReturn(Optional.of(existingEntity));
        when(noteRepository.save(existingEntity)).thenReturn(updatedEntity);

        // When
        Note result = noteService.updateNote(5, updateData);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getValeur());
        assertEquals("Excellent - Performance exceptionnelle", result.getLibelle());
        verify(noteRepository, times(1)).findById(5);
        verify(noteRepository, times(1)).save(existingEntity);
        verify(noteMapper, times(1)).mapToNoteDomain(updatedEntity);
    }

    @Test
    void testUpdateNote_NullLibelle() {
        // Given
        NoteEntity existingEntity = new NoteEntity();
        existingEntity.setValeur(5);
        existingEntity.setLibelle("Excellent");

        NoteEntity updatedEntity = new NoteEntity();
        updatedEntity.setValeur(5);
        updatedEntity.setLibelle("Excellent"); // Unchanged

        Note updateData = new Note();
        // Libellé non fourni (null)

        when(noteRepository.findById(5)).thenReturn(Optional.of(existingEntity));
        when(noteRepository.save(existingEntity)).thenReturn(updatedEntity);

        // When
        Note result = noteService.updateNote(5, updateData);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getValeur());
        assertEquals("Excellent", result.getLibelle());
        verify(noteRepository, times(1)).findById(5);
        verify(noteRepository, times(1)).save(existingEntity);
        verify(noteMapper, times(1)).mapToNoteDomain(updatedEntity);
    }

    @Test
    void testUpdateNote_NotFound() {
        // Given
        Note updateData = new Note();
        updateData.setLibelle("Excellent - Performance exceptionnelle");

        when(noteRepository.findById(999)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> noteService.updateNote(999, updateData));
        verify(noteRepository, times(1)).findById(999);
        verify(noteRepository, never()).save(any(NoteEntity.class));
        verify(noteMapper, never()).mapToNoteDomain(any(NoteEntity.class));
    }

    @Test
    void testDeleteNote() {
        // Given
        when(noteRepository.existsById(5)).thenReturn(true);

        // When
        noteService.deleteNote(5);

        // Then
        verify(noteRepository, times(1)).existsById(5);
        verify(noteRepository, times(1)).deleteById(5);
    }

    @Test
    void testDeleteNote_NotFound() {
        // Given
        when(noteRepository.existsById(999)).thenReturn(false);

        // When / Then
        assertThrows(RuntimeException.class, () -> noteService.deleteNote(999));
        verify(noteRepository, times(1)).existsById(999);
        verify(noteRepository, never()).deleteById(anyInt());
    }
}