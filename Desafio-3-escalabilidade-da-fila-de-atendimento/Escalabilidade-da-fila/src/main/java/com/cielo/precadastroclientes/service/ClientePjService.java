package com.cielo.precadastroclientes.service;

import com.cielo.precadastroclientes.model.ClientePj;

import java.util.List;

public interface ClientePjService {
    ClientePj cadastrarClientePj(ClientePj cliente);
    ClientePj atualizarCliente(ClientePj cliente);

    void excluirClientePorCnpj(String cnpj);

    List<ClientePj> listarClientes();
    ClientePj consultarClientePorCnpj(String cnpj);
}
