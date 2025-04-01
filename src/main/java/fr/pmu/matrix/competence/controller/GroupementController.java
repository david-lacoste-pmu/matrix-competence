package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Groupement;
import fr.pmu.matrix.competence.dto.CreateGroupementRequest;
import fr.pmu.matrix.competence.dto.UpdateGroupementRequest;
import fr.pmu.matrix.competence.service.GroupementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groupements")
public class GroupementController {

    private final GroupementService groupementService;

    @Autowired
    public GroupementController(GroupementService groupementService) {
        this.groupementService = groupementService;
    }

    /**
     * Récupère tous les groupements
     * 
     * @return Liste des groupements
     */
    @GetMapping
    public ResponseEntity<List<Groupement>> getAllGroupements() {
        List<Groupement> groupements = groupementService.getAllGroupements();
        return ResponseEntity.ok(groupements);
    }

    /**
     * Crée un nouveau groupement
     * 
     * @param createGroupementRequest La requête de création de groupement
     * @return Le groupement créé
     */
    @PostMapping
    public ResponseEntity<Groupement> createGroupement(@RequestBody CreateGroupementRequest createGroupementRequest) {
        try {
            Groupement groupement = new Groupement();
            groupement.setCode(createGroupementRequest.getCode());
            groupement.setLibelle(createGroupementRequest.getLibelle());
            groupement.setDirection(createGroupementRequest.getDirection());
            
            Groupement createdGroupement = groupementService.createGroupement(groupement);
            return new ResponseEntity<>(createdGroupement, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            throw e;
        }
    }

    /**
     * Récupère un groupement par son code
     * 
     * @param code Code unique du groupement
     * @return Le groupement correspondant
     */
    @GetMapping("/{code}")
    public ResponseEntity<Groupement> getGroupementByCode(@PathVariable String code) {
        try {
            Groupement groupement = groupementService.getGroupementByCode(code);
            return ResponseEntity.ok(groupement);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Met à jour un groupement existant
     * 
     * @param code Code du groupement à mettre à jour
     * @param updateGroupementRequest Requête de mise à jour du groupement
     * @return Le groupement mis à jour
     */
    @PutMapping("/{code}")
    public ResponseEntity<Groupement> updateGroupement(
            @PathVariable String code,
            @RequestBody UpdateGroupementRequest updateGroupementRequest) {
        try {
            Groupement groupement = new Groupement();
            groupement.setLibelle(updateGroupementRequest.getLibelle());
            groupement.setDirection(updateGroupementRequest.getDirection());
            
            Groupement updatedGroupement = groupementService.updateGroupement(code, groupement);
            return ResponseEntity.ok(updatedGroupement);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    /**
     * Supprime un groupement
     * 
     * @param code Code du groupement à supprimer
     * @return Réponse sans contenu
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteGroupement(@PathVariable String code) {
        try {
            groupementService.deleteGroupement(code);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}