package com.cielo.precadastroclientes.model;

import com.cielo.precadastroclientes.model.ClientePf;
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

public class ClientePfTest {

    @InjectMocks
    private ClientePf clientePf;

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
    void testValidClientePf() {
        // Configurar um cliente válido
        clientePf.setCpf("12345678901");
        clientePf.setMcc("4325");
        clientePf.setNome("Nome da pessoa física");
        clientePf.setEmail("contato@pessoa.com");

        // Validar o cliente usando o Bean Validation
        var violations = validator.validate(clientePf);
        assertTrue(violations.isEmpty(), "O cliente deve ser válido");
    }

    @Test
    void testInvalidClientePf() {
        // Configurar um cliente inválido (cpf em branco)
        clientePf.setCpf("");
        clientePf.setMcc("4325");
        clientePf.setNome("Nome da pessoa física");
        clientePf.setEmail("contato@pessoa.com");

        // Validar o cliente usando o Bean Validation
        var violations = validator.validate(clientePf);
        assertFalse(violations.isEmpty(), "O cliente deve ser inválido");
    }

    @Test
    void testFindClienteById() {
        // Configurar um ID fictício para o cliente
        clientePf.setId(1L);

        // Configurar o EntityManager mock para retornar o cliente fictício quando solicitado por ID
        when(query.getSingleResult()).thenReturn(1L);
        when(entityManager.find(ClientePf.class, 1L)).thenReturn(clientePf);

        // Realizar a busca do cliente por ID
        ClientePf foundCliente = entityManager.find(ClientePf.class, 1L);

        // Verificar se o cliente foi encontrado corretamente
        assertNotNull(foundCliente, "O cliente deve ser encontrado");
        assertEquals(1L, foundCliente.getId(), "O ID do cliente deve corresponder ao esperado");
    }
}
