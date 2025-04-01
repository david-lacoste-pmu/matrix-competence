package fr.pmu.matrix.competence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pmu.matrix.competence.domain.Groupement;
import fr.pmu.matrix.competence.dto.CreateGroupementRequest;
import fr.pmu.matrix.competence.dto.UpdateGroupementRequest;
import fr.pmu.matrix.competence.service.GroupementService;
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

@WebMvcTest(GroupementController.class)
class GroupementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GroupementService groupementService;

    @Test
    void testGetAllGroupements() throws Exception {
        // Given
        Groupement groupement1 = new Groupement();
        groupement1.setCode("G001");
        groupement1.setLibelle("Groupement IT");
        groupement1.setDirection("Direction Informatique");

        Groupement groupement2 = new Groupement();
        groupement2.setCode("G002");
        groupement2.setLibelle("Groupement Finance");
        groupement2.setDirection("Direction Financière");

        List<Groupement> groupements = Arrays.asList(groupement1, groupement2);

        when(groupementService.getAllGroupements()).thenReturn(groupements);

        // When & Then
        mockMvc.perform(get("/groupements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(groupements)))
                .andExpect(jsonPath("$[0].code").value("G001"))
                .andExpect(jsonPath("$[1].code").value("G002"));

        verify(groupementService, times(1)).getAllGroupements();
    }

    @Test
    void testGetGroupementByCode() throws Exception {
        // Given
        Groupement groupement = new Groupement();
        groupement.setCode("G001");
        groupement.setLibelle("Groupement IT");
        groupement.setDirection("Direction Informatique");

        when(groupementService.getGroupementByCode("G001")).thenReturn(groupement);

        // When & Then
        mockMvc.perform(get("/groupements/G001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("G001"))
                .andExpect(jsonPath("$.libelle").value("Groupement IT"))
                .andExpect(jsonPath("$.direction").value("Direction Informatique"));

        verify(groupementService, times(1)).getGroupementByCode("G001");
    }

    @Test
    void testGetGroupementByCode_NotFound() throws Exception {
        // Given
        when(groupementService.getGroupementByCode("G999")).thenThrow(new RuntimeException("Groupement non trouvé"));

        // When & Then
        mockMvc.perform(get("/groupements/G999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(groupementService, times(1)).getGroupementByCode("G999");
    }

    @Test
    void testCreateGroupement() throws Exception {
        // Given
        CreateGroupementRequest request = new CreateGroupementRequest();
        request.setCode("G001");
        request.setLibelle("Groupement IT");
        request.setDirection("Direction Informatique");

        Groupement createdGroupement = new Groupement();
        createdGroupement.setCode("G001");
        createdGroupement.setLibelle("Groupement IT");
        createdGroupement.setDirection("Direction Informatique");

        when(groupementService.createGroupement(any(Groupement.class))).thenReturn(createdGroupement);

        // When & Then
        mockMvc.perform(post("/groupements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("G001"))
                .andExpect(jsonPath("$.libelle").value("Groupement IT"))
                .andExpect(jsonPath("$.direction").value("Direction Informatique"));

        verify(groupementService, times(1)).createGroupement(any(Groupement.class));
    }

    @Test
    void testCreateGroupement_AlreadyExists() throws Exception {
        // Given
        CreateGroupementRequest request = new CreateGroupementRequest();
        request.setCode("G001");
        request.setLibelle("Groupement IT");
        request.setDirection("Direction Informatique");

        when(groupementService.createGroupement(any(Groupement.class)))
                .thenThrow(new RuntimeException("Un groupement avec ce code existe déjà"));

        // When & Then
        mockMvc.perform(post("/groupements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(groupementService, times(1)).createGroupement(any(Groupement.class));
    }

    @Test
    void testUpdateGroupement() throws Exception {
        // Given
        UpdateGroupementRequest request = new UpdateGroupementRequest();
        request.setLibelle("Groupement SI");
        request.setDirection("Direction SI");

        Groupement updatedGroupement = new Groupement();
        updatedGroupement.setCode("G001");
        updatedGroupement.setLibelle("Groupement SI");
        updatedGroupement.setDirection("Direction SI");

        when(groupementService.updateGroupement(eq("G001"), any(Groupement.class))).thenReturn(updatedGroupement);

        // When & Then
        mockMvc.perform(put("/groupements/G001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("G001"))
                .andExpect(jsonPath("$.libelle").value("Groupement SI"))
                .andExpect(jsonPath("$.direction").value("Direction SI"));

        verify(groupementService, times(1)).updateGroupement(eq("G001"), any(Groupement.class));
    }

    @Test
    void testUpdateGroupement_NotFound() throws Exception {
        // Given
        UpdateGroupementRequest request = new UpdateGroupementRequest();
        request.setLibelle("Groupement SI");
        request.setDirection("Direction SI");

        when(groupementService.updateGroupement(eq("G999"), any(Groupement.class)))
                .thenThrow(new RuntimeException("Groupement non trouvé"));

        // When & Then
        mockMvc.perform(put("/groupements/G999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(groupementService, times(1)).updateGroupement(eq("G999"), any(Groupement.class));
    }

    @Test
    void testDeleteGroupement() throws Exception {
        // Given
        doNothing().when(groupementService).deleteGroupement("G001");

        // When & Then
        mockMvc.perform(delete("/groupements/G001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(groupementService, times(1)).deleteGroupement("G001");
    }

    @Test
    void testDeleteGroupement_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Groupement non trouvé")).when(groupementService).deleteGroupement("G999");

        // When & Then
        mockMvc.perform(delete("/groupements/G999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(groupementService, times(1)).deleteGroupement("G999");
    }
}