package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.entity.CompetenceEntity;
import fr.pmu.matrix.competence.mapper.CompetenceMapper;
import fr.pmu.matrix.competence.repository.CompetenceRepository;
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
class CompetenceServiceTest {

    @Mock
    private CompetenceRepository competenceRepository;
    
    @Spy
    private CompetenceMapper competenceMapper = new CompetenceMapper();

    @InjectMocks
    private CompetenceService competenceService;

    @Test
    void testGetAllCompetences() {
        // Given
        CompetenceEntity entity1 = createCompetenceEntity("JAVA", "Java Programming");
        CompetenceEntity entity2 = createCompetenceEntity("SPRING", "Spring Framework");

        when(competenceRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        // When
        List<Competence> result = competenceService.getAllCompetences();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("JAVA", result.get(0).getLibelle());
        assertEquals("SPRING", result.get(1).getLibelle());
        verify(competenceRepository, times(1)).findAll();
        verify(competenceMapper, times(2)).mapToCompetenceDomain(any(CompetenceEntity.class));
    }

    @Test
    void testGetCompetenceByLibelle() {
        // Given
        String libelle = "JAVA";
        CompetenceEntity entity = createCompetenceEntity(libelle, "Java Programming");

        when(competenceRepository.findById(libelle)).thenReturn(Optional.of(entity));

        // When
        Competence result = competenceService.getCompetenceByLibelle(libelle);

        // Then
        assertNotNull(result);
        assertEquals("JAVA", result.getLibelle());
        assertEquals("Java Programming", result.getDescription());
        verify(competenceRepository, times(1)).findById(libelle);
        verify(competenceMapper, times(1)).mapToCompetenceDomain(entity);
    }

    @Test
    void testGetCompetenceByLibelle_NotFound() {
        // Given
        String libelle = "UNKNOWN";

        when(competenceRepository.findById(libelle)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            competenceService.getCompetenceByLibelle(libelle)
        );
        
        assertEquals("Compétence non trouvée avec le libellé: " + libelle, exception.getMessage());
        verify(competenceRepository, times(1)).findById(libelle);
        verify(competenceMapper, never()).mapToCompetenceDomain(any(CompetenceEntity.class));
    }

    @Test
    void testCreateCompetence() {
        // Given
        Competence competence = new Competence();
        competence.setLibelle("JAVA");
        competence.setDescription("Java Programming");

        CompetenceEntity entityToSave = createCompetenceEntity("JAVA", "Java Programming");
        CompetenceEntity savedEntity = createCompetenceEntity("JAVA", "Java Programming");

        when(competenceRepository.existsById("JAVA")).thenReturn(false);
        when(competenceMapper.mapToCompetenceEntity(competence)).thenReturn(entityToSave);
        when(competenceRepository.save(entityToSave)).thenReturn(savedEntity);

        // When
        Competence result = competenceService.createCompetence(competence);

        // Then
        assertNotNull(result);
        assertEquals("JAVA", result.getLibelle());
        assertEquals("Java Programming", result.getDescription());
        verify(competenceRepository, times(1)).existsById("JAVA");
        verify(competenceMapper, times(1)).mapToCompetenceEntity(competence);
        verify(competenceRepository, times(1)).save(entityToSave);
        verify(competenceMapper, times(1)).mapToCompetenceDomain(savedEntity);
    }

    @Test
    void testCreateCompetence_AlreadyExists() {
        // Given
        Competence competence = new Competence();
        competence.setLibelle("JAVA");
        competence.setDescription("Java Programming");

        when(competenceRepository.existsById("JAVA")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            competenceService.createCompetence(competence)
        );
        
        assertEquals("Une compétence avec ce libellé existe déjà: JAVA", exception.getMessage());
        verify(competenceRepository, times(1)).existsById("JAVA");
        verify(competenceRepository, never()).save(any(CompetenceEntity.class));
        verify(competenceMapper, never()).mapToCompetenceDomain(any(CompetenceEntity.class));
        verify(competenceMapper, never()).mapToCompetenceEntity(any(Competence.class));
    }

    @Test
    void testUpdateCompetence() {
        // Given
        String libelle = "JAVA";
        Competence competence = new Competence();
        competence.setDescription("Updated Java Programming");

        CompetenceEntity existingEntity = createCompetenceEntity(libelle, "Java Programming");
        CompetenceEntity updatedEntity = createCompetenceEntity(libelle, "Updated Java Programming");

        when(competenceRepository.findById(libelle)).thenReturn(Optional.of(existingEntity));
        when(competenceRepository.save(existingEntity)).thenReturn(updatedEntity);

        // When
        Competence result = competenceService.updateCompetence(libelle, competence);

        // Then
        assertNotNull(result);
        assertEquals(libelle, result.getLibelle());
        assertEquals("Updated Java Programming", result.getDescription());
        verify(competenceRepository, times(1)).findById(libelle);
        verify(competenceRepository, times(1)).save(existingEntity);
        verify(competenceMapper, times(1)).mapToCompetenceDomain(updatedEntity);
    }

    @Test
    void testUpdateCompetence_NotFound() {
        // Given
        String libelle = "UNKNOWN";
        Competence competence = new Competence();
        competence.setDescription("New Description");

        when(competenceRepository.findById(libelle)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            competenceService.updateCompetence(libelle, competence)
        );
        
        assertEquals("Compétence non trouvée avec le libellé: " + libelle, exception.getMessage());
        verify(competenceRepository, times(1)).findById(libelle);
        verify(competenceRepository, never()).save(any(CompetenceEntity.class));
        verify(competenceMapper, never()).mapToCompetenceDomain(any(CompetenceEntity.class));
    }

    @Test
    void testDeleteCompetence() {
        // Given
        String libelle = "JAVA";

        when(competenceRepository.existsById(libelle)).thenReturn(true);

        // When
        competenceService.deleteCompetence(libelle);

        // Then
        verify(competenceRepository, times(1)).existsById(libelle);
        verify(competenceRepository, times(1)).deleteById(libelle);
    }

    @Test
    void testDeleteCompetence_NotFound() {
        // Given
        String libelle = "UNKNOWN";

        when(competenceRepository.existsById(libelle)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            competenceService.deleteCompetence(libelle)
        );
        
        assertEquals("Compétence non trouvée avec le libellé: " + libelle, exception.getMessage());
        verify(competenceRepository, times(1)).existsById(libelle);
        verify(competenceRepository, never()).deleteById(anyString());
    }

    // Méthode utilitaire pour créer des entités de test
    private CompetenceEntity createCompetenceEntity(String libelle, String description) {
        CompetenceEntity entity = new CompetenceEntity();
        entity.setLibelle(libelle);
        entity.setDescription(description);
        return entity;
    }
}