package com.cielo.precadastroclientes.controller;

import com.cielo.precadastroclientes.DTO.ClientePjRequestDTO;
import com.cielo.precadastroclientes.DTO.ClientePjResponseDTO;
import com.cielo.precadastroclientes.DTO.ValidationErrorResponse;
import com.cielo.precadastroclientes.exception.ClienteException;
import com.cielo.precadastroclientes.model.ClientePj;
import com.cielo.precadastroclientes.queue.FilaDeAtendimento;
import com.cielo.precadastroclientes.service.ClientePjServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente-pj")
public class ClientePjController {

    private FilaDeAtendimento filaDeAtendimento;
    private final ClientePjServiceImpl clientePjService;

    @Autowired
    public ClientePjController(ClientePjServiceImpl clientePjService, FilaDeAtendimento filaDeAtendimento) {
        this.clientePjService = clientePjService;
        this.filaDeAtendimento = filaDeAtendimento;
    }

    private ClientePjResponseDTO createResponseDTO(ClientePj clientePj) {
        ClientePjResponseDTO responseDTO = new ClientePjResponseDTO();
        responseDTO.setRazaoSocial(clientePj.getRazaoSocial());
        responseDTO.setCnpj(clientePj.getCnpj());
        responseDTO.setMcc(clientePj.getMcc());
        responseDTO.setCpf(clientePj.getCpfContatoEstabelecimento());
        responseDTO.setNome(clientePj.getNomeContatoEstabelecimento());
        responseDTO.setEmail(clientePj.getEmail());
        return responseDTO;
    }


    @Operation(
            summary = "Cadastrar Cliente Pj",
            description = "Cadastrar Cliente PJ",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Cliente cadastrado com sucesso",
                            content = @Content(mediaType = "application/json")
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
    public ResponseEntity<ClientePjResponseDTO> cadastrarClientePj(@Valid @RequestBody ClientePjRequestDTO clientePjRequestDTO) {
        try {

            // Use o método público para converter o DTO em uma entidade ClientePj
            ClientePj clientePj = clientePjService.convertRequestDTOToEntity(clientePjRequestDTO);
            // Chame o serviço para cadastrar o cliente PJ
            clientePjService.cadastrarClientePj(clientePj);

            // Adicione o ID do cliente à fila de atendimento
            filaDeAtendimento.adicionarClienteNaFila(clientePj.getCnpj());

            //Crie um objeto de resposta e preencha-o com os dados relevantes
            ClientePjResponseDTO responseDTO = createResponseDTO(clientePj);
            responseDTO.setMessage("Cliente cadastrado com sucesso");

            //retorna um responsebody DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (ClienteException.ClienteExistenteException ex) {
            // Se ocorrer algum erro, você pode lidar com a exceção aqui e retornar uma resposta de erro apropriada (por exemplo, HTTP 400 ou 409).
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ClientePjResponseDTO("Cliente já cadastrado"));
        }
    }

    @Operation(
            summary = "Atualizar Cliente Pj",
            description = "Cadastrar Cliente PJ",
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
    public ResponseEntity<ClientePjResponseDTO> atualizarClientePj(@Valid @RequestBody ClientePjRequestDTO clientePjRequestDTO) {
        try {

            // Verifique se o cliente já existe com base no CNPJ
            ClientePj clienteExistente = clientePjService.consultarClientePorCnpj(clientePjRequestDTO.getCnpj());

            // Se o cliente não existir, retorne uma resposta de erro
            if (clienteExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ClientePjResponseDTO("Cliente não cadastrado"));
            }

            // Atualize os campos relevantes do cliente com base nos dados do DTO
            clienteExistente.setMcc(clientePjRequestDTO.getMcc());
            clienteExistente.setCnpj(clientePjRequestDTO.getCnpj());
            clienteExistente.setCpfContatoEstabelecimento(clientePjRequestDTO.getCpf());
            clienteExistente.setRazaoSocial(clientePjRequestDTO.getRazaoSocial());
            clienteExistente.setNomeContatoEstabelecimento(clientePjRequestDTO.getNome());
            clienteExistente.setEmail(clientePjRequestDTO.getEmail());
            // Atualize outros campos conforme necessário

            // Chame o serviço para efetuar a atualização no banco de dados
            clientePjService.atualizarCliente(clienteExistente);

            // Adicione o ID do cliente à fila de atendimento
            filaDeAtendimento.adicionarClienteNaFila(clienteExistente.getCnpj());

            // Crie um objeto de resposta com os valores atualizados
            ClientePjResponseDTO responseDTO = createResponseDTO(clienteExistente);
            responseDTO.setMessage("Cliente atualizado com sucesso");

            // Retorna uma resposta de sucesso
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (ClienteException.ClienteNaoCadastradoException ex) {
            // Se ocorrer algum erro, você pode lidar com a exceção aqui e retornar uma resposta de erro apropriada (por exemplo, HTTP 400).
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ClientePjResponseDTO("Requisição Inválida ou Cliente Não Cadastrado"));
        }
    }


    @Operation(
            summary = "Consultar Cliente PJ",
            description = "Endpoint para consultar um Cliente Pessoa Jurídica pelo CNPJ.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente encontrado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientePjResponseDTO.class))
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
    @GetMapping("/consultar/{cnpj}")
    public ResponseEntity<ClientePjResponseDTO> consultarClientePj(@Valid @PathVariable String cnpj) {
        try {
            ClientePj clientePj = clientePjService.consultarClientePorCnpj(cnpj);

            if (clientePj == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ClientePjResponseDTO("Cliente não cadastrado"));
            }

            ClientePjResponseDTO responseDTO = createResponseDTO(clientePj);
            responseDTO.setMessage("Cliente encontrado com sucesso");


            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (ClienteException.ClienteNaoCadastradoException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ClientePjResponseDTO("Requisição Inválida"));
        }
    }


    @Operation(
            summary = "Excluir Cliente PJ",
            description = "Endpoint para excluir um Cliente Pessoa Jurídica pelo CNPJ.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente excluído com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientePjResponseDTO.class))
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
    public ResponseEntity<ClientePjResponseDTO> excluirClientePj(@Valid @RequestParam String cnpj) {
        try {
            ClientePj clientePj = clientePjService.consultarClientePorCnpj(cnpj);

            if (clientePj == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ClientePjResponseDTO("Cliente não encontrado"));
            }

            // Chame o serviço para excluir o cliente
            clientePjService.excluirClientePorCnpj(cnpj);

            ClientePjResponseDTO responseDTO = createResponseDTO(clientePj);
            responseDTO.setMessage("Cliente excluído com sucesso");

            // Retorne uma resposta de sucesso
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (ClienteException.ClienteNaoCadastradoException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ClientePjResponseDTO("Cliente não encontrado"));
        } catch (Exception ex) {
            // Trate qualquer outra exceção que possa ocorrer aqui e retorne uma resposta apropriada, por exemplo, HTTP 500 - Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ClientePjResponseDTO("Erro interno do servidor"));
        }

    }

}


