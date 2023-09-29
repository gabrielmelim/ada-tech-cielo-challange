package com.cielo.precadastroclientes.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class ClientePj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size( min =1, max = 14, message = "CNPJ não pode ser nulo e deve conter no máximo 14 dígitos.")
    @Schema(example = "12345678912354")
    private String cnpj;

    @NotNull
    @Size(min = 1, max = 50, message = "A razão social não deve ser nula e deve ter no máximo 50 caracteres.")
    @Schema(example = "Nome da Empresa")
    private String razaoSocial;

    @NotNull
    @Size(min = 1, max = 4, message = "MCC não pode ser nulo e deve ter no máximo 4 caracteres")
    @Schema(example = "1232")
    private String mcc;

    @NotNull
    @Size( min =1, max = 11, message = "CPF não pode ser nulo e deve conter no máximo 11 dígitos.")
    @Schema(example = "12345678901")
    private String cpfContatoEstabelecimento;

    @NotNull
    @Size(min = 1, max = 50, message = "Nome do contato nao deve ser nulo e deve ter no máximo 50 caracteres.")
    @Schema(example = "Nome do Contato")
    private String nomeContatoEstabelecimento;

    @NotNull(message = "O endereço de email não pode ser nulo.")
    @Pattern(
            regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$",
            message = "O endereço de email é inválido."
    )
    @Email(message = "O endereço de email é inválido.")
    @Schema(example = "contato@empresa.com")
    private String email;
}
