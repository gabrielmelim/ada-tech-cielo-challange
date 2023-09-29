package com.cielo.precadastroclientes.controller;

import com.cielo.precadastroclientes.DTO.ClientePjRequestDTO;
import com.cielo.precadastroclientes.DTO.ClientePjResponseDTO;
import com.cielo.precadastroclientes.exception.ClienteException;
import com.cielo.precadastroclientes.model.ClientePj;
import com.cielo.precadastroclientes.queue.FilaDeAtendimento;
import com.cielo.precadastroclientes.service.ClientePjServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientePjControllerTest {

    @Mock
    private ClientePjServiceImpl clientePjService;

    @Mock
    private FilaDeAtendimento filaDeAtendimento;

    @InjectMocks
    private ClientePjController clientePjController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCadastrarClientePjSucesso() {
        // Configurar objetos simulados e comportamentos
        ClientePjRequestDTO clientePjRequestDTO = new ClientePjRequestDTO(/* Preencha com os dados necessários */);
        when(clientePjService.convertRequestDTOToEntity(clientePjRequestDTO)).thenReturn(new ClientePj());
        when(clientePjService.cadastrarClientePj(any())).thenReturn(new ClientePj());
        doNothing().when(filaDeAtendimento).adicionarClienteNaFila(anyString());

        // Chamar o método e verificar os resultados
        ResponseEntity<ClientePjResponseDTO> responseEntity = clientePjController.cadastrarClientePj(clientePjRequestDTO);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Cliente cadastrado com sucesso", responseEntity.getBody().getMessage());
    }

    @Test
    public void testCadastrarClientePjClienteExistente() {
        // Configurar objetos simulados e comportamentos
        ClientePjRequestDTO clientePjRequestDTO = new ClientePjRequestDTO(/* Preencha com os dados necessários */);
        when(clientePjService.convertRequestDTOToEntity(clientePjRequestDTO)).thenReturn(new ClientePj());
        when(clientePjService.cadastrarClientePj(any())).thenThrow(new ClienteException.ClienteExistenteException("Cliente já cadastrado"));

        // Chamar o método e verificar os resultados
        ResponseEntity<ClientePjResponseDTO> responseEntity = clientePjController.cadastrarClientePj(clientePjRequestDTO);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Cliente já cadastrado", responseEntity.getBody().getMessage());
    }

    @Test
    public void testAtualizarClientePjSucesso() {
        // Configurar objetos simulados e comportamentos
        ClientePjRequestDTO clientePjRequestDTO = new ClientePjRequestDTO(/* Preencha com os dados necessários */);
        when(clientePjService.consultarClientePorCnpj(anyString())).thenReturn(new ClientePj());
        when(clientePjService.atualizarCliente(any())).thenReturn(new ClientePj());
        doNothing().when(filaDeAtendimento).adicionarClienteNaFila(anyString());

        // Chamar o método e verificar os resultados
        ResponseEntity<ClientePjResponseDTO> responseEntity = clientePjController.atualizarClientePj(clientePjRequestDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Cliente atualizado com sucesso", responseEntity.getBody().getMessage());
    }

    @Test
    public void testAtualizarClientePjClienteNaoCadastrado() {
        // Configurar objetos simulados e comportamentos
        ClientePjRequestDTO clientePjRequestDTO = new ClientePjRequestDTO(/* Preencha com os dados necessários */);
        when(clientePjService.consultarClientePorCnpj(anyString())).thenReturn(null);

        // Chamar o método e verificar os resultados
        ResponseEntity<ClientePjResponseDTO> responseEntity = clientePjController.atualizarClientePj(clientePjRequestDTO);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Cliente não cadastrado", responseEntity.getBody().getMessage());
    }

    @Test
    public void testExcluirClientePjSucesso() {
        // Configurar objetos simulados e comportamentos
        String cnpj = "12345678901234";
        when(clientePjService.consultarClientePorCnpj(cnpj)).thenReturn(new ClientePj());
        doNothing().when(clientePjService).excluirClientePorCnpj(cnpj);

        // Chamar o método e verificar os resultados
        ResponseEntity<ClientePjResponseDTO> responseEntity = clientePjController.excluirClientePj(cnpj);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Cliente excluído com sucesso", responseEntity.getBody().getMessage());
    }

    @Test
    public void testExcluirClientePjClienteNaoCadastrado() {
        // Configurar objetos simulados e comportamentos
        String cnpj = "12345678901234";
        when(clientePjService.consultarClientePorCnpj(cnpj)).thenReturn(null);

        // Chamar o método e verificar os resultados
        ResponseEntity<ClientePjResponseDTO> responseEntity = clientePjController.excluirClientePj(cnpj);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Cliente não encontrado", responseEntity.getBody().getMessage());
    }

    @Test
    public void testExcluirClientePjErroInterno() {
        // Configurar objetos simulados e comportamentos
        String cnpj = "12345678901234";
        when(clientePjService.consultarClientePorCnpj(cnpj)).thenReturn(new ClientePj());
        doThrow(new RuntimeException("Erro interno")).when(clientePjService).excluirClientePorCnpj(cnpj);

        // Chamar o método e verificar os resultados
        ResponseEntity<ClientePjResponseDTO> responseEntity = clientePjController.excluirClientePj(cnpj);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Erro interno do servidor", responseEntity.getBody().getMessage());
    }

    // Testes semelhantes para outros métodos

}
