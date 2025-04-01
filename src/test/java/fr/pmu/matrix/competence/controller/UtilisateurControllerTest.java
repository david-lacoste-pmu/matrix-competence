package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Utilisateur;
import fr.pmu.matrix.competence.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UtilisateurControllerTest {

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private UtilisateurController utilisateurController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUtilisateurs() {
        // Préparer les données de test
        List<Utilisateur> utilisateurs = new ArrayList<>();
        Utilisateur utilisateur1 = new Utilisateur();
        utilisateur1.setMatricule("MAT001");
        Utilisateur utilisateur2 = new Utilisateur();
        utilisateur2.setMatricule("MAT002");
        utilisateurs.add(utilisateur1);
        utilisateurs.add(utilisateur2);

        // Configurer le mock
        when(utilisateurService.getAllUtilisateurs()).thenReturn(utilisateurs);

        // Exécuter la méthode à tester
        ResponseEntity<List<Utilisateur>> response = utilisateurController.getAllUtilisateurs();

        // Vérifier les résultats
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(utilisateurService, times(1)).getAllUtilisateurs();
    }

    @Test
    void testGetUtilisateurByMatricule() {
        // Préparer les données de test
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMatricule("MAT001");

        // Configurer le mock
        when(utilisateurService.getUtilisateurByMatricule("MAT001")).thenReturn(utilisateur);

        // Exécuter la méthode à tester
        ResponseEntity<Utilisateur> response = utilisateurController.getUtilisateurByMatricule("MAT001");

        // Vérifier les résultats
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("MAT001", response.getBody().getMatricule());
        verify(utilisateurService, times(1)).getUtilisateurByMatricule("MAT001");
    }

    @Test
    void testCreateUtilisateur() {
        // Préparer les données de test
        UtilisateurController.CreateUtilisateurRequest request = new UtilisateurController.CreateUtilisateurRequest();
        request.setMatricule("MAT001");
        request.setHabilitationsIds(Arrays.asList("HAB1", "HAB2"));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMatricule("MAT001");

        // Configurer le mock
        when(utilisateurService.createUtilisateur(any(Utilisateur.class), anyList())).thenReturn(utilisateur);

        // Exécuter la méthode à tester
        ResponseEntity<Utilisateur> response = utilisateurController.createUtilisateur(request);

        // Vérifier les résultats
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("MAT001", response.getBody().getMatricule());
        verify(utilisateurService, times(1)).createUtilisateur(any(Utilisateur.class), eq(Arrays.asList("HAB1", "HAB2")));
    }

    @Test
    void testUpdateUtilisateurHabilitations() {
        // Préparer les données de test
        UtilisateurController.UpdateUtilisateurRequest request = new UtilisateurController.UpdateUtilisateurRequest();
        request.setHabilitationsIds(Arrays.asList("HAB3", "HAB4"));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMatricule("MAT001");

        // Configurer le mock
        when(utilisateurService.updateUtilisateurHabilitations(eq("MAT001"), anyList())).thenReturn(utilisateur);

        // Exécuter la méthode à tester
        ResponseEntity<Utilisateur> response = utilisateurController.updateUtilisateurHabilitations("MAT001", request);

        // Vérifier les résultats
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("MAT001", response.getBody().getMatricule());
        verify(utilisateurService, times(1)).updateUtilisateurHabilitations(eq("MAT001"), eq(Arrays.asList("HAB3", "HAB4")));
    }

    @Test
    void testDeleteUtilisateur() {
        // Exécuter la méthode à tester
        ResponseEntity<Void> response = utilisateurController.deleteUtilisateur("MAT001");

        // Vérifier les résultats
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(utilisateurService, times(1)).deleteUtilisateur("MAT001");
    }
}