package fr.pmu.matrix.competence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoteService noteService;

    @Test
    void testGetAllNotes() throws Exception {
        // Given
        Note note1 = new Note();
        note1.setValeur(1);
        note1.setLibelle("Insuffisant");

        Note note2 = new Note();
        note2.setValeur(5);
        note2.setLibelle("Excellent");

        List<Note> notes = Arrays.asList(note1, note2);

        when(noteService.getAllNotes()).thenReturn(notes);

        // When & Then
        mockMvc.perform(get("/notes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(notes)))
                .andExpect(jsonPath("$[0].valeur").value(1))
                .andExpect(jsonPath("$[0].libelle").value("Insuffisant"))
                .andExpect(jsonPath("$[1].valeur").value(5))
                .andExpect(jsonPath("$[1].libelle").value("Excellent"));

        verify(noteService, times(1)).getAllNotes();
    }

    @Test
    void testGetNoteByValeur() throws Exception {
        // Given
        Note note = new Note();
        note.setValeur(5);
        note.setLibelle("Excellent");

        when(noteService.getNoteByValeur(5)).thenReturn(note);

        // When & Then
        mockMvc.perform(get("/notes/5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valeur").value(5))
                .andExpect(jsonPath("$.libelle").value("Excellent"));

        verify(noteService, times(1)).getNoteByValeur(5);
    }

    @Test
    void testGetNoteByValeur_NotFound() throws Exception {
        // Given
        when(noteService.getNoteByValeur(999)).thenThrow(new RuntimeException("Note non trouvée"));

        // When & Then
        mockMvc.perform(get("/notes/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(noteService, times(1)).getNoteByValeur(999);
    }

    @Test
    void testCreateNote() throws Exception {
        // Given
        NoteController.CreateNoteRequest request = new NoteController.CreateNoteRequest();
        request.setValeur(5);
        request.setLibelle("Excellent");

        Note createdNote = new Note();
        createdNote.setValeur(5);
        createdNote.setLibelle("Excellent");

        when(noteService.createNote(any(Note.class))).thenReturn(createdNote);

        // When & Then
        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valeur").value(5))
                .andExpect(jsonPath("$.libelle").value("Excellent"));

        verify(noteService, times(1)).createNote(any(Note.class));
    }

    @Test
    void testCreateNote_AlreadyExists() throws Exception {
        // Given
        NoteController.CreateNoteRequest request = new NoteController.CreateNoteRequest();
        request.setValeur(5);
        request.setLibelle("Excellent");

        when(noteService.createNote(any(Note.class)))
                .thenThrow(new RuntimeException("Une note avec cette valeur existe déjà"));

        // When & Then
        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(noteService, times(1)).createNote(any(Note.class));
    }

    @Test
    void testUpdateNote() throws Exception {
        // Given
        NoteController.UpdateNoteRequest request = new NoteController.UpdateNoteRequest();
        request.setLibelle("Excellent - Performance exceptionnelle");

        Note updatedNote = new Note();
        updatedNote.setValeur(5);
        updatedNote.setLibelle("Excellent - Performance exceptionnelle");

        when(noteService.updateNote(eq(5), any(Note.class))).thenReturn(updatedNote);

        // When & Then
        mockMvc.perform(put("/notes/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valeur").value(5))
                .andExpect(jsonPath("$.libelle").value("Excellent - Performance exceptionnelle"));

        verify(noteService, times(1)).updateNote(eq(5), any(Note.class));
    }

    @Test
    void testUpdateNote_NotFound() throws Exception {
        // Given
        NoteController.UpdateNoteRequest request = new NoteController.UpdateNoteRequest();
        request.setLibelle("Excellent - Performance exceptionnelle");

        when(noteService.updateNote(eq(999), any(Note.class)))
                .thenThrow(new RuntimeException("Note non trouvée"));

        // When & Then
        mockMvc.perform(put("/notes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(noteService, times(1)).updateNote(eq(999), any(Note.class));
    }

    @Test
    void testDeleteNote() throws Exception {
        // Given
        doNothing().when(noteService).deleteNote(5);

        // When & Then
        mockMvc.perform(delete("/notes/5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(noteService, times(1)).deleteNote(5);
    }

    @Test
    void testDeleteNote_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Note non trouvée")).when(noteService).deleteNote(999);

        // When & Then
        mockMvc.perform(delete("/notes/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(noteService, times(1)).deleteNote(999);
    }
    
    @Test
    void testGetAllNotes_ServerError() throws Exception {
        // Given
        when(noteService.getAllNotes()).thenThrow(new RuntimeException("Erreur serveur inattendue"));

        // When & Then
        mockMvc.perform(get("/notes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(noteService, times(1)).getAllNotes();
    }
    
    @Test
    void testCreateNote_ServerError() throws Exception {
        // Given
        NoteController.CreateNoteRequest request = new NoteController.CreateNoteRequest();
        request.setValeur(5);
        request.setLibelle("Excellent");
        
        when(noteService.createNote(any(Note.class))).thenThrow(new RuntimeException("Erreur inattendue lors de la création"));

        // When & Then
        mockMvc.perform(post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(noteService, times(1)).createNote(any(Note.class));
    }
}