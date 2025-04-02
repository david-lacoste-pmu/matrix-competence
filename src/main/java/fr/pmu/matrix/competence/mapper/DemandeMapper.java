package fr.pmu.matrix.competence.mapper;

import fr.pmu.matrix.competence.domain.*;
import fr.pmu.matrix.competence.entity.*;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre les entités DemandeEntity et les objets domaine Demande
 */
@Component
public class DemandeMapper {

    /**
     * Convertit une entité DemandeEntity en objet domain Demande
     * @param entity L'entité à convertir
     * @param destinationObject L'objet destination (Equipe ou Groupement) à utiliser
     * @return L'objet domain correspondant
     */
    public Demande mapToDemandeDomain(DemandeEntity entity, Object destinationObject) {
        if (entity == null) {
            return null;
        }

        Demande demande = new Demande();
        demande.setId(entity.getId());
        demande.setMatriculeDemandeur(entity.getMatriculeDemandeur());
        demande.setDescription(entity.getDescription());
        demande.setNature(entity.getNature());
        demande.setDateDebut(entity.getDateDebut());
        demande.setDateFin(entity.getDateFin());

        // Conversion des compétences requises
        for (CompetenceRequiseEntity crEntity : entity.getCompetencesRequises()) {
            Competence competence = new Competence();
            competence.setLibelle(crEntity.getCompetence().getLibelle());
            competence.setDescription(crEntity.getCompetence().getDescription());

            Note note = new Note();
            note.setValeur(crEntity.getNoteRequise().getValeur());
            note.setLibelle(crEntity.getNoteRequise().getLibelle());

            CompetenceRequise competenceRequise = new CompetenceRequise(competence, note);
            demande.getCompetencesRecherchees().add(competenceRequise);
        }

        // Conversion de la destination
        if (destinationObject != null) {
            if (entity.getEstGroupement() && destinationObject instanceof GroupementEntity) {
                GroupementEntity groupementEntity = (GroupementEntity) destinationObject;
                Groupement groupement = new Groupement();
                groupement.setCode(groupementEntity.getCode());
                groupement.setLibelle(groupementEntity.getLibelle());
                groupement.setDirection(groupementEntity.getDirection());
                demande.setDestination(new DestinationGroupement(groupement));
            } else if (!entity.getEstGroupement() && destinationObject instanceof EquipeEntity) {
                EquipeEntity equipeEntity = (EquipeEntity) destinationObject;
                Equipe equipe = new Equipe();
                equipe.setCode(equipeEntity.getCode());
                equipe.setNom(equipeEntity.getNom());
                equipe.setDescription(equipeEntity.getDescription());
                demande.setDestination(new DestinationEquipe(equipe));
            }
        }

        return demande;
    }
}