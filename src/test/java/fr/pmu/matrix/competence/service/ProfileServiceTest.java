package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.domain.MatriceCompetence;
import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.domain.Profil;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.entity.ProfilEntity;
import fr.pmu.matrix.competence.repository.MatriceCompetenceRepository;
import fr.pmu.matrix.competence.repository.ProfilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProfileServiceTest {

    @Mock
    private ProfilRepository profilRepository;

    @Mock
    private MatriceCompetenceService matriceCompetenceService;

    @Mock
    private PersonneService personneService;

    @Mock
    private MatriceCompetenceRepository matriceCompetenceRepository;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPersonnesDisponibles() {
        // Préparation des données de test
        Date now = new Date();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, -1);
        Date pastDate = calendar.getTime();
        
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, 1);
        Date futureDate = calendar.getTime();

        // Création des entités pour le test
        PersonneEntity personneEntity1 = new PersonneEntity();
        personneEntity1.setIdentifiant("P001");
        personneEntity1.setNom("Dupont");
        personneEntity1.setPrenom("Jean");

        PersonneEntity personneEntity2 = new PersonneEntity();
        personneEntity2.setIdentifiant("P002");
        personneEntity2.setNom("Martin");
        personneEntity2.setPrenom("Sophie");

        ProfilEntity profilEntity1 = new ProfilEntity();
        profilEntity1.setId(1L);
        profilEntity1.setPersonne(personneEntity1);
        profilEntity1.setRapporteur("Manager1");
        profilEntity1.setDateDebutDisponibilite(pastDate);
        profilEntity1.setDateFinDisponibilite(futureDate);

        ProfilEntity profilEntity2 = new ProfilEntity();
        profilEntity2.setId(2L);
        profilEntity2.setPersonne(personneEntity2);
        profilEntity2.setRapporteur("Manager2");
        profilEntity2.setDateDebutDisponibilite(futureDate);  // Pas encore disponible
        profilEntity2.setDateFinDisponibilite(null);

        List<ProfilEntity> profilEntities = Arrays.asList(profilEntity1, profilEntity2);

        // Configuration des mocks
        when(profilRepository.findAll()).thenReturn(profilEntities);
        
        Personne personne1 = new Personne();
        personne1.setIdentifiant("P001");
        personne1.setNom("Dupont");
        personne1.setPrenom("Jean");
        
        when(personneService.getPersonneByIdentifiant("P001")).thenReturn(personne1);

        // Exécution de la méthode testée
        List<Profil> result = profileService.getPersonnesDisponibles();

        // Vérification des résultats
        assertEquals(1, result.size());
        assertEquals("P001", result.get(0).getPersonne().getIdentifiant());
        assertEquals("Manager1", result.get(0).getRapporteur());
    }

    @Test
    public void testFiltrerPersonnesParNotes() {
        // Préparation des données
        Date now = new Date();
        
        Personne personne1 = new Personne();
        personne1.setIdentifiant("P001");
        personne1.setNom("Dupont");
        
        Personne personne2 = new Personne();
        personne2.setIdentifiant("P002");
        personne2.setNom("Martin");

        Profil profil1 = new Profil();
        profil1.setPersonne(personne1);
        profil1.setDateDebutDisponibilite(new Date(now.getTime() - 1000000));
        profil1.setDateFinDisponibilite(new Date(now.getTime() + 1000000));
        
        Profil profil2 = new Profil();
        profil2.setPersonne(personne2);
        profil2.setDateDebutDisponibilite(new Date(now.getTime() - 1000000));
        profil2.setDateFinDisponibilite(new Date(now.getTime() + 1000000));

        List<Profil> profils = Arrays.asList(profil1, profil2);

        // Création des matrices de compétences
        Competence java = new Competence("Java", "Langage de programmation");
        Competence spring = new Competence("Spring", "Framework Java");
        
        Note note3 = new Note(3, "Intermédiaire");
        Note note4 = new Note(4, "Avancé");
        
        MatriceCompetence mc1Java = new MatriceCompetence(personne1, java, note4);
        MatriceCompetence mc1Spring = new MatriceCompetence(personne1, spring, note3);
        
        MatriceCompetence mc2Java = new MatriceCompetence(personne2, java, note3);
        MatriceCompetence mc2Spring = new MatriceCompetence(personne2, spring, note3);
        
        // Configuration des mocks
        // Au lieu de mocker profileService.getPersonnesDisponibles(), créons un espion pour le service
        ProfileService spyProfileService = spy(profileService);
        doReturn(profils).when(spyProfileService).getPersonnesDisponibles();
        
        when(matriceCompetenceService.getCompetencesByPersonne("P001"))
            .thenReturn(Arrays.asList(mc1Java, mc1Spring));
        when(matriceCompetenceService.getCompetencesByPersonne("P002"))
            .thenReturn(Arrays.asList(mc2Java, mc2Spring));

        // Test avec filtrage pour compétence Java avec note minimale 4
        List<Personne> result = spyProfileService.filtrerPersonnesParNotes(Arrays.asList("Java"), 4);
        
        // Vérification
        assertEquals(1, result.size());
        assertEquals("P001", result.get(0).getIdentifiant());
    }
    
    @Test
    public void testFiltrerPersonnesParCompetences() {
        // Préparation des données
        Date now = new Date();
        
        Personne personne1 = new Personne();
        personne1.setIdentifiant("P001");
        personne1.setNom("Dupont");
        
        Personne personne2 = new Personne();
        personne2.setIdentifiant("P002");
        personne2.setNom("Martin");

        Profil profil1 = new Profil();
        profil1.setPersonne(personne1);
        profil1.setDateDebutDisponibilite(new Date(now.getTime() - 1000000));
        profil1.setDateFinDisponibilite(new Date(now.getTime() + 1000000));
        
        Profil profil2 = new Profil();
        profil2.setPersonne(personne2);
        profil2.setDateDebutDisponibilite(new Date(now.getTime() - 1000000));
        profil2.setDateFinDisponibilite(new Date(now.getTime() + 1000000));

        List<Profil> profils = Arrays.asList(profil1, profil2);

        // Création des matrices de compétences
        Competence java = new Competence("Java", "Langage de programmation");
        Competence spring = new Competence("Spring", "Framework Java");
        Competence angular = new Competence("Angular", "Framework JavaScript");
        
        MatriceCompetence mc1Java = new MatriceCompetence(personne1, java, null);
        MatriceCompetence mc1Spring = new MatriceCompetence(personne1, spring, null);
        MatriceCompetence mc1Angular = new MatriceCompetence(personne1, angular, null);
        
        MatriceCompetence mc2Java = new MatriceCompetence(personne2, java, null);
        MatriceCompetence mc2Spring = new MatriceCompetence(personne2, spring, null);
        // personne2 n'a pas Angular
        
        // Configuration des mocks
        ProfileService spyProfileService = spy(profileService);
        doReturn(profils).when(spyProfileService).getPersonnesDisponibles();
        
        when(matriceCompetenceService.getCompetencesByPersonne("P001"))
            .thenReturn(Arrays.asList(mc1Java, mc1Spring, mc1Angular));
        when(matriceCompetenceService.getCompetencesByPersonne("P002"))
            .thenReturn(Arrays.asList(mc2Java, mc2Spring));

        // Test avec filtrage pour compétences Java et Angular
        List<Personne> result = spyProfileService.filtrerPersonnesParCompetences(Arrays.asList("Java", "Angular"));
        
        // Vérification - seule la personne1 a toutes les compétences recherchées
        assertEquals(1, result.size());
        assertEquals("P001", result.get(0).getIdentifiant());
    }
}