package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.entity.CompetenceEntity;
import fr.pmu.matrix.competence.repository.CompetenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompetenceService {

    private final CompetenceRepository competenceRepository;

    @Autowired
    public CompetenceService(CompetenceRepository competenceRepository) {
        this.competenceRepository = competenceRepository;
    }

    /**
     * Récupère toutes les compétences
     * @return Liste des compétences
     */
    public List<Competence> getAllCompetences() {
        return competenceRepository.findAll().stream()
                .map(this::mapToCompetenceDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une compétence par son libellé
     * @param libelle Libellé de la compétence
     * @return La compétence correspondante
     * @throws RuntimeException Si la compétence n'existe pas
     */
    public Competence getCompetenceByLibelle(String libelle) {
        CompetenceEntity competenceEntity = competenceRepository.findById(libelle)
                .orElseThrow(() -> new RuntimeException("Compétence non trouvée avec le libellé: " + libelle));
                
        return mapToCompetenceDomain(competenceEntity);
    }

    /**
     * Crée une nouvelle compétence
     * @param competence La compétence à créer
     * @return La compétence créée
     * @throws RuntimeException Si une compétence avec ce libellé existe déjà
     */
    @Transactional
    public Competence createCompetence(Competence competence) {
        // Vérifier si une compétence avec ce libellé existe déjà
        if (competenceRepository.existsById(competence.getLibelle())) {
            throw new RuntimeException("Une compétence avec ce libellé existe déjà: " + competence.getLibelle());
        }
        
        CompetenceEntity competenceEntity = new CompetenceEntity();
        competenceEntity.setLibelle(competence.getLibelle());
        competenceEntity.setDescription(competence.getDescription());
        
        competenceEntity = competenceRepository.save(competenceEntity);
        return mapToCompetenceDomain(competenceEntity);
    }

    /**
     * Met à jour une compétence existante
     * @param libelle Libellé de la compétence à mettre à jour
     * @param competence Nouvelles données de la compétence
     * @return La compétence mise à jour
     * @throws RuntimeException Si la compétence n'existe pas
     */
    @Transactional
    public Competence updateCompetence(String libelle, Competence competence) {
        CompetenceEntity competenceEntity = competenceRepository.findById(libelle)
                .orElseThrow(() -> new RuntimeException("Compétence non trouvée avec le libellé: " + libelle));
        
        // On ne met à jour que la description, pas le libellé
        competenceEntity.setDescription(competence.getDescription());
        
        competenceEntity = competenceRepository.save(competenceEntity);
        return mapToCompetenceDomain(competenceEntity);
    }

    /**
     * Supprime une compétence
     * @param libelle Libellé de la compétence à supprimer
     * @throws RuntimeException Si la compétence n'existe pas
     */
    @Transactional
    public void deleteCompetence(String libelle) {
        if (!competenceRepository.existsById(libelle)) {
            throw new RuntimeException("Compétence non trouvée avec le libellé: " + libelle);
        }
        competenceRepository.deleteById(libelle);
    }

    /**
     * Convertit une entité compétence en objet domain
     * @param entity L'entité à convertir
     * @return L'objet domain correspondant
     */
    private Competence mapToCompetenceDomain(CompetenceEntity entity) {
        Competence competence = new Competence();
        competence.setLibelle(entity.getLibelle());
        competence.setDescription(entity.getDescription());
        return competence;
    }
}