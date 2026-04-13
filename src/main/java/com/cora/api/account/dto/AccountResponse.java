package com.cora.api.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados da conta bancária")
public record AccountResponse(
        @Schema(description = "ID gerado automaticamente", example = "1") Long id,
        @Schema(description = "Nome do titular", example = "Nome do Usuário") String name,
        @Schema(description = "CPF do titular", example = "12345678901") String cpf
) {
}
