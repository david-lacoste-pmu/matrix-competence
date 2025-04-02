package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.*;
import fr.pmu.matrix.competence.entity.CompetenceEntity;
import fr.pmu.matrix.competence.entity.CompetenceRequiseEntity;
import fr.pmu.matrix.competence.entity.DemandeEntity;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.GroupementEntity;
import fr.pmu.matrix.competence.entity.NoteEntity;
import fr.pmu.matrix.competence.mapper.DemandeMapper;
import fr.pmu.matrix.competence.repository.CompetenceRepository;
import fr.pmu.matrix.competence.repository.DemandeRepository;
import fr.pmu.matrix.competence.repository.EquipeRepository;
import fr.pmu.matrix.competence.repository.GroupementRepository;
import fr.pmu.matrix.competence.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des demandes de compétences
 */
@Service
public class DemandeService {

    private final DemandeRepository demandeRepository;
    private final CompetenceRepository competenceRepository;
    private final NoteRepository noteRepository;
    private final EquipeRepository equipeRepository;
    private final GroupementRepository groupementRepository;
    private final DemandeMapper demandeMapper;

    @Autowired
    public DemandeService(DemandeRepository demandeRepository, 
                         CompetenceRepository competenceRepository,
                         NoteRepository noteRepository,
                         EquipeRepository equipeRepository,
                         GroupementRepository groupementRepository,
                         DemandeMapper demandeMapper) {
        this.demandeRepository = demandeRepository;
        this.competenceRepository = competenceRepository;
        this.noteRepository = noteRepository;
        this.equipeRepository = equipeRepository;
        this.groupementRepository = groupementRepository;
        this.demandeMapper = demandeMapper;
    }

    /**
     * Récupère toutes les demandes
     * @return Liste des demandes
     */
    public List<Demande> getAllDemandes() {
        return demandeRepository.findAll().stream()
                .map(entity -> {
                    Object destination = getDestinationEntity(entity);
                    return demandeMapper.mapToDemandeDomain(entity, destination);
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère une demande par son ID
     * @param id L'identifiant de la demande
     * @return La demande correspondante
     * @throws RuntimeException Si la demande n'existe pas
     */
    public Demande getDemandeById(String id) {
        DemandeEntity demandeEntity = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'ID: " + id));
        Object destination = getDestinationEntity(demandeEntity);
        return demandeMapper.mapToDemandeDomain(demandeEntity, destination);
    }

    /**
     * Crée une nouvelle demande
     * @param demande La demande à créer
     * @return La demande créée avec son ID généré
     */
    @Transactional
    public Demande createDemande(Demande demande) {
        if (!demande.estValide()) {
            throw new RuntimeException("La demande n'est pas valide");
        }

        // Génération d'un ID unique
        if (demande.getId() == null || demande.getId().isEmpty()) {
            demande.setId(UUID.randomUUID().toString());
        } else if (demandeRepository.existsById(demande.getId())) {
            throw new RuntimeException("Une demande avec cet ID existe déjà: " + demande.getId());
        }

        DemandeEntity demandeEntity = new DemandeEntity();
        demandeEntity.setId(demande.getId());
        demandeEntity.setMatriculeDemandeur(demande.getMatriculeDemandeur());
        demandeEntity.setDescription(demande.getDescription());
        demandeEntity.setNature(demande.getNature());
        demandeEntity.setDateDebut(demande.getDateDebut());
        demandeEntity.setDateFin(demande.getDateFin());

        // Gestion de la destination
        String destinationCode = null;
        boolean estGroupement = false;
        
        if (demande.getDestination() instanceof DestinationGroupement) {
            estGroupement = true;
            destinationCode = ((DestinationGroupement) demande.getDestination()).getGroupement().getCode();
            // Vérifier que le groupement existe
            if (!groupementRepository.existsById(destinationCode)) {
                throw new RuntimeException("Groupement non trouvé avec le code: " + destinationCode);
            }
        } else if (demande.getDestination() instanceof DestinationEquipe) {
            estGroupement = false;
            destinationCode = ((DestinationEquipe) demande.getDestination()).getEquipe().getCode();
            // Vérifier que l'équipe existe
            if (!equipeRepository.existsById(destinationCode)) {
                throw new RuntimeException("Équipe non trouvée avec le code: " + destinationCode);
            }
        } else {
            throw new RuntimeException("Type de destination non supporté");
        }
        
        demandeEntity.setEstGroupement(estGroupement);
        demandeEntity.setDestinationCode(destinationCode);

        // Sauvegarde d'abord la demande sans les compétences
        demandeEntity = demandeRepository.save(demandeEntity);

        // Ajout des compétences requises
        for (CompetenceRequise competenceRequise : demande.getCompetencesRecherchees()) {
            CompetenceEntity competenceEntity = competenceRepository.findById(competenceRequise.getCompetence().getLibelle())
                    .orElseThrow(() -> new RuntimeException("Compétence non trouvée: " 
                            + competenceRequise.getCompetence().getLibelle()));
            
            NoteEntity noteEntity = noteRepository.findById(competenceRequise.getNoteRequise().getValeur())
                    .orElseThrow(() -> new RuntimeException("Note non trouvée: " 
                            + competenceRequise.getNoteRequise().getValeur()));
            
            CompetenceRequiseEntity competenceRequiseEntity = new CompetenceRequiseEntity();
            competenceRequiseEntity.setCompetence(competenceEntity);
            competenceRequiseEntity.setNoteRequise(noteEntity);
            
            addCompetenceRequiseToDemandeEntity(demandeEntity, competenceRequiseEntity);
        }

        // Sauvegarde à nouveau pour persister les compétences requises
        demandeEntity = demandeRepository.save(demandeEntity);
        Object destination = getDestinationEntity(demandeEntity);
        return demandeMapper.mapToDemandeDomain(demandeEntity, destination);
    }

    /**
     * Met à jour une demande existante
     * @param id ID de la demande à mettre à jour
     * @param demande Nouvelles données de la demande
     * @return La demande mise à jour
     * @throws RuntimeException Si la demande n'existe pas
     */
    @Transactional
    public Demande updateDemande(String id, Demande demande) {
        if (!demande.estValide()) {
            throw new RuntimeException("La demande n'est pas valide");
        }

        DemandeEntity demandeEntity = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'ID: " + id));

        demandeEntity.setMatriculeDemandeur(demande.getMatriculeDemandeur());
        demandeEntity.setDescription(demande.getDescription());
        demandeEntity.setNature(demande.getNature());
        demandeEntity.setDateDebut(demande.getDateDebut());
        demandeEntity.setDateFin(demande.getDateFin());

        // Mise à jour de la destination
        String destinationCode = null;
        boolean estGroupement = false;
        
        if (demande.getDestination() instanceof DestinationGroupement) {
            estGroupement = true;
            destinationCode = ((DestinationGroupement) demande.getDestination()).getGroupement().getCode();
            // Vérifier que le groupement existe
            if (!groupementRepository.existsById(destinationCode)) {
                throw new RuntimeException("Groupement non trouvé avec le code: " + destinationCode);
            }
        } else if (demande.getDestination() instanceof DestinationEquipe) {
            estGroupement = false;
            destinationCode = ((DestinationEquipe) demande.getDestination()).getEquipe().getCode();
            // Vérifier que l'équipe existe
            if (!equipeRepository.existsById(destinationCode)) {
                throw new RuntimeException("Équipe non trouvée avec le code: " + destinationCode);
            }
        } else {
            throw new RuntimeException("Type de destination non supporté");
        }
        
        demandeEntity.setEstGroupement(estGroupement);
        demandeEntity.setDestinationCode(destinationCode);

        // Supprimer toutes les anciennes compétences requises
        demandeEntity.getCompetencesRequises().clear();

        // Ajouter les nouvelles compétences requises
        for (CompetenceRequise competenceRequise : demande.getCompetencesRecherchees()) {
            CompetenceEntity competenceEntity = competenceRepository.findById(competenceRequise.getCompetence().getLibelle())
                    .orElseThrow(() -> new RuntimeException("Compétence non trouvée: " 
                            + competenceRequise.getCompetence().getLibelle()));
            
            NoteEntity noteEntity = noteRepository.findById(competenceRequise.getNoteRequise().getValeur())
                    .orElseThrow(() -> new RuntimeException("Note non trouvée: " 
                            + competenceRequise.getNoteRequise().getValeur()));
            
            CompetenceRequiseEntity competenceRequiseEntity = new CompetenceRequiseEntity();
            competenceRequiseEntity.setCompetence(competenceEntity);
            competenceRequiseEntity.setNoteRequise(noteEntity);
            
            addCompetenceRequiseToDemandeEntity(demandeEntity, competenceRequiseEntity);
        }

        demandeEntity = demandeRepository.save(demandeEntity);
        Object destination = getDestinationEntity(demandeEntity);
        return demandeMapper.mapToDemandeDomain(demandeEntity, destination);
    }

    /**
     * Supprime une demande
     * @param id ID de la demande à supprimer
     * @throws RuntimeException Si la demande n'existe pas
     */
    @Transactional
    public void deleteDemande(String id) {
        if (!demandeRepository.existsById(id)) {
            throw new RuntimeException("Demande non trouvée avec l'ID: " + id);
        }
        demandeRepository.deleteById(id);
    }

    /**
     * Recherche les demandes actives à une date donnée
     * @param date La date de référence
     * @return Liste des demandes actives à cette date
     */
    public List<Demande> getDemandesActiveAtDate(Date date) {
        return demandeRepository.findActiveAtDate(date).stream()
                .map(entity -> {
                    Object destination = getDestinationEntity(entity);
                    return demandeMapper.mapToDemandeDomain(entity, destination);
                })
                .collect(Collectors.toList());
    }

    /**
     * Recherche les demandes par matricule du demandeur
     * @param matricule Le matricule du demandeur
     * @return Liste des demandes correspondantes
     */
    public List<Demande> getDemandesByDemandeur(String matricule) {
        return demandeRepository.findByMatriculeDemandeur(matricule).stream()
                .map(entity -> {
                    Object destination = getDestinationEntity(entity);
                    return demandeMapper.mapToDemandeDomain(entity, destination);
                })
                .collect(Collectors.toList());
    }

    /**
     * Recherche les demandes qui requièrent une compétence spécifique
     * @param competenceLibelle Le libellé de la compétence
     * @return Liste des demandes qui requièrent cette compétence
     */
    public List<Demande> getDemandesByCompetence(String competenceLibelle) {
        return demandeRepository.findByCompetenceRequise(competenceLibelle).stream()
                .map(entity -> {
                    Object destination = getDestinationEntity(entity);
                    return demandeMapper.mapToDemandeDomain(entity, destination);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère l'entité de destination (équipe ou groupement) pour une demande
     * @param demandeEntity L'entité demande
     * @return L'entité de destination ou null si non trouvée
     */
    private Object getDestinationEntity(DemandeEntity demandeEntity) {
        if (demandeEntity.getDestinationCode() == null || demandeEntity.getDestinationCode().isEmpty()) {
            return null;
        }
        
        if (demandeEntity.getEstGroupement()) {
            Optional<GroupementEntity> groupementOpt = groupementRepository.findById(demandeEntity.getDestinationCode());
            return groupementOpt.orElse(null);
        } else {
            Optional<EquipeEntity> equipeOpt = equipeRepository.findById(demandeEntity.getDestinationCode());
            return equipeOpt.orElse(null);
        }
    }

    /**
     * Ajoute une compétence requise à une demande
     * 
     * @param demandeEntity La demande
     * @param competenceRequiseEntity La compétence requise à ajouter
     */
    public void addCompetenceRequiseToDemandeEntity(DemandeEntity demandeEntity, CompetenceRequiseEntity competenceRequiseEntity) {
        demandeEntity.getCompetencesRequises().add(competenceRequiseEntity);
        competenceRequiseEntity.setDemande(demandeEntity);
    }
    
    /**
     * Supprime une compétence requise d'une demande
     * 
     * @param demandeEntity La demande
     * @param competenceRequiseEntity La compétence requise à supprimer
     */
    public void removeCompetenceRequiseFromDemandeEntity(DemandeEntity demandeEntity, CompetenceRequiseEntity competenceRequiseEntity) {
        demandeEntity.getCompetencesRequises().remove(competenceRequiseEntity);
        competenceRequiseEntity.setDemande(null);
    }
}