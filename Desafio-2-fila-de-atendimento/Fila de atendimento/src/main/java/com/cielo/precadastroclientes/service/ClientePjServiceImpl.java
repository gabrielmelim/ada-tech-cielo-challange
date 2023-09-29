package com.cielo.precadastroclientes.service;

import com.cielo.precadastroclientes.DTO.ClientePjRequestDTO;
import com.cielo.precadastroclientes.exception.ClienteException;
import com.cielo.precadastroclientes.model.ClientePj;
import com.cielo.precadastroclientes.repository.ClientePjRepository;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientePjServiceImpl implements ClientePjService {


    private final ClientePjRepository clientePjRepository;

    @Autowired
    public ClientePjServiceImpl(Validator validator, ClientePjRepository clientePjRepository) {
        this.clientePjRepository = clientePjRepository;
    }


    // Método para formatar o CNPJ
    public String formatarCnpj(String cnpj) {
        if (cnpj == null) {
            // Trate o caso em que o CNPJ é nulo (ou seja, retorne um valor padrão ou lance uma exceção)
            throw new IllegalArgumentException("CNPJ não pode ser nulo");
        }

        // Remove qualquer caractere não numérico do CNPJ
        String cnpjNumerico = cnpj.replaceAll("[^0-9]", "");

        // Verifica se o CNPJ não está formatado (não contém pontos, barras ou traços)
        if (!cnpj.matches(".*[./-].*")) {
            // Completa com zeros à esquerda se o CNPJ tiver menos de 14 dígitos
            while (cnpjNumerico.length() < 14) {
                cnpjNumerico = "0" + cnpjNumerico;
            }
        }

        // Retorna o CNPJ apenas com os dígitos numéricos
        return cnpjNumerico;
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
    private ClientePj converterClientePjRequestParaClientePj(ClientePjRequestDTO clientePjRequestDTO) {
        ClientePj clientePj = new ClientePj();

        // Formate o CNPJ usando o método formatarCnpj
        String cnpjFormatado = formatarCnpj(clientePjRequestDTO.getCnpj());

        // Formate o CPF usando o método formatarCpf
        String cpfFormatado = formatarCpf(clientePjRequestDTO.getCpf());

        clientePj.setCnpj(cnpjFormatado);
        clientePj.setCpfContatoEstabelecimento(cpfFormatado);
        clientePj.setRazaoSocial(clientePjRequestDTO.getRazaoSocial());
        clientePj.setMcc(clientePjRequestDTO.getMcc());
        clientePj.setNomeContatoEstabelecimento(clientePjRequestDTO.getNome());
        clientePj.setEmail(clientePjRequestDTO.getEmail());
        return clientePj;
    }


    // Método público que utiliza a conversão privada
    public ClientePj convertRequestDTOToEntity(ClientePjRequestDTO clientePjRequestDTO) {
        return converterClientePjRequestParaClientePj(clientePjRequestDTO);
    }

    @Override
    public ClientePj cadastrarClientePj(@Valid ClientePj cliente) {

        if (clientePjRepository.existsByCnpj(cliente.getCnpj())) {
            throw new ClienteException.ClienteExistenteException("Cliente PJ já cadastrado.");
        }

        return clientePjRepository.save(cliente);
    }

    @Override
    public ClientePj atualizarCliente(@Valid ClientePj cliente) {

        String cnpj = cliente.getCnpj();

        if (!clientePjRepository.existsByCnpj(cnpj)) {
            throw new ClienteException.ClienteNaoCadastradoException("Cliente PJ não encontrado.");
        }

        // Obtém o CPF do cliente a ser atualizado
        String cpf = cliente.getCpfContatoEstabelecimento();

        // Formate o CPF usando o método formatarCpf
        String cpfFormatado = formatarCpf(cpf);

        // Defina o CPF formatado no cliente
        cliente.setCpfContatoEstabelecimento(cpfFormatado);


        return clientePjRepository.save(cliente);
    }

    @Override
    public void excluirClientePorCnpj(@Valid String cnpj) {
        if (!clientePjRepository.existsByCnpj(cnpj)) {
            throw new ClienteException.ClienteNaoCadastradoException("Cliente PJ não encontrado.");
        }

        // Use o novo método personalizado para excluir por CNPJ
        clientePjRepository.deleteByCnpj(cnpj);
    }

    @Override
    public List<ClientePj> listarClientes() {
        return clientePjRepository.findAll();
    }

    @Override
    public ClientePj consultarClientePorCnpj(@Valid String cnpj) {

        ClientePj cliente = clientePjRepository.findByCnpj(cnpj);

        if (cliente == null) {
            throw new ClienteException.ClienteNaoCadastradoException("Cliente PJ não encontrado");
        }

        return cliente;
    }
}
