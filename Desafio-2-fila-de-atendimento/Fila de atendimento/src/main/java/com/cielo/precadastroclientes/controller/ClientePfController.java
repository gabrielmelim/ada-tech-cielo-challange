package com.cielo.precadastroclientes.controller;

import com.cielo.precadastroclientes.DTO.ClientePfRequestDTO;
import com.cielo.precadastroclientes.DTO.ClientePfResponseDTO;
import com.cielo.precadastroclientes.DTO.ValidationErrorResponse;
import com.cielo.precadastroclientes.exception.ClienteException;
import com.cielo.precadastroclientes.model.ClientePf;
import com.cielo.precadastroclientes.queue.FilaDeAtendimento;
import com.cielo.precadastroclientes.service.ClientePfServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente-pf")
public class ClientePfController {

    private FilaDeAtendimento filaDeAtendimento;
    private final ClientePfServiceImpl clientePfService;

    public ClientePfController(ClientePfServiceImpl clientePfService, FilaDeAtendimento filaDeAtendimento) {
        this.filaDeAtendimento = filaDeAtendimento;
        this.clientePfService = clientePfService;
    }

    private ClientePfResponseDTO createResponseDTO(ClientePf clientePf) {
        ClientePfResponseDTO responseDTO = new ClientePfResponseDTO();
        responseDTO.setMcc(clientePf.getMcc());
        responseDTO.setCpf(clientePf.getCpf());
        responseDTO.setNome(clientePf.getNome());
        responseDTO.setEmail(clientePf.getEmail());
        return responseDTO;
    }

    @Operation(
            summary = "Cadastrar Cliente Pf",
            description = "Cadastrar Cliente Pf",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Cliente cadastrado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientePf.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Cliente já cadastrado",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/cadastrar")
    public ResponseEntity<ClientePfResponseDTO> cadastrarClientePj(@Valid @RequestBody ClientePfRequestDTO clientePfRequestDTO) {
        try {

            // Use o método público para converter o DTO em uma entidade ClientePf
            ClientePf clientePf = clientePfService.convertRequestDTOToEntity(clientePfRequestDTO);

            // Chame o serviço para cadastrar o cliente Pf
            clientePfService.cadastrarCliente(clientePf);

            // Adicione o CPF do cliente à fila de atendimento
            filaDeAtendimento.adicionarClienteNaFila(clientePf.getCpf());


            //Crie um objeto de resposta e preencha-o com os dados relevantes
            ClientePfResponseDTO responseDTO = createResponseDTO(clientePf);
            responseDTO.setMessage("Cliente cadastrado com sucesso");

            //retorna um responsebody DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (ClienteException.ClienteExistenteException ex) {
            // Se ocorrer algum erro, você pode lidar com a exceção aqui e retornar uma resposta de erro apropriada (por exemplo, HTTP 400 ou 409).
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ClientePfResponseDTO("Cliente já cadastrado"));
        }
    }

    @Operation(
            summary = "Atualizar Cliente Pf",
            description = "Cadastrar Cliente Pf",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente atualizado com sucesso",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente Não Cadastrado",
                            content = @Content(mediaType = "application/json")
                    ),
            }
    )
    @PutMapping("/atualizar")
    public ResponseEntity<ClientePfResponseDTO> atualizarClientePf(@Valid @RequestBody ClientePfRequestDTO clientePfRequestDTO) {
        try {

            // Verifique se o cliente já existe com base no CNPJ
            ClientePf clienteExistente = clientePfService.consultarClientePorCpf(clientePfRequestDTO.getCpf());

            // Se o cliente não existir, retorne uma resposta de erro
            if (clienteExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ClientePfResponseDTO("Cliente não cadastrado"));
            }

            // Atualize os campos relevantes do cliente com base nos dados do DTO
            clienteExistente.setMcc(clientePfRequestDTO.getMcc());
            clienteExistente.setCpf(clientePfRequestDTO.getCpf());
            clienteExistente.setNome(clientePfRequestDTO.getNome());
            clienteExistente.setEmail(clientePfRequestDTO.getEmail());
            // Atualize outros campos conforme necessário

            // Chame o serviço para efetuar a atualização no banco de dados
            clientePfService.atualizarCliente(clienteExistente);

            // Adicione o ID do cliente à fila de atendimento
            filaDeAtendimento.adicionarClienteNaFila(clienteExistente.getCpf());

            // Crie um objeto de resposta com os valores atualizados
            ClientePfResponseDTO responseDTO = createResponseDTO(clienteExistente);
            responseDTO.setMessage("Cliente atualizado com sucesso");

            // Retorna uma resposta de sucesso
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (ClienteException.ClienteNaoCadastradoException ex) {
            // Se ocorrer algum erro, você pode lidar com a exceção aqui e retornar uma resposta de erro apropriada (por exemplo, HTTP 400).
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ClientePfResponseDTO("Requisição Inválida ou Cliente Não Cadastrado"));
        }
    }

    @Operation(
            summary = "Consultar Cliente Pf",
            description = "Endpoint para consultar um Cliente Pessoa Física pelo CPF.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente encontrado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientePfResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @GetMapping("/consultar/{cpf}")
    public ResponseEntity<ClientePfResponseDTO> consultarClientePf(@Valid @PathVariable String cpf) {
        try {
            ClientePf clientePf = clientePfService.consultarClientePorCpf(cpf);

            if (clientePf == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ClientePfResponseDTO("Cliente não cadastrado"));
            }

            ClientePfResponseDTO responseDTO = createResponseDTO(clientePf);
            responseDTO.setMessage("Cliente encontrado com sucesso");


            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (ClienteException.ClienteNaoCadastradoException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ClientePfResponseDTO("Requisição Inválida"));
        }
    }


    @Operation(
            summary = "Excluir Cliente Pf",
            description = "Endpoint para excluir um Cliente Pessoa Física pelo CPF.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente excluído com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientePfResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @DeleteMapping("/excluir")
    public ResponseEntity<ClientePfResponseDTO> excluirClientePf(@Valid @RequestParam String cpf) {
        try {
            ClientePf clientePf = clientePfService.consultarClientePorCpf(cpf);

            if (clientePf == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ClientePfResponseDTO("Cliente não encontrado"));
            }

            // Chame o serviço para excluir o cliente
            clientePfService.excluirCliente(cpf);

            ClientePfResponseDTO responseDTO = createResponseDTO(clientePf);
            responseDTO.setMessage("Cliente excluído com sucesso");

            // Retorne uma resposta de sucesso
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (ClienteException.ClienteNaoCadastradoException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ClientePfResponseDTO("Cliente não encontrado"));
        } catch (Exception ex) {
            // Trate qualquer outra exceção que possa ocorrer aqui e retorne uma resposta apropriada, por exemplo, HTTP 500 - Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ClientePfResponseDTO("Erro interno do servidor"));
        }
    }

}
