package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.MatriceCompetence;
import fr.pmu.matrix.competence.entity.CompetenceEntity;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.MatriceCompetenceEntity;
import fr.pmu.matrix.competence.entity.NoteEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.repository.CompetenceRepository;
import fr.pmu.matrix.competence.repository.MatriceCompetenceRepository;
import fr.pmu.matrix.competence.repository.NoteRepository;
import fr.pmu.matrix.competence.repository.PersonneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriceCompetenceServiceTest {

    @Mock
    private MatriceCompetenceRepository matriceCompetenceRepository;

    @Mock
    private PersonneRepository personneRepository;

    @Mock
    private CompetenceRepository competenceRepository;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private MatriceCompetenceService matriceCompetenceService;

    @Test
    void testGetAllMatricesCompetences() {
        // Given
        PersonneEntity personneEntity = createPersonneEntity("P123", "Dupont", "Jean", "Développeur");
        CompetenceEntity competenceEntity = createCompetenceEntity("JAVA", "Java Programming");
        NoteEntity noteEntity = createNoteEntity(3, "Intermédiaire");
        
        MatriceCompetenceEntity matriceEntity1 = new MatriceCompetenceEntity();
        matriceEntity1.setPersonne(personneEntity);
        matriceEntity1.setCompetence(competenceEntity);
        matriceEntity1.setNote(noteEntity);
        
        CompetenceEntity competenceEntity2 = createCompetenceEntity("SPRING", "Spring Framework");
        
        MatriceCompetenceEntity matriceEntity2 = new MatriceCompetenceEntity();
        matriceEntity2.setPersonne(personneEntity);
        matriceEntity2.setCompetence(competenceEntity2);
        matriceEntity2.setNote(noteEntity);

        when(matriceCompetenceRepository.findAll()).thenReturn(Arrays.asList(matriceEntity1, matriceEntity2));

        // When
        List<MatriceCompetence> result = matriceCompetenceService.getAllMatricesCompetences();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("P123", result.get(0).getPersonne().getIdentifiant());
        assertEquals("JAVA", result.get(0).getCompetence().getLibelle());
        assertEquals("SPRING", result.get(1).getCompetence().getLibelle());
        verify(matriceCompetenceRepository, times(1)).findAll();
    }

    @Test
    void testGetMatriceCompetence() {
        // Given
        String personneId = "P123";
        String competenceId = "JAVA";
        
        PersonneEntity personneEntity = createPersonneEntity(personneId, "Dupont", "Jean", "Développeur");
        CompetenceEntity competenceEntity = createCompetenceEntity(competenceId, "Java Programming");
        NoteEntity noteEntity = createNoteEntity(3, "Intermédiaire");
        
        MatriceCompetenceEntity matriceEntity = new MatriceCompetenceEntity();
        matriceEntity.setPersonne(personneEntity);
        matriceEntity.setCompetence(competenceEntity);
        matriceEntity.setNote(noteEntity);

        when(personneRepository.findById(personneId)).thenReturn(Optional.of(personneEntity));
        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competenceEntity));
        when(matriceCompetenceRepository.findByPersonneAndCompetence(personneEntity, competenceEntity))
                .thenReturn(Optional.of(matriceEntity));

        // When
        MatriceCompetence result = matriceCompetenceService.getMatriceCompetence(personneId, competenceId);

        // Then
        assertNotNull(result);
        assertEquals(personneId, result.getPersonne().getIdentifiant());
        assertEquals(competenceId, result.getCompetence().getLibelle());
        assertEquals(3, result.getNote().getValeur());
        verify(personneRepository, times(1)).findById(personneId);
        verify(competenceRepository, times(1)).findById(competenceId);
        verify(matriceCompetenceRepository, times(1)).findByPersonneAndCompetence(personneEntity, competenceEntity);
    }

    @Test
    void testGetMatriceCompetence_PersonneNotFound() {
        // Given
        String personneId = "P999";
        String competenceId = "JAVA";

        when(personneRepository.findById(personneId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            matriceCompetenceService.getMatriceCompetence(personneId, competenceId)
        );
        
        assertEquals("Personne non trouvée avec l'identifiant: P999", exception.getMessage());
        verify(personneRepository, times(1)).findById(personneId);
        verify(competenceRepository, never()).findById(anyString());
        verify(matriceCompetenceRepository, never()).findByPersonneAndCompetence(any(), any());
    }

    @Test
    void testGetMatriceCompetence_CompetenceNotFound() {
        // Given
        String personneId = "P123";
        String competenceId = "UNKNOWN";
        
        PersonneEntity personneEntity = createPersonneEntity(personneId, "Dupont", "Jean", "Développeur");

        when(personneRepository.findById(personneId)).thenReturn(Optional.of(personneEntity));
        when(competenceRepository.findById(competenceId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            matriceCompetenceService.getMatriceCompetence(personneId, competenceId)
        );
        
        assertEquals("Compétence non trouvée avec l'identifiant: UNKNOWN", exception.getMessage());
        verify(personneRepository, times(1)).findById(personneId);
        verify(competenceRepository, times(1)).findById(competenceId);
        verify(matriceCompetenceRepository, never()).findByPersonneAndCompetence(any(), any());
    }

    @Test
    void testGetMatriceCompetence_MatriceNotFound() {
        // Given
        String personneId = "P123";
        String competenceId = "JAVA";
        
        PersonneEntity personneEntity = createPersonneEntity(personneId, "Dupont", "Jean", "Développeur");
        CompetenceEntity competenceEntity = createCompetenceEntity(competenceId, "Java Programming");

        when(personneRepository.findById(personneId)).thenReturn(Optional.of(personneEntity));
        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competenceEntity));
        when(matriceCompetenceRepository.findByPersonneAndCompetence(personneEntity, competenceEntity))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            matriceCompetenceService.getMatriceCompetence(personneId, competenceId)
        );
        
        assertEquals("Matrice de compétence non trouvée pour cette personne et cette compétence", exception.getMessage());
        verify(personneRepository, times(1)).findById(personneId);
        verify(competenceRepository, times(1)).findById(competenceId);
        verify(matriceCompetenceRepository, times(1)).findByPersonneAndCompetence(personneEntity, competenceEntity);
    }

    @Test
    void testCreateMatriceCompetence() {
        // Given
        String personneId = "P123";
        String competenceId = "JAVA";
        int noteValeur = 3;
        
        PersonneEntity personneEntity = createPersonneEntity(personneId, "Dupont", "Jean", "Développeur");
        CompetenceEntity competenceEntity = createCompetenceEntity(competenceId, "Java Programming");
        NoteEntity noteEntity = createNoteEntity(noteValeur, "Intermédiaire");
        
        MatriceCompetenceEntity savedEntity = new MatriceCompetenceEntity();
        savedEntity.setPersonne(personneEntity);
        savedEntity.setCompetence(competenceEntity);
        savedEntity.setNote(noteEntity);

        when(personneRepository.findById(personneId)).thenReturn(Optional.of(personneEntity));
        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competenceEntity));
        when(noteRepository.findById(noteValeur)).thenReturn(Optional.of(noteEntity));
        when(matriceCompetenceRepository.findByPersonneAndCompetence(personneEntity, competenceEntity))
                .thenReturn(Optional.empty());
        when(matriceCompetenceRepository.save(any(MatriceCompetenceEntity.class))).thenReturn(savedEntity);

        // When
        MatriceCompetence result = matriceCompetenceService.createMatriceCompetence(personneId, competenceId, noteValeur);

        // Then
        assertNotNull(result);
        assertEquals(personneId, result.getPersonne().getIdentifiant());
        assertEquals(competenceId, result.getCompetence().getLibelle());
        assertEquals(noteValeur, result.getNote().getValeur());
        verify(personneRepository, times(1)).findById(personneId);
        verify(competenceRepository, times(1)).findById(competenceId);
        verify(noteRepository, times(1)).findById(noteValeur);
        verify(matriceCompetenceRepository, times(1)).findByPersonneAndCompetence(personneEntity, competenceEntity);
        verify(matriceCompetenceRepository, times(1)).save(any(MatriceCompetenceEntity.class));
    }

    @Test
    void testCreateMatriceCompetence_AlreadyExists() {
        // Given
        String personneId = "P123";
        String competenceId = "JAVA";
        int noteValeur = 3;
        
        PersonneEntity personneEntity = createPersonneEntity(personneId, "Dupont", "Jean", "Développeur");
        CompetenceEntity competenceEntity = createCompetenceEntity(competenceId, "Java Programming");
        NoteEntity noteEntity = createNoteEntity(noteValeur, "Intermédiaire");
        
        MatriceCompetenceEntity existingEntity = new MatriceCompetenceEntity();
        existingEntity.setPersonne(personneEntity);
        existingEntity.setCompetence(competenceEntity);
        existingEntity.setNote(noteEntity);

        when(personneRepository.findById(personneId)).thenReturn(Optional.of(personneEntity));
        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competenceEntity));
        when(noteRepository.findById(noteValeur)).thenReturn(Optional.of(noteEntity));
        when(matriceCompetenceRepository.findByPersonneAndCompetence(personneEntity, competenceEntity))
                .thenReturn(Optional.of(existingEntity));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            matriceCompetenceService.createMatriceCompetence(personneId, competenceId, noteValeur)
        );
        
        assertEquals("Une matrice de compétence existe déjà pour cette personne et cette compétence", exception.getMessage());
        verify(personneRepository, times(1)).findById(personneId);
        verify(competenceRepository, times(1)).findById(competenceId);
        verify(noteRepository, times(1)).findById(noteValeur);
        verify(matriceCompetenceRepository, times(1)).findByPersonneAndCompetence(personneEntity, competenceEntity);
        verify(matriceCompetenceRepository, never()).save(any(MatriceCompetenceEntity.class));
    }

    @Test
    void testUpdateMatriceCompetence() {
        // Given
        String personneId = "P123";
        String competenceId = "JAVA";
        int newNoteValeur = 4;
        
        PersonneEntity personneEntity = createPersonneEntity(personneId, "Dupont", "Jean", "Développeur");
        CompetenceEntity competenceEntity = createCompetenceEntity(competenceId, "Java Programming");
        NoteEntity oldNoteEntity = createNoteEntity(3, "Intermédiaire");
        NoteEntity newNoteEntity = createNoteEntity(newNoteValeur, "Avancé");
        
        MatriceCompetenceEntity existingEntity = new MatriceCompetenceEntity();
        existingEntity.setPersonne(personneEntity);
        existingEntity.setCompetence(competenceEntity);
        existingEntity.setNote(oldNoteEntity);
        
        MatriceCompetenceEntity updatedEntity = new MatriceCompetenceEntity();
        updatedEntity.setPersonne(personneEntity);
        updatedEntity.setCompetence(competenceEntity);
        updatedEntity.setNote(newNoteEntity);

        when(personneRepository.findById(personneId)).thenReturn(Optional.of(personneEntity));
        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competenceEntity));
        when(noteRepository.findById(newNoteValeur)).thenReturn(Optional.of(newNoteEntity));
        when(matriceCompetenceRepository.findByPersonneAndCompetence(personneEntity, competenceEntity))
                .thenReturn(Optional.of(existingEntity));
        when(matriceCompetenceRepository.save(any(MatriceCompetenceEntity.class))).thenReturn(updatedEntity);

        // When
        MatriceCompetence result = matriceCompetenceService.updateMatriceCompetence(personneId, competenceId, newNoteValeur);

        // Then
        assertNotNull(result);
        assertEquals(personneId, result.getPersonne().getIdentifiant());
        assertEquals(competenceId, result.getCompetence().getLibelle());
        assertEquals(newNoteValeur, result.getNote().getValeur());
        assertEquals("Avancé", result.getNote().getLibelle());
        verify(personneRepository, times(1)).findById(personneId);
        verify(competenceRepository, times(1)).findById(competenceId);
        verify(noteRepository, times(1)).findById(newNoteValeur);
        verify(matriceCompetenceRepository, times(1)).findByPersonneAndCompetence(personneEntity, competenceEntity);
        verify(matriceCompetenceRepository, times(1)).save(any(MatriceCompetenceEntity.class));
    }

    @Test
    void testDeleteMatriceCompetence() {
        // Given
        String personneId = "P123";
        String competenceId = "JAVA";
        
        PersonneEntity personneEntity = createPersonneEntity(personneId, "Dupont", "Jean", "Développeur");
        CompetenceEntity competenceEntity = createCompetenceEntity(competenceId, "Java Programming");
        NoteEntity noteEntity = createNoteEntity(3, "Intermédiaire");
        
        MatriceCompetenceEntity matriceEntity = new MatriceCompetenceEntity();
        matriceEntity.setPersonne(personneEntity);
        matriceEntity.setCompetence(competenceEntity);
        matriceEntity.setNote(noteEntity);

        when(personneRepository.findById(personneId)).thenReturn(Optional.of(personneEntity));
        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competenceEntity));
        when(matriceCompetenceRepository.findByPersonneAndCompetence(personneEntity, competenceEntity))
                .thenReturn(Optional.of(matriceEntity));

        // When
        matriceCompetenceService.deleteMatriceCompetence(personneId, competenceId);

        // Then
        verify(personneRepository, times(1)).findById(personneId);
        verify(competenceRepository, times(1)).findById(competenceId);
        verify(matriceCompetenceRepository, times(1)).findByPersonneAndCompetence(personneEntity, competenceEntity);
        verify(matriceCompetenceRepository, times(1)).delete(matriceEntity);
    }

    @Test
    void testGetCompetencesByPersonne() {
        // Given
        String personneId = "P123";
        
        PersonneEntity personneEntity = createPersonneEntity(personneId, "Dupont", "Jean", "Développeur");
        
        CompetenceEntity competenceEntity1 = createCompetenceEntity("JAVA", "Java Programming");
        CompetenceEntity competenceEntity2 = createCompetenceEntity("SPRING", "Spring Framework");
        
        NoteEntity noteEntity1 = createNoteEntity(3, "Intermédiaire");
        NoteEntity noteEntity2 = createNoteEntity(4, "Avancé");
        
        MatriceCompetenceEntity matriceEntity1 = new MatriceCompetenceEntity();
        matriceEntity1.setPersonne(personneEntity);
        matriceEntity1.setCompetence(competenceEntity1);
        matriceEntity1.setNote(noteEntity1);
        
        MatriceCompetenceEntity matriceEntity2 = new MatriceCompetenceEntity();
        matriceEntity2.setPersonne(personneEntity);
        matriceEntity2.setCompetence(competenceEntity2);
        matriceEntity2.setNote(noteEntity2);
        
        List<MatriceCompetenceEntity> matricesList = Arrays.asList(matriceEntity1, matriceEntity2);

        when(personneRepository.findById(personneId)).thenReturn(Optional.of(personneEntity));
        when(matriceCompetenceRepository.findByPersonne(personneEntity)).thenReturn(matricesList);

        // When
        List<MatriceCompetence> result = matriceCompetenceService.getCompetencesByPersonne(personneId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(personneId, result.get(0).getPersonne().getIdentifiant());
        assertEquals("JAVA", result.get(0).getCompetence().getLibelle());
        assertEquals("SPRING", result.get(1).getCompetence().getLibelle());
        verify(personneRepository, times(1)).findById(personneId);
        verify(matriceCompetenceRepository, times(1)).findByPersonne(personneEntity);
    }

    @Test
    void testGetPersonnesByCompetence() {
        // Given
        String competenceId = "JAVA";
        
        CompetenceEntity competenceEntity = createCompetenceEntity(competenceId, "Java Programming");
        
        PersonneEntity personneEntity1 = createPersonneEntity("P123", "Dupont", "Jean", "Développeur");
        PersonneEntity personneEntity2 = createPersonneEntity("P124", "Martin", "Sophie", "Lead Dev");
        
        NoteEntity noteEntity1 = createNoteEntity(3, "Intermédiaire");
        NoteEntity noteEntity2 = createNoteEntity(5, "Expert");
        
        MatriceCompetenceEntity matriceEntity1 = new MatriceCompetenceEntity();
        matriceEntity1.setPersonne(personneEntity1);
        matriceEntity1.setCompetence(competenceEntity);
        matriceEntity1.setNote(noteEntity1);
        
        MatriceCompetenceEntity matriceEntity2 = new MatriceCompetenceEntity();
        matriceEntity2.setPersonne(personneEntity2);
        matriceEntity2.setCompetence(competenceEntity);
        matriceEntity2.setNote(noteEntity2);
        
        List<MatriceCompetenceEntity> matricesList = Arrays.asList(matriceEntity1, matriceEntity2);

        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competenceEntity));
        when(matriceCompetenceRepository.findByCompetence(competenceEntity)).thenReturn(matricesList);

        // When
        List<MatriceCompetence> result = matriceCompetenceService.getPersonnesByCompetence(competenceId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(competenceId, result.get(0).getCompetence().getLibelle());
        assertEquals("P123", result.get(0).getPersonne().getIdentifiant());
        assertEquals("P124", result.get(1).getPersonne().getIdentifiant());
        verify(competenceRepository, times(1)).findById(competenceId);
        verify(matriceCompetenceRepository, times(1)).findByCompetence(competenceEntity);
    }

    // Méthodes utilitaires pour créer des entités
    private PersonneEntity createPersonneEntity(String id, String nom, String prenom, String poste) {
        PersonneEntity entity = new PersonneEntity();
        entity.setIdentifiant(id);
        entity.setNom(nom);
        entity.setPrenom(prenom);
        entity.setPoste(poste);
        return entity;
    }

    private CompetenceEntity createCompetenceEntity(String libelle, String description) {
        CompetenceEntity entity = new CompetenceEntity();
        entity.setLibelle(libelle);
        entity.setDescription(description);
        return entity;
    }

    private NoteEntity createNoteEntity(int valeur, String libelle) {
        NoteEntity entity = new NoteEntity();
        entity.setValeur(valeur);
        entity.setLibelle(libelle);
        return entity;
    }

    private EquipeEntity createEquipeEntity(String code, String nom, String description) {
        EquipeEntity entity = new EquipeEntity();
        entity.setCode(code);
        entity.setNom(nom);
        entity.setDescription(description);
        return entity;
    }
}