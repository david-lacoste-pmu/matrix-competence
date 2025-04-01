package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.Groupement;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.GroupementEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
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

    @Autowired
    public EquipeService(EquipeRepository equipeRepository, 
                        GroupementRepository groupementRepository,
                        PersonneRepository personneRepository) {
        this.equipeRepository = equipeRepository;
        this.groupementRepository = groupementRepository;
        this.personneRepository = personneRepository;
    }

    /**
     * Récupère toutes les équipes
     * 
     * @return Liste des équipes
     */
    public List<Equipe> getAllEquipes() {
        return equipeRepository.findAll()
                .stream()
                .map(this::convertToEquipe)
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
        return convertToEquipe(equipeEntity);
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
        return convertToEquipe(savedEntity);
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
        return convertToEquipe(updatedEntity);
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

    /**
     * Convertit une entité équipe en objet de domaine
     * 
     * @param entity Entité équipe
     * @return Objet de domaine équipe
     */
    private Equipe convertToEquipe(EquipeEntity entity) {
        Equipe equipe = new Equipe();
        equipe.setCode(entity.getCode());
        equipe.setNom(entity.getNom());
        equipe.setDescription(entity.getDescription());
        
        // Convertir le groupement
        if (entity.getGroupement() != null) {
            GroupementEntity groupementEntity = entity.getGroupement();
            Groupement groupement = new Groupement();
            groupement.setCode(groupementEntity.getCode());
            groupement.setLibelle(groupementEntity.getLibelle());
            groupement.setDirection(groupementEntity.getDirection());
            equipe.setGroupement(groupement);
        }
        
        // Charger et convertir les membres de l'équipe
        List<PersonneEntity> personneEntities = personneRepository.findByEquipeCode(entity.getCode());
        if (personneEntities != null && !personneEntities.isEmpty()) {
            List<Personne> membres = personneEntities.stream()
                    .map(personneEntity -> {
                        Personne personne = new Personne();
                        personne.setIdentifiant(personneEntity.getIdentifiant());
                        personne.setNom(personneEntity.getNom());
                        personne.setPrenom(personneEntity.getPrenom());
                        personne.setPoste(personneEntity.getPoste());
                        // Ne pas inclure l'équipe pour éviter une référence circulaire
                        return personne;
                    })
                    .collect(Collectors.toList());
            equipe.setMembres(membres);
        }
        
        return equipe;
    }
}