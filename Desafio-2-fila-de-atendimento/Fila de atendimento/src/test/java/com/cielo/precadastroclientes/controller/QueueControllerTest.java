package com.cielo.precadastroclientes.controller;

import com.cielo.precadastroclientes.DTO.ClientePfResponseDTO;
import com.cielo.precadastroclientes.DTO.GenericResponseDTO;
import com.cielo.precadastroclientes.model.ClientePf;
import com.cielo.precadastroclientes.queue.FilaDeAtendimento;
import com.cielo.precadastroclientes.service.ClientePfService;
import com.cielo.precadastroclientes.service.ClientePjService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QueueControllerTest {

    @Mock
    private FilaDeAtendimento filaDeAtendimento;

    @Mock
    private ClientePfService clientePfService;

    @Mock
    private ClientePjService clientePjService;

    private QueueController queueController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        queueController = new QueueController(filaDeAtendimento, clientePfService, clientePjService);
    }

    @Test
    public void testRetirarProximoClienteDaFila() {
        when(filaDeAtendimento.getTamanho()).thenReturn(1);
        when(filaDeAtendimento.proximoCliente()).thenReturn("12345678901"); // CPF válido
        when(clientePfService.consultarClientePorCpf("12345678901")).thenReturn(new ClientePf());

        ResponseEntity<GenericResponseDTO<?>> response = queueController.retirarProximoClienteDaFila();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getData() instanceof ClientePfResponseDTO);
        //assertNull(response.getBody().getError());
    }

    @Test
    public void testVisualizarFilaDeAtendimento() {
        when(filaDeAtendimento.obterConteudoDaFila()).thenReturn(new String[]{"12345678901", "98765432109"});

        ResponseEntity<?> response = queueController.visualizarFilaDeAtendimento();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Iterable);
    }
}
