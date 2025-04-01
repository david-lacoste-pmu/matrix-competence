package fr.pmu.matrix.competence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.dto.CreateCompetenceRequest;
import fr.pmu.matrix.competence.dto.UpdateCompetenceRequest;
import fr.pmu.matrix.competence.service.CompetenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompetenceController.class)
class CompetenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompetenceService competenceService;

    @Test
    void testGetAllCompetences() throws Exception {
        // Given
        List<Competence> competences = Arrays.asList(
            createCompetence("JAVA", "Java Programming"),
            createCompetence("SPRING", "Spring Framework")
        );

        when(competenceService.getAllCompetences()).thenReturn(competences);

        // When & Then
        mockMvc.perform(get("/competences")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].libelle").value("JAVA"))
                .andExpect(jsonPath("$[1].libelle").value("SPRING"));

        verify(competenceService, times(1)).getAllCompetences();
    }

    @Test
    void testCreateCompetence() throws Exception {
        // Given
        CreateCompetenceRequest request = new CreateCompetenceRequest();
        request.setLibelle("JAVA");
        request.setDescription("Java Programming");

        Competence createdCompetence = createCompetence("JAVA", "Java Programming");

        when(competenceService.createCompetence(any(Competence.class))).thenReturn(createdCompetence);

        // When & Then
        mockMvc.perform(post("/competences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.libelle").value("JAVA"))
                .andExpect(jsonPath("$.description").value("Java Programming"));

        verify(competenceService, times(1)).createCompetence(any(Competence.class));
    }

    @Test
    void testCreateCompetence_AlreadyExists() throws Exception {
        // Given
        CreateCompetenceRequest request = new CreateCompetenceRequest();
        request.setLibelle("JAVA");
        request.setDescription("Java Programming");

        when(competenceService.createCompetence(any(Competence.class)))
                .thenThrow(new RuntimeException("Une compétence avec ce libellé existe déjà: JAVA"));

        // When & Then
        mockMvc.perform(post("/competences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(competenceService, times(1)).createCompetence(any(Competence.class));
    }

    @Test
    void testCreateCompetence_BadRequest() throws Exception {
        // Given
        CreateCompetenceRequest request = new CreateCompetenceRequest();
        request.setLibelle("");  // Libellé vide
        request.setDescription("Java Programming");

        // When & Then
        mockMvc.perform(post("/competences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(competenceService, never()).createCompetence(any(Competence.class));
    }

    @Test
    void testGetCompetenceByLibelle() throws Exception {
        // Given
        String libelle = "JAVA";
        Competence competence = createCompetence(libelle, "Java Programming");

        when(competenceService.getCompetenceByLibelle(libelle)).thenReturn(competence);

        // When & Then
        mockMvc.perform(get("/competences/{libelle}", libelle)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.libelle").value(libelle))
                .andExpect(jsonPath("$.description").value("Java Programming"));

        verify(competenceService, times(1)).getCompetenceByLibelle(libelle);
    }

    @Test
    void testGetCompetenceByLibelle_NotFound() throws Exception {
        // Given
        String libelle = "UNKNOWN";

        when(competenceService.getCompetenceByLibelle(libelle))
                .thenThrow(new RuntimeException("Compétence non trouvée avec le libellé: " + libelle));

        // When & Then
        mockMvc.perform(get("/competences/{libelle}", libelle)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(competenceService, times(1)).getCompetenceByLibelle(libelle);
    }

    @Test
    void testUpdateCompetence() throws Exception {
        // Given
        String libelle = "JAVA";
        UpdateCompetenceRequest request = new UpdateCompetenceRequest();
        request.setDescription("Updated Java Programming");

        Competence updatedCompetence = createCompetence(libelle, "Updated Java Programming");

        when(competenceService.updateCompetence(eq(libelle), any(Competence.class))).thenReturn(updatedCompetence);

        // When & Then
        mockMvc.perform(put("/competences/{libelle}", libelle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.libelle").value(libelle))
                .andExpect(jsonPath("$.description").value("Updated Java Programming"));

        verify(competenceService, times(1)).updateCompetence(eq(libelle), any(Competence.class));
    }

    @Test
    void testUpdateCompetence_NotFound() throws Exception {
        // Given
        String libelle = "UNKNOWN";
        UpdateCompetenceRequest request = new UpdateCompetenceRequest();
        request.setDescription("New Description");

        when(competenceService.updateCompetence(eq(libelle), any(Competence.class)))
                .thenThrow(new RuntimeException("Compétence non trouvée avec le libellé: " + libelle));

        // When & Then
        mockMvc.perform(put("/competences/{libelle}", libelle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(competenceService, times(1)).updateCompetence(eq(libelle), any(Competence.class));
    }

    @Test
    void testUpdateCompetence_BadRequest() throws Exception {
        // Given
        String libelle = "JAVA";
        UpdateCompetenceRequest request = new UpdateCompetenceRequest();
        request.setDescription("");  // Description vide

        // When & Then
        mockMvc.perform(put("/competences/{libelle}", libelle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(competenceService, never()).updateCompetence(anyString(), any(Competence.class));
    }

    @Test
    void testDeleteCompetence() throws Exception {
        // Given
        String libelle = "JAVA";

        doNothing().when(competenceService).deleteCompetence(libelle);

        // When & Then
        mockMvc.perform(delete("/competences/{libelle}", libelle)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(competenceService, times(1)).deleteCompetence(libelle);
    }

    @Test
    void testDeleteCompetence_NotFound() throws Exception {
        // Given
        String libelle = "UNKNOWN";

        doThrow(new RuntimeException("Compétence non trouvée avec le libellé: " + libelle))
                .when(competenceService).deleteCompetence(libelle);

        // When & Then
        mockMvc.perform(delete("/competences/{libelle}", libelle)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(competenceService, times(1)).deleteCompetence(libelle);
    }

    // Méthode utilitaire pour créer des compétences
    private Competence createCompetence(String libelle, String description) {
        Competence competence = new Competence();
        competence.setLibelle(libelle);
        competence.setDescription(description);
        return competence;
    }
}