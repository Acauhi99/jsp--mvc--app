package dtos.alimentacao;

import models.Alimentacao;
import models.Animal;
import models.Funcionario;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlimentacaoResponse(
    UUID id,
    UUID animalId,
    String animalNome,
    String tipoAlimento,
    Double quantidade,
    String unidadeMedida,
    LocalDateTime dataHora,
    UUID funcionarioId,
    String funcionarioNome,
    String observacoes
) {
    public static AlimentacaoResponse fromEntity(Alimentacao alimentacao) {
        Animal animal = alimentacao.getAnimal();
        Funcionario funcionario = alimentacao.getFuncionarioResponsavel();
        
        return new AlimentacaoResponse(
            alimentacao.getId(),
            animal.getId(),
            animal.getNome(),
            alimentacao.getTipoAlimento(),
            alimentacao.getQuantidade(),
            alimentacao.getUnidadeMedida(),
            alimentacao.getDataHora(),
            funcionario.getId(),
            funcionario.getNome(),
            alimentacao.getObservacoes()
        );
    }
}