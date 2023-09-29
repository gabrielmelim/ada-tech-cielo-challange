package com.cielo.precadastroclientes.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientePjResponseDTO {

    @Schema(example = "12345678912354")
    @Size( min =1, max = 14, message = "CNPJ não pode ser nulo e deve conter no máximo 14 dígitos.")
    private String cnpj;

    @Size( min =1, max = 11, message = "CPF não pode ser nulo e deve conter no máximo 11 dígitos.")
    @Schema(example = "12345678901")
    private String cpf;
    @Schema(example = "1232")
    @Size(min = 1, max = 4, message = "MCC não pode ser nulo e deve ter no máximo 4 caracteres")
    private String mcc;

    @Schema(example = "Nome da Empresa")
    @Size(min = 1, max = 50, message = "A razão social não deve ser nula e deve ter no máximo 50 caracteres.")
    private String razaoSocial;

    @Size(min = 1, max = 50, message = "Nome do contato do estabelecimento nao deve ser nulo e deve ter no máximo 50 caracteres.")
    @Schema(example = "Gabriel Melim")
    private String nome;

    @NotNull(message = "O endereço de email não pode ser nulo.")
    @Schema(example = "contato@empresa.com")
    @Email(message = "O endereço de email é inválido.")
    @Pattern(
            regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$",
            message = "O endereço de email é inválido."
    )
    private String email;


    private String message;
    public ClientePjResponseDTO(String errorMessage) {
        this.message = errorMessage;
    }

}
