package com.cielo.precadastroclientes.service;

import com.cielo.precadastroclientes.model.ClientePf;

import java.util.List;


public interface ClientePfService {
    ClientePf cadastrarCliente(ClientePf cliente);
    ClientePf atualizarCliente(ClientePf cliente);
    void excluirCliente(String cpf);
    List<ClientePf> listarClientes();

    ClientePf consultarClientePorCpf(String cpf);
}
