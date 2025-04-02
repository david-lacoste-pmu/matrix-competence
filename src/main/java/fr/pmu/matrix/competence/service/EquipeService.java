package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.domain.CompetenceRequise;
import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.entity.CompetenceEntity;
import fr.pmu.matrix.competence.entity.CompetenceRequiseEntity;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.GroupementEntity;
import fr.pmu.matrix.competence.entity.NoteEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.mapper.EquipeMapper;
import fr.pmu.matrix.competence.repository.CompetenceRepository;
import fr.pmu.matrix.competence.repository.EquipeRepository;
import fr.pmu.matrix.competence.repository.GroupementRepository;
import fr.pmu.matrix.competence.repository.NoteRepository;
import fr.pmu.matrix.competence.repository.PersonneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des équipes et leurs associations avec des groupements et personnes
 */
@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final GroupementRepository groupementRepository;
    private final PersonneRepository personneRepository;
    private final CompetenceRepository competenceRepository;
    private final NoteRepository noteRepository;
    private final EquipeMapper equipeMapper;

    @Autowired
    public EquipeService(EquipeRepository equipeRepository, 
                        GroupementRepository groupementRepository,
                        PersonneRepository personneRepository,
                        CompetenceRepository competenceRepository,
                        NoteRepository noteRepository,
                        EquipeMapper equipeMapper) {
        this.equipeRepository = equipeRepository;
        this.groupementRepository = groupementRepository;
        this.personneRepository = personneRepository;
        this.competenceRepository = competenceRepository;
        this.noteRepository = noteRepository;
        this.equipeMapper = equipeMapper;
    }

    /**
     * Récupère toutes les équipes
     * 
     * @return Liste des équipes
     */
    public List<Equipe> getAllEquipes() {
        return equipeRepository.findAll()
                .stream()
                .map(entity -> {
                    List<PersonneEntity> personneEntities = personneRepository.findByEquipeCode(entity.getCode());
                    return equipeMapper.convertToEquipe(entity, personneEntities);
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère une équipe par son code
     * 
     * @param code Code unique de l'équipe
     * @return Équipe correspondante au code
     * @throws RuntimeException si l'équipe n'existe pas
     */
    public Equipe getEquipeByCode(String code) {
        EquipeEntity equipeEntity = equipeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec le code: " + code));
        List<PersonneEntity> personneEntities = personneRepository.findByEquipeCode(code);
        return equipeMapper.convertToEquipe(equipeEntity, personneEntities);
    }

    /**
     * Crée une nouvelle équipe
     * 
     * @param equipe Équipe à créer
     * @param groupementCode Code du groupement auquel l'équipe appartient
     * @return Équipe créée
     * @throws RuntimeException si une équipe avec le même code existe déjà
     * @throws RuntimeException si le groupement n'existe pas
     */
    @Transactional
    public Equipe createEquipe(Equipe equipe, String groupementCode) {
        if (equipeRepository.existsById(equipe.getCode())) {
            throw new RuntimeException("Une équipe avec le code " + equipe.getCode() + " existe déjà");
        }

        // Vérifier et récupérer le groupement
        GroupementEntity groupementEntity = groupementRepository.findById(groupementCode)
                .orElseThrow(() -> new RuntimeException("Groupement non trouvé avec le code: " + groupementCode));

        // Créer l'entité équipe
        EquipeEntity equipeEntity = new EquipeEntity();
        equipeEntity.setCode(equipe.getCode());
        equipeEntity.setNom(equipe.getNom());
        equipeEntity.setDescription(equipe.getDescription());
        equipeEntity.setGroupement(groupementEntity);

        // Traiter le profil de recherche si présent
        if (equipe.getProfilRecherche() != null && !equipe.getProfilRecherche().isEmpty()) {
            List<CompetenceRequiseEntity> profilEntities = new ArrayList<>();
            for (CompetenceRequise competenceRequise : equipe.getProfilRecherche()) {
                CompetenceEntity competenceEntity = competenceRepository.findById(competenceRequise.getCompetence().getLibelle())
                        .orElseThrow(() -> new RuntimeException("Compétence non trouvée: " + competenceRequise.getCompetence().getLibelle()));
                
                NoteEntity noteEntity = noteRepository.findById(competenceRequise.getNoteRequise().getValeur())
                        .orElseThrow(() -> new RuntimeException("Note non trouvée: " + competenceRequise.getNoteRequise().getValeur()));
                
                CompetenceRequiseEntity competenceRequiseEntity = new CompetenceRequiseEntity();
                competenceRequiseEntity.setCompetence(competenceEntity);
                competenceRequiseEntity.setNoteRequise(noteEntity);
                
                profilEntities.add(competenceRequiseEntity);
            }
            equipeEntity.setProfilRecherche(profilEntities);
        }

        // Sauvegarder l'équipe
        EquipeEntity savedEntity = equipeRepository.save(equipeEntity);
        // Pour une nouvelle équipe, il n'y a pas encore de membres
        return equipeMapper.convertToEquipe(savedEntity, List.of());
    }

    /**
     * Met à jour une équipe existante
     * 
     * @param code Code de l'équipe à mettre à jour
     * @param equipe Nouvelles données de l'équipe
     * @param groupementCode Nouveau code du groupement (peut être null si pas de changement)
     * @return Équipe mise à jour
     * @throws RuntimeException si l'équipe n'existe pas
     * @throws RuntimeException si le groupement n'existe pas
     */
    @Transactional
    public Equipe updateEquipe(String code, Equipe equipe, String groupementCode) {
        // Récupérer l'équipe existante
        EquipeEntity equipeEntity = equipeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec le code: " + code));

        // Mise à jour des champs
        if (equipe.getNom() != null) {
            equipeEntity.setNom(equipe.getNom());
        }
        if (equipe.getDescription() != null) {
            equipeEntity.setDescription(equipe.getDescription());
        }

        // Mise à jour du groupement si spécifié
        if (groupementCode != null) {
            GroupementEntity groupementEntity = groupementRepository.findById(groupementCode)
                    .orElseThrow(() -> new RuntimeException("Groupement non trouvé avec le code: " + groupementCode));
            equipeEntity.setGroupement(groupementEntity);
        }

        // Mettre à jour le profil de recherche si fourni
        if (equipe.getProfilRecherche() != null) {
            // Vider le profil actuel
            equipeEntity.getProfilRecherche().clear();
            
            // Ajouter les nouvelles compétences requises
            for (CompetenceRequise competenceRequise : equipe.getProfilRecherche()) {
                CompetenceEntity competenceEntity = competenceRepository.findById(competenceRequise.getCompetence().getLibelle())
                        .orElseThrow(() -> new RuntimeException("Compétence non trouvée: " + competenceRequise.getCompetence().getLibelle()));
                
                NoteEntity noteEntity = noteRepository.findById(competenceRequise.getNoteRequise().getValeur())
                        .orElseThrow(() -> new RuntimeException("Note non trouvée: " + competenceRequise.getNoteRequise().getValeur()));
                
                CompetenceRequiseEntity competenceRequiseEntity = new CompetenceRequiseEntity();
                competenceRequiseEntity.setCompetence(competenceEntity);
                competenceRequiseEntity.setNoteRequise(noteEntity);
                
                equipeEntity.getProfilRecherche().add(competenceRequiseEntity);
            }
        }

        // Sauvegarder les modifications
        EquipeEntity updatedEntity = equipeRepository.save(equipeEntity);
        List<PersonneEntity> personneEntities = personneRepository.findByEquipeCode(code);
        return equipeMapper.convertToEquipe(updatedEntity, personneEntities);
    }

    /**
     * Supprime une équipe
     * 
     * @param code Code de l'équipe à supprimer
     * @throws RuntimeException si l'équipe n'existe pas
     */
    @Transactional
    public void deleteEquipe(String code) {
        if (!equipeRepository.existsById(code)) {
            throw new RuntimeException("Équipe non trouvée avec le code: " + code);
        }
        
        equipeRepository.deleteById(code);
    }

    /**
     * Récupère le profil de recherche d'une équipe
     * 
     * @param code Code de l'équipe
     * @return Liste des compétences requises du profil de recherche
     * @throws RuntimeException si l'équipe n'existe pas
     */
    public List<CompetenceRequise> getProfilRecherche(String code) {
        EquipeEntity equipeEntity = equipeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec le code: " + code));
        
        return equipeEntity.getProfilRecherche().stream()
                .map(entity -> {
                    Competence competence = new Competence();
                    competence.setLibelle(entity.getCompetence().getLibelle());
                    competence.setDescription(entity.getCompetence().getDescription());
                    
                    Note note = new Note();
                    note.setValeur(entity.getNoteRequise().getValeur());
                    note.setLibelle(entity.getNoteRequise().getLibelle());
                    
                    return new CompetenceRequise(competence, note);
                })
                .collect(Collectors.toList());
    }

    /**
     * Met à jour le profil de recherche d'une équipe
     * 
     * @param code Code de l'équipe
     * @param competencesRequises Liste des nouvelles compétences requises
     * @return Équipe mise à jour
     * @throws RuntimeException si l'équipe n'existe pas
     * @throws RuntimeException si une compétence ou note n'existe pas
     */
    @Transactional
    public Equipe updateProfilRecherche(String code, List<CompetenceRequise> competencesRequises) {
        EquipeEntity equipeEntity = equipeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec le code: " + code));
        
        // Vider le profil actuel
        equipeEntity.getProfilRecherche().clear();
        
        // Ajouter les nouvelles compétences requises
        for (CompetenceRequise competenceRequise : competencesRequises) {
            CompetenceEntity competenceEntity = competenceRepository.findById(competenceRequise.getCompetence().getLibelle())
                    .orElseThrow(() -> new RuntimeException("Compétence non trouvée: " + competenceRequise.getCompetence().getLibelle()));
            
            NoteEntity noteEntity = noteRepository.findById(competenceRequise.getNoteRequise().getValeur())
                    .orElseThrow(() -> new RuntimeException("Note non trouvée: " + competenceRequise.getNoteRequise().getValeur()));
            
            CompetenceRequiseEntity competenceRequiseEntity = new CompetenceRequiseEntity();
            competenceRequiseEntity.setCompetence(competenceEntity);
            competenceRequiseEntity.setNoteRequise(noteEntity);
            
            equipeEntity.getProfilRecherche().add(competenceRequiseEntity);
        }
        
        EquipeEntity savedEntity = equipeRepository.save(equipeEntity);
        List<PersonneEntity> personneEntities = personneRepository.findByEquipeCode(code);
        return equipeMapper.convertToEquipe(savedEntity, personneEntities);
    }

    /**
     * Ajoute une compétence requise au profil de recherche d'une équipe
     * 
     * @param code Code de l'équipe
     * @param competenceRequise Compétence requise à ajouter
     * @return Équipe mise à jour
     * @throws RuntimeException si l'équipe n'existe pas
     * @throws RuntimeException si la compétence ou note n'existe pas
     */
    @Transactional
    public Equipe addCompetenceRequiseToProfilRecherche(String code, CompetenceRequise competenceRequise) {
        EquipeEntity equipeEntity = equipeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec le code: " + code));
        
        // Vérifier si la compétence existe déjà dans le profil
        boolean exists = equipeEntity.getProfilRecherche().stream()
                .anyMatch(cr -> cr.getCompetence().getLibelle().equals(competenceRequise.getCompetence().getLibelle()));
        
        if (exists) {
            throw new RuntimeException("Cette compétence fait déjà partie du profil de recherche");
        }
        
        // Récupérer les entités nécessaires
        CompetenceEntity competenceEntity = competenceRepository.findById(competenceRequise.getCompetence().getLibelle())
                .orElseThrow(() -> new RuntimeException("Compétence non trouvée: " + competenceRequise.getCompetence().getLibelle()));
        
        NoteEntity noteEntity = noteRepository.findById(competenceRequise.getNoteRequise().getValeur())
                .orElseThrow(() -> new RuntimeException("Note non trouvée: " + competenceRequise.getNoteRequise().getValeur()));
        
        // Créer et ajouter la compétence requise
        CompetenceRequiseEntity competenceRequiseEntity = new CompetenceRequiseEntity();
        competenceRequiseEntity.setCompetence(competenceEntity);
        competenceRequiseEntity.setNoteRequise(noteEntity);
        
        equipeEntity.getProfilRecherche().add(competenceRequiseEntity);
        
        EquipeEntity savedEntity = equipeRepository.save(equipeEntity);
        List<PersonneEntity> personneEntities = personneRepository.findByEquipeCode(code);
        return equipeMapper.convertToEquipe(savedEntity, personneEntities);
    }

    /**
     * Supprime une compétence requise du profil de recherche d'une équipe
     * 
     * @param code Code de l'équipe
     * @param competenceLibelle Libellé de la compétence à supprimer
     * @return Équipe mise à jour
     * @throws RuntimeException si l'équipe n'existe pas
     * @throws RuntimeException si la compétence n'est pas trouvée dans le profil
     */
    @Transactional
    public Equipe removeCompetenceRequiseFromProfilRecherche(String code, String competenceLibelle) {
        EquipeEntity equipeEntity = equipeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec le code: " + code));
        
        // Trouver la compétence requise à supprimer
        CompetenceRequiseEntity toRemove = equipeEntity.getProfilRecherche().stream()
                .filter(cr -> cr.getCompetence().getLibelle().equals(competenceLibelle))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Compétence non trouvée dans le profil de recherche: " + competenceLibelle));
        
        equipeEntity.getProfilRecherche().remove(toRemove);
        
        EquipeEntity savedEntity = equipeRepository.save(equipeEntity);
        List<PersonneEntity> personneEntities = personneRepository.findByEquipeCode(code);
        return equipeMapper.convertToEquipe(savedEntity, personneEntities);
    }
}