package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Habilitation;
import fr.pmu.matrix.competence.entity.HabilitationEntity;
import fr.pmu.matrix.competence.repository.HabilitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabilitationServiceTest {

    @Mock
    private HabilitationRepository habilitationRepository;

    @InjectMocks
    private HabilitationService habilitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllHabilitations() {
        // Préparation des données de test
        HabilitationEntity entity1 = new HabilitationEntity();
        entity1.setCode("HAB1");
        entity1.setDescription("Habilitation 1");

        HabilitationEntity entity2 = new HabilitationEntity();
        entity2.setCode("HAB2");
        entity2.setDescription("Habilitation 2");

        List<HabilitationEntity> entities = Arrays.asList(entity1, entity2);

        // Configuration du mock
        when(habilitationRepository.findAll()).thenReturn(entities);

        // Exécution du test
        List<Habilitation> result = habilitationService.getAllHabilitations();

        // Vérification des résultats
        assertEquals(2, result.size());
        assertEquals("HAB1", result.get(0).getCode());
        assertEquals("Habilitation 1", result.get(0).getDescription());
        assertEquals("HAB2", result.get(1).getCode());
        assertEquals("Habilitation 2", result.get(1).getDescription());

        // Vérification des appels au repository
        verify(habilitationRepository, times(1)).findAll();
    }

    @Test
    void testGetHabilitationByCode() {
        // Préparation des données de test
        HabilitationEntity entity = new HabilitationEntity();
        entity.setCode("HAB1");
        entity.setDescription("Habilitation 1");

        // Configuration du mock
        when(habilitationRepository.findById("HAB1")).thenReturn(Optional.of(entity));

        // Exécution du test
        Habilitation result = habilitationService.getHabilitationByCode("HAB1");

        // Vérification des résultats
        assertEquals("HAB1", result.getCode());
        assertEquals("Habilitation 1", result.getDescription());

        // Vérification des appels au repository
        verify(habilitationRepository, times(1)).findById("HAB1");
    }

    @Test
    void testGetHabilitationByCodeNotFound() {
        // Configuration du mock
        when(habilitationRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

        // Exécution du test et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habilitationService.getHabilitationByCode("NONEXISTENT");
        });

        assertEquals("Habilitation non trouvée avec le code: NONEXISTENT", exception.getMessage());

        // Vérification des appels au repository
        verify(habilitationRepository, times(1)).findById("NONEXISTENT");
    }

    @Test
    void testCreateHabilitation() {
        // Préparation des données de test
        Habilitation habilitation = new Habilitation();
        habilitation.setCode("HAB1");
        habilitation.setDescription("Habilitation 1");

        HabilitationEntity savedEntity = new HabilitationEntity();
        savedEntity.setCode("HAB1");
        savedEntity.setDescription("Habilitation 1");

        // Configuration du mock
        when(habilitationRepository.existsById("HAB1")).thenReturn(false);
        when(habilitationRepository.save(any(HabilitationEntity.class))).thenReturn(savedEntity);

        // Exécution du test
        Habilitation result = habilitationService.createHabilitation(habilitation);

        // Vérification des résultats
        assertEquals("HAB1", result.getCode());
        assertEquals("Habilitation 1", result.getDescription());

        // Vérification des appels au repository
        verify(habilitationRepository, times(1)).existsById("HAB1");
        verify(habilitationRepository, times(1)).save(any(HabilitationEntity.class));
    }

    @Test
    void testCreateHabilitationAlreadyExists() {
        // Préparation des données de test
        Habilitation habilitation = new Habilitation();
        habilitation.setCode("HAB1");
        habilitation.setDescription("Habilitation 1");

        // Configuration du mock
        when(habilitationRepository.existsById("HAB1")).thenReturn(true);

        // Exécution du test et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habilitationService.createHabilitation(habilitation);
        });

        assertEquals("Une habilitation avec le code HAB1 existe déjà", exception.getMessage());

        // Vérification des appels au repository
        verify(habilitationRepository, times(1)).existsById("HAB1");
        verify(habilitationRepository, never()).save(any(HabilitationEntity.class));
    }

    @Test
    void testUpdateHabilitation() {
        // Préparation des données de test
        Habilitation habilitation = new Habilitation();
        habilitation.setCode("HAB1");
        habilitation.setDescription("Description mise à jour");

        HabilitationEntity existingEntity = new HabilitationEntity();
        existingEntity.setCode("HAB1");
        existingEntity.setDescription("Ancienne description");

        HabilitationEntity updatedEntity = new HabilitationEntity();
        updatedEntity.setCode("HAB1");
        updatedEntity.setDescription("Description mise à jour");

        // Configuration du mock
        when(habilitationRepository.findById("HAB1")).thenReturn(Optional.of(existingEntity));
        when(habilitationRepository.save(any(HabilitationEntity.class))).thenReturn(updatedEntity);

        // Exécution du test
        Habilitation result = habilitationService.updateHabilitation("HAB1", habilitation);

        // Vérification des résultats
        assertEquals("HAB1", result.getCode());
        assertEquals("Description mise à jour", result.getDescription());

        // Vérification des appels au repository
        verify(habilitationRepository, times(1)).findById("HAB1");
        verify(habilitationRepository, times(1)).save(any(HabilitationEntity.class));
    }

    @Test
    void testUpdateHabilitationNotFound() {
        // Préparation des données de test
        Habilitation habilitation = new Habilitation();
        habilitation.setCode("NONEXISTENT");
        habilitation.setDescription("Description mise à jour");

        // Configuration du mock
        when(habilitationRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

        // Exécution du test et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habilitationService.updateHabilitation("NONEXISTENT", habilitation);
        });

        assertEquals("Habilitation non trouvée avec le code: NONEXISTENT", exception.getMessage());

        // Vérification des appels au repository
        verify(habilitationRepository, times(1)).findById("NONEXISTENT");
        verify(habilitationRepository, never()).save(any(HabilitationEntity.class));
    }

    @Test
    void testDeleteHabilitation() {
        // Configuration du mock
        when(habilitationRepository.existsById("HAB1")).thenReturn(true);

        // Exécution du test
        habilitationService.deleteHabilitation("HAB1");

        // Vérification des appels au repository
        verify(habilitationRepository, times(1)).existsById("HAB1");
        verify(habilitationRepository, times(1)).deleteById("HAB1");
    }

    @Test
    void testDeleteHabilitationNotFound() {
        // Configuration du mock
        when(habilitationRepository.existsById("NONEXISTENT")).thenReturn(false);

        // Exécution du test et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            habilitationService.deleteHabilitation("NONEXISTENT");
        });

        assertEquals("Habilitation non trouvée avec le code: NONEXISTENT", exception.getMessage());

        // Vérification des appels au repository
        verify(habilitationRepository, times(1)).existsById("NONEXISTENT");
        verify(habilitationRepository, never()).deleteById(anyString());
    }
}