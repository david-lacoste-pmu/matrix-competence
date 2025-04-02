package fr.pmu.matrix.competence.service;

import fr.pmu.matrix.competence.domain.Competence;
import fr.pmu.matrix.competence.domain.MatriceCompetence;
import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.domain.Profil;
import fr.pmu.matrix.competence.entity.PersonneEntity;
import fr.pmu.matrix.competence.entity.ProfilEntity;
import fr.pmu.matrix.competence.repository.MatriceCompetenceRepository;
import fr.pmu.matrix.competence.repository.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DemandeurService {

    private final ProfilRepository profilRepository;
    private final MatriceCompetenceService matriceCompetenceService;
    private final PersonneService personneService;
    private final MatriceCompetenceRepository matriceCompetenceRepository;

    @Autowired
    public DemandeurService(
            ProfilRepository profilRepository,
            MatriceCompetenceService matriceCompetenceService,
            PersonneService personneService,
            MatriceCompetenceRepository matriceCompetenceRepository) {
        this.profilRepository = profilRepository;
        this.matriceCompetenceService = matriceCompetenceService;
        this.personneService = personneService;
        this.matriceCompetenceRepository = matriceCompetenceRepository;
    }

    /**
     * Récupère toutes les personnes disponibles sur le marché (sortantes ou cherchant un nouveau challenge)
     * @return Liste des personnes disponibles avec leurs profils
     */
    public List<Profil> getPersonnesDisponibles() {
        Date now = new Date();
        // Récupération des profils dont la période de disponibilité inclut la date actuelle
        List<ProfilEntity> profilsEntities = profilRepository.findAll().stream()
                .filter(profil -> 
                    (profil.getDateDebutDisponibilite() == null || profil.getDateDebutDisponibilite().before(now)) &&
                    (profil.getDateFinDisponibilite() == null || profil.getDateFinDisponibilite().after(now)))
                .collect(Collectors.toList());
        
        return profilsEntities.stream()
                .map(this::mapToProfilDomain)
                .collect(Collectors.toList());
    }

    /**
     * Récupère la matrice de compétence complète pour une personne sur le marché
     * @param personneId Identifiant de la personne
     * @return Liste des évaluations de compétence pour cette personne
     * @throws RuntimeException Si la personne n'existe pas ou n'est pas disponible
     */
    public List<MatriceCompetence> getMatriceCompetencePersonneDisponible(String personneId) {
        // Vérifier si la personne est disponible sur le marché
        Date now = new Date();
        boolean isDisponible = profilRepository.findByPersonneIdentifiant(personneId).stream()
                .anyMatch(profil ->
                    (profil.getDateDebutDisponibilite() == null || profil.getDateDebutDisponibilite().before(now)) &&
                    (profil.getDateFinDisponibilite() == null || profil.getDateFinDisponibilite().after(now)));
        
        if (!isDisponible) {
            throw new RuntimeException("La personne n'est pas disponible actuellement");
        }
        
        // Récupérer toutes les compétences de la personne
        return matriceCompetenceService.getCompetencesByPersonne(personneId);
    }

    /**
     * Filtre les personnes disponibles selon un niveau de note minimal pour certaines compétences
     * @param competences Liste des libellés de compétences à filtrer
     * @param noteMinimale Note minimale requise pour ces compétences
     * @return Liste des personnes répondant aux critères
     */
    public List<Personne> filtrerPersonnesParNotes(List<String> competences, int noteMinimale) {
        List<Personne> personnesFiltered = new ArrayList<>();
        List<Profil> personnesDisponibles = getPersonnesDisponibles();
        
        for (Profil profil : personnesDisponibles) {
            Personne personne = profil.getPersonne();
            List<MatriceCompetence> matrices = matriceCompetenceService.getCompetencesByPersonne(personne.getIdentifiant());
            
            // Vérifier si la personne possède toutes les compétences avec la note minimale requise
            boolean matchAllCriteria = true;
            for (String competenceLibelle : competences) {
                boolean hasCompetenceWithMinNote = matrices.stream()
                    .anyMatch(mc -> mc.getCompetence().getLibelle().equals(competenceLibelle) 
                        && mc.getNote().getValeur() >= noteMinimale);
                
                if (!hasCompetenceWithMinNote) {
                    matchAllCriteria = false;
                    break;
                }
            }
            
            if (matchAllCriteria) {
                personnesFiltered.add(personne);
            }
        }
        
        return personnesFiltered;
    }

    /**
     * Filtre les personnes disponibles selon des compétences spécifiques recherchées
     * @param competencesRecherchees Liste des libellés de compétences recherchées
     * @return Liste des personnes ayant ces compétences
     */
    public List<Personne> filtrerPersonnesParCompetences(List<String> competencesRecherchees) {
        List<Personne> personnesFiltered = new ArrayList<>();
        List<Profil> personnesDisponibles = getPersonnesDisponibles();
        
        for (Profil profil : personnesDisponibles) {
            Personne personne = profil.getPersonne();
            List<MatriceCompetence> matrices = matriceCompetenceService.getCompetencesByPersonne(personne.getIdentifiant());
            
            // Extraire les libellés de compétences de la personne
            List<String> competencesPersonne = matrices.stream()
                .map(mc -> mc.getCompetence().getLibelle())
                .collect(Collectors.toList());
            
            // Vérifier si la personne possède toutes les compétences recherchées
            boolean hasAllCompetences = competencesRecherchees.stream()
                .allMatch(competencesPersonne::contains);
            
            if (hasAllCompetences) {
                personnesFiltered.add(personne);
            }
        }
        
        return personnesFiltered;
    }
    
    /**
     * Convertit une entité Profil en objet domain
     * @param entity L'entité à convertir
     * @return L'objet domain correspondant
     */
    private Profil mapToProfilDomain(ProfilEntity entity) {
        Profil profil = new Profil();
        profil.setRapporteur(entity.getRapporteur());
        profil.setDateDebutDisponibilite(entity.getDateDebutDisponibilite());
        profil.setDateFinDisponibilite(entity.getDateFinDisponibilite());
        
        // Convertir l'entité personne en objet domain
        Personne personne = personneService.getPersonneByIdentifiant(entity.getPersonne().getIdentifiant());
        profil.setPersonne(personne);
        
        return profil;
    }
}