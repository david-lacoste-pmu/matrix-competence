package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.domain.MatriceCompetence;
import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.entity.CompetenceEntity;
import fr.pmu.matrix.competence.entity.MatriceCompetenceEntity;
import fr.pmu.matrix.competence.entity.NoteEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.repository.CompetenceRepository;
import fr.pmu.matrix.competence.repository.MatriceCompetenceRepository;
import fr.pmu.matrix.competence.repository.NoteRepository;
import fr.pmu.matrix.competence.repository.PersonneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatriceCompetenceService {

    private final MatriceCompetenceRepository matriceCompetenceRepository;
    private final PersonneRepository personneRepository;
    private final CompetenceRepository competenceRepository;
    private final NoteRepository noteRepository;

    @Autowired
    public MatriceCompetenceService(
            MatriceCompetenceRepository matriceCompetenceRepository,
            PersonneRepository personneRepository,
            CompetenceRepository competenceRepository,
            NoteRepository noteRepository) {
        this.matriceCompetenceRepository = matriceCompetenceRepository;
        this.personneRepository = personneRepository;
        this.competenceRepository = competenceRepository;
        this.noteRepository = noteRepository;
    }

    /**
     * Récupère toutes les matrices de compétences
     * @return Liste des matrices de compétences
     */
    public List<MatriceCompetence> getAllMatricesCompetences() {
        return matriceCompetenceRepository.findAll().stream()
                .map(this::mapToMatriceCompetenceDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une matrice de compétence spécifique
     * @param personneId Identifiant de la personne
     * @param competenceId Identifiant de la compétence
     * @return La matrice de compétence correspondante
     * @throws RuntimeException Si la matrice n'existe pas
     */
    public MatriceCompetence getMatriceCompetence(String personneId, String competenceId) {
        PersonneEntity personneEntity = personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'identifiant: " + personneId));
                
        CompetenceEntity competenceEntity = competenceRepository.findById(competenceId)
                .orElseThrow(() -> new RuntimeException("Compétence non trouvée avec l'identifiant: " + competenceId));
        
        MatriceCompetenceEntity matriceEntity = matriceCompetenceRepository
                .findByPersonneAndCompetence(personneEntity, competenceEntity)
                .orElseThrow(() -> new RuntimeException("Matrice de compétence non trouvée pour cette personne et cette compétence"));
        
        return mapToMatriceCompetenceDomain(matriceEntity);
    }

    /**
     * Crée une nouvelle matrice de compétence
     * @param personneId Identifiant de la personne
     * @param competenceId Identifiant de la compétence
     * @param noteValeur Valeur de la note
     * @return La matrice de compétence créée
     * @throws RuntimeException Si la personne, la compétence ou la note n'existe pas
     */
    @Transactional
    public MatriceCompetence createMatriceCompetence(String personneId, String competenceId, int noteValeur) {
        PersonneEntity personneEntity = personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'identifiant: " + personneId));
                
        CompetenceEntity competenceEntity = competenceRepository.findById(competenceId)
                .orElseThrow(() -> new RuntimeException("Compétence non trouvée avec l'identifiant: " + competenceId));
                
        NoteEntity noteEntity = noteRepository.findById(noteValeur)
                .orElseThrow(() -> new RuntimeException("Note non trouvée avec la valeur: " + noteValeur));
        
        // Vérifier si une matrice existe déjà
        Optional<MatriceCompetenceEntity> existingMatrice = matriceCompetenceRepository
                .findByPersonneAndCompetence(personneEntity, competenceEntity);
                
        if (existingMatrice.isPresent()) {
            throw new RuntimeException("Une matrice de compétence existe déjà pour cette personne et cette compétence");
        }
        
        MatriceCompetenceEntity matriceEntity = new MatriceCompetenceEntity();
        matriceEntity.setPersonne(personneEntity);
        matriceEntity.setCompetence(competenceEntity);
        matriceEntity.setNote(noteEntity);
        
        matriceEntity = matriceCompetenceRepository.save(matriceEntity);
        return mapToMatriceCompetenceDomain(matriceEntity);
    }

    /**
     * Met à jour une matrice de compétence existante
     * @param personneId Identifiant de la personne
     * @param competenceId Identifiant de la compétence
     * @param noteValeur Nouvelle valeur de la note
     * @return La matrice de compétence mise à jour
     * @throws RuntimeException Si la matrice n'existe pas ou si la note n'est pas valide
     */
    @Transactional
    public MatriceCompetence updateMatriceCompetence(String personneId, String competenceId, int noteValeur) {
        PersonneEntity personneEntity = personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'identifiant: " + personneId));
                
        CompetenceEntity competenceEntity = competenceRepository.findById(competenceId)
                .orElseThrow(() -> new RuntimeException("Compétence non trouvée avec l'identifiant: " + competenceId));
                
        NoteEntity noteEntity = noteRepository.findById(noteValeur)
                .orElseThrow(() -> new RuntimeException("Note non trouvée avec la valeur: " + noteValeur));
        
        MatriceCompetenceEntity matriceEntity = matriceCompetenceRepository
                .findByPersonneAndCompetence(personneEntity, competenceEntity)
                .orElseThrow(() -> new RuntimeException("Matrice de compétence non trouvée pour cette personne et cette compétence"));
        
        matriceEntity.setNote(noteEntity);
        matriceEntity = matriceCompetenceRepository.save(matriceEntity);
        
        return mapToMatriceCompetenceDomain(matriceEntity);
    }

    /**
     * Supprime une matrice de compétence
     * @param personneId Identifiant de la personne
     * @param competenceId Identifiant de la compétence
     * @throws RuntimeException Si la matrice n'existe pas
     */
    @Transactional
    public void deleteMatriceCompetence(String personneId, String competenceId) {
        PersonneEntity personneEntity = personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'identifiant: " + personneId));
                
        CompetenceEntity competenceEntity = competenceRepository.findById(competenceId)
                .orElseThrow(() -> new RuntimeException("Compétence non trouvée avec l'identifiant: " + competenceId));
        
        MatriceCompetenceEntity matriceEntity = matriceCompetenceRepository
                .findByPersonneAndCompetence(personneEntity, competenceEntity)
                .orElseThrow(() -> new RuntimeException("Matrice de compétence non trouvée pour cette personne et cette compétence"));
        
        matriceCompetenceRepository.delete(matriceEntity);
    }

    /**
     * Récupère toutes les compétences d'une personne
     * @param personneId Identifiant de la personne
     * @return Liste des matrices de compétences pour cette personne
     * @throws RuntimeException Si la personne n'existe pas
     */
    public List<MatriceCompetence> getCompetencesByPersonne(String personneId) {
        PersonneEntity personneEntity = personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'identifiant: " + personneId));
        
        List<MatriceCompetenceEntity> matricesEntities = matriceCompetenceRepository.findByPersonne(personneEntity);
        
        return matricesEntities.stream()
                .map(this::mapToMatriceCompetenceDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les personnes possédant une compétence donnée
     * @param competenceId Identifiant de la compétence
     * @return Liste des matrices de compétences pour cette compétence
     * @throws RuntimeException Si la compétence n'existe pas
     */
    public List<MatriceCompetence> getPersonnesByCompetence(String competenceId) {
        CompetenceEntity competenceEntity = competenceRepository.findById(competenceId)
                .orElseThrow(() -> new RuntimeException("Compétence non trouvée avec l'identifiant: " + competenceId));
        
        List<MatriceCompetenceEntity> matricesEntities = matriceCompetenceRepository.findByCompetence(competenceEntity);
        
        return matricesEntities.stream()
                .map(this::mapToMatriceCompetenceDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité MatriceCompetence en objet domain
     * @param entity L'entité à convertir
     * @return L'objet domain correspondant
     */
    private MatriceCompetence mapToMatriceCompetenceDomain(MatriceCompetenceEntity entity) {
        // Création de l'objet Personne
        Personne personne = new Personne();
        PersonneEntity personneEntity = entity.getPersonne();
        personne.setIdentifiant(personneEntity.getIdentifiant());
        personne.setNom(personneEntity.getNom());
        personne.setPrenom(personneEntity.getPrenom());
        personne.setPoste(personneEntity.getPoste());
        if (personneEntity.getEquipe() != null) {
            personne.setEquipe(mapToEquipeDomain(personneEntity.getEquipe()));
        }
        
        // Création de l'objet Competence
        Competence competence = new Competence();
        CompetenceEntity competenceEntity = entity.getCompetence();
        competence.setLibelle(competenceEntity.getLibelle());
        competence.setDescription(competenceEntity.getDescription());
        
        // Création de l'objet Note
        Note note = new Note();
        NoteEntity noteEntity = entity.getNote();
        note.setValeur(noteEntity.getValeur());
        note.setLibelle(noteEntity.getLibelle());
        
        return new MatriceCompetence(personne, competence, note);
    }
    
    /**
     * Méthode utilitaire pour convertir une entité Equipe en objet domain
     * @param equipeEntity L'entité à convertir
     * @return L'objet domain correspondant
     */
    private fr.pmu.matrix.competence.domain.Equipe mapToEquipeDomain(fr.pmu.matrix.competence.entity.EquipeEntity equipeEntity) {
        fr.pmu.matrix.competence.domain.Equipe equipe = new fr.pmu.matrix.competence.domain.Equipe();
        equipe.setCode(equipeEntity.getCode());
        equipe.setNom(equipeEntity.getNom());
        equipe.setDescription(equipeEntity.getDescription());
        return equipe;
    }
}