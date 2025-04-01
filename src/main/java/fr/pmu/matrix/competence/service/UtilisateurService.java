package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Utilisateur;
import fr.pmu.matrix.competence.domain.Habilitation;
import fr.pmu.matrix.competence.entity.UtilisateurEntity;
import fr.pmu.matrix.competence.entity.HabilitationEntity;
import fr.pmu.matrix.competence.repository.UtilisateurRepository;
import fr.pmu.matrix.competence.repository.HabilitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs et leurs habilitations
 */
@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final HabilitationRepository habilitationRepository;

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository, HabilitationRepository habilitationRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.habilitationRepository = habilitationRepository;
    }

    /**
     * Récupère tous les utilisateurs
     * 
     * @return Liste des utilisateurs
     */
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll()
                .stream()
                .map(this::convertToUtilisateur)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un utilisateur par son matricule
     * 
     * @param matricule Matricule unique de l'utilisateur
     * @return Utilisateur correspondant au matricule
     * @throws RuntimeException si l'utilisateur n'existe pas
     */
    public Utilisateur getUtilisateurByMatricule(String matricule) {
        UtilisateurEntity utilisateurEntity = utilisateurRepository.findById(matricule)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le matricule: " + matricule));
        return convertToUtilisateur(utilisateurEntity);
    }

    /**
     * Crée un nouvel utilisateur avec ses habilitations
     * 
     * @param utilisateur Utilisateur à créer (contient le matricule)
     * @param habilitationsIds Liste des codes d'habilitations à associer
     * @return Utilisateur créé avec ses habilitations
     * @throws RuntimeException si un utilisateur avec le même matricule existe déjà
     * @throws RuntimeException si une des habilitations n'existe pas
     */
    @Transactional
    public Utilisateur createUtilisateur(Utilisateur utilisateur, List<String> habilitationsIds) {
        // Vérifier si l'utilisateur existe déjà
        if (utilisateurRepository.existsById(utilisateur.getMatricule())) {
            throw new RuntimeException("Un utilisateur avec le matricule " + utilisateur.getMatricule() + " existe déjà");
        }

        // Créer l'entité utilisateur
        UtilisateurEntity utilisateurEntity = new UtilisateurEntity();
        utilisateurEntity.setMatricule(utilisateur.getMatricule());
        
        // Associer les habilitations si fournies
        if (habilitationsIds != null && !habilitationsIds.isEmpty()) {
            List<HabilitationEntity> habilitations = habilitationRepository.findAllById(habilitationsIds);
            
            // Vérifier que toutes les habilitations ont été trouvées
            if (habilitations.size() != habilitationsIds.size()) {
                throw new RuntimeException("Une ou plusieurs habilitations demandées n'existent pas");
            }
            
            utilisateurEntity.setHabilitations(habilitations);
        } else {
            utilisateurEntity.setHabilitations(new ArrayList<>());
        }

        // Sauvegarder l'utilisateur
        UtilisateurEntity savedEntity = utilisateurRepository.save(utilisateurEntity);
        return convertToUtilisateur(savedEntity);
    }

    /**
     * Met à jour les habilitations d'un utilisateur existant
     * 
     * @param matricule Matricule unique de l'utilisateur
     * @param habilitationsIds Nouveaux codes d'habilitations à associer
     * @return Utilisateur mis à jour avec ses nouvelles habilitations
     * @throws RuntimeException si l'utilisateur n'existe pas
     * @throws RuntimeException si une des habilitations n'existe pas
     */
    @Transactional
    public Utilisateur updateUtilisateurHabilitations(String matricule, List<String> habilitationsIds) {
        // Récupérer l'utilisateur
        UtilisateurEntity utilisateurEntity = utilisateurRepository.findById(matricule)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le matricule: " + matricule));

        // Vérifier que la liste d'habilitations n'est pas null
        if (habilitationsIds == null) {
            throw new RuntimeException("La liste des habilitations ne peut pas être nulle");
        }

        // Récupérer les entités d'habilitation
        List<HabilitationEntity> habilitationEntities = habilitationRepository.findAllById(habilitationsIds);
        
        // Vérifier que toutes les habilitations ont été trouvées
        if (habilitationEntities.size() != habilitationsIds.size()) {
            throw new RuntimeException("Une ou plusieurs habilitations demandées n'existent pas");
        }
        
        // Mettre à jour les habilitations de l'utilisateur
        utilisateurEntity.setHabilitations(habilitationEntities);

        // Sauvegarder les modifications
        UtilisateurEntity updatedEntity = utilisateurRepository.save(utilisateurEntity);
        return convertToUtilisateur(updatedEntity);
    }

    /**
     * Supprime un utilisateur
     * 
     * @param matricule Matricule de l'utilisateur à supprimer
     * @throws RuntimeException si l'utilisateur n'existe pas
     */
    @Transactional
    public void deleteUtilisateur(String matricule) {
        // Vérifier que l'utilisateur existe
        if (!utilisateurRepository.existsById(matricule)) {
            throw new RuntimeException("Utilisateur non trouvé avec le matricule: " + matricule);
        }
        // Supprimer l'utilisateur
        utilisateurRepository.deleteById(matricule);
    }

    /**
     * Convertit une entité utilisateur en objet de domaine
     * 
     * @param entity Entité utilisateur JPA
     * @return Objet de domaine utilisateur
     */
    private Utilisateur convertToUtilisateur(UtilisateurEntity entity) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMatricule(entity.getMatricule());
        
        // Convertir les habilitations
        List<Habilitation> habilitations = entity.getHabilitations().stream()
                .map(habilitationEntity -> {
                    Habilitation habilitation = new Habilitation();
                    habilitation.setCode(habilitationEntity.getCode());  // Le code est l'id dans l'API
                    habilitation.setDescription(habilitationEntity.getDescription());
                    return habilitation;
                })
                .collect(Collectors.toList());
        
        utilisateur.setHabilitations(habilitations);
        return utilisateur;
    }
}