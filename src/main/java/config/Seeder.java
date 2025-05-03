package config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import models.*;
import models.Animal.Classe;
import models.Animal.Genero;
import models.Animal.StatusSaude;
import models.Funcionario.Cargo;
import models.Habitat.TipoAmbiente;
import models.ManutencaoHabitat.PrioridadeManutencao;
import models.ManutencaoHabitat.TipoManutencao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Seeder {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("zoologicoPU");
    EntityManager em = emf.createEntityManager();

    em.getTransaction().begin();

    // Usuários já existentes (IDs fixos)
    Customer visitante = em.find(Customer.class, UUID.fromString("05e81bf8-03ee-449b-a443-98301d6c9ea0"));
    Funcionario admin = em.find(Funcionario.class, UUID.fromString("12ad950a-0967-4ced-ace4-3d38b1dbbde7"));
    Funcionario veterinario = em.find(Funcionario.class, UUID.fromString("89ffea78-564c-409c-80ff-ebf2bc9b61bf"));
    Funcionario tratador = em.find(Funcionario.class, UUID.fromString("a425a2da-ebe4-4548-943e-a5840cbec40d"));
    Funcionario manutencao = em.find(Funcionario.class, UUID.fromString("104d0d21-d433-45fd-80ec-b9f791931580"));

    // Se não existirem, crie-os (ajuste conforme seu construtor)
    if (visitante == null) {
      visitante = new Customer();
      visitante.setId(UUID.fromString("05e81bf8-03ee-449b-a443-98301d6c9ea0"));
      visitante.setNome("Visitante Teste");
      visitante.setEmail("visitante@visitante.com");
      visitante.setPassword("123");
      em.persist(visitante);
    }
    if (admin == null) {
      admin = Funcionario.create("Administrador", "admin@admin.com", "123", Cargo.ADMINISTRADOR);
      admin.setId(UUID.fromString("12ad950a-0967-4ced-ace4-3d38b1dbbde7"));
      em.persist(admin);
    }
    if (veterinario == null) {
      veterinario = Funcionario.create("Veterinario", "veterinario@veterinario.com", "123", Cargo.VETERINARIO);
      veterinario.setId(UUID.fromString("89ffea78-564c-409c-80ff-ebf2bc9b61bf"));
      em.persist(veterinario);
    }
    if (tratador == null) {
      tratador = Funcionario.create("Tratador", "tratador@tratador.com", "123", Cargo.TRATADOR);
      tratador.setId(UUID.fromString("a425a2da-ebe4-4548-943e-a5840cbec40d"));
      em.persist(tratador);
    }
    if (manutencao == null) {
      manutencao = Funcionario.create("Manutencao", "manutencao@manutencao.com", "123", Cargo.MANUTENCAO);
      manutencao.setId(UUID.fromString("104d0d21-d433-45fd-80ec-b9f791931580"));
      em.persist(manutencao);
    }

    // Habitats
    List<Habitat> habitats = new ArrayList<>();
    for (TipoAmbiente tipo : TipoAmbiente.values()) {
      Habitat h = Habitat.create(
          tipo.name() + " Habitat",
          tipo,
          500 + new Random().nextInt(500),
          10 + new Random().nextInt(10),
          true);
      em.persist(h);
      habitats.add(h);
    }

    // Animais
    List<Animal> animais = new ArrayList<>();
    for (int i = 1; i <= 20; i++) {
      Classe classe = Classe.values()[i % Classe.values().length];
      Genero genero = (i % 2 == 0) ? Genero.MASCULINO : Genero.FEMININO;
      StatusSaude status = (i % 3 == 0) ? StatusSaude.EM_TRATAMENTO : StatusSaude.SAUDAVEL;
      Habitat habitat = habitats.get(i % habitats.size());
      Animal a = Animal.create(
          "Animal " + i,
          "Especie " + ((i % 5) + 1),
          "NomeCientifico" + i,
          classe,
          genero,
          status,
          status == StatusSaude.EM_TRATAMENTO ? "Em observação" : "Saudável",
          LocalDate.now().minusDays(i * 10),
          habitat);
      em.persist(a);
      animais.add(a);
    }

    // Alimentações
    for (Animal a : animais) {
      for (int j = 0; j < 3; j++) {
        Alimentacao ali = Alimentacao.create(
            a,
            "Alimento " + ((j % 4) + 1),
            2.0 + j,
            "kg",
            LocalDateTime.now().minusDays(j),
            tratador,
            "Observação " + j);
        em.persist(ali);
      }
    }

    // Consultas Veterinárias
    for (Animal a : animais) {
      if (a.getStatusSaude() == StatusSaude.EM_TRATAMENTO) {
        ConsultaVeterinaria c = ConsultaVeterinaria.create(
            a,
            veterinario,
            LocalDateTime.now().minusDays(5),
            "Diagnóstico para " + a.getNome(),
            "Tratamento padrão",
            "Medicamento X",
            true,
            LocalDateTime.now().plusDays(10),
            "Acompanhamento necessário");
        em.persist(c);
      }
    }

    // Manutenções de Habitat
    for (Habitat h : habitats) {
      for (int k = 0; k < 2; k++) {
        ManutencaoHabitat m = ManutencaoHabitat.create(
            h,
            TipoManutencao.values()[k % TipoManutencao.values().length],
            LocalDateTime.now().minusDays(k * 7),
            LocalDateTime.now().plusDays(k * 7),
            "Manutenção " + k + " para " + h.getNome(),
            PrioridadeManutencao.values()[k % PrioridadeManutencao.values().length],
            manutencao);
        em.persist(m);
      }
    }

    em.getTransaction().commit();
    em.close();
    emf.close();

    System.out.println("Banco populado com sucesso!");
  }
}