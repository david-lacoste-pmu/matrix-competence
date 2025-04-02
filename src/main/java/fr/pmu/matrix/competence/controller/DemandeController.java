package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.*;
import fr.pmu.matrix.competence.dto.CompetenceRequiseRequest;
import fr.pmu.matrix.competence.dto.DemandeCreationRequest;
import fr.pmu.matrix.competence.dto.DemandeUpdateRequest;
import fr.pmu.matrix.competence.dto.DestinationRequest;
import fr.pmu.matrix.competence.service.CompetenceService;
import fr.pmu.matrix.competence.service.DemandeService;
import fr.pmu.matrix.competence.service.EquipeService;
import fr.pmu.matrix.competence.service.GroupementService;
import fr.pmu.matrix.competence.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des demandes de compétences.
 * Implémente les opérations CRUD définies dans l'API OpenAPI.
 */
@RestController
@RequestMapping("/demandes")
public class DemandeController {

    private final DemandeService demandeService;
    private final CompetenceService competenceService;
    private final NoteService noteService;
    private final EquipeService equipeService;
    private final GroupementService groupementService;

    @Autowired
    public DemandeController(DemandeService demandeService, 
                            CompetenceService competenceService,
                            NoteService noteService,
                            EquipeService equipeService,
                            GroupementService groupementService) {
        this.demandeService = demandeService;
        this.competenceService = competenceService;
        this.noteService = noteService;
        this.equipeService = equipeService;
        this.groupementService = groupementService;
    }

    /**
     * Liste toutes les demandes avec filtres optionnels
     */
    @GetMapping
    public ResponseEntity<List<Demande>> getAllDemandes(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String matricule, 
            @RequestParam(required = false) String competence) {
        
        try {
            List<Demande> demandes;
            
            if (active != null && active) {
                demandes = demandeService.getDemandesActiveAtDate(new Date());
            } else if (matricule != null && !matricule.isEmpty()) {
                demandes = demandeService.getDemandesByDemandeur(matricule);
            } else if (competence != null && !competence.isEmpty()) {
                demandes = demandeService.getDemandesByCompetence(competence);
            } else {
                demandes = demandeService.getAllDemandes();
            }
            
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Erreur lors de la récupération des demandes: " + e.getMessage());
        }
    }

    /**
     * Récupère une demande par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Demande> getDemandeById(@PathVariable String id) {
        try {
            Demande demande = demandeService.getDemandeById(id);
            return ResponseEntity.ok(demande);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Demande non trouvée avec l'ID: " + id);
        }
    }

    /**
     * Crée une nouvelle demande
     */
    @PostMapping
    public ResponseEntity<Demande> createDemande(@RequestBody DemandeCreationRequest request) {
        try {
            Demande demande = mapToDemandeFromCreationRequest(request);
            Demande createdDemande = demandeService.createDemande(demande);
            return new ResponseEntity<>(createdDemande, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("existe déjà")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
    }

    /**
     * Met à jour une demande existante
     */
    @PutMapping("/{id}")
    public ResponseEntity<Demande> updateDemande(@PathVariable String id, @RequestBody DemandeUpdateRequest request) {
        try {
            Demande demande = mapToDemandeFromUpdateRequest(request);
            Demande updatedDemande = demandeService.updateDemande(id, demande);
            return ResponseEntity.ok(updatedDemande);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvée")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
    }

    /**
     * Supprime une demande
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemande(@PathVariable String id) {
        try {
            demandeService.deleteDemande(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Demande non trouvée avec l'ID: " + id);
        }
    }

    /**
     * Liste les demandes actives à une date donnée
     */
    @GetMapping("/active/{date}")
    public ResponseEntity<List<Demande>> getDemandesActiveAtDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        try {
            List<Demande> demandes = demandeService.getDemandesActiveAtDate(date);
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Format de date invalide ou erreur lors de la récupération: " + e.getMessage());
        }
    }

    /**
     * Convertit une requête de création en objet Demande
     */
    private Demande mapToDemandeFromCreationRequest(DemandeCreationRequest request) {
        Demande demande = new Demande();
        demande.setMatriculeDemandeur(request.getMatriculeDemandeur());
        demande.setDescription(request.getDescription());
        demande.setNature(request.getNature());
        demande.setDateDebut(request.getDateDebut());
        demande.setDateFin(request.getDateFin());
        
        // Création de la destination
        setDestinationFromRequest(demande, request.getDestination());
        
        // Création des compétences requises
        setCompetencesRequisesFromRequest(demande, request.getCompetencesRecherchees());
        
        return demande;
    }

    /**
     * Convertit une requête de mise à jour en objet Demande
     */
    private Demande mapToDemandeFromUpdateRequest(DemandeUpdateRequest request) {
        Demande demande = new Demande();
        demande.setMatriculeDemandeur(request.getMatriculeDemandeur());
        demande.setDescription(request.getDescription());
        demande.setNature(request.getNature());
        demande.setDateDebut(request.getDateDebut());
        demande.setDateFin(request.getDateFin());
        
        // Création de la destination
        setDestinationFromRequest(demande, request.getDestination());
        
        // Création des compétences requises
        setCompetencesRequisesFromRequest(demande, request.getCompetencesRecherchees());
        
        return demande;
    }
    
    /**
     * Configure la destination de la demande
     */
    private void setDestinationFromRequest(Demande demande, DestinationRequest destinationRequest) {
        if (destinationRequest != null) {
            if ("EQUIPE".equals(destinationRequest.getType())) {
                Equipe equipe = equipeService.getEquipeByCode(destinationRequest.getCode());
                demande.setDestination(new DestinationEquipe(equipe));
            } else if ("GROUPEMENT".equals(destinationRequest.getType())) {
                Groupement groupement = groupementService.getGroupementByCode(destinationRequest.getCode());
                demande.setDestination(new DestinationGroupement(groupement));
            } else {
                throw new IllegalArgumentException("Type de destination non supporté: " + destinationRequest.getType());
            }
        } else {
            throw new IllegalArgumentException("La destination est requise");
        }
    }
    
    /**
     * Configure les compétences requises pour la demande
     */
    private void setCompetencesRequisesFromRequest(Demande demande, List<CompetenceRequiseRequest> competencesRequises) {
        if (competencesRequises != null && !competencesRequises.isEmpty()) {
            List<CompetenceRequise> competenceRequiseList = new ArrayList<>();
            
            for (CompetenceRequiseRequest crRequest : competencesRequises) {
                Competence competence = competenceService.getCompetenceByLibelle(crRequest.getCompetenceLibelle());
                Note note = noteService.getNoteByValeur(crRequest.getNoteValeur());
                
                CompetenceRequise competenceRequise = new CompetenceRequise(competence, note);
                competenceRequiseList.add(competenceRequise);
            }
            
            demande.setCompetencesRecherchees(competenceRequiseList);
        } else {
            throw new IllegalArgumentException("Au moins une compétence requise est nécessaire");
        }
    }
}