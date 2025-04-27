package models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "consultas_veterinarias")
public class ConsultaVeterinaria {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Funcionario veterinario;
    
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    
    @Column(nullable = false, length = 1000)
    private String diagnostico;
    
    @Column(length = 1000)
    private String tratamento;
    
    @Column(length = 500)
    private String medicamentos;
    
    @Column(name = "acompanhamento_necessario")
    private boolean acompanhamentoNecessario;
    
    @Column(name = "data_retorno")
    private LocalDateTime dataRetorno;
    
    @Column(length = 1000)
    private String observacoes;
    
    // Constructors
    public ConsultaVeterinaria() {}
    
    public ConsultaVeterinaria(Animal animal, Funcionario veterinario, LocalDateTime dataHora,
                               String diagnostico, String tratamento, String medicamentos,
                               boolean acompanhamentoNecessario, LocalDateTime dataRetorno,
                               String observacoes) {
        this.animal = animal;
        this.veterinario = veterinario;
        this.dataHora = dataHora;
        this.diagnostico = diagnostico;
        this.tratamento = tratamento;
        this.medicamentos = medicamentos;
        this.acompanhamentoNecessario = acompanhamentoNecessario;
        this.dataRetorno = dataRetorno;
        this.observacoes = observacoes;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    
    public Funcionario getVeterinario() { return veterinario; }
    public void setVeterinario(Funcionario veterinario) { this.veterinario = veterinario; }
    
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    
    public String getTratamento() { return tratamento; }
    public void setTratamento(String tratamento) { this.tratamento = tratamento; }
    
    public String getMedicamentos() { return medicamentos; }
    public void setMedicamentos(String medicamentos) { this.medicamentos = medicamentos; }
    
    public boolean isAcompanhamentoNecessario() { return acompanhamentoNecessario; }
    public void setAcompanhamentoNecessario(boolean acompanhamentoNecessario) { this.acompanhamentoNecessario = acompanhamentoNecessario; }
    
    public LocalDateTime getDataRetorno() { return dataRetorno; }
    public void setDataRetorno(LocalDateTime dataRetorno) { this.dataRetorno = dataRetorno; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    // Factory method
    public static ConsultaVeterinaria create(Animal animal, Funcionario veterinario, LocalDateTime dataHora,
                                           String diagnostico, String tratamento, String medicamentos,
                                           boolean acompanhamentoNecessario, LocalDateTime dataRetorno,
                                           String observacoes) {
        ConsultaVeterinaria consulta = new ConsultaVeterinaria();
        consulta.animal = animal;
        consulta.veterinario = veterinario;
        consulta.dataHora = dataHora;
        consulta.diagnostico = diagnostico;
        consulta.tratamento = tratamento;
        consulta.medicamentos = medicamentos;
        consulta.acompanhamentoNecessario = acompanhamentoNecessario;
        consulta.dataRetorno = dataRetorno;
        consulta.observacoes = observacoes;
        return consulta;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsultaVeterinaria that = (ConsultaVeterinaria) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ConsultaVeterinaria{" +
                "id=" + id +
                ", animal=" + animal.getNome() +
                ", veterinario=" + veterinario.getNome() +
                ", dataHora=" + dataHora +
                ", diagnostico='" + diagnostico + '\'' +
                '}';
    }
}