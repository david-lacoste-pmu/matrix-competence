package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Groupement;
import fr.pmu.matrix.competence.entity.GroupementEntity;
import fr.pmu.matrix.competence.mapper.GroupementMapper;
import fr.pmu.matrix.competence.repository.GroupementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupementServiceTest {

    @Mock
    private GroupementRepository groupementRepository;
    
    @Spy
    private GroupementMapper groupementMapper = new GroupementMapper();

    @InjectMocks
    private GroupementService groupementService;

    @Test
    void testGetAllGroupements() {
        // Given
        GroupementEntity entity1 = new GroupementEntity();
        entity1.setCode("G001");
        entity1.setLibelle("Groupement IT");
        entity1.setDirection("Direction Informatique");

        GroupementEntity entity2 = new GroupementEntity();
        entity2.setCode("G002");
        entity2.setLibelle("Groupement Finance");
        entity2.setDirection("Direction Financière");

        when(groupementRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        // When
        List<Groupement> result = groupementService.getAllGroupements();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("G001", result.get(0).getCode());
        assertEquals("G002", result.get(1).getCode());
        verify(groupementRepository, times(1)).findAll();
        verify(groupementMapper, times(2)).mapToGroupementDomainSansEquipes(any(GroupementEntity.class));
    }

    @Test
    void testGetGroupementByCode() {
        // Given
        GroupementEntity entity = new GroupementEntity();
        entity.setCode("G001");
        entity.setLibelle("Groupement IT");
        entity.setDirection("Direction Informatique");

        when(groupementRepository.findById("G001")).thenReturn(Optional.of(entity));

        // When
        Groupement result = groupementService.getGroupementByCode("G001");

        // Then
        assertNotNull(result);
        assertEquals("G001", result.getCode());
        assertEquals("Groupement IT", result.getLibelle());
        assertEquals("Direction Informatique", result.getDirection());
        verify(groupementRepository, times(1)).findById("G001");
        verify(groupementMapper, times(1)).mapToGroupementDomain(entity);
    }

    @Test
    void testGetGroupementByCode_NotFound() {
        // Given
        when(groupementRepository.findById("G999")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> groupementService.getGroupementByCode("G999"));
        verify(groupementRepository, times(1)).findById("G999");
        verify(groupementMapper, never()).mapToGroupementDomain(any(GroupementEntity.class));
    }

    @Test
    void testCreateGroupement() {
        // Given
        Groupement groupement = new Groupement();
        groupement.setCode("G001");
        groupement.setLibelle("Groupement IT");
        groupement.setDirection("Direction Informatique");

        GroupementEntity entityToSave = new GroupementEntity();
        entityToSave.setCode("G001");
        entityToSave.setLibelle("Groupement IT");
        entityToSave.setDirection("Direction Informatique");

        GroupementEntity savedEntity = new GroupementEntity();
        savedEntity.setCode("G001");
        savedEntity.setLibelle("Groupement IT");
        savedEntity.setDirection("Direction Informatique");

        when(groupementRepository.existsById("G001")).thenReturn(false);
        when(groupementMapper.mapToGroupementEntity(groupement)).thenReturn(entityToSave);
        when(groupementRepository.save(entityToSave)).thenReturn(savedEntity);

        // When
        Groupement result = groupementService.createGroupement(groupement);

        // Then
        assertNotNull(result);
        assertEquals("G001", result.getCode());
        assertEquals("Groupement IT", result.getLibelle());
        assertEquals("Direction Informatique", result.getDirection());
        verify(groupementRepository, times(1)).existsById("G001");
        verify(groupementMapper, times(1)).mapToGroupementEntity(groupement);
        verify(groupementRepository, times(1)).save(entityToSave);
        verify(groupementMapper, times(1)).mapToGroupementDomainSansEquipes(savedEntity);
    }

    @Test
    void testCreateGroupement_AlreadyExists() {
        // Given
        Groupement groupement = new Groupement();
        groupement.setCode("G001");
        groupement.setLibelle("Groupement IT");
        groupement.setDirection("Direction Informatique");

        when(groupementRepository.existsById("G001")).thenReturn(true);

        // When / Then
        assertThrows(RuntimeException.class, () -> groupementService.createGroupement(groupement));
        verify(groupementRepository, times(1)).existsById("G001");
        verify(groupementRepository, never()).save(any(GroupementEntity.class));
        verify(groupementMapper, never()).mapToGroupementEntity(any(Groupement.class));
        verify(groupementMapper, never()).mapToGroupementDomainSansEquipes(any(GroupementEntity.class));
    }

    @Test
    void testUpdateGroupement() {
        // Given
        GroupementEntity existingEntity = new GroupementEntity();
        existingEntity.setCode("G001");
        existingEntity.setLibelle("Groupement IT");
        existingEntity.setDirection("Direction Informatique");

        GroupementEntity updatedEntity = new GroupementEntity();
        updatedEntity.setCode("G001");
        updatedEntity.setLibelle("Groupement SI");  // Changed
        updatedEntity.setDirection("Direction SI");  // Changed

        Groupement updateData = new Groupement();
        updateData.setLibelle("Groupement SI");
        updateData.setDirection("Direction SI");

        when(groupementRepository.findById("G001")).thenReturn(Optional.of(existingEntity));
        when(groupementRepository.save(existingEntity)).thenReturn(updatedEntity);

        // When
        Groupement result = groupementService.updateGroupement("G001", updateData);

        // Then
        assertNotNull(result);
        assertEquals("G001", result.getCode());
        assertEquals("Groupement SI", result.getLibelle());
        assertEquals("Direction SI", result.getDirection());
        verify(groupementRepository, times(1)).findById("G001");
        verify(groupementRepository, times(1)).save(existingEntity);
        verify(groupementMapper, times(1)).mapToGroupementDomain(updatedEntity);
    }

    @Test
    void testUpdateGroupement_PartialUpdate() {
        // Given
        GroupementEntity existingEntity = new GroupementEntity();
        existingEntity.setCode("G001");
        existingEntity.setLibelle("Groupement IT");
        existingEntity.setDirection("Direction Informatique");

        GroupementEntity updatedEntity = new GroupementEntity();
        updatedEntity.setCode("G001");
        updatedEntity.setLibelle("Groupement SI");  // Changed
        updatedEntity.setDirection("Direction Informatique");  // Unchanged

        Groupement updateData = new Groupement();
        updateData.setLibelle("Groupement SI");
        // Direction not provided

        when(groupementRepository.findById("G001")).thenReturn(Optional.of(existingEntity));
        when(groupementRepository.save(existingEntity)).thenReturn(updatedEntity);

        // When
        Groupement result = groupementService.updateGroupement("G001", updateData);

        // Then
        assertNotNull(result);
        assertEquals("G001", result.getCode());
        assertEquals("Groupement SI", result.getLibelle());
        assertEquals("Direction Informatique", result.getDirection());
        verify(groupementRepository, times(1)).findById("G001");
        verify(groupementRepository, times(1)).save(existingEntity);
        verify(groupementMapper, times(1)).mapToGroupementDomain(updatedEntity);
    }

    @Test
    void testUpdateGroupement_NotFound() {
        // Given
        Groupement updateData = new Groupement();
        updateData.setLibelle("Groupement SI");
        updateData.setDirection("Direction SI");

        when(groupementRepository.findById("G999")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> groupementService.updateGroupement("G999", updateData));
        verify(groupementRepository, times(1)).findById("G999");
        verify(groupementRepository, never()).save(any(GroupementEntity.class));
        verify(groupementMapper, never()).mapToGroupementDomain(any(GroupementEntity.class));
        verify(groupementMapper, never()).mapToGroupementDomainSansEquipes(any(GroupementEntity.class));
    }

    @Test
    void testDeleteGroupement() {
        // Given
        when(groupementRepository.existsById("G001")).thenReturn(true);

        // When
        groupementService.deleteGroupement("G001");

        // Then
        verify(groupementRepository, times(1)).existsById("G001");
        verify(groupementRepository, times(1)).deleteById("G001");
    }

    @Test
    void testDeleteGroupement_NotFound() {
        // Given
        when(groupementRepository.existsById("G999")).thenReturn(false);

        // When / Then
        assertThrows(RuntimeException.class, () -> groupementService.deleteGroupement("G999"));
        verify(groupementRepository, times(1)).existsById("G999");
        verify(groupementRepository, never()).deleteById(anyString());
    }
}