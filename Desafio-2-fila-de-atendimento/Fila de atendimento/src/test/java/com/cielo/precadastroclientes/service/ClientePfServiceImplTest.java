package com.cielo.precadastroclientes.service;

import com.cielo.precadastroclientes.DTO.ClientePfRequestDTO;
import com.cielo.precadastroclientes.exception.ClienteException;
import com.cielo.precadastroclientes.model.ClientePf;
import com.cielo.precadastroclientes.repository.ClientePfRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientePfServiceImplTest {

    @InjectMocks
    private ClientePfServiceImpl clientePfService;

    @Mock
    private ClientePfRepository clientePfRepository;

    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Configurar o Validator usando a implementação padrão do Bean Validation
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testFormatarCpf() {
        String cpf = "123.456.789-01";
        String cpfFormatado = clientePfService.formatarCpf(cpf);
        assertEquals("12345678901", cpfFormatado, "O CPF deve ser formatado corretamente");
    }

    @Test
    void testConverterClientePfRequestParaClientePf() {
        ClientePfRequestDTO requestDTO = new ClientePfRequestDTO();
        requestDTO.setCpf("123.456.789-01");
        requestDTO.setNome("Nome do Cliente");
        requestDTO.setMcc("1234");
        requestDTO.setEmail("cliente@example.com");

        ClientePf clientePf = clientePfService.convertRequestDTOToEntity(requestDTO);

        assertEquals("12345678901", clientePf.getCpf(), "O CPF deve ser formatado corretamente");
        assertEquals("Nome do Cliente", clientePf.getNome(), "O nome deve ser igual");
        assertEquals("1234", clientePf.getMcc(), "O MCC deve ser igual");
        assertEquals("cliente@example.com", clientePf.getEmail(), "O email deve ser igual");
    }

    @Test
    void testCadastrarCliente() {
        ClientePf cliente = new ClientePf();
        cliente.setCpf("12345678901");

        when(clientePfRepository.existsByCpf("12345678901")).thenReturn(false);
        when(clientePfRepository.save(cliente)).thenReturn(cliente);

        ClientePf cadastrado = clientePfService.cadastrarCliente(cliente);

        assertNotNull(cadastrado, "O cliente deve ser cadastrado com sucesso");
        verify(clientePfRepository, times(1)).existsByCpf("12345678901");
        verify(clientePfRepository, times(1)).save(cliente);
    }

    @Test
    void testCadastrarClienteExistente() {
        ClientePf cliente = new ClientePf();
        cliente.setCpf("12345678901");

        when(clientePfRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(ClienteException.ClienteExistenteException.class, () -> clientePfService.cadastrarCliente(cliente));
        verify(clientePfRepository, times(1)).existsByCpf("12345678901");
        verify(clientePfRepository, never()).save(cliente);
    }

    @Test
    void testAtualizarCliente() {
        ClientePf cliente = new ClientePf();
        cliente.setCpf("12345678901");

        when(clientePfRepository.existsByCpf("12345678901")).thenReturn(true);
        when(clientePfRepository.save(cliente)).thenReturn(cliente);

        ClientePf atualizado = clientePfService.atualizarCliente(cliente);

        assertNotNull(atualizado, "O cliente deve ser atualizado com sucesso");
        verify(clientePfRepository, times(1)).existsByCpf("12345678901");
        verify(clientePfRepository, times(1)).save(cliente);
    }

    @Test
    void testAtualizarClienteNaoCadastrado() {
        ClientePf cliente = new ClientePf();
        cliente.setCpf("12345678901");

        when(clientePfRepository.existsByCpf("12345678901")).thenReturn(false);

        assertThrows(ClienteException.ClienteNaoCadastradoException.class, () -> clientePfService.atualizarCliente(cliente));
        verify(clientePfRepository, times(1)).existsByCpf("12345678901");
        verify(clientePfRepository, never()).save(cliente);
    }

    @Test
    void testExcluirCliente() {
        String cpf = "12345678901";

        when(clientePfRepository.existsByCpf(cpf)).thenReturn(true);

        assertDoesNotThrow(() -> clientePfService.excluirCliente(cpf));
        verify(clientePfRepository, times(1)).existsByCpf(cpf);
        verify(clientePfRepository, times(1)).deleteByCpf(cpf);
    }

    @Test
    void testExcluirClienteNaoCadastrado() {
        String cpf = "12345678901";

        when(clientePfRepository.existsByCpf(cpf)).thenReturn(false);

        assertThrows(ClienteException.ClienteNaoCadastradoException.class, () -> clientePfService.excluirCliente(cpf));
        verify(clientePfRepository, times(1)).existsByCpf(cpf);
        verify(clientePfRepository, never()).deleteByCpf(cpf);
    }

    @Test
    void testListarClientes() {
        List<ClientePf> clientes = Collections.emptyList();

        when(clientePfRepository.findAll()).thenReturn(clientes);

        List<ClientePf> lista = clientePfService.listarClientes();

        assertEquals(clientes, lista, "A lista de clientes deve ser igual");
        verify(clientePfRepository, times(1)).findAll();
    }

    @Test
    void testConsultarClientePorCpf() {
        String cpf = "12345678901";
        ClientePf cliente = new ClientePf();
        cliente.setCpf(cpf);

        when(clientePfRepository.findByCpf(cpf)).thenReturn(cliente);

        ClientePf encontrado = clientePfService.consultarClientePorCpf(cpf);

        assertEquals(cliente, encontrado, "O cliente consultado deve ser igual");
        verify(clientePfRepository, times(1)).findByCpf(cpf);
    }

    @Test
    void testConsultarClientePorCpfNaoEncontrado() {
        String cpf = "12345678901";

        when(clientePfRepository.findByCpf(cpf)).thenReturn(null);

        assertThrows(ClienteException.ClienteNaoCadastradoException.class, () -> clientePfService.consultarClientePorCpf(cpf));
        verify(clientePfRepository, times(1)).findByCpf(cpf);
    }
}
