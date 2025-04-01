package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Habilitation;
import fr.pmu.matrix.competence.domain.Utilisateur;
import fr.pmu.matrix.competence.entity.HabilitationEntity;
import fr.pmu.matrix.competence.entity.UtilisateurEntity;
import fr.pmu.matrix.competence.repository.HabilitationRepository;
import fr.pmu.matrix.competence.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private HabilitationRepository habilitationRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUtilisateurs() {
        // Préparation des données de test
        HabilitationEntity hab1 = new HabilitationEntity();
        hab1.setCode("HAB1");
        hab1.setDescription("Habilitation 1");

        HabilitationEntity hab2 = new HabilitationEntity();
        hab2.setCode("HAB2");
        hab2.setDescription("Habilitation 2");

        UtilisateurEntity user1 = new UtilisateurEntity();
        user1.setMatricule("MAT001");
        user1.setHabilitations(Arrays.asList(hab1));

        UtilisateurEntity user2 = new UtilisateurEntity();
        user2.setMatricule("MAT002");
        user2.setHabilitations(Arrays.asList(hab1, hab2));

        List<UtilisateurEntity> entities = Arrays.asList(user1, user2);

        // Configuration des mocks
        when(utilisateurRepository.findAll()).thenReturn(entities);

        // Exécution de la méthode à tester
        List<Utilisateur> result = utilisateurService.getAllUtilisateurs();

        // Vérification des résultats
        assertEquals(2, result.size());
        assertEquals("MAT001", result.get(0).getMatricule());
        assertEquals(1, result.get(0).getHabilitations().size());
        assertEquals("HAB1", result.get(0).getHabilitations().get(0).getCode());
        
        assertEquals("MAT002", result.get(1).getMatricule());
        assertEquals(2, result.get(1).getHabilitations().size());
        assertEquals("HAB1", result.get(1).getHabilitations().get(0).getCode());
        assertEquals("HAB2", result.get(1).getHabilitations().get(1).getCode());

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).findAll();
    }

    @Test
    void testGetUtilisateurByMatricule() {
        // Préparation des données de test
        HabilitationEntity hab1 = new HabilitationEntity();
        hab1.setCode("HAB1");
        hab1.setDescription("Habilitation 1");

        UtilisateurEntity user = new UtilisateurEntity();
        user.setMatricule("MAT001");
        user.setHabilitations(Arrays.asList(hab1));

        // Configuration des mocks
        when(utilisateurRepository.findById("MAT001")).thenReturn(Optional.of(user));

        // Exécution de la méthode à tester
        Utilisateur result = utilisateurService.getUtilisateurByMatricule("MAT001");

        // Vérification des résultats
        assertEquals("MAT001", result.getMatricule());
        assertEquals(1, result.getHabilitations().size());
        assertEquals("HAB1", result.getHabilitations().get(0).getCode());
        assertEquals("Habilitation 1", result.getHabilitations().get(0).getDescription());

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).findById("MAT001");
    }

    @Test
    void testGetUtilisateurByMatriculeNotFound() {
        // Configuration des mocks
        when(utilisateurRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

        // Exécution et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            utilisateurService.getUtilisateurByMatricule("NONEXISTENT");
        });

        assertEquals("Utilisateur non trouvé avec le matricule: NONEXISTENT", exception.getMessage());

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).findById("NONEXISTENT");
    }

    @Test
    void testCreateUtilisateur() {
        // Préparation des données de test
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMatricule("MAT001");
        
        List<String> habilitationsIds = Arrays.asList("HAB1", "HAB2");
        
        HabilitationEntity hab1 = new HabilitationEntity();
        hab1.setCode("HAB1");
        hab1.setDescription("Habilitation 1");
        
        HabilitationEntity hab2 = new HabilitationEntity();
        hab2.setCode("HAB2");
        hab2.setDescription("Habilitation 2");
        
        List<HabilitationEntity> habilitationEntities = Arrays.asList(hab1, hab2);
        
        UtilisateurEntity savedEntity = new UtilisateurEntity();
        savedEntity.setMatricule("MAT001");
        savedEntity.setHabilitations(habilitationEntities);

        // Configuration des mocks
        when(utilisateurRepository.existsById("MAT001")).thenReturn(false);
        when(habilitationRepository.findAllById(habilitationsIds)).thenReturn(habilitationEntities);
        when(utilisateurRepository.save(any(UtilisateurEntity.class))).thenReturn(savedEntity);

        // Exécution de la méthode à tester
        Utilisateur result = utilisateurService.createUtilisateur(utilisateur, habilitationsIds);

        // Vérification des résultats
        assertEquals("MAT001", result.getMatricule());
        assertEquals(2, result.getHabilitations().size());
        assertEquals("HAB1", result.getHabilitations().get(0).getCode());
        assertEquals("HAB2", result.getHabilitations().get(1).getCode());

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).existsById("MAT001");
        verify(habilitationRepository, times(1)).findAllById(habilitationsIds);
        verify(utilisateurRepository, times(1)).save(any(UtilisateurEntity.class));
    }

    @Test
    void testCreateUtilisateurWithNoHabilitations() {
        // Préparation des données de test
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMatricule("MAT001");
        
        UtilisateurEntity savedEntity = new UtilisateurEntity();
        savedEntity.setMatricule("MAT001");
        savedEntity.setHabilitations(new ArrayList<>());

        // Configuration des mocks
        when(utilisateurRepository.existsById("MAT001")).thenReturn(false);
        when(utilisateurRepository.save(any(UtilisateurEntity.class))).thenReturn(savedEntity);

        // Exécution de la méthode à tester
        Utilisateur result = utilisateurService.createUtilisateur(utilisateur, null);

        // Vérification des résultats
        assertEquals("MAT001", result.getMatricule());
        assertTrue(result.getHabilitations().isEmpty());

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).existsById("MAT001");
        verify(habilitationRepository, never()).findAllById(anyList());
        verify(utilisateurRepository, times(1)).save(any(UtilisateurEntity.class));
    }

    @Test
    void testCreateUtilisateurAlreadyExists() {
        // Préparation des données de test
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMatricule("MAT001");
        
        List<String> habilitationsIds = Arrays.asList("HAB1");

        // Configuration des mocks
        when(utilisateurRepository.existsById("MAT001")).thenReturn(true);

        // Exécution et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            utilisateurService.createUtilisateur(utilisateur, habilitationsIds);
        });

        assertEquals("Un utilisateur avec le matricule MAT001 existe déjà", exception.getMessage());

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).existsById("MAT001");
        verify(habilitationRepository, never()).findAllById(anyList());
        verify(utilisateurRepository, never()).save(any(UtilisateurEntity.class));
    }

    @Test
    void testUpdateUtilisateurHabilitations() {
        // Préparation des données de test
        String matricule = "MAT001";
        List<String> habilitationsIds = Arrays.asList("HAB1", "HAB2");
        
        UtilisateurEntity existingEntity = new UtilisateurEntity();
        existingEntity.setMatricule(matricule);
        existingEntity.setHabilitations(new ArrayList<>());
        
        HabilitationEntity hab1 = new HabilitationEntity();
        hab1.setCode("HAB1");
        hab1.setDescription("Habilitation 1");
        
        HabilitationEntity hab2 = new HabilitationEntity();
        hab2.setCode("HAB2");
        hab2.setDescription("Habilitation 2");
        
        List<HabilitationEntity> habilitationEntities = Arrays.asList(hab1, hab2);
        
        UtilisateurEntity updatedEntity = new UtilisateurEntity();
        updatedEntity.setMatricule(matricule);
        updatedEntity.setHabilitations(habilitationEntities);

        // Configuration des mocks
        when(utilisateurRepository.findById(matricule)).thenReturn(Optional.of(existingEntity));
        when(habilitationRepository.findAllById(habilitationsIds)).thenReturn(habilitationEntities);
        when(utilisateurRepository.save(any(UtilisateurEntity.class))).thenReturn(updatedEntity);

        // Exécution de la méthode à tester
        Utilisateur result = utilisateurService.updateUtilisateurHabilitations(matricule, habilitationsIds);

        // Vérification des résultats
        assertEquals(matricule, result.getMatricule());
        assertEquals(2, result.getHabilitations().size());
        assertEquals("HAB1", result.getHabilitations().get(0).getCode());
        assertEquals("HAB2", result.getHabilitations().get(1).getCode());

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).findById(matricule);
        verify(habilitationRepository, times(1)).findAllById(habilitationsIds);
        verify(utilisateurRepository, times(1)).save(any(UtilisateurEntity.class));
    }

    @Test
    void testUpdateUtilisateurHabilitationsNotFound() {
        // Préparation des données de test
        String matricule = "NONEXISTENT";
        List<String> habilitationsIds = Arrays.asList("HAB1");

        // Configuration des mocks
        when(utilisateurRepository.findById(matricule)).thenReturn(Optional.empty());

        // Exécution et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            utilisateurService.updateUtilisateurHabilitations(matricule, habilitationsIds);
        });

        assertEquals("Utilisateur non trouvé avec le matricule: NONEXISTENT", exception.getMessage());

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).findById(matricule);
        verify(habilitationRepository, never()).findAllById(anyList());
        verify(utilisateurRepository, never()).save(any(UtilisateurEntity.class));
    }

    @Test
    void testDeleteUtilisateur() {
        // Préparation des données de test
        String matricule = "MAT001";

        // Configuration des mocks
        when(utilisateurRepository.existsById(matricule)).thenReturn(true);

        // Exécution de la méthode à tester
        utilisateurService.deleteUtilisateur(matricule);

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).existsById(matricule);
        verify(utilisateurRepository, times(1)).deleteById(matricule);
    }

    @Test
    void testDeleteUtilisateurNotFound() {
        // Préparation des données de test
        String matricule = "NONEXISTENT";

        // Configuration des mocks
        when(utilisateurRepository.existsById(matricule)).thenReturn(false);

        // Exécution et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            utilisateurService.deleteUtilisateur(matricule);
        });

        assertEquals("Utilisateur non trouvé avec le matricule: NONEXISTENT", exception.getMessage());

        // Vérification des appels aux repositories
        verify(utilisateurRepository, times(1)).existsById(matricule);
        verify(utilisateurRepository, never()).deleteById(anyString());
    }
}