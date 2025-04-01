package fr.pmu.matrix.competence.controller;

import fr.pmu.matrix.competence.domain.Habilitation;
import fr.pmu.matrix.competence.service.HabilitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HabilitationControllerTest {

    @Mock
    private HabilitationService habilitationService;

    @InjectMocks
    private HabilitationController habilitationController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllHabilitations() {
        List<Habilitation> habilitations = new ArrayList<>();
        habilitations.add(new Habilitation());
        habilitations.add(new Habilitation());

        when(habilitationService.getAllHabilitations()).thenReturn(habilitations);

        ResponseEntity<List<Habilitation>> response = habilitationController.getAllHabilitations();

        assertEquals(2, response.getBody().size());
        verify(habilitationService, times(1)).getAllHabilitations();
    }

    @Test
    void testGetHabilitationByCode() {
        Habilitation habilitation = new Habilitation();
        habilitation.setCode("CODE123");

        when(habilitationService.getHabilitationByCode("CODE123")).thenReturn(habilitation);

        ResponseEntity<Habilitation> response = habilitationController.getHabilitationByCode("CODE123");

        assertEquals(habilitation, response.getBody());
        verify(habilitationService, times(1)).getHabilitationByCode("CODE123");
    }

    @Test
    void testCreateHabilitation() {
        HabilitationController.CreateHabilitationRequest request = new HabilitationController.CreateHabilitationRequest();
        request.setCode("CODE123");
        request.setDescription("Description");

        Habilitation habilitation = new Habilitation();
        habilitation.setCode("CODE123");
        habilitation.setDescription("Description");

        when(habilitationService.createHabilitation(any(Habilitation.class))).thenReturn(habilitation);

        ResponseEntity<Habilitation> response = habilitationController.createHabilitation(request);

        assertEquals(habilitation, response.getBody());
        verify(habilitationService, times(1)).createHabilitation(any(Habilitation.class));
    }

    @Test
    void testUpdateHabilitation() {
        HabilitationController.UpdateHabilitationRequest request = new HabilitationController.UpdateHabilitationRequest();
        request.setDescription("Updated Description");

        Habilitation habilitation = new Habilitation();
        habilitation.setCode("CODE123");
        habilitation.setDescription("Updated Description");

        when(habilitationService.updateHabilitation(eq("CODE123"), any(Habilitation.class))).thenReturn(habilitation);

        ResponseEntity<Habilitation> response = habilitationController.updateHabilitation("CODE123", request);

        assertEquals(habilitation, response.getBody());
        verify(habilitationService, times(1)).updateHabilitation(eq("CODE123"), any(Habilitation.class));
    }

    @Test
    void testDeleteHabilitation() {
        habilitationController.deleteHabilitation("CODE123");
        verify(habilitationService, times(1)).deleteHabilitation("CODE123");
    }
}
