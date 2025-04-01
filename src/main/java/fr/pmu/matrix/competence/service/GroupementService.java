package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Groupement;
import fr.pmu.matrix.competence.entity.GroupementEntity;
import fr.pmu.matrix.competence.repository.GroupementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupementService {

    private final GroupementRepository groupementRepository;

    @Autowired
    public GroupementService(GroupementRepository groupementRepository) {
        this.groupementRepository = groupementRepository;
    }

    /**
     * Récupère tous les groupements
     * @return Liste des groupements
     */
    public List<Groupement> getAllGroupements() {
        return groupementRepository.findAll()
                .stream()
                .map(this::mapToGroupementDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un groupement par son code
     * @param code Code unique du groupement
     * @return Le groupement correspondant
     */
    public Groupement getGroupementByCode(String code) {
        return groupementRepository.findById(code)
                .map(this::mapToGroupementDomain)
                .orElseThrow(() -> new RuntimeException("Groupement non trouvé avec le code: " + code));
    }

    /**
     * Crée un nouveau groupement
     * @param groupement Le groupement à créer
     * @return Le groupement créé
     */
    @Transactional
    public Groupement createGroupement(Groupement groupement) {
        // Vérifier si le groupement existe déjà
        if (groupementRepository.existsById(groupement.getCode())) {
            throw new RuntimeException("Un groupement avec ce code existe déjà: " + groupement.getCode());
        }
        
        GroupementEntity groupementEntity = new GroupementEntity();
        groupementEntity.setCode(groupement.getCode());
        groupementEntity.setLibelle(groupement.getLibelle());
        groupementEntity.setDirection(groupement.getDirection());
        
        groupementEntity = groupementRepository.save(groupementEntity);
        return mapToGroupementDomain(groupementEntity);
    }

    /**
     * Met à jour un groupement existant
     * @param code Code du groupement à mettre à jour
     * @param groupement Nouvelles données du groupement
     * @return Le groupement mis à jour
     */
    @Transactional
    public Groupement updateGroupement(String code, Groupement groupement) {
        GroupementEntity groupementEntity = groupementRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Groupement non trouvé avec le code: " + code));
        
        // On ne met à jour que les champs libelle et direction, pas le code
        if (groupement.getLibelle() != null) {
            groupementEntity.setLibelle(groupement.getLibelle());
        }
        if (groupement.getDirection() != null) {
            groupementEntity.setDirection(groupement.getDirection());
        }
        
        groupementEntity = groupementRepository.save(groupementEntity);
        return mapToGroupementDomain(groupementEntity);
    }

    /**
     * Supprime un groupement
     * @param code Code du groupement à supprimer
     */
    @Transactional
    public void deleteGroupement(String code) {
        if (!groupementRepository.existsById(code)) {
            throw new RuntimeException("Groupement non trouvé avec le code: " + code);
        }
        groupementRepository.deleteById(code);
    }

    /**
     * Convertit une entité groupement en objet domain
     * @param entity L'entité à convertir
     * @return L'objet domain correspondant
     */
    private Groupement mapToGroupementDomain(GroupementEntity entity) {
        Groupement groupement = new Groupement();
        groupement.setCode(entity.getCode());
        groupement.setLibelle(entity.getLibelle());
        groupement.setDirection(entity.getDirection());
        return groupement;
    }
}