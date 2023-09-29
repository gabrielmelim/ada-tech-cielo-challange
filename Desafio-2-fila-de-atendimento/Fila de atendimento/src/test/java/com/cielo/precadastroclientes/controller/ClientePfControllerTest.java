package com.cielo.precadastroclientes.controller;

import com.cielo.precadastroclientes.DTO.ClientePfRequestDTO;
import com.cielo.precadastroclientes.DTO.ClientePfResponseDTO;
import com.cielo.precadastroclientes.DTO.ValidationErrorResponse;
import com.cielo.precadastroclientes.exception.ClienteException;
import com.cielo.precadastroclientes.model.ClientePf;
import com.cielo.precadastroclientes.queue.FilaDeAtendimento;
import com.cielo.precadastroclientes.service.ClientePfServiceImpl;
import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientePfControllerTest {

    @InjectMocks
    private ClientePfController clientePfController;

    @Mock
    private ClientePfServiceImpl clientePfService;

    @Mock
    private FilaDeAtendimento filaDeAtendimento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCadastrarClientePf() {
        ClientePfRequestDTO requestDTO = new ClientePfRequestDTO();
        requestDTO.setCpf("12345678901");
        requestDTO.setNome("Nome do Cliente");
        requestDTO.setMcc("1234");
        requestDTO.setEmail("cliente@example.com");

        ClientePf clientePf = new ClientePf();
        clientePf.setCpf("12345678901");
        clientePf.setNome("Nome do Cliente");
        clientePf.setMcc("1234");
        clientePf.setEmail("cliente@example.com");

        when(clientePfService.convertRequestDTOToEntity(requestDTO)).thenReturn(clientePf);
        when(clientePfService.cadastrarCliente(clientePf)).thenReturn(clientePf);
        when(filaDeAtendimento.adicionarClienteNaFila("12345678901")).thenReturn(true);

        ResponseEntity<ClientePfResponseDTO> responseEntity = clientePfController.cadastrarClientePj(requestDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode(), "O status deve ser 201 - Created");
        assertNotNull(responseEntity.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals("Cliente cadastrado com sucesso", responseEntity.getBody().getMessage(), "A mensagem de resposta deve ser 'Cliente cadastrado com sucesso'");
    }

    @Test
    void testCadastrarClientePfExistente() {
        ClientePfRequestDTO requestDTO = new ClientePfRequestDTO();
        requestDTO.setCpf("12345678901");
        requestDTO.setNome("Nome do Cliente");
        requestDTO.setMcc("1234");
        requestDTO.setEmail("cliente@example.com");

        ClientePf clientePf = new ClientePf();
        clientePf.setCpf("12345678901");
        clientePf.setNome("Nome do Cliente");
        clientePf.setMcc("1234");
        clientePf.setEmail("cliente@example.com");

        when(clientePfService.convertRequestDTOToEntity(requestDTO)).thenReturn(clientePf);
        when(clientePfService.cadastrarCliente(clientePf)).thenThrow(new ClienteException.ClienteExistenteException("Cliente já cadastrado"));

        ResponseEntity<ClientePfResponseDTO> responseEntity = clientePfController.cadastrarClientePj(requestDTO);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode(), "O status deve ser 409 - Conflict");
        assertNotNull(responseEntity.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals("Cliente já cadastrado", responseEntity.getBody().getMessage(), "A mensagem de resposta deve ser 'Cliente já cadastrado'");
    }

    @Test
    void testAtualizarClientePf() {
        ClientePfRequestDTO requestDTO = new ClientePfRequestDTO();
        requestDTO.setCpf("12345678901");
        requestDTO.setNome("Nome do Cliente");
        requestDTO.setMcc("1234");
        requestDTO.setEmail("cliente@example.com");

        ClientePf clientePf = new ClientePf();
        clientePf.setCpf("12345678901");
        clientePf.setNome("Nome do Cliente");
        clientePf.setMcc("1234");
        clientePf.setEmail("cliente@example.com");

        when(clientePfService.consultarClientePorCpf("12345678901")).thenReturn(clientePf);
        when(clientePfService.atualizarCliente(clientePf)).thenReturn(clientePf);
        when(filaDeAtendimento.adicionarClienteNaFila("12345678901")).thenReturn(true);

        ResponseEntity<ClientePfResponseDTO> responseEntity = clientePfController.atualizarClientePf(requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "O status deve ser 200 - OK");
        assertNotNull(responseEntity.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals("Cliente atualizado com sucesso", responseEntity.getBody().getMessage(), "A mensagem de resposta deve ser 'Cliente atualizado com sucesso'");
    }

    @Test
    void testAtualizarClientePfNaoCadastrado() {
        ClientePfRequestDTO requestDTO = new ClientePfRequestDTO();
        requestDTO.setCpf("12345678901");
        requestDTO.setNome("Nome do Cliente");
        requestDTO.setMcc("1234");
        requestDTO.setEmail("cliente@example.com");

        when(clientePfService.consultarClientePorCpf("12345678901")).thenReturn(null);

        ResponseEntity<ClientePfResponseDTO> responseEntity = clientePfController.atualizarClientePf(requestDTO);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "O status deve ser 404 - Not Found");
        assertNotNull(responseEntity.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals("Cliente não cadastrado", responseEntity.getBody().getMessage(), "A mensagem de resposta deve ser 'Cliente não cadastrado'");
    }

    @Test
    void testConsultarClientePf() {
        String cpf = "12345678901";
        ClientePf clientePf = new ClientePf();
        clientePf.setCpf(cpf);

        when(clientePfService.consultarClientePorCpf(cpf)).thenReturn(clientePf);

        ResponseEntity<ClientePfResponseDTO> responseEntity = clientePfController.consultarClientePf(cpf);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "O status deve ser 200 - OK");
        assertNotNull(responseEntity.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals("Cliente encontrado com sucesso", responseEntity.getBody().getMessage(), "A mensagem de resposta deve ser 'Cliente encontrado com sucesso'");
    }

    @Test
    void testConsultarClientePfNaoCadastrado() {
        String cpf = "12345678901";

        when(clientePfService.consultarClientePorCpf(cpf)).thenReturn(null);

        ResponseEntity<ClientePfResponseDTO> responseEntity = clientePfController.consultarClientePf(cpf);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "O status deve ser 404 - Not Found");
        assertNotNull(responseEntity.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals("Cliente não cadastrado", responseEntity.getBody().getMessage(), "A mensagem de resposta deve ser 'Cliente não cadastrado'");
    }

    @Test
    void testExcluirClientePf() {
        String cpf = "12345678901";
        ClientePf clientePf = new ClientePf();
        clientePf.setCpf(cpf);

        when(clientePfService.consultarClientePorCpf(cpf)).thenReturn(clientePf);

        ResponseEntity<ClientePfResponseDTO> responseEntity = clientePfController.excluirClientePf(cpf);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "O status deve ser 200 - OK");
        assertNotNull(responseEntity.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals("Cliente excluído com sucesso", responseEntity.getBody().getMessage(), "A mensagem de resposta deve ser 'Cliente excluído com sucesso'");
    }

    @Test
    void testExcluirClientePfNaoCadastrado() {
        String cpf = "12345678901";

        when(clientePfService.consultarClientePorCpf(cpf)).thenReturn(null);

        ResponseEntity<ClientePfResponseDTO> responseEntity = clientePfController.excluirClientePf(cpf);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "O status deve ser 404 - Not Found");
        assertNotNull(responseEntity.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals("Cliente não encontrado", responseEntity.getBody().getMessage(), "A mensagem de resposta deve ser 'Cliente não encontrado'");
    }

    @Test
    void testExcluirClientePfException() {
        String cpf = "12345678901";
        ClientePf clientePf = new ClientePf();
        clientePf.setCpf(cpf);

        when(clientePfService.consultarClientePorCpf(cpf)).thenReturn(clientePf);

        // Use doThrow para indicar que o método excluirCliente deve lançar uma exceção
        doThrow(new RuntimeException("Erro interno")).when(clientePfService).excluirCliente(cpf);

        ResponseEntity<ClientePfResponseDTO> responseEntity = clientePfController.excluirClientePf(cpf);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(), "O status deve ser 500 - Internal Server Error");
        assertNotNull(responseEntity.getBody(), "O corpo da resposta não deve ser nulo");
        assertEquals("Erro interno do servidor", responseEntity.getBody().getMessage(), "A mensagem de resposta deve ser 'Erro interno do servidor'");
    }
}
