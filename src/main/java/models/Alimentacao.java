package models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "alimentacoes")
public class Alimentacao {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;
    
    @Column(name = "tipo_alimento", nullable = false)
    private String tipoAlimento;
    
    @Column(name = "quantidade", nullable = false)
    private Double quantidade;
    
    @Column(name = "unidade_medida")
    private String unidadeMedida;
    
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionarioResponsavel;
    
    @Column(name = "observacoes", length = 500)
    private String observacoes;
    
    // Constructors
    public Alimentacao() {}
    
    public Alimentacao(Animal animal, String tipoAlimento, Double quantidade, 
                      String unidadeMedida, LocalDateTime dataHora, 
                      Funcionario funcionarioResponsavel, String observacoes) {
        this.animal = animal;
        this.tipoAlimento = tipoAlimento;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.dataHora = dataHora;
        this.funcionarioResponsavel = funcionarioResponsavel;
        this.observacoes = observacoes;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    
    public String getTipoAlimento() { return tipoAlimento; }
    public void setTipoAlimento(String tipoAlimento) { this.tipoAlimento = tipoAlimento; }
    
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }
    
    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
    
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    
    public Funcionario getFuncionarioResponsavel() { return funcionarioResponsavel; }
    public void setFuncionarioResponsavel(Funcionario funcionarioResponsavel) { this.funcionarioResponsavel = funcionarioResponsavel; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    // Factory method
    public static Alimentacao create(Animal animal, String tipoAlimento, Double quantidade, 
                                    String unidadeMedida, LocalDateTime dataHora, 
                                    Funcionario funcionarioResponsavel, String observacoes) {
        Alimentacao alimentacao = new Alimentacao();
        alimentacao.animal = animal;
        alimentacao.tipoAlimento = tipoAlimento;
        alimentacao.quantidade = quantidade;
        alimentacao.unidadeMedida = unidadeMedida;
        alimentacao.dataHora = dataHora;
        alimentacao.funcionarioResponsavel = funcionarioResponsavel;
        alimentacao.observacoes = observacoes;
        return alimentacao;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alimentacao that = (Alimentacao) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Alimentacao{" +
                "id=" + id +
                ", animal=" + animal.getNome() +
                ", tipoAlimento='" + tipoAlimento + '\'' +
                ", dataHora=" + dataHora +
                ", funcionarioResponsavel=" + funcionarioResponsavel.getNome() +
                '}';
    }
}