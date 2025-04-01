package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Personne;
import fr.pmu.matrix.competence.dto.CreatePersonneRequest;
import fr.pmu.matrix.competence.dto.UpdatePersonneRequest;
import fr.pmu.matrix.competence.service.PersonneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personnes")
public class PersonneController {

    private final PersonneService personneService;

    @Autowired
    public PersonneController(PersonneService personneService) {
        this.personneService = personneService;
    }

    /**
     * Récupère toutes les personnes
     * 
     * @return Liste des personnes
     */
    @GetMapping
    public ResponseEntity<List<Personne>> getAllPersonnes() {
        List<Personne> personnes = personneService.getAllPersonnes();
        return ResponseEntity.ok(personnes);
    }

    /**
     * Crée une nouvelle personne
     * 
     * @param createPersonneRequest La requête de création de personne
     * @return La personne créée
     */
    @PostMapping
    public ResponseEntity<Personne> createPersonne(@RequestBody CreatePersonneRequest createPersonneRequest) {
        try {
            Personne personne = new Personne();
            personne.setIdentifiant(createPersonneRequest.getIdentifiant());
            personne.setNom(createPersonneRequest.getNom());
            personne.setPrenom(createPersonneRequest.getPrenom());
            personne.setPoste(createPersonneRequest.getPoste());
            
            Personne createdPersonne = personneService.createPersonne(personne, createPersonneRequest.getEquipeId());
            return new ResponseEntity<>(createdPersonne, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            throw e;
        }
    }

    /**
     * Récupère une personne par son identifiant
     * 
     * @param identifiant Identifiant unique de la personne
     * @return La personne correspondante
     */
    @GetMapping("/{identifiant}")
    public ResponseEntity<Personne> getPersonneByIdentifiant(@PathVariable String identifiant) {
        try {
            Personne personne = personneService.getPersonneByIdentifiant(identifiant);
            return ResponseEntity.ok(personne);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Met à jour une personne existante
     * 
     * @param identifiant Identifiant de la personne à mettre à jour
     * @param updatePersonneRequest Requête de mise à jour de la personne
     * @return La personne mise à jour
     */
    @PutMapping("/{identifiant}")
    public ResponseEntity<Personne> updatePersonne(
            @PathVariable String identifiant,
            @RequestBody UpdatePersonneRequest updatePersonneRequest) {
        try {
            Personne personne = new Personne();
            personne.setNom(updatePersonneRequest.getNom());
            personne.setPrenom(updatePersonneRequest.getPrenom());
            personne.setPoste(updatePersonneRequest.getPoste());
            
            Personne updatedPersonne = personneService.updatePersonne(
                    identifiant, personne, updatePersonneRequest.getEquipeId());
            
            return ResponseEntity.ok(updatedPersonne);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvée")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    /**
     * Supprime une personne
     * 
     * @param identifiant Identifiant de la personne à supprimer
     * @return Réponse sans contenu
     */
    @DeleteMapping("/{identifiant}")
    public ResponseEntity<Void> deletePersonne(@PathVariable String identifiant) {
        try {
            personneService.deletePersonne(identifiant);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}