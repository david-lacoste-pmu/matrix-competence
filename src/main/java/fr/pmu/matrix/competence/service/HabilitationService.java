package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Habilitation;
import fr.pmu.matrix.competence.entity.HabilitationEntity;
import fr.pmu.matrix.competence.repository.HabilitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des habilitations
 */
@Service
public class HabilitationService {

    private final HabilitationRepository habilitationRepository;

    @Autowired
    public HabilitationService(HabilitationRepository habilitationRepository) {
        this.habilitationRepository = habilitationRepository;
    }

    /**
     * Récupère toutes les habilitations
     * 
     * @return Liste de toutes les habilitations
     */
    public List<Habilitation> getAllHabilitations() {
        return habilitationRepository.findAll()
                .stream()
                .map(this::convertToHabilitation)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une habilitation par son code
     * 
     * @param code Code unique de l'habilitation
     * @return Habilitation correspondant au code
     * @throws RuntimeException si l'habilitation n'existe pas
     */
    public Habilitation getHabilitationByCode(String code) {
        HabilitationEntity habilitationEntity = habilitationRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Habilitation non trouvée avec le code: " + code));
        return convertToHabilitation(habilitationEntity);
    }

    /**
     * Crée une nouvelle habilitation
     * 
     * @param habilitation Habilitation à créer
     * @return Habilitation créée
     * @throws RuntimeException si une habilitation avec le même code existe déjà
     */
    @Transactional
    public Habilitation createHabilitation(Habilitation habilitation) {
        if (habilitationRepository.existsById(habilitation.getCode())) {
            throw new RuntimeException("Une habilitation avec le code " + habilitation.getCode() + " existe déjà");
        }

        HabilitationEntity habilitationEntity = new HabilitationEntity();
        habilitationEntity.setCode(habilitation.getCode());
        habilitationEntity.setDescription(habilitation.getDescription());

        HabilitationEntity savedEntity = habilitationRepository.save(habilitationEntity);
        return convertToHabilitation(savedEntity);
    }

    /**
     * Met à jour une habilitation existante
     * 
     * @param code Code de l'habilitation à mettre à jour
     * @param habilitation Nouvelles données de l'habilitation (description uniquement)
     * @return Habilitation mise à jour
     * @throws RuntimeException si l'habilitation n'existe pas
     */
    @Transactional
    public Habilitation updateHabilitation(String code, Habilitation habilitation) {
        HabilitationEntity habilitationEntity = habilitationRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Habilitation non trouvée avec le code: " + code));

        // On ne modifie que la description, pas le code
        habilitationEntity.setDescription(habilitation.getDescription());

        HabilitationEntity updatedEntity = habilitationRepository.save(habilitationEntity);
        return convertToHabilitation(updatedEntity);
    }

    /**
     * Supprime une habilitation
     * 
     * @param code Code de l'habilitation à supprimer
     * @throws RuntimeException si l'habilitation n'existe pas
     */
    @Transactional
    public void deleteHabilitation(String code) {
        if (!habilitationRepository.existsById(code)) {
            throw new RuntimeException("Habilitation non trouvée avec le code: " + code);
        }
        
        // Note: dans un système plus complexe, il faudrait vérifier que l'habilitation 
        // n'est pas utilisée par des utilisateurs avant de la supprimer
        habilitationRepository.deleteById(code);
    }

    /**
     * Convertit une entité habilitation en objet de domaine
     * 
     * @param entity Entité habilitation
     * @return Objet de domaine habilitation
     */
    private Habilitation convertToHabilitation(HabilitationEntity entity) {
        Habilitation habilitation = new Habilitation();
        habilitation.setCode(entity.getCode());
        habilitation.setDescription(entity.getDescription());
        return habilitation;
    }
}