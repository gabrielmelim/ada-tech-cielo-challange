package com.cielo.precadastroclientes.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class ClientePf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size( min =1, max = 11, message = "CPF não pode ser nulo e deve conter no máximo 11 dígitos.")
    @Schema(example = "12345678901")
    private String cpf;

    @NotNull
    @Size(min = 1, max = 4, message = "MCC não pode ser nulo e deve ter no máximo 4 caracteres")
    @Schema(example = "4325")
    private String mcc;


    @NotNull
    @Size(min = 1, max = 50, message = "Nome do contato nao deve ser nulo e deve ter no máximo 50 caracteres.")
    @Schema(example = "Nome da pessoa física")
    private String nome;

    @NotNull(message = "O endereço de email não pode ser nulo.")
    @Pattern(
            regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$",
            message = "O endereço de email é inválido."
    )
    @Email(message = "O endereço de email é inválido.")
    @Schema(example = "contato@pessoa.com")
    private String email;

}
