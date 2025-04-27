package dtos.alimentacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlimentacaoCreateRequest(
    UUID animalId,
    String tipoAlimento,
    Double quantidade,
    String unidadeMedida,
    LocalDateTime dataHora,
    UUID funcionarioId,
    String observacoes
) {}