package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.GroupementEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.mapper.EquipeMapper;
import fr.pmu.matrix.competence.repository.EquipeRepository;
import fr.pmu.matrix.competence.repository.GroupementRepository;
import fr.pmu.matrix.competence.repository.PersonneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private GroupementRepository groupementRepository;

    @Mock
    private PersonneRepository personneRepository;
    
    @Spy
    private EquipeMapper equipeMapper = new EquipeMapper();

    @InjectMocks
    private EquipeService equipeService;

    @Test
    void testGetAllEquipes() {
        // Given
        GroupementEntity groupementEntity = new GroupementEntity();
        groupementEntity.setCode("G001");
        groupementEntity.setLibelle("Groupement IT");
        groupementEntity.setDirection("Direction Informatique");

        EquipeEntity entity1 = new EquipeEntity();
        entity1.setCode("EQ001");
        entity1.setNom("Équipe Dev");
        entity1.setDescription("Équipe de développement");
        entity1.setGroupement(groupementEntity);

        EquipeEntity entity2 = new EquipeEntity();
        entity2.setCode("EQ002");
        entity2.setNom("Équipe QA");
        entity2.setDescription("Équipe de test");
        entity2.setGroupement(groupementEntity);

        when(equipeRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));
        when(personneRepository.findByEquipeCode(anyString())).thenReturn(Collections.emptyList());

        // When
        List<Equipe> result = equipeService.getAllEquipes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("EQ001", result.get(0).getCode());
        assertEquals("EQ002", result.get(1).getCode());
        verify(equipeRepository, times(1)).findAll();
        verify(personneRepository, times(2)).findByEquipeCode(anyString());
        verify(equipeMapper, times(2)).convertToEquipe(any(EquipeEntity.class), anyList());
    }

    @Test
    void testGetEquipeByCode() {
        // Given
        GroupementEntity groupementEntity = new GroupementEntity();
        groupementEntity.setCode("G001");
        groupementEntity.setLibelle("Groupement IT");
        groupementEntity.setDirection("Direction Informatique");

        EquipeEntity entity = new EquipeEntity();
        entity.setCode("EQ001");
        entity.setNom("Équipe Dev");
        entity.setDescription("Équipe de développement");
        entity.setGroupement(groupementEntity);

        PersonneEntity personneEntity = new PersonneEntity();
        personneEntity.setIdentifiant("P001");
        personneEntity.setNom("Dupont");
        personneEntity.setPrenom("Jean");
        personneEntity.setPoste("Développeur");

        List<PersonneEntity> personneEntities = Arrays.asList(personneEntity);

        when(equipeRepository.findById("EQ001")).thenReturn(Optional.of(entity));
        when(personneRepository.findByEquipeCode("EQ001")).thenReturn(personneEntities);

        // When
        Equipe result = equipeService.getEquipeByCode("EQ001");

        // Then
        assertNotNull(result);
        assertEquals("EQ001", result.getCode());
        assertEquals("Équipe Dev", result.getNom());
        assertEquals("Équipe de développement", result.getDescription());
        assertNotNull(result.getGroupement());
        assertEquals("G001", result.getGroupement().getCode());
        assertEquals(1, result.getMembres().size());
        assertEquals("P001", result.getMembres().get(0).getIdentifiant());
        verify(equipeRepository, times(1)).findById("EQ001");
        verify(personneRepository, times(1)).findByEquipeCode("EQ001");
        verify(equipeMapper, times(1)).convertToEquipe(entity, personneEntities);
    }

    @Test
    void testGetEquipeByCode_NotFound() {
        // Given
        when(equipeRepository.findById("EQ999")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> equipeService.getEquipeByCode("EQ999"));
        verify(equipeRepository, times(1)).findById("EQ999");
        verify(personneRepository, never()).findByEquipeCode(anyString());
        verify(equipeMapper, never()).convertToEquipe(any(EquipeEntity.class), anyList());
    }

    @Test
    void testCreateEquipe() {
        // Given
        Equipe equipe = new Equipe();
        equipe.setCode("EQ001");
        equipe.setNom("Équipe Dev");
        equipe.setDescription("Équipe de développement");

        GroupementEntity groupementEntity = new GroupementEntity();
        groupementEntity.setCode("G001");
        groupementEntity.setLibelle("Groupement IT");
        groupementEntity.setDirection("Direction Informatique");

        EquipeEntity savedEntity = new EquipeEntity();
        savedEntity.setCode("EQ001");
        savedEntity.setNom("Équipe Dev");
        savedEntity.setDescription("Équipe de développement");
        savedEntity.setGroupement(groupementEntity);

        List<PersonneEntity> emptyPersonneList = Collections.emptyList();

        when(equipeRepository.existsById("EQ001")).thenReturn(false);
        when(groupementRepository.findById("G001")).thenReturn(Optional.of(groupementEntity));
        when(equipeRepository.save(any(EquipeEntity.class))).thenReturn(savedEntity);
        when(personneRepository.findByEquipeCode("EQ001")).thenReturn(emptyPersonneList);

        // When
        Equipe result = equipeService.createEquipe(equipe, "G001");

        // Then
        assertNotNull(result);
        assertEquals("EQ001", result.getCode());
        assertEquals("Équipe Dev", result.getNom());
        assertEquals("Équipe de développement", result.getDescription());
        assertNotNull(result.getGroupement());
        assertEquals("G001", result.getGroupement().getCode());
        verify(equipeRepository, times(1)).existsById("EQ001");
        verify(groupementRepository, times(1)).findById("G001");
        verify(equipeRepository, times(1)).save(any(EquipeEntity.class));
        verify(personneRepository, times(1)).findByEquipeCode("EQ001");
        verify(equipeMapper, times(1)).convertToEquipe(savedEntity, emptyPersonneList);
    }

    @Test
    void testCreateEquipe_AlreadyExists() {
        // Given
        Equipe equipe = new Equipe();
        equipe.setCode("EQ001");
        equipe.setNom("Équipe Dev");
        equipe.setDescription("Équipe de développement");

        when(equipeRepository.existsById("EQ001")).thenReturn(true);

        // When / Then
        assertThrows(RuntimeException.class, () -> equipeService.createEquipe(equipe, "G001"));
        verify(equipeRepository, times(1)).existsById("EQ001");
        verify(groupementRepository, never()).findById(anyString());
        verify(equipeRepository, never()).save(any(EquipeEntity.class));
        verify(equipeMapper, never()).convertToEquipe(any(EquipeEntity.class), anyList());
    }

    @Test
    void testCreateEquipe_GroupementNotFound() {
        // Given
        Equipe equipe = new Equipe();
        equipe.setCode("EQ001");
        equipe.setNom("Équipe Dev");
        equipe.setDescription("Équipe de développement");

        when(equipeRepository.existsById("EQ001")).thenReturn(false);
        when(groupementRepository.findById("G999")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> equipeService.createEquipe(equipe, "G999"));
        verify(equipeRepository, times(1)).existsById("EQ001");
        verify(groupementRepository, times(1)).findById("G999");
        verify(equipeRepository, never()).save(any(EquipeEntity.class));
        verify(equipeMapper, never()).convertToEquipe(any(EquipeEntity.class), anyList());
    }

    @Test
    void testUpdateEquipe() {
        // Given
        Equipe equipe = new Equipe();
        equipe.setNom("Équipe Dev Updated");
        equipe.setDescription("Description mise à jour");

        GroupementEntity oldGroupementEntity = new GroupementEntity();
        oldGroupementEntity.setCode("G001");
        oldGroupementEntity.setLibelle("Groupement IT");

        GroupementEntity newGroupementEntity = new GroupementEntity();
        newGroupementEntity.setCode("G002");
        newGroupementEntity.setLibelle("Groupement Dev");

        EquipeEntity existingEntity = new EquipeEntity();
        existingEntity.setCode("EQ001");
        existingEntity.setNom("Équipe Dev");
        existingEntity.setDescription("Équipe de développement");
        existingEntity.setGroupement(oldGroupementEntity);

        EquipeEntity updatedEntity = new EquipeEntity();
        updatedEntity.setCode("EQ001");
        updatedEntity.setNom("Équipe Dev Updated");
        updatedEntity.setDescription("Description mise à jour");
        updatedEntity.setGroupement(newGroupementEntity);

        List<PersonneEntity> personneEntities = Collections.emptyList();

        when(equipeRepository.findById("EQ001")).thenReturn(Optional.of(existingEntity));
        when(groupementRepository.findById("G002")).thenReturn(Optional.of(newGroupementEntity));
        when(equipeRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(personneRepository.findByEquipeCode("EQ001")).thenReturn(personneEntities);

        // When
        Equipe result = equipeService.updateEquipe("EQ001", equipe, "G002");

        // Then
        assertNotNull(result);
        assertEquals("EQ001", result.getCode());
        assertEquals("Équipe Dev Updated", result.getNom());
        assertEquals("Description mise à jour", result.getDescription());
        assertNotNull(result.getGroupement());
        assertEquals("G002", result.getGroupement().getCode());
        verify(equipeRepository, times(1)).findById("EQ001");
        verify(groupementRepository, times(1)).findById("G002");
        verify(equipeRepository, times(1)).save(existingEntity);
        verify(personneRepository, times(1)).findByEquipeCode("EQ001");
        verify(equipeMapper, times(1)).convertToEquipe(updatedEntity, personneEntities);
    }

    @Test
    void testUpdateEquipe_PartialUpdate() {
        // Given
        Equipe equipe = new Equipe();
        equipe.setNom("Équipe Dev Updated");
        // No description update

        GroupementEntity groupementEntity = new GroupementEntity();
        groupementEntity.setCode("G001");
        groupementEntity.setLibelle("Groupement IT");

        EquipeEntity existingEntity = new EquipeEntity();
        existingEntity.setCode("EQ001");
        existingEntity.setNom("Équipe Dev");
        existingEntity.setDescription("Équipe de développement");
        existingEntity.setGroupement(groupementEntity);

        EquipeEntity updatedEntity = new EquipeEntity();
        updatedEntity.setCode("EQ001");
        updatedEntity.setNom("Équipe Dev Updated");
        updatedEntity.setDescription("Équipe de développement"); // Unchanged
        updatedEntity.setGroupement(groupementEntity);  // Unchanged

        List<PersonneEntity> personneEntities = Collections.emptyList();

        when(equipeRepository.findById("EQ001")).thenReturn(Optional.of(existingEntity));
        when(equipeRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(personneRepository.findByEquipeCode("EQ001")).thenReturn(personneEntities);

        // When
        Equipe result = equipeService.updateEquipe("EQ001", equipe, null); // No groupement update

        // Then
        assertNotNull(result);
        assertEquals("EQ001", result.getCode());
        assertEquals("Équipe Dev Updated", result.getNom());
        assertEquals("Équipe de développement", result.getDescription());
        assertNotNull(result.getGroupement());
        assertEquals("G001", result.getGroupement().getCode());
        verify(equipeRepository, times(1)).findById("EQ001");
        verify(groupementRepository, never()).findById(anyString());
        verify(equipeRepository, times(1)).save(existingEntity);
        verify(personneRepository, times(1)).findByEquipeCode("EQ001");
        verify(equipeMapper, times(1)).convertToEquipe(updatedEntity, personneEntities);
    }

    @Test
    void testUpdateEquipe_NotFound() {
        // Given
        Equipe equipe = new Equipe();
        equipe.setNom("Équipe Dev Updated");
        equipe.setDescription("Description mise à jour");

        when(equipeRepository.findById("EQ999")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> equipeService.updateEquipe("EQ999", equipe, "G001"));
        verify(equipeRepository, times(1)).findById("EQ999");
        verify(groupementRepository, never()).findById(anyString());
        verify(equipeRepository, never()).save(any(EquipeEntity.class));
        verify(equipeMapper, never()).convertToEquipe(any(EquipeEntity.class), anyList());
    }

    @Test
    void testUpdateEquipe_GroupementNotFound() {
        // Given
        Equipe equipe = new Equipe();
        equipe.setNom("Équipe Dev Updated");
        equipe.setDescription("Description mise à jour");

        GroupementEntity groupementEntity = new GroupementEntity();
        groupementEntity.setCode("G001");
        groupementEntity.setLibelle("Groupement IT");

        EquipeEntity existingEntity = new EquipeEntity();
        existingEntity.setCode("EQ001");
        existingEntity.setNom("Équipe Dev");
        existingEntity.setDescription("Équipe de développement");
        existingEntity.setGroupement(groupementEntity);

        when(equipeRepository.findById("EQ001")).thenReturn(Optional.of(existingEntity));
        when(groupementRepository.findById("G999")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> equipeService.updateEquipe("EQ001", equipe, "G999"));
        verify(equipeRepository, times(1)).findById("EQ001");
        verify(groupementRepository, times(1)).findById("G999");
        verify(equipeRepository, never()).save(any(EquipeEntity.class));
        verify(equipeMapper, never()).convertToEquipe(any(EquipeEntity.class), anyList());
    }

    @Test
    void testDeleteEquipe() {
        // Given
        when(equipeRepository.existsById("EQ001")).thenReturn(true);

        // When
        equipeService.deleteEquipe("EQ001");

        // Then
        verify(equipeRepository, times(1)).existsById("EQ001");
        verify(equipeRepository, times(1)).deleteById("EQ001");
    }

    @Test
    void testDeleteEquipe_NotFound() {
        // Given
        when(equipeRepository.existsById("EQ999")).thenReturn(false);

        // When / Then
        assertThrows(RuntimeException.class, () -> equipeService.deleteEquipe("EQ999"));
        verify(equipeRepository, times(1)).existsById("EQ999");
        verify(equipeRepository, never()).deleteById(anyString());
    }

    @Test
    void testGetEquipeWithMembers() {
        // Given
        GroupementEntity groupementEntity = new GroupementEntity();
        groupementEntity.setCode("G001");
        groupementEntity.setLibelle("Groupement IT");
        groupementEntity.setDirection("Direction Informatique");

        EquipeEntity equipeEntity = new EquipeEntity();
        equipeEntity.setCode("EQ001");
        equipeEntity.setNom("Équipe Dev");
        equipeEntity.setDescription("Équipe de développement");
        equipeEntity.setGroupement(groupementEntity);

        PersonneEntity personneEntity1 = new PersonneEntity();
        personneEntity1.setIdentifiant("P001");
        personneEntity1.setNom("Dupont");
        personneEntity1.setPrenom("Jean");
        personneEntity1.setPoste("Développeur");

        PersonneEntity personneEntity2 = new PersonneEntity();
        personneEntity2.setIdentifiant("P002");
        personneEntity2.setNom("Martin");
        personneEntity2.setPrenom("Sophie");
        personneEntity2.setPoste("Testeuse");

        List<PersonneEntity> personneEntities = Arrays.asList(personneEntity1, personneEntity2);

        when(personneRepository.findByEquipeCode("EQ001")).thenReturn(personneEntities);
        when(equipeRepository.findById("EQ001")).thenReturn(Optional.of(equipeEntity));

        // When
        Equipe result = equipeService.getEquipeByCode("EQ001");

        // Then
        assertNotNull(result);
        assertEquals("EQ001", result.getCode());
        assertEquals("Équipe Dev", result.getNom());
        assertEquals("Équipe de développement", result.getDescription());
        assertNotNull(result.getGroupement());
        assertEquals("G001", result.getGroupement().getCode());
        assertEquals(2, result.getMembres().size());
        assertEquals("P001", result.getMembres().get(0).getIdentifiant());
        assertEquals("Dupont", result.getMembres().get(0).getNom());
        assertEquals("P002", result.getMembres().get(1).getIdentifiant());
        assertEquals("Martin", result.getMembres().get(1).getNom());
        verify(equipeMapper, times(1)).convertToEquipe(equipeEntity, personneEntities);
    }

    @Test
    void testGetEquipeWithNoGroupement() {
        // Given
        EquipeEntity equipeEntity = new EquipeEntity();
        equipeEntity.setCode("EQ001");
        equipeEntity.setNom("Équipe Dev");
        equipeEntity.setDescription("Équipe de développement");
        // No groupement

        List<PersonneEntity> personneEntities = Collections.emptyList();

        when(personneRepository.findByEquipeCode("EQ001")).thenReturn(personneEntities);
        when(equipeRepository.findById("EQ001")).thenReturn(Optional.of(equipeEntity));

        // When
        Equipe result = equipeService.getEquipeByCode("EQ001");

        // Then
        assertNotNull(result);
        assertEquals("EQ001", result.getCode());
        assertEquals("Équipe Dev", result.getNom());
        assertEquals("Équipe de développement", result.getDescription());
        assertNull(result.getGroupement());
        assertTrue(result.getMembres().isEmpty());
        verify(equipeMapper, times(1)).convertToEquipe(equipeEntity, personneEntities);
    }
}