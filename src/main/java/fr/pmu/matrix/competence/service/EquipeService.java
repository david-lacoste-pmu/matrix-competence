package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.GroupementEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.mapper.EquipeMapper;
import fr.pmu.matrix.competence.repository.EquipeRepository;
import fr.pmu.matrix.competence.repository.GroupementRepository;
import fr.pmu.matrix.competence.repository.PersonneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final EquipeMapper equipeMapper;

    @Autowired
    public EquipeService(EquipeRepository equipeRepository, 
                        GroupementRepository groupementRepository,
                        PersonneRepository personneRepository,
                        EquipeMapper equipeMapper) {
        this.equipeRepository = equipeRepository;
        this.groupementRepository = groupementRepository;
        this.personneRepository = personneRepository;
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
        
        // Dans un système réel, il faudrait gérer les personnes associées à cette équipe
        // Par exemple, soit les supprimer, soit les détacher de l'équipe
        
        equipeRepository.deleteById(code);
    }
}