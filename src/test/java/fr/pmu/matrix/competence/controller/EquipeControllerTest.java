package fr.pmu.matrix.competence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.Groupement;
import fr.pmu.matrix.competence.dto.CompetenceRequiseDto;
import fr.pmu.matrix.competence.dto.CreateEquipeRequest;
import fr.pmu.matrix.competence.dto.UpdateEquipeRequest;
import fr.pmu.matrix.competence.service.CompetenceService;
import fr.pmu.matrix.competence.service.EquipeService;
import fr.pmu.matrix.competence.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EquipeController.class)
class EquipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EquipeService equipeService;
    
    @MockBean
    private CompetenceService competenceService;
    
    @MockBean
    private NoteService noteService;

    @Test
    void testGetAllEquipes() throws Exception {
        // Given
        Groupement groupement = new Groupement();
        groupement.setCode("G001");
        groupement.setLibelle("Groupement IT");

        Equipe equipe1 = new Equipe();
        equipe1.setCode("EQ001");
        equipe1.setNom("Équipe Dev");
        equipe1.setDescription("Équipe de développement");
        equipe1.setGroupement(groupement);
        equipe1.setMembres(Collections.emptyList());

        Equipe equipe2 = new Equipe();
        equipe2.setCode("EQ002");
        equipe2.setNom("Équipe QA");
        equipe2.setDescription("Équipe de test");
        equipe2.setGroupement(groupement);
        equipe2.setMembres(Collections.emptyList());

        List<Equipe> equipes = Arrays.asList(equipe1, equipe2);

        when(equipeService.getAllEquipes()).thenReturn(equipes);

        // When & Then
        mockMvc.perform(get("/equipes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(equipes)))
                .andExpect(jsonPath("$[0].code").value("EQ001"))
                .andExpect(jsonPath("$[1].code").value("EQ002"));

        verify(equipeService, times(1)).getAllEquipes();
    }

    @Test
    void testGetEquipeByCode() throws Exception {
        // Given
        Groupement groupement = new Groupement();
        groupement.setCode("G001");
        groupement.setLibelle("Groupement IT");

        Equipe equipe = new Equipe();
        equipe.setCode("EQ001");
        equipe.setNom("Équipe Dev");
        equipe.setDescription("Équipe de développement");
        equipe.setGroupement(groupement);
        equipe.setMembres(Collections.emptyList());

        when(equipeService.getEquipeByCode("EQ001")).thenReturn(equipe);

        // When & Then
        mockMvc.perform(get("/equipes/EQ001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("EQ001"))
                .andExpect(jsonPath("$.nom").value("Équipe Dev"))
                .andExpect(jsonPath("$.description").value("Équipe de développement"))
                .andExpect(jsonPath("$.groupement.code").value("G001"));

        verify(equipeService, times(1)).getEquipeByCode("EQ001");
    }

    @Test
    void testGetEquipeByCode_NotFound() throws Exception {
        // Given
        when(equipeService.getEquipeByCode("EQ999")).thenThrow(new RuntimeException("Équipe non trouvée"));

        // When & Then
        mockMvc.perform(get("/equipes/EQ999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(equipeService, times(1)).getEquipeByCode("EQ999");
    }

    @Test
    void testCreateEquipe() throws Exception {
        // Given
        CreateEquipeRequest request = new CreateEquipeRequest();
        request.setCode("EQ001");
        request.setNom("Équipe Dev");
        request.setDescription("Équipe de développement");
        request.setGroupementCode("G001");

        Groupement groupement = new Groupement();
        groupement.setCode("G001");
        groupement.setLibelle("Groupement IT");

        Equipe createdEquipe = new Equipe();
        createdEquipe.setCode("EQ001");
        createdEquipe.setNom("Équipe Dev");
        createdEquipe.setDescription("Équipe de développement");
        createdEquipe.setGroupement(groupement);
        createdEquipe.setMembres(Collections.emptyList());

        when(equipeService.createEquipe(any(Equipe.class), eq("G001"))).thenReturn(createdEquipe);

        // When & Then
        mockMvc.perform(post("/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("EQ001"))
                .andExpect(jsonPath("$.nom").value("Équipe Dev"))
                .andExpect(jsonPath("$.description").value("Équipe de développement"))
                .andExpect(jsonPath("$.groupement.code").value("G001"));

        verify(equipeService, times(1)).createEquipe(any(Equipe.class), eq("G001"));
    }

    @Test
    void testCreateEquipe_AlreadyExists() throws Exception {
        // Given
        CreateEquipeRequest request = new CreateEquipeRequest();
        request.setCode("EQ001");
        request.setNom("Équipe Dev");
        request.setDescription("Équipe de développement");
        request.setGroupementCode("G001");

        when(equipeService.createEquipe(any(Equipe.class), eq("G001")))
                .thenThrow(new RuntimeException("Une équipe avec ce code existe déjà"));

        // When & Then
        mockMvc.perform(post("/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(equipeService, times(1)).createEquipe(any(Equipe.class), eq("G001"));
    }

    @Test
    void testUpdateEquipe() throws Exception {
        // Given
        UpdateEquipeRequest request = new UpdateEquipeRequest();
        request.setNom("Équipe Development");
        request.setDescription("Équipe de développement modifiée");
        request.setGroupementCode("G002");

        Groupement groupement = new Groupement();
        groupement.setCode("G002");
        groupement.setLibelle("Nouveau Groupement");

        Equipe updatedEquipe = new Equipe();
        updatedEquipe.setCode("EQ001");
        updatedEquipe.setNom("Équipe Development");
        updatedEquipe.setDescription("Équipe de développement modifiée");
        updatedEquipe.setGroupement(groupement);
        updatedEquipe.setMembres(Collections.emptyList());

        when(equipeService.updateEquipe(eq("EQ001"), any(Equipe.class), eq("G002"))).thenReturn(updatedEquipe);

        // When & Then
        mockMvc.perform(put("/equipes/EQ001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("EQ001"))
                .andExpect(jsonPath("$.nom").value("Équipe Development"))
                .andExpect(jsonPath("$.description").value("Équipe de développement modifiée"))
                .andExpect(jsonPath("$.groupement.code").value("G002"));

        verify(equipeService, times(1)).updateEquipe(eq("EQ001"), any(Equipe.class), eq("G002"));
    }

    @Test
    void testUpdateEquipe_NotFound() throws Exception {
        // Given
        UpdateEquipeRequest request = new UpdateEquipeRequest();
        request.setNom("Équipe Development");
        request.setDescription("Équipe de développement modifiée");
        request.setGroupementCode("G002");

        when(equipeService.updateEquipe(eq("EQ999"), any(Equipe.class), eq("G002")))
                .thenThrow(new RuntimeException("Équipe non trouvée"));

        // When & Then
        mockMvc.perform(put("/equipes/EQ999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(equipeService, times(1)).updateEquipe(eq("EQ999"), any(Equipe.class), eq("G002"));
    }

    @Test
    void testDeleteEquipe() throws Exception {
        // Given
        doNothing().when(equipeService).deleteEquipe("EQ001");

        // When & Then
        mockMvc.perform(delete("/equipes/EQ001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(equipeService, times(1)).deleteEquipe("EQ001");
    }

    @Test
    void testDeleteEquipe_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Équipe non trouvée")).when(equipeService).deleteEquipe("EQ999");

        // When & Then
        mockMvc.perform(delete("/equipes/EQ999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(equipeService, times(1)).deleteEquipe("EQ999");
    }
    
    @Test
    void testServerError_GetAllEquipes() throws Exception {
        // Given
        when(equipeService.getAllEquipes()).thenThrow(new RuntimeException("Erreur serveur"));

        // When & Then
        mockMvc.perform(get("/equipes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
    
    @Test
    void testServerError_CreateEquipe() throws Exception {
        // Given
        CreateEquipeRequest request = new CreateEquipeRequest();
        request.setCode("EQ001");
        request.setNom("Équipe Dev");
        request.setDescription("Équipe de développement");
        request.setGroupementCode("G001");

        when(equipeService.createEquipe(any(Equipe.class), anyString()))
                .thenThrow(new RuntimeException("Erreur serveur"));

        // When & Then
        mockMvc.perform(post("/equipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
    
    @Test
    void testGetProfilRecherche() throws Exception {
        // Given
        when(equipeService.getProfilRecherche("EQ001")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/equipes/EQ001/profil-recherche")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(equipeService, times(1)).getProfilRecherche("EQ001");
    }
    
    @Test
    void testUpdateProfilRecherche() throws Exception {
        // Given
        List<CompetenceRequiseDto> dtos = Collections.emptyList();
        Equipe updatedEquipe = new Equipe();
        updatedEquipe.setCode("EQ001");
        
        when(equipeService.updateProfilRecherche(eq("EQ001"), anyList())).thenReturn(updatedEquipe);

        // When & Then
        mockMvc.perform(put("/equipes/EQ001/profil-recherche")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtos)))
                .andExpect(status().isOk());
                
        verify(equipeService, times(1)).updateProfilRecherche(eq("EQ001"), anyList());
    }
    
    @Test
    void testAddCompetenceRequiseToProfilRecherche() throws Exception {
        // Given
        CompetenceRequiseDto dto = new CompetenceRequiseDto();
        dto.setCompetenceLibelle("JAVA");
        dto.setNoteValeur(4);
        
        Equipe updatedEquipe = new Equipe();
        updatedEquipe.setCode("EQ001");
        
        when(equipeService.addCompetenceRequiseToProfilRecherche(eq("EQ001"), any())).thenReturn(updatedEquipe);

        // When & Then
        mockMvc.perform(post("/equipes/EQ001/profil-recherche")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
                
        verify(equipeService, times(1)).addCompetenceRequiseToProfilRecherche(eq("EQ001"), any());
    }
    
    @Test
    void testRemoveCompetenceRequiseFromProfilRecherche() throws Exception {
        // Given
        Equipe updatedEquipe = new Equipe();
        updatedEquipe.setCode("EQ001");
        
        when(equipeService.removeCompetenceRequiseFromProfilRecherche("EQ001", "JAVA")).thenReturn(updatedEquipe);

        // When & Then
        mockMvc.perform(delete("/equipes/EQ001/profil-recherche/JAVA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                
        verify(equipeService, times(1)).removeCompetenceRequiseFromProfilRecherche("EQ001", "JAVA");
    }
}