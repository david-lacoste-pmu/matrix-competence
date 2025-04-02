package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.mapper.PersonneMapper;
import fr.pmu.matrix.competence.repository.EquipeRepository;
import fr.pmu.matrix.competence.repository.PersonneRepository;
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
class PersonneServiceTest {

    @Mock
    private PersonneRepository personneRepository;

    @Mock
    private EquipeRepository equipeRepository;
    
    @Spy
    private PersonneMapper personneMapper = new PersonneMapper();

    @InjectMocks
    private PersonneService personneService;

    @Test
    void testGetAllPersonnes() {
        // Given
        PersonneEntity personneEntity1 = new PersonneEntity();
        personneEntity1.setIdentifiant("P123");
        personneEntity1.setNom("Dupont");
        personneEntity1.setPrenom("Jean");
        personneEntity1.setPoste("Développeur");

        PersonneEntity personneEntity2 = new PersonneEntity();
        personneEntity2.setIdentifiant("P124");
        personneEntity2.setNom("Martin");
        personneEntity2.setPrenom("Sophie");
        personneEntity2.setPoste("Designer");

        when(personneRepository.findAll()).thenReturn(Arrays.asList(personneEntity1, personneEntity2));

        // When
        List<Personne> result = personneService.getAllPersonnes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("P123", result.get(0).getIdentifiant());
        assertEquals("P124", result.get(1).getIdentifiant());
        verify(personneRepository, times(1)).findAll();
        verify(personneMapper, times(2)).mapToPersonneDomain(any(PersonneEntity.class));
    }

    @Test
    void testGetPersonneByIdentifiant() {
        // Given
        EquipeEntity equipeEntity = new EquipeEntity();
        equipeEntity.setCode("E001");
        equipeEntity.setNom("Équipe Backend");
        equipeEntity.setDescription("Équipe responsable du développement backend");

        PersonneEntity personneEntity = new PersonneEntity();
        personneEntity.setIdentifiant("P123");
        personneEntity.setNom("Dupont");
        personneEntity.setPrenom("Jean");
        personneEntity.setPoste("Développeur");
        personneEntity.setEquipe(equipeEntity);

        when(personneRepository.findById("P123")).thenReturn(Optional.of(personneEntity));

        // When
        Personne result = personneService.getPersonneByIdentifiant("P123");

        // Then
        assertNotNull(result);
        assertEquals("P123", result.getIdentifiant());
        assertEquals("Dupont", result.getNom());
        assertEquals("Jean", result.getPrenom());
        assertEquals("Développeur", result.getPoste());
        assertNotNull(result.getEquipe());
        assertEquals("E001", result.getEquipe().getCode());
        verify(personneRepository, times(1)).findById("P123");
        verify(personneMapper, times(1)).mapToPersonneDomain(personneEntity);
    }

    @Test
    void testGetPersonneByIdentifiant_NotFound() {
        // Given
        when(personneRepository.findById("P999")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> personneService.getPersonneByIdentifiant("P999"));
        verify(personneRepository, times(1)).findById("P999");
        verify(personneMapper, never()).mapToPersonneDomain(any(PersonneEntity.class));
    }

    @Test
    void testCreatePersonne_WithoutEquipe() {
        // Given
        Personne personne = new Personne();
        personne.setIdentifiant("P123");
        personne.setNom("Dupont");
        personne.setPrenom("Jean");
        personne.setPoste("Développeur");

        PersonneEntity personneEntity = new PersonneEntity();
        personneEntity.setIdentifiant("P123");
        personneEntity.setNom("Dupont");
        personneEntity.setPrenom("Jean");
        personneEntity.setPoste("Développeur");

        PersonneEntity savedEntity = new PersonneEntity();
        savedEntity.setIdentifiant("P123");
        savedEntity.setNom("Dupont");
        savedEntity.setPrenom("Jean");
        savedEntity.setPoste("Développeur");

        when(personneRepository.existsById(anyString())).thenReturn(false);
        when(personneMapper.mapToPersonneEntity(personne)).thenReturn(personneEntity);
        when(personneRepository.save(personneEntity)).thenReturn(savedEntity);

        // When
        Personne result = personneService.createPersonne(personne, null);

        // Then
        assertNotNull(result);
        assertEquals("P123", result.getIdentifiant());
        assertEquals("Dupont", result.getNom());
        verify(personneMapper, times(1)).mapToPersonneEntity(personne);
        verify(personneRepository, times(1)).save(personneEntity);
        verify(personneMapper, times(1)).mapToPersonneDomain(savedEntity);
        verify(equipeRepository, never()).findById(anyString());
    }

    @Test
    void testCreatePersonne_WithEquipe() {
        // Given
        Personne personne = new Personne();
        personne.setIdentifiant("P123");
        personne.setNom("Dupont");
        personne.setPrenom("Jean");
        personne.setPoste("Développeur");

        PersonneEntity personneEntity = new PersonneEntity();
        personneEntity.setIdentifiant("P123");
        personneEntity.setNom("Dupont");
        personneEntity.setPrenom("Jean");
        personneEntity.setPoste("Développeur");

        EquipeEntity equipeEntity = new EquipeEntity();
        equipeEntity.setCode("E001");
        equipeEntity.setNom("Équipe Backend");
        equipeEntity.setDescription("Équipe responsable du développement backend");

        PersonneEntity savedEntity = new PersonneEntity();
        savedEntity.setIdentifiant("P123");
        savedEntity.setNom("Dupont");
        savedEntity.setPrenom("Jean");
        savedEntity.setPoste("Développeur");
        savedEntity.setEquipe(equipeEntity);

        when(personneRepository.existsById(anyString())).thenReturn(false);
        when(personneMapper.mapToPersonneEntity(personne)).thenReturn(personneEntity);
        when(equipeRepository.findById("E001")).thenReturn(Optional.of(equipeEntity));
        when(personneRepository.save(personneEntity)).thenReturn(savedEntity);

        // When
        Personne result = personneService.createPersonne(personne, "E001");

        // Then
        assertNotNull(result);
        assertEquals("P123", result.getIdentifiant());
        assertNotNull(result.getEquipe());
        assertEquals("E001", result.getEquipe().getCode());
        verify(personneMapper, times(1)).mapToPersonneEntity(personne);
        verify(personneRepository, times(1)).save(personneEntity);
        verify(personneMapper, times(1)).mapToPersonneDomain(savedEntity);
        verify(equipeRepository, times(1)).findById("E001");
    }

    @Test
    void testCreatePersonne_EquipeNotFound() {
        // Given
        Personne personne = new Personne();
        personne.setIdentifiant("P123");
        personne.setNom("Dupont");
        personne.setPrenom("Jean");
        personne.setPoste("Développeur");

        PersonneEntity personneEntity = new PersonneEntity();
        personneEntity.setIdentifiant("P123");
        personneEntity.setNom("Dupont");
        personneEntity.setPrenom("Jean");
        personneEntity.setPoste("Développeur");

        when(personneRepository.existsById(anyString())).thenReturn(false);
        when(personneMapper.mapToPersonneEntity(personne)).thenReturn(personneEntity);
        when(equipeRepository.findById("E999")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> personneService.createPersonne(personne, "E999"));
        verify(personneMapper, times(1)).mapToPersonneEntity(personne);
        verify(personneRepository, never()).save(any(PersonneEntity.class));
        verify(personneMapper, never()).mapToPersonneDomain(any(PersonneEntity.class));
        verify(equipeRepository, times(1)).findById("E999");
    }

    @Test
    void testCreatePersonne_AlreadyExists() {
        // Given
        Personne personne = new Personne();
        personne.setIdentifiant("P123");
        personne.setNom("Dupont");
        personne.setPrenom("Jean");
        personne.setPoste("Développeur");

        when(personneRepository.existsById("P123")).thenReturn(true);

        // When / Then
        assertThrows(RuntimeException.class, () -> personneService.createPersonne(personne, null));
        verify(personneMapper, never()).mapToPersonneEntity(any(Personne.class));
        verify(personneRepository, never()).save(any(PersonneEntity.class));
        verify(personneMapper, never()).mapToPersonneDomain(any(PersonneEntity.class));
    }

    @Test
    void testUpdatePersonne() {
        // Given
        PersonneEntity existingEntity = new PersonneEntity();
        existingEntity.setIdentifiant("P123");
        existingEntity.setNom("Dupont");
        existingEntity.setPrenom("Jean");
        existingEntity.setPoste("Développeur");

        PersonneEntity updatedEntity = new PersonneEntity();
        updatedEntity.setIdentifiant("P123");
        updatedEntity.setNom("Dupont");
        updatedEntity.setPrenom("Pierre");  // Changed
        updatedEntity.setPoste("Chef de Projet");  // Changed

        Personne updateData = new Personne();
        updateData.setPrenom("Pierre");
        updateData.setPoste("Chef de Projet");

        when(personneRepository.findById("P123")).thenReturn(Optional.of(existingEntity));
        when(personneRepository.save(existingEntity)).thenReturn(updatedEntity);

        // When
        Personne result = personneService.updatePersonne("P123", updateData, null);

        // Then
        assertNotNull(result);
        assertEquals("P123", result.getIdentifiant());
        assertEquals("Dupont", result.getNom());
        assertEquals("Pierre", result.getPrenom());
        assertEquals("Chef de Projet", result.getPoste());
        verify(personneRepository, times(1)).findById("P123");
        verify(personneRepository, times(1)).save(existingEntity);
        verify(personneMapper, times(1)).mapToPersonneDomain(updatedEntity);
    }

    @Test
    void testUpdatePersonne_WithEquipe() {
        // Given
        PersonneEntity existingEntity = new PersonneEntity();
        existingEntity.setIdentifiant("P123");
        existingEntity.setNom("Dupont");
        existingEntity.setPrenom("Jean");
        existingEntity.setPoste("Développeur");

        EquipeEntity newEquipeEntity = new EquipeEntity();
        newEquipeEntity.setCode("E002");
        newEquipeEntity.setNom("Équipe Frontend");

        PersonneEntity updatedEntity = new PersonneEntity();
        updatedEntity.setIdentifiant("P123");
        updatedEntity.setNom("Dupont");
        updatedEntity.setPrenom("Jean");
        updatedEntity.setEquipe(newEquipeEntity);

        Personne updateData = new Personne();

        when(personneRepository.findById("P123")).thenReturn(Optional.of(existingEntity));
        when(equipeRepository.findById("E002")).thenReturn(Optional.of(newEquipeEntity));
        when(personneRepository.save(existingEntity)).thenReturn(updatedEntity);

        // When
        Personne result = personneService.updatePersonne("P123", updateData, "E002");

        // Then
        assertNotNull(result);
        assertEquals("P123", result.getIdentifiant());
        assertNotNull(result.getEquipe());
        assertEquals("E002", result.getEquipe().getCode());
        verify(personneRepository, times(1)).findById("P123");
        verify(equipeRepository, times(1)).findById("E002");
        verify(personneRepository, times(1)).save(existingEntity);
        verify(personneMapper, times(1)).mapToPersonneDomain(updatedEntity);
    }

    @Test
    void testUpdatePersonne_RemoveEquipe() {
        // Given
        EquipeEntity equipeEntity = new EquipeEntity();
        equipeEntity.setCode("E001");
        equipeEntity.setNom("Équipe Backend");

        PersonneEntity existingEntity = new PersonneEntity();
        existingEntity.setIdentifiant("P123");
        existingEntity.setNom("Dupont");
        existingEntity.setPrenom("Jean");
        existingEntity.setPoste("Développeur");
        existingEntity.setEquipe(equipeEntity);

        PersonneEntity updatedEntity = new PersonneEntity();
        updatedEntity.setIdentifiant("P123");
        updatedEntity.setNom("Dupont");
        updatedEntity.setPrenom("Jean");
        updatedEntity.setPoste("Développeur");
        updatedEntity.setEquipe(null);  // No equipe

        Personne updateData = new Personne();

        when(personneRepository.findById("P123")).thenReturn(Optional.of(existingEntity));
        when(personneRepository.save(existingEntity)).thenReturn(updatedEntity);

        // When
        Personne result = personneService.updatePersonne("P123", updateData, "");

        // Then
        assertNotNull(result);
        assertEquals("P123", result.getIdentifiant());
        assertNull(result.getEquipe());
        verify(personneRepository, times(1)).findById("P123");
        verify(personneRepository, times(1)).save(existingEntity);
        verify(personneMapper, times(1)).mapToPersonneDomain(updatedEntity);
    }

    @Test
    void testUpdatePersonne_NotFound() {
        // Given
        when(personneRepository.findById("P999")).thenReturn(Optional.empty());
        Personne updateData = new Personne();

        // When / Then
        assertThrows(RuntimeException.class, () -> {
            personneService.updatePersonne("P999", updateData, null);
        });
        verify(personneRepository, times(1)).findById("P999");
        verify(personneRepository, never()).save(any(PersonneEntity.class));
        verify(personneMapper, never()).mapToPersonneDomain(any(PersonneEntity.class));
    }

    @Test
    void testDeletePersonne() {
        // Given
        when(personneRepository.existsById("P123")).thenReturn(true);

        // When
        personneService.deletePersonne("P123");

        // Then
        verify(personneRepository, times(1)).existsById("P123");
        verify(personneRepository, times(1)).deleteById("P123");
    }

    @Test
    void testDeletePersonne_NotFound() {
        // Given
        when(personneRepository.existsById("P999")).thenReturn(false);

        // When / Then
        assertThrows(RuntimeException.class, () -> {
            personneService.deletePersonne("P999");
        });
        verify(personneRepository, times(1)).existsById("P999");
        verify(personneRepository, never()).deleteById(anyString());
    }
}