package models;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Objects;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "habitats")
public class Habitat {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ambiente", nullable = false)
    private TipoAmbiente tipoAmbiente;

    @Column(nullable = false)
    private double tamanho;

    @Column(name = "capacidade_maxima")
    private int capacidadeMaximaAnimais;

    @OneToMany(mappedBy = "habitat", cascade = CascadeType.ALL)
    private List<Animal> animais = new ArrayList<>();

    @Column(name = "publico_acessivel")
    private boolean publicoAcessivel;

    // Constructors
    public Habitat() {
    }

    public Habitat(String nome, TipoAmbiente tipoAmbiente, double tamanho,
            int capacidadeMaximaAnimais, boolean publicoAcessivel) {
        this.nome = nome;
        this.tipoAmbiente = tipoAmbiente;
        this.tamanho = tamanho;
        this.capacidadeMaximaAnimais = capacidadeMaximaAnimais;
        this.publicoAcessivel = publicoAcessivel;
        this.animais = new ArrayList<>();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoAmbiente getTipoAmbiente() {
        return tipoAmbiente;
    }

    public void setTipoAmbiente(TipoAmbiente tipoAmbiente) {
        this.tipoAmbiente = tipoAmbiente;
    }

    public double getTamanho() {
        return tamanho;
    }

    public void setTamanho(double tamanho) {
        this.tamanho = tamanho;
    }

    public int getCapacidadeMaximaAnimais() {
        return capacidadeMaximaAnimais;
    }

    public void setCapacidadeMaximaAnimais(int capacidadeMaximaAnimais) {
        this.capacidadeMaximaAnimais = capacidadeMaximaAnimais;
    }

    public List<Animal> getAnimais() {
        return animais;
    }

    public void setAnimais(List<Animal> animais) {
        this.animais = animais;
    }

    public boolean isPublicoAcessivel() {
        return publicoAcessivel;
    }

    public void setPublicoAcessivel(boolean publicoAcessivel) {
        this.publicoAcessivel = publicoAcessivel;
    }

    // Factory method
    public static Habitat create(String nome, TipoAmbiente tipoAmbiente,
            double tamanho, int capacidadeMaximaAnimais,
            boolean publicoAcessivel) {
        Habitat habitat = new Habitat();
        habitat.nome = nome;
        habitat.tipoAmbiente = tipoAmbiente;
        habitat.tamanho = tamanho;
        habitat.capacidadeMaximaAnimais = capacidadeMaximaAnimais;
        habitat.publicoAcessivel = publicoAcessivel;
        habitat.animais = new ArrayList<>();
        return habitat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Habitat habitat = (Habitat) o;
        return Objects.equals(id, habitat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Habitat{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", tipoAmbiente=" + tipoAmbiente +
                ", tamanho=" + tamanho +
                ", capacidadeMaximaAnimais=" + capacidadeMaximaAnimais +
                ", publicoAcessivel=" + publicoAcessivel +
                '}';
    }

    public enum TipoAmbiente {
        AQUATICO,
        SAVANA,
        FLORESTA,
        DESERTO,
        TUNDRA,
        MONTANHA,
        PANTANAL
    }
}