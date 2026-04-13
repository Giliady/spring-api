package com.cora.api.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação de uma conta bancária")
public record CreateAccountRequest(
        @Schema(description = "Nome do titular", example = "Nome do Usuário") String name,
        @Schema(description = "CPF do titular (texto livre)", example = "12345678901") String cpf
) {
}
