package models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "manutencoes_habitat")
public class ManutencaoHabitat {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitat_id", nullable = false)
    private Habitat habitat;
    
    @Column(name = "tipo_manutencao", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoManutencao tipoManutencao;
    
    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;
    
    @Column(name = "data_programada")
    private LocalDateTime dataProgramada;
    
    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusManutencao status;
    
    @Column(name = "descricao", length = 1000, nullable = false)
    private String descricao;
    
    @Column(name = "prioridade", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrioridadeManutencao prioridade;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Funcionario solicitante;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsavel_id")
    private Funcionario responsavel;
    
    @Column(name = "observacao_conclusao", length = 1000)
    private String observacaoConclusao;
    
    // Constructors
    public ManutencaoHabitat() {}
    
    public ManutencaoHabitat(Habitat habitat, TipoManutencao tipoManutencao, LocalDateTime dataSolicitacao,
                           LocalDateTime dataProgramada, StatusManutencao status, String descricao,
                           PrioridadeManutencao prioridade, Funcionario solicitante, Funcionario responsavel) {
        this.habitat = habitat;
        this.tipoManutencao = tipoManutencao;
        this.dataSolicitacao = dataSolicitacao;
        this.dataProgramada = dataProgramada;
        this.status = status;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.solicitante = solicitante;
        this.responsavel = responsavel;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Habitat getHabitat() { return habitat; }
    public void setHabitat(Habitat habitat) { this.habitat = habitat; }
    
    public TipoManutencao getTipoManutencao() { return tipoManutencao; }
    public void setTipoManutencao(TipoManutencao tipoManutencao) { this.tipoManutencao = tipoManutencao; }
    
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
    
    public LocalDateTime getDataProgramada() { return dataProgramada; }
    public void setDataProgramada(LocalDateTime dataProgramada) { this.dataProgramada = dataProgramada; }
    
    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
    
    public StatusManutencao getStatus() { return status; }
    public void setStatus(StatusManutencao status) { this.status = status; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public PrioridadeManutencao getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeManutencao prioridade) { this.prioridade = prioridade; }
    
    public Funcionario getSolicitante() { return solicitante; }
    public void setSolicitante(Funcionario solicitante) { this.solicitante = solicitante; }
    
    public Funcionario getResponsavel() { return responsavel; }
    public void setResponsavel(Funcionario responsavel) { this.responsavel = responsavel; }
    
    public String getObservacaoConclusao() { return observacaoConclusao; }
    public void setObservacaoConclusao(String observacaoConclusao) { this.observacaoConclusao = observacaoConclusao; }
    
    // Factory method
    public static ManutencaoHabitat create(Habitat habitat, TipoManutencao tipoManutencao, 
                                          LocalDateTime dataSolicitacao, LocalDateTime dataProgramada, 
                                          String descricao, PrioridadeManutencao prioridade,
                                          Funcionario solicitante) {
        ManutencaoHabitat manutencao = new ManutencaoHabitat();
        manutencao.habitat = habitat;
        manutencao.tipoManutencao = tipoManutencao;
        manutencao.dataSolicitacao = dataSolicitacao;
        manutencao.dataProgramada = dataProgramada;
        manutencao.status = StatusManutencao.PENDENTE;
        manutencao.descricao = descricao;
        manutencao.prioridade = prioridade;
        manutencao.solicitante = solicitante;
        return manutencao;
    }
    
    public void concluir(Funcionario responsavel, LocalDateTime dataConclusao, String observacaoConclusao) {
        this.responsavel = responsavel;
        this.dataConclusao = dataConclusao;
        this.observacaoConclusao = observacaoConclusao;
        this.status = StatusManutencao.CONCLUIDA;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManutencaoHabitat that = (ManutencaoHabitat) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ManutencaoHabitat{" +
                "id=" + id +
                ", habitat=" + habitat.getNome() +
                ", tipoManutencao=" + tipoManutencao +
                ", status=" + status +
                ", prioridade=" + prioridade +
                ", dataProgramada=" + dataProgramada +
                '}';
    }
    
    public enum TipoManutencao {
        LIMPEZA,
        REPARO,
        MODIFICACAO,
        MONITORAMENTO_AMBIENTAL,
        TROCA_SUBSTRATO,
        PAISAGISMO,
        MANUTENCAO_EQUIPAMENTOS
    }
    
    public enum StatusManutencao {
        PENDENTE,
        EM_ANDAMENTO,
        CONCLUIDA,
        CANCELADA
    }
    
    public enum PrioridadeManutencao {
        BAIXA,
        MEDIA,
        ALTA,
        URGENTE
    }
}