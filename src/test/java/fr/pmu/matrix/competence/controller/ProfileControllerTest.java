package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.domain.MatriceCompetence;
import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.domain.Profil;
import fr.pmu.matrix.competence.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
    }

    @Test
    public void testGetPersonnesDisponibles() throws Exception {
        // Préparation des données de test
        Personne personne1 = new Personne();
        personne1.setIdentifiant("P001");
        personne1.setNom("Dupont");
        personne1.setPrenom("Jean");

        Personne personne2 = new Personne();
        personne2.setIdentifiant("P002");
        personne2.setNom("Martin");
        personne2.setPrenom("Sophie");

        Profil profil1 = new Profil();
        profil1.setPersonne(personne1);
        profil1.setRapporteur("Manager1");
        profil1.setDateDebutDisponibilite(new Date());
        profil1.setDateFinDisponibilite(new Date());

        Profil profil2 = new Profil();
        profil2.setPersonne(personne2);
        profil2.setRapporteur("Manager2");
        profil2.setDateDebutDisponibilite(new Date());
        profil2.setDateFinDisponibilite(null);

        List<Profil> profils = Arrays.asList(profil1, profil2);

        // Configuration du mock
        when(profileService.getPersonnesDisponibles()).thenReturn(profils);

        // Exécution et vérification
        mockMvc.perform(get("/profiles/personnes-disponibles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].personne.identifiant", is("P001")))
                .andExpect(jsonPath("$[1].personne.identifiant", is("P002")));
    }

    @Test
    public void testGetMatriceCompetencePersonne() throws Exception {
        // Préparation des données
        Personne personne = new Personne();
        personne.setIdentifiant("P001");
        personne.setNom("Dupont");
        personne.setPrenom("Jean");

        Competence java = new Competence("Java", "Langage de programmation");
        Competence spring = new Competence("Spring", "Framework Java");
        
        Note note3 = new Note(3, "Intermédiaire");
        Note note4 = new Note(4, "Avancé");
        
        MatriceCompetence mc1 = new MatriceCompetence(personne, java, note4);
        MatriceCompetence mc2 = new MatriceCompetence(personne, spring, note3);
        
        List<MatriceCompetence> matriceCompetences = Arrays.asList(mc1, mc2);

        // Configuration du mock
        when(profileService.getMatriceCompetencePersonneDisponible("P001")).thenReturn(matriceCompetences);

        // Exécution et vérification
        mockMvc.perform(get("/profiles/personnes-disponibles/P001/competences")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].competence.libelle", is("Java")))
                .andExpect(jsonPath("$[0].note.valeur", is(4)))
                .andExpect(jsonPath("$[1].competence.libelle", is("Spring")))
                .andExpect(jsonPath("$[1].note.valeur", is(3)));
    }
    
    @Test
    public void testFiltrerPersonnesParNotes() throws Exception {
        // Préparation des données
        Personne personne1 = new Personne();
        personne1.setIdentifiant("P001");
        personne1.setNom("Dupont");
        personne1.setPrenom("Jean");

        List<Personne> personnes = Arrays.asList(personne1);

        // Configuration du mock
        when(profileService.filtrerPersonnesParNotes(anyList(), anyInt())).thenReturn(personnes);

        // Exécution et vérification
        mockMvc.perform(get("/profiles/filtrer-par-notes")
                .param("competences", "Java", "Spring")
                .param("noteMinimale", "3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].identifiant", is("P001")));
    }
    
    @Test
    public void testFiltrerPersonnesParCompetences() throws Exception {
        // Préparation des données
        Personne personne1 = new Personne();
        personne1.setIdentifiant("P001");
        personne1.setNom("Dupont");
        personne1.setPrenom("Jean");
        
        Personne personne2 = new Personne();
        personne2.setIdentifiant("P002");
        personne2.setNom("Martin");
        personne2.setPrenom("Sophie");

        List<Personne> personnes = Arrays.asList(personne1, personne2);

        // Configuration du mock
        when(profileService.filtrerPersonnesParCompetences(anyList())).thenReturn(personnes);

        // Exécution et vérification
        mockMvc.perform(get("/profiles/filtrer-par-competences")
                .param("competences", "Java", "Spring", "Angular")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].identifiant", is("P001")))
                .andExpect(jsonPath("$[1].identifiant", is("P002")));
    }
}