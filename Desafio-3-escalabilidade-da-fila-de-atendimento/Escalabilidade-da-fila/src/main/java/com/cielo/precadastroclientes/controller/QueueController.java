package com.cielo.precadastroclientes.controller;

import com.cielo.precadastroclientes.DTO.ClientePfResponseDTO;
import com.cielo.precadastroclientes.DTO.ClientePjResponseDTO;
import com.cielo.precadastroclientes.DTO.GenericResponseDTO;
import com.cielo.precadastroclientes.model.ClientePf;
import com.cielo.precadastroclientes.model.ClientePj;
import com.cielo.precadastroclientes.queue.FilaDeAtendimento;
import com.cielo.precadastroclientes.service.ClientePfService;
import com.cielo.precadastroclientes.service.ClientePjService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/fila-atendimento")
public class QueueController {
    private final ClientePfService clientePfService;
    private final ClientePjService clientePjService;
    private final FilaDeAtendimento filaDeAtendimento;

    public QueueController(FilaDeAtendimento filaDeAtendimento, ClientePfService clientePfService1, ClientePjService clientePjService) {
        this.filaDeAtendimento = filaDeAtendimento;
        this.clientePfService = clientePfService1;
        this.clientePjService = clientePjService;
    }

    // Endpoint para retirar o próximo cliente da fila de atendimento
    @GetMapping("/fila/atendimento/proximo")
    public ResponseEntity<GenericResponseDTO<?>> retirarProximoClienteDaFila() {
        try {
            if (filaDeAtendimento.getTamanho() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponseDTO<>(null, "Fila de atendimento vazia"));
            }

            String clienteIdentificador = filaDeAtendimento.proximoCliente();
            GenericResponseDTO<?> responseDTO;

            // Verifica se o identificador do cliente é um CPF (para PF)
            if (clienteIdentificador != null && clienteIdentificador.length() == 11) {
                // Consulta o cliente PF por CPF usando o serviço
                ClientePf clientePf = clientePfService.consultarClientePorCpf(clienteIdentificador);

                if (clientePf != null) {
                    // Crie um ClientePfResponseDTO com as informações apropriadas
                    ClientePfResponseDTO clienteResponseDTO = new ClientePfResponseDTO();
                    clienteResponseDTO.setCpf(clientePf.getCpf());
                    clienteResponseDTO.setNome(clientePf.getNome());
                    clienteResponseDTO.setEmail(clientePf.getEmail());
                    clienteResponseDTO.setMcc(clientePf.getMcc());
                    clienteResponseDTO.setMessage("Cliente PF retirado com sucesso da fila");
                    responseDTO = new GenericResponseDTO<>(clienteResponseDTO, null);
                } else {
                    responseDTO = new GenericResponseDTO<>(null, "Cliente PF não encontrado na fila");
                }
            }

            // Se não for CPF, verifica se é CNPJ (para PJ)
            else if (clienteIdentificador != null && clienteIdentificador.length() == 14) {
                // Consulta o cliente PJ por CNPJ usando o serviço PJ correspondente
                ClientePj clientePj = clientePjService.consultarClientePorCnpj(clienteIdentificador);

                if (clientePj != null) {
                    // Crie um ClientePjResponseDTO com as informações apropriadas
                    ClientePjResponseDTO clienteResponseDTO = new ClientePjResponseDTO();
                    clienteResponseDTO.setCnpj(clientePj.getCnpj());
                    clienteResponseDTO.setEmail(clientePj.getEmail());
                    clienteResponseDTO.setMcc(clientePj.getMcc());
                    clienteResponseDTO.setRazaoSocial(clientePj.getRazaoSocial());
                    clienteResponseDTO.setNome(clientePj.getNomeContatoEstabelecimento());
                    clienteResponseDTO.setCpf(clientePj.getCpfContatoEstabelecimento());
                    clienteResponseDTO.setMessage("Cliente PJ retirado com sucesso da fila");
                    responseDTO = new GenericResponseDTO<>(clienteResponseDTO, null);
                } else {
                    responseDTO = new GenericResponseDTO<>(null, "Cliente PJ não encontrado na fila");
                }
            } else {
                responseDTO = new GenericResponseDTO<>(null, "Identificador inválido na fila");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponseDTO<>(null, "Fila de atendimento vazia"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponseDTO<>(null, "Erro interno do servidor"));
        }
    }

    // Endpoint para visualizar o conteúdo da fila de atendimento
    @Operation(summary = "Visualizar conteúdo da fila de atendimento", description = "Retorna o conteúdo da fila de atendimento.")
    @GetMapping("/fila/atendimento")
    public ResponseEntity<?> visualizarFilaDeAtendimento() {
        try {
            // Obtenha o conteúdo da fila (por exemplo, uma lista de IDs de clientes)
            String[] conteudoDaFilaArray = filaDeAtendimento.obterConteudoDaFila();

            if (conteudoDaFilaArray == null || conteudoDaFilaArray.length == 0) {
                // Se a fila está vazia, retorne uma mensagem apropriada com o status 200 (OK)
                return ResponseEntity.status(HttpStatus.OK).body("Fila de atendimento vazia");
            }

            // Retorne o conteúdo da fila como uma lista de strings com o status 200 (OK)
            return ResponseEntity.status(HttpStatus.OK).body(Arrays.asList(conteudoDaFilaArray));
        } catch (Exception ex) {
            // Trate qualquer outra exceção que possa ocorrer aqui e retorne uma resposta apropriada, por exemplo, HTTP 500 - Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
        }
    }

}
