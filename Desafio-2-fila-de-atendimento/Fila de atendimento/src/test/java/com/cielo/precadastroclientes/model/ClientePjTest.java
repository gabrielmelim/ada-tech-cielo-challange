package com.cielo.precadastroclientes.model;

import com.cielo.precadastroclientes.model.ClientePj;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientePjTest {

    @InjectMocks
    private ClientePj clientePj;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Long> query;

    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Configurar o EntityManager mock para retornar uma consulta fictícia
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(query);

        // Configurar o Validator usando a implementação padrão do Bean Validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidClientePj() {
        // Configurar um cliente válido
        clientePj.setCnpj("12345678912345");
        clientePj.setRazaoSocial("Nome da Empresa");
        clientePj.setMcc("1232");
        clientePj.setCpfContatoEstabelecimento("12345678901");
        clientePj.setNomeContatoEstabelecimento("Nome do Contato");
        clientePj.setEmail("contato@empresa.com");

        // Validar o cliente usando o Bean Validation
        var violations = validator.validate(clientePj);
        assertTrue(violations.isEmpty(), "O cliente deve ser válido");
    }

    @Test
    void testInvalidCnpj() {
        // Configurar um cliente com um CNPJ inválido (menos de 14 dígitos)
        clientePj.setCnpj("12345");
        clientePj.setRazaoSocial("Nome da Empresa");
        clientePj.setMcc("1232");
        clientePj.setCpfContatoEstabelecimento("12345678901");
        clientePj.setNomeContatoEstabelecimento("Nome do Contato");
        clientePj.setEmail("contato@empresa.com");

        // Validar o cliente usando o Bean Validation
        var violations = validator.validate(clientePj);
        assertFalse(violations.isEmpty(), "O cliente deve ser inválido devido ao CNPJ");
    }

    @Test
    void testInvalidRazaoSocial() {
        // Configurar um cliente com uma razão social inválida (mais de 50 caracteres)
        clientePj.setCnpj("12345678912345");
        clientePj.setRazaoSocial("Nome da Empresa com um nome muito longo que excede 50 caracteres");
        clientePj.setMcc("1232");
        clientePj.setCpfContatoEstabelecimento("12345678901");
        clientePj.setNomeContatoEstabelecimento("Nome do Contato");
        clientePj.setEmail("contato@empresa.com");

        // Validar o cliente usando o Bean Validation
        var violations = validator.validate(clientePj);
        assertFalse(violations.isEmpty(), "O cliente deve ser inválido devido à Razão Social longa");
    }

    @Test
    void testInvalidCpfContatoEstabelecimento() {
        // Configurar um cliente com um CPF de contato de estabelecimento inválido (mais de 11 dígitos)
        clientePj.setCnpj("12345678912345");
        clientePj.setRazaoSocial("Nome da Empresa");
        clientePj.setMcc("1232");
        clientePj.setCpfContatoEstabelecimento("1234567890123"); // CPF inválido
        clientePj.setNomeContatoEstabelecimento("Nome do Contato");
        clientePj.setEmail("contato@empresa.com");

        // Validar o cliente usando o Bean Validation
        var violations = validator.validate(clientePj);
        assertFalse(violations.isEmpty(), "O cliente deve ser inválido devido ao CPF de contato de estabelecimento inválido");
    }

    @Test
    void testValidClientePjWithNullEmail() {
        // Configurar um cliente válido com email nulo (mas ainda válido)
        clientePj.setCnpj("12345678912345");
        clientePj.setRazaoSocial("Nome da Empresa");
        clientePj.setMcc("1232");
        clientePj.setCpfContatoEstabelecimento("12345678901");
        clientePj.setNomeContatoEstabelecimento("Nome do Contato");
        clientePj.setEmail(null);

        // Validar o cliente usando o Bean Validation
        var violations = validator.validate(clientePj);
        assertTrue(violations.isEmpty(), "O cliente deve ser válido mesmo com email nulo");
    }

    // Adicione mais testes de validação conforme necessário para outros campos, como MCC, CPF, etc.

    // Testes adicionais, como testes de busca por ID, podem ser adicionados conforme necessário.
}
