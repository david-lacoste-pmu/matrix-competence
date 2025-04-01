package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Equipe;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.entity.EquipeEntity;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.repository.EquipeRepository;
import fr.pmu.matrix.competence.repository.PersonneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonneService {

    private final PersonneRepository personneRepository;
    private final EquipeRepository equipeRepository;

    @Autowired
    public PersonneService(PersonneRepository personneRepository, EquipeRepository equipeRepository) {
        this.personneRepository = personneRepository;
        this.equipeRepository = equipeRepository;
    }

    /**
     * Récupère toutes les personnes
     * @return Liste des personnes
     */
    public List<Personne> getAllPersonnes() {
        return personneRepository.findAll()
                .stream()
                .map(this::mapToPersonneDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une personne par son identifiant
     * @param identifiant Identifiant unique de la personne
     * @return La personne correspondante
     */
    public Personne getPersonneByIdentifiant(String identifiant) {
        return personneRepository.findById(identifiant)
                .map(this::mapToPersonneDomain)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'identifiant: " + identifiant));
    }

    /**
     * Crée une nouvelle personne
     * @param personne La personne à créer
     * @param equipeId Identifiant de l'équipe (optionnel)
     * @return La personne créée
     */
    @Transactional
    public Personne createPersonne(Personne personne, String equipeId) {
        // Vérifier si une personne avec cet identifiant existe déjà
        if (personneRepository.existsById(personne.getIdentifiant())) {
            throw new RuntimeException("Une personne avec cet identifiant existe déjà: " + personne.getIdentifiant());
        }
        
        PersonneEntity personneEntity = new PersonneEntity();
        personneEntity.setIdentifiant(personne.getIdentifiant());
        personneEntity.setNom(personne.getNom());
        personneEntity.setPrenom(personne.getPrenom());
        personneEntity.setPoste(personne.getPoste());
        
        // Attribuer l'équipe si un identifiant d'équipe est fourni
        if (equipeId != null && !equipeId.isEmpty()) {
            EquipeEntity equipeEntity = equipeRepository.findById(equipeId)
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec l'identifiant: " + equipeId));
            personneEntity.setEquipe(equipeEntity);
        }
        
        personneEntity = personneRepository.save(personneEntity);
        return mapToPersonneDomain(personneEntity);
    }

    /**
     * Met à jour une personne existante
     * @param identifiant Identifiant de la personne à mettre à jour
     * @param personne Nouvelles données de la personne
     * @param equipeId Identifiant de l'équipe (optionnel)
     * @return La personne mise à jour
     */
    @Transactional
    public Personne updatePersonne(String identifiant, Personne personne, String equipeId) {
        PersonneEntity personneEntity = personneRepository.findById(identifiant)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'identifiant: " + identifiant));
        
        // Mettre à jour les champs
        if (personne.getNom() != null) {
            personneEntity.setNom(personne.getNom());
        }
        if (personne.getPrenom() != null) {
            personneEntity.setPrenom(personne.getPrenom());
        }
        if (personne.getPoste() != null) {
            personneEntity.setPoste(personne.getPoste());
        }
        
        // Mettre à jour l'équipe si un identifiant d'équipe est fourni
        if (equipeId != null) {
            if (equipeId.isEmpty()) {
                personneEntity.setEquipe(null);
            } else {
                EquipeEntity equipeEntity = equipeRepository.findById(equipeId)
                        .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec l'identifiant: " + equipeId));
                personneEntity.setEquipe(equipeEntity);
            }
        }
        
        personneEntity = personneRepository.save(personneEntity);
        return mapToPersonneDomain(personneEntity);
    }

    /**
     * Supprime une personne
     * @param identifiant Identifiant de la personne à supprimer
     */
    @Transactional
    public void deletePersonne(String identifiant) {
        if (!personneRepository.existsById(identifiant)) {
            throw new RuntimeException("Personne non trouvée avec l'identifiant: " + identifiant);
        }
        personneRepository.deleteById(identifiant);
    }

    /**
     * Convertit une entité personne en objet domain
     * @param entity L'entité à convertir
     * @return L'objet domain correspondant
     */
    private Personne mapToPersonneDomain(PersonneEntity entity) {
        Personne personne = new Personne();
        personne.setIdentifiant(entity.getIdentifiant());
        personne.setNom(entity.getNom());
        personne.setPrenom(entity.getPrenom());
        personne.setPoste(entity.getPoste());
        
        // Convertir l'équipe si elle existe
        if (entity.getEquipe() != null) {
            Equipe equipe = new Equipe();
            equipe.setCode(entity.getEquipe().getCode());
            equipe.setNom(entity.getEquipe().getNom());
            equipe.setDescription(entity.getEquipe().getDescription());
            personne.setEquipe(equipe);
        }
        
        return personne;
    }
}