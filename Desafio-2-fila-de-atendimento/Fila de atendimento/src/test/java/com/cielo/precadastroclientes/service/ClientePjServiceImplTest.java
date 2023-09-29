package com.cielo.precadastroclientes.service;

import com.cielo.precadastroclientes.DTO.ClientePjRequestDTO;
import com.cielo.precadastroclientes.exception.ClienteException;
import com.cielo.precadastroclientes.model.ClientePj;
import com.cielo.precadastroclientes.repository.ClientePjRepository;
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

public class ClientePjServiceImplTest {

    @InjectMocks
    private ClientePjServiceImpl clientePjService;

    @Mock
    private ClientePjRepository clientePjRepository;

    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Configurar o Validator usando a implementação padrão do Bean Validation
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testFormatarCnpj() {
        String cnpj = "12.345.678/0001-90";
        String cnpjFormatado = clientePjService.formatarCnpj(cnpj);
        assertEquals("12345678000190", cnpjFormatado, "O CNPJ deve ser formatado corretamente");
    }

    @Test
    void testFormatarCpf() {
        String cpf = "123.456.789-01";
        String cpfFormatado = clientePjService.formatarCpf(cpf);
        assertEquals("12345678901", cpfFormatado, "O CPF deve ser formatado corretamente");
    }

    @Test
    void testConverterClientePjRequestParaClientePj() {
        ClientePjRequestDTO requestDTO = new ClientePjRequestDTO();
        requestDTO.setCnpj("12.345.678/0001-90");
        requestDTO.setCpf("123.456.789-01");
        requestDTO.setRazaoSocial("Nome da Empresa");
        requestDTO.setMcc("1234");
        requestDTO.setNome("Nome do Contato");
        requestDTO.setEmail("cliente@example.com");

        ClientePj clientePj = clientePjService.convertRequestDTOToEntity(requestDTO);

        assertEquals("12345678000190", clientePj.getCnpj(), "O CNPJ deve ser formatado corretamente");
        assertEquals("12345678901", clientePj.getCpfContatoEstabelecimento(), "O CPF deve ser formatado corretamente");
        assertEquals("Nome da Empresa", clientePj.getRazaoSocial(), "A razão social deve ser igual");
        assertEquals("1234", clientePj.getMcc(), "O MCC deve ser igual");
        assertEquals("Nome do Contato", clientePj.getNomeContatoEstabelecimento(), "O nome deve ser igual");
        assertEquals("cliente@example.com", clientePj.getEmail(), "O email deve ser igual");
    }

    @Test
    void testCadastrarClientePj() {
        ClientePj cliente = new ClientePj();
        cliente.setCnpj("12345678000190");

        when(clientePjRepository.existsByCnpj("12345678000190")).thenReturn(false);
        when(clientePjRepository.save(cliente)).thenReturn(cliente);

        ClientePj cadastrado = clientePjService.cadastrarClientePj(cliente);

        assertNotNull(cadastrado, "O cliente PJ deve ser cadastrado com sucesso");
        verify(clientePjRepository, times(1)).existsByCnpj("12345678000190");
        verify(clientePjRepository, times(1)).save(cliente);
    }

    @Test
    void testCadastrarClientePjExistente() {
        ClientePj cliente = new ClientePj();
        cliente.setCnpj("12345678000190");

        when(clientePjRepository.existsByCnpj("12345678000190")).thenReturn(true);

        assertThrows(ClienteException.ClienteExistenteException.class, () -> clientePjService.cadastrarClientePj(cliente));
        verify(clientePjRepository, times(1)).existsByCnpj("12345678000190");
        verify(clientePjRepository, never()).save(cliente);
    }

    @Test
    void testAtualizarCliente() {
        ClientePj cliente = new ClientePj();
        cliente.setCnpj("12345678000190");

        when(clientePjRepository.existsByCnpj("12345678000190")).thenReturn(true);
        when(clientePjRepository.save(cliente)).thenReturn(cliente);

        ClientePj atualizado = clientePjService.atualizarCliente(cliente);

        assertNotNull(atualizado, "O cliente PJ deve ser atualizado com sucesso");
        verify(clientePjRepository, times(1)).existsByCnpj("12345678000190");
        verify(clientePjRepository, times(1)).save(cliente);
    }

    @Test
    void testAtualizarClienteNaoCadastrado() {
        ClientePj cliente = new ClientePj();
        cliente.setCnpj("12345678000190");

        when(clientePjRepository.existsByCnpj("12345678000190")).thenReturn(false);

        assertThrows(ClienteException.ClienteNaoCadastradoException.class, () -> clientePjService.atualizarCliente(cliente));
        verify(clientePjRepository, times(1)).existsByCnpj("12345678000190");
        verify(clientePjRepository, never()).save(cliente);
    }

    @Test
    void testExcluirClientePorCnpj() {
        String cnpj = "12345678000190";

        when(clientePjRepository.existsByCnpj(cnpj)).thenReturn(true);

        assertDoesNotThrow(() -> clientePjService.excluirClientePorCnpj(cnpj));
        verify(clientePjRepository, times(1)).existsByCnpj(cnpj);
        verify(clientePjRepository, times(1)).deleteByCnpj(cnpj);
    }

    @Test
    void testExcluirClientePorCnpjNaoCadastrado() {
        String cnpj = "12345678000190";

        when(clientePjRepository.existsByCnpj(cnpj)).thenReturn(false);

        assertThrows(ClienteException.ClienteNaoCadastradoException.class, () -> clientePjService.excluirClientePorCnpj(cnpj));
        verify(clientePjRepository, times(1)).existsByCnpj(cnpj);
        verify(clientePjRepository, never()).deleteByCnpj(cnpj);
    }

    @Test
    void testListarClientes() {
        List<ClientePj> clientes = Collections.emptyList();

        when(clientePjRepository.findAll()).thenReturn(clientes);

        List<ClientePj> lista = clientePjService.listarClientes();

        assertEquals(clientes, lista, "A lista de clientes PJ deve ser igual");
        verify(clientePjRepository, times(1)).findAll();
    }

    @Test
    void testConsultarClientePorCnpj() {
        String cnpj = "12345678000190";
        ClientePj cliente = new ClientePj();
        cliente.setCnpj(cnpj);

        when(clientePjRepository.findByCnpj(cnpj)).thenReturn(cliente);

        ClientePj encontrado = clientePjService.consultarClientePorCnpj(cnpj);

        assertEquals(cliente, encontrado, "O cliente PJ consultado deve ser igual");
        verify(clientePjRepository, times(1)).findByCnpj(cnpj);
    }

    @Test
    void testConsultarClientePorCnpjNaoEncontrado() {
        String cnpj = "12345678000190";

        when(clientePjRepository.findByCnpj(cnpj)).thenReturn(null);

        assertThrows(ClienteException.ClienteNaoCadastradoException.class, () -> clientePjService.consultarClientePorCnpj(cnpj));
        verify(clientePjRepository, times(1)).findByCnpj(cnpj);
    }
}
