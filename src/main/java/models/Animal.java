package models;

import java.util.UUID;
import java.util.Objects;
import java.time.LocalDate;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "animais")
public class Animal {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String especie;
    
    @Column(name = "nome_cientifico")
    private String nomeCientifico;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Classe classe;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_saude")
    private StatusSaude statusSaude;
    
    @Column(name = "detalhes_saude", length = 1000)
    private String detalhesSaude;
    
    @Column(name = "data_chegada")
    private LocalDate dataChegada;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitat_id")
    private Habitat habitat;
    
    // Constructors
    public Animal() {}
    
    public Animal(String nome, String especie, String nomeCientifico, Classe classe,
                 Genero genero, StatusSaude statusSaude, String detalhesSaude, 
                 LocalDate dataChegada, Habitat habitat) {
        this.nome = nome;
        this.especie = especie;
        this.nomeCientifico = nomeCientifico;
        this.classe = classe;
        this.genero = genero;
        this.statusSaude = statusSaude;
        this.detalhesSaude = detalhesSaude;
        this.dataChegada = dataChegada;
        this.habitat = habitat;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    
    public String getNomeCientifico() { return nomeCientifico; }
    public void setNomeCientifico(String nomeCientifico) { this.nomeCientifico = nomeCientifico; }
    
    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }
    
    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }
    
    public StatusSaude getStatusSaude() { return statusSaude; }
    public void setStatusSaude(StatusSaude statusSaude) { this.statusSaude = statusSaude; }
    
    public String getDetalhesSaude() { return detalhesSaude; }
    public void setDetalhesSaude(String detalhesSaude) { this.detalhesSaude = detalhesSaude; }
    
    public LocalDate getDataChegada() { return dataChegada; }
    public void setDataChegada(LocalDate dataChegada) { this.dataChegada = dataChegada; }
    
    public Habitat getHabitat() { return habitat; }
    public void setHabitat(Habitat habitat) { this.habitat = habitat; }
    
    // Factory method 
    public static Animal create(String nome, String especie, String nomeCientifico, 
                               Classe classe, Genero genero, StatusSaude statusSaude,
                               String detalhesSaude, LocalDate dataChegada, Habitat habitat) {
        Animal animal = new Animal();
        animal.nome = nome;
        animal.especie = especie;
        animal.nomeCientifico = nomeCientifico;
        animal.classe = classe;
        animal.genero = genero;
        animal.statusSaude = statusSaude;
        animal.detalhesSaude = detalhesSaude;
        animal.dataChegada = dataChegada;
        animal.habitat = habitat;
        return animal;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especie='" + especie + '\'' +
                ", classe=" + classe +
                ", genero=" + genero +
                ", statusSaude=" + statusSaude +
                '}';
    }
    
    public enum Classe {
        MAMIFERO,
        REPTIL,
        AVE,
        PEIXE,
        ANFIBIO,
        INSETO,
        ARACNIDEO
    }
    
    public enum Genero {
        MASCULINO,
        FEMININO
    }
    
    public enum StatusSaude {
        SAUDAVEL,
        EM_TRATAMENTO
    }
}