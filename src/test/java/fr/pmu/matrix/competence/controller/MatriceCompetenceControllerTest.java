package fr.pmu.matrix.competence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.MatriceCompetence;
import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.dto.CreateMatriceCompetenceRequest;
import fr.pmu.matrix.competence.dto.UpdateMatriceCompetenceRequest;
import fr.pmu.matrix.competence.service.MatriceCompetenceService;
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

@WebMvcTest(MatriceCompetenceController.class)
class MatriceCompetenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatriceCompetenceService matriceCompetenceService;

    @Test
    void testGetAllMatricesCompetences() throws Exception {
        // Given
        List<MatriceCompetence> matrices = Arrays.asList(
            createMatriceCompetence("P123", "Dupont", "Jean", "JAVA", "Java Programming", 3, "Intermédiaire"),
            createMatriceCompetence("P123", "Dupont", "Jean", "SPRING", "Spring Framework", 4, "Avancé")
        );

        when(matriceCompetenceService.getAllMatricesCompetences()).thenReturn(matrices);

        // When & Then
        mockMvc.perform(get("/matrices-competences")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].personne.identifiant").value("P123"))
                .andExpect(jsonPath("$[0].competence.libelle").value("JAVA"))
                .andExpect(jsonPath("$[1].competence.libelle").value("SPRING"));

        verify(matriceCompetenceService, times(1)).getAllMatricesCompetences();
    }

    @Test
    void testCreateMatriceCompetence() throws Exception {
        // Given
        CreateMatriceCompetenceRequest request = new CreateMatriceCompetenceRequest();
        request.setPersonneId("P123");
        request.setCompetenceId("JAVA");
        request.setNoteValeur(3);

        MatriceCompetence createdMatrice = createMatriceCompetence(
            "P123", "Dupont", "Jean", "JAVA", "Java Programming", 3, "Intermédiaire"
        );

        when(matriceCompetenceService.createMatriceCompetence("P123", "JAVA", 3)).thenReturn(createdMatrice);

        // When & Then
        mockMvc.perform(post("/matrices-competences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.personne.identifiant").value("P123"))
                .andExpect(jsonPath("$.competence.libelle").value("JAVA"))
                .andExpect(jsonPath("$.note.valeur").value(3));

        verify(matriceCompetenceService, times(1)).createMatriceCompetence("P123", "JAVA", 3);
    }

    @Test
    void testCreateMatriceCompetence_AlreadyExists() throws Exception {
        // Given
        CreateMatriceCompetenceRequest request = new CreateMatriceCompetenceRequest();
        request.setPersonneId("P123");
        request.setCompetenceId("JAVA");
        request.setNoteValeur(3);

        when(matriceCompetenceService.createMatriceCompetence("P123", "JAVA", 3))
                .thenThrow(new RuntimeException("Une matrice de compétence existe déjà pour cette personne et cette compétence"));

        // When & Then
        mockMvc.perform(post("/matrices-competences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(matriceCompetenceService, times(1)).createMatriceCompetence("P123", "JAVA", 3);
    }

    @Test
    void testGetMatriceCompetence() throws Exception {
        // Given
        String personneId = "P123";
        String competenceId = "JAVA";

        MatriceCompetence matrice = createMatriceCompetence(
            personneId, "Dupont", "Jean", competenceId, "Java Programming", 3, "Intermédiaire"
        );

        when(matriceCompetenceService.getMatriceCompetence(personneId, competenceId)).thenReturn(matrice);

        // When & Then
        mockMvc.perform(get("/matrices-competences/personnes/{personneId}/competences/{competenceId}", personneId, competenceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personne.identifiant").value(personneId))
                .andExpect(jsonPath("$.competence.libelle").value(competenceId))
                .andExpect(jsonPath("$.note.valeur").value(3));

        verify(matriceCompetenceService, times(1)).getMatriceCompetence(personneId, competenceId);
    }

    @Test
    void testGetMatriceCompetence_NotFound() throws Exception {
        // Given
        String personneId = "P123";
        String competenceId = "UNKNOWN";

        when(matriceCompetenceService.getMatriceCompetence(personneId, competenceId))
                .thenThrow(new RuntimeException("Matrice de compétence non trouvée pour cette personne et cette compétence"));

        // When & Then
        mockMvc.perform(get("/matrices-competences/personnes/{personneId}/competences/{competenceId}", personneId, competenceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(matriceCompetenceService, times(1)).getMatriceCompetence(personneId, competenceId);
    }

    @Test
    void testUpdateMatriceCompetence() throws Exception {
        // Given
        String personneId = "P123";
        String competenceId = "JAVA";

        UpdateMatriceCompetenceRequest request = new UpdateMatriceCompetenceRequest();
        request.setNoteValeur(4);

        MatriceCompetence updatedMatrice = createMatriceCompetence(
            personneId, "Dupont", "Jean", competenceId, "Java Programming", 4, "Avancé"
        );

        when(matriceCompetenceService.updateMatriceCompetence(personneId, competenceId, 4)).thenReturn(updatedMatrice);

        // When & Then
        mockMvc.perform(put("/matrices-competences/personnes/{personneId}/competences/{competenceId}", personneId, competenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personne.identifiant").value(personneId))
                .andExpect(jsonPath("$.competence.libelle").value(competenceId))
                .andExpect(jsonPath("$.note.valeur").value(4));

        verify(matriceCompetenceService, times(1)).updateMatriceCompetence(personneId, competenceId, 4);
    }

    @Test
    void testUpdateMatriceCompetence_NotFound() throws Exception {
        // Given
        String personneId = "P123";
        String competenceId = "UNKNOWN";

        UpdateMatriceCompetenceRequest request = new UpdateMatriceCompetenceRequest();
        request.setNoteValeur(4);

        when(matriceCompetenceService.updateMatriceCompetence(personneId, competenceId, 4))
                .thenThrow(new RuntimeException("Matrice de compétence non trouvée pour cette personne et cette compétence"));

        // When & Then
        mockMvc.perform(put("/matrices-competences/personnes/{personneId}/competences/{competenceId}", personneId, competenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(matriceCompetenceService, times(1)).updateMatriceCompetence(personneId, competenceId, 4);
    }

    @Test
    void testDeleteMatriceCompetence() throws Exception {
        // Given
        String personneId = "P123";
        String competenceId = "JAVA";

        doNothing().when(matriceCompetenceService).deleteMatriceCompetence(personneId, competenceId);

        // When & Then
        mockMvc.perform(delete("/matrices-competences/personnes/{personneId}/competences/{competenceId}", personneId, competenceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(matriceCompetenceService, times(1)).deleteMatriceCompetence(personneId, competenceId);
    }

    @Test
    void testDeleteMatriceCompetence_NotFound() throws Exception {
        // Given
        String personneId = "P123";
        String competenceId = "UNKNOWN";

        doThrow(new RuntimeException("Matrice de compétence non trouvée pour cette personne et cette compétence"))
                .when(matriceCompetenceService).deleteMatriceCompetence(personneId, competenceId);

        // When & Then
        mockMvc.perform(delete("/matrices-competences/personnes/{personneId}/competences/{competenceId}", personneId, competenceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(matriceCompetenceService, times(1)).deleteMatriceCompetence(personneId, competenceId);
    }

    @Test
    void testGetCompetencesByPersonne() throws Exception {
        // Given
        String personneId = "P123";

        List<MatriceCompetence> matrices = Arrays.asList(
            createMatriceCompetence(personneId, "Dupont", "Jean", "JAVA", "Java Programming", 3, "Intermédiaire"),
            createMatriceCompetence(personneId, "Dupont", "Jean", "SPRING", "Spring Framework", 4, "Avancé")
        );

        when(matriceCompetenceService.getCompetencesByPersonne(personneId)).thenReturn(matrices);

        // When & Then
        mockMvc.perform(get("/matrices-competences/personnes/{personneId}", personneId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].personne.identifiant").value(personneId))
                .andExpect(jsonPath("$[0].competence.libelle").value("JAVA"))
                .andExpect(jsonPath("$[1].competence.libelle").value("SPRING"));

        verify(matriceCompetenceService, times(1)).getCompetencesByPersonne(personneId);
    }

    @Test
    void testGetCompetencesByPersonne_NotFound() throws Exception {
        // Given
        String personneId = "P999";

        when(matriceCompetenceService.getCompetencesByPersonne(personneId))
                .thenThrow(new RuntimeException("Personne non trouvée avec l'identifiant: " + personneId));

        // When & Then
        mockMvc.perform(get("/matrices-competences/personnes/{personneId}", personneId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(matriceCompetenceService, times(1)).getCompetencesByPersonne(personneId);
    }

    @Test
    void testGetPersonnesByCompetence() throws Exception {
        // Given
        String competenceId = "JAVA";

        List<MatriceCompetence> matrices = Arrays.asList(
            createMatriceCompetence("P123", "Dupont", "Jean", competenceId, "Java Programming", 3, "Intermédiaire"),
            createMatriceCompetence("P124", "Martin", "Sophie", competenceId, "Java Programming", 5, "Expert")
        );

        when(matriceCompetenceService.getPersonnesByCompetence(competenceId)).thenReturn(matrices);

        // When & Then
        mockMvc.perform(get("/matrices-competences/competences/{competenceId}", competenceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].competence.libelle").value(competenceId))
                .andExpect(jsonPath("$[0].personne.identifiant").value("P123"))
                .andExpect(jsonPath("$[1].personne.identifiant").value("P124"));

        verify(matriceCompetenceService, times(1)).getPersonnesByCompetence(competenceId);
    }

    @Test
    void testGetPersonnesByCompetence_NotFound() throws Exception {
        // Given
        String competenceId = "UNKNOWN";

        when(matriceCompetenceService.getPersonnesByCompetence(competenceId))
                .thenThrow(new RuntimeException("Compétence non trouvée avec l'identifiant: " + competenceId));

        // When & Then
        mockMvc.perform(get("/matrices-competences/competences/{competenceId}", competenceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(matriceCompetenceService, times(1)).getPersonnesByCompetence(competenceId);
    }

    // Méthode utilitaire pour créer une MatriceCompetence
    private MatriceCompetence createMatriceCompetence(
            String personneId, String nom, String prenom,
            String competenceLibelle, String competenceDescription,
            int noteValeur, String noteLibelle) {
            
        Personne personne = new Personne();
        personne.setIdentifiant(personneId);
        personne.setNom(nom);
        personne.setPrenom(prenom);
        
        Competence competence = new Competence();
        competence.setLibelle(competenceLibelle);
        competence.setDescription(competenceDescription);
        
        Note note = new Note();
        note.setValeur(noteValeur);
        note.setLibelle(noteLibelle);
        
        return new MatriceCompetence(personne, competence, note);
    }
}