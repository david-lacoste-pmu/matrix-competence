package fr.pmu.matrix.competence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.dto.CreatePersonneRequest;
import fr.pmu.matrix.competence.dto.UpdatePersonneRequest;
import fr.pmu.matrix.competence.service.PersonneService;
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

@WebMvcTest(PersonneController.class)
class PersonneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonneService personneService;

    @Test
    void testGetAllPersonnes() throws Exception {
        // Given
        Personne personne1 = new Personne();
        personne1.setIdentifiant("P123");
        personne1.setNom("Dupont");
        personne1.setPrenom("Jean");
        personne1.setPoste("Développeur");

        Personne personne2 = new Personne();
        personne2.setIdentifiant("P124");
        personne2.setNom("Martin");
        personne2.setPrenom("Sophie");
        personne2.setPoste("Designer");

        List<Personne> personnes = Arrays.asList(personne1, personne2);

        when(personneService.getAllPersonnes()).thenReturn(personnes);

        // When & Then
        mockMvc.perform(get("/personnes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personnes)))
                .andExpect(jsonPath("$[0].identifiant").value("P123"))
                .andExpect(jsonPath("$[1].identifiant").value("P124"));

        verify(personneService, times(1)).getAllPersonnes();
    }

    @Test
    void testGetPersonneByIdentifiant() throws Exception {
        // Given
        Personne personne = new Personne();
        personne.setIdentifiant("P123");
        personne.setNom("Dupont");
        personne.setPrenom("Jean");
        personne.setPoste("Développeur");
        
        Equipe equipe = new Equipe();
        equipe.setCode("E001");
        equipe.setNom("Équipe Backend");
        equipe.setDescription("Équipe responsable du développement backend");
        personne.setEquipe(equipe);

        when(personneService.getPersonneByIdentifiant("P123")).thenReturn(personne);

        // When & Then
        mockMvc.perform(get("/personnes/P123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifiant").value("P123"))
                .andExpect(jsonPath("$.equipe.code").value("E001"));

        verify(personneService, times(1)).getPersonneByIdentifiant("P123");
    }

    @Test
    void testGetPersonneByIdentifiant_NotFound() throws Exception {
        // Given
        when(personneService.getPersonneByIdentifiant("P999")).thenThrow(new RuntimeException("Personne non trouvée"));

        // When & Then
        mockMvc.perform(get("/personnes/P999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(personneService, times(1)).getPersonneByIdentifiant("P999");
    }

    @Test
    void testCreatePersonne() throws Exception {
        // Given
        CreatePersonneRequest request = new CreatePersonneRequest();
        request.setIdentifiant("P123");
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setPoste("Développeur");
        request.setEquipeId("E001");

        Personne createdPersonne = new Personne();
        createdPersonne.setIdentifiant("P123");
        createdPersonne.setNom("Dupont");
        createdPersonne.setPrenom("Jean");
        createdPersonne.setPoste("Développeur");
        
        Equipe equipe = new Equipe();
        equipe.setCode("E001");
        equipe.setNom("Équipe Backend");
        equipe.setDescription("Équipe responsable du développement backend");
        createdPersonne.setEquipe(equipe);

        when(personneService.createPersonne(any(Personne.class), eq("E001"))).thenReturn(createdPersonne);

        // When & Then
        mockMvc.perform(post("/personnes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifiant").value("P123"))
                .andExpect(jsonPath("$.equipe.code").value("E001"));

        verify(personneService, times(1)).createPersonne(any(Personne.class), eq("E001"));
    }

    @Test
    void testCreatePersonne_AlreadyExists() throws Exception {
        // Given
        CreatePersonneRequest request = new CreatePersonneRequest();
        request.setIdentifiant("P123");
        request.setNom("Dupont");
        request.setPrenom("Jean");

        when(personneService.createPersonne(any(Personne.class), isNull()))
                .thenThrow(new RuntimeException("Une personne avec cet identifiant existe déjà"));

        // When & Then
        mockMvc.perform(post("/personnes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(personneService, times(1)).createPersonne(any(Personne.class), isNull());
    }

    @Test
    void testUpdatePersonne() throws Exception {
        // Given
        UpdatePersonneRequest request = new UpdatePersonneRequest();
        request.setNom("Dupont");
        request.setPrenom("Pierre");
        request.setPoste("Chef de Projet");
        request.setEquipeId("E002");

        Personne updatedPersonne = new Personne();
        updatedPersonne.setIdentifiant("P123");
        updatedPersonne.setNom("Dupont");
        updatedPersonne.setPrenom("Pierre");
        updatedPersonne.setPoste("Chef de Projet");
        
        Equipe newEquipe = new Equipe();
        newEquipe.setCode("E002");
        newEquipe.setNom("Équipe Frontend");
        updatedPersonne.setEquipe(newEquipe);

        when(personneService.updatePersonne(eq("P123"), any(Personne.class), eq("E002")))
                .thenReturn(updatedPersonne);

        // When & Then
        mockMvc.perform(put("/personnes/P123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifiant").value("P123"))
                .andExpect(jsonPath("$.prenom").value("Pierre"))
                .andExpect(jsonPath("$.poste").value("Chef de Projet"))
                .andExpect(jsonPath("$.equipe.code").value("E002"));

        verify(personneService, times(1))
                .updatePersonne(eq("P123"), any(Personne.class), eq("E002"));
    }

    @Test
    void testUpdatePersonne_NotFound() throws Exception {
        // Given
        UpdatePersonneRequest request = new UpdatePersonneRequest();
        request.setNom("Dupont");
        request.setPrenom("Pierre");

        when(personneService.updatePersonne(eq("P999"), any(Personne.class), isNull()))
                .thenThrow(new RuntimeException("Personne non trouvée"));

        // When & Then
        mockMvc.perform(put("/personnes/P999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(personneService, times(1))
                .updatePersonne(eq("P999"), any(Personne.class), isNull());
    }

    @Test
    void testDeletePersonne() throws Exception {
        // Given
        doNothing().when(personneService).deletePersonne("P123");

        // When & Then
        mockMvc.perform(delete("/personnes/P123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(personneService, times(1)).deletePersonne("P123");
    }

    @Test
    void testDeletePersonne_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Personne non trouvée")).when(personneService).deletePersonne("P999");

        // When & Then
        mockMvc.perform(delete("/personnes/P999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(personneService, times(1)).deletePersonne("P999");
    }
}