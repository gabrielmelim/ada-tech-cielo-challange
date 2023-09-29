package com.cielo.precadastroclientes.service;

import com.cielo.precadastroclientes.DTO.ClientePfRequestDTO;
import com.cielo.precadastroclientes.exception.ClienteException;
import com.cielo.precadastroclientes.model.ClientePf;
import com.cielo.precadastroclientes.repository.ClientePfRepository;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientePfServiceImpl implements ClientePfService {

    private final ClientePfRepository clientePfRepository;

    public ClientePfServiceImpl(Validator validator, ClientePfRepository clientePfRepository) {
        this.clientePfRepository = clientePfRepository;
    }

    // Método para formatar o CPF
    public String formatarCpf(String cpf) {
        // Remove qualquer caractere não numérico do CPF
        String cpfNumerico = cpf.replaceAll("[^0-9]", "");

        // Completa com zeros à esquerda se o CPF tiver menos de 11 dígitos
        while (cpfNumerico.length() < 11) {
            cpfNumerico = "0" + cpfNumerico;
        }

        return cpfNumerico;
    }

    // Método privado para converter ClientePjRequestDTO para ClientePj
    private ClientePf converterClientePfRequestParaClientePf( ClientePfRequestDTO clientePfRequestDTO) {
        ClientePf clientePf = new ClientePf();

        // Formate o CPF usando o método formatarCpf
        String cpfFormatado = formatarCpf(clientePfRequestDTO.getCpf());

        clientePf.setCpf(cpfFormatado);
        clientePf.setNome(clientePfRequestDTO.getNome());
        clientePf.setMcc(clientePfRequestDTO.getMcc());
        clientePf.setEmail(clientePfRequestDTO.getEmail());

        return clientePf;
    }

    // Método público que utiliza a conversão privada
    public ClientePf convertRequestDTOToEntity(ClientePfRequestDTO clientePfRequestDTO) {
        return converterClientePfRequestParaClientePf(clientePfRequestDTO);
    }


    @Override
    public ClientePf cadastrarCliente(@Valid ClientePf cliente) {
        if (clientePfRepository.existsByCpf(cliente.getCpf())) {
            throw new ClienteException.ClienteExistenteException("Cliente Pf já cadastrado.");
        }

        return clientePfRepository.save(cliente);
    }

    @Override
    public ClientePf atualizarCliente(@Valid ClientePf cliente) {
        String cnpj = cliente.getCpf();

        if (!clientePfRepository.existsByCpf(cnpj)) {
            throw new ClienteException.ClienteNaoCadastradoException("Cliente Pf não encontrado.");
        }

        // Obtém o CPF do cliente a ser atualizado
        String cpf = cliente.getCpf();

        // Formate o CPF usando o método formatarCpf
        String cpfFormatado = formatarCpf(cpf);

        // Defina o CPF formatado no cliente
        cliente.setCpf(cpfFormatado);

        return clientePfRepository.save(cliente);
    }


    @Override
    public void excluirCliente(@Valid String cpf) {
        if (!clientePfRepository.existsByCpf(cpf)) {
            throw new ClienteException.ClienteNaoCadastradoException("Cliente Pf não encontrado.");
        }

        // Use o novo método personalizado para excluir por Cpf
        clientePfRepository.deleteByCpf(cpf);
    }

    @Override
    public List<ClientePf> listarClientes() {
        return clientePfRepository.findAll();
    }

    @Override
    public ClientePf consultarClientePorCpf(@Valid String cpf) {
        ClientePf cliente = clientePfRepository.findByCpf(cpf);

        if (cliente == null) {
            throw new ClienteException.ClienteNaoCadastradoException("Cliente Pf não encontrado");
        }

        return cliente;
    }
}
