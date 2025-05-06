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
import models.ManutencaoHabitat.StatusManutencao;
import models.Ingresso.TipoIngresso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Seeder {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("zoologicoPU");
    EntityManager em = emf.createEntityManager();

    em.getTransaction().begin();

    // Limpar tabelas (exceto Customer e Funcionario)
    em.createQuery("DELETE FROM Alimentacao").executeUpdate();
    em.createQuery("DELETE FROM ConsultaVeterinaria").executeUpdate();
    em.createQuery("DELETE FROM ManutencaoHabitat").executeUpdate();
    em.createQuery("DELETE FROM Animal").executeUpdate();
    em.createQuery("DELETE FROM Habitat").executeUpdate();

    // Usuários já existentes (IDs fixos)
    Customer visitante = em.find(Customer.class, UUID.fromString("05e81bf8-03ee-449b-a443-98301d6c9ea0"));
    Funcionario admin = em.find(Funcionario.class, UUID.fromString("12ad950a-0967-4ced-ace4-3d38b1dbbde7"));
    Funcionario veterinario = em.find(Funcionario.class, UUID.fromString("89ffea78-564c-409c-80ff-ebf2bc9b61bf"));
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
    if (manutencao == null) {
      manutencao = Funcionario.create("Manutencao", "manutencao@manutencao.com", "123", Cargo.MANUTENCAO);
      manutencao.setId(UUID.fromString("104d0d21-d433-45fd-80ec-b9f791931580"));
      em.persist(manutencao);
    }

    // Habitats realistas (ajustados para os tipos do enum)
    Habitat[] habitats = {
        Habitat.create("Savana Africana", TipoAmbiente.SAVANA, 1200, 15, true),
        Habitat.create("Floresta Amazônica", TipoAmbiente.FLORESTA, 900, 12, true),
        Habitat.create("Pantanal", TipoAmbiente.PANTANAL, 800, 10, true),
        Habitat.create("Deserto do Saara", TipoAmbiente.DESERTO, 700, 8, false),
        Habitat.create("Montanhas Rochosas", TipoAmbiente.MONTANHA, 500, 20, true),
        Habitat.create("Aquário Oceânico", TipoAmbiente.AQUATICO, 600, 25, true)
    };
    for (Habitat h : habitats)
      em.persist(h);

    // Animais realistas (corrigido: nome, especie, nomeCientifico)
    Animal[] animais = {
        Animal.create("Leão", "Leão", "Panthera leo", Classe.MAMIFERO, Genero.MASCULINO, StatusSaude.SAUDAVEL,
            "Saudável", LocalDate.of(2020, 3, 15), habitats[0]),
        Animal.create("Elefante Africano", "Elefante Africano", "Loxodonta africana", Classe.MAMIFERO, Genero.FEMININO,
            StatusSaude.SAUDAVEL, "Saudável", LocalDate.of(2018, 7, 10), habitats[0]),
        Animal.create("Arara Azul", "Arara Azul", "Anodorhynchus hyacinthinus", Classe.AVE, Genero.FEMININO,
            StatusSaude.SAUDAVEL, "Saudável", LocalDate.of(2021, 1, 5), habitats[1]),
        Animal.create("Jacaré-do-Pantanal", "Jacaré-do-Pantanal", "Caiman yacare", Classe.REPTIL, Genero.MASCULINO,
            StatusSaude.EM_TRATAMENTO, "Ferimento na pata", LocalDate.of(2019, 11, 20), habitats[2]),
        Animal.create("Tubarão Branco", "Tubarão Branco", "Carcharodon carcharias", Classe.PEIXE, Genero.MASCULINO,
            StatusSaude.SAUDAVEL, "Saudável", LocalDate.of(2022, 6, 30), habitats[5]),
        Animal.create("Pinguim-de-Magalhães", "Pinguim-de-Magalhães", "Spheniscus magellanicus", Classe.AVE,
            Genero.FEMININO,
            StatusSaude.SAUDAVEL, "Saudável", LocalDate.of(2020, 9, 12), habitats[5]),
        Animal.create("Cobra Sucuri", "Sucuri", "Eunectes murinus", Classe.REPTIL, Genero.FEMININO,
            StatusSaude.SAUDAVEL, "Saudável", LocalDate.of(2017, 4, 18), habitats[2]),
        Animal.create("Águia Harpia", "Águia Harpia", "Harpia harpyja", Classe.AVE, Genero.MASCULINO,
            StatusSaude.SAUDAVEL,
            "Saudável", LocalDate.of(2021, 8, 22), habitats[4]),
        Animal.create("Camelo", "Camelo", "Camelus dromedarius", Classe.MAMIFERO, Genero.MASCULINO,
            StatusSaude.SAUDAVEL, "Saudável", LocalDate.of(2016, 2, 14), habitats[3]),
        Animal.create("Tartaruga-da-Amazônia", "Tartaruga-da-Amazônia", "Podocnemis expansa", Classe.REPTIL,
            Genero.FEMININO, StatusSaude.EM_TRATAMENTO, "Casco rachado", LocalDate.of(2018, 12, 1), habitats[2])
    };
    for (Animal a : animais)
      em.persist(a);

    // Alimentações realistas
    for (Animal a : animais) {
      Alimentacao ali = Alimentacao.create(
          a,
          a.getClasse() == Classe.MAMIFERO ? "Frutas e carne" : "Peixes e vegetais",
          3.0,
          "kg",
          LocalDateTime.now().minusDays(1),
          veterinario,
          "Alimentação regular");
      em.persist(ali);
    }

    // Consultas Veterinárias realistas
    // 1. Consultas para animais em tratamento
    for (Animal a : animais) {
      if (a.getStatusSaude() == StatusSaude.EM_TRATAMENTO) {
        // Consulta inicial (já realizada)
        ConsultaVeterinaria consulta1 = new ConsultaVeterinaria();
        consulta1.setAnimal(a);
        consulta1.setVeterinario(veterinario);
        consulta1.setDataHora(LocalDateTime.now().minusDays(7));
        consulta1.setTipoConsulta(ConsultaVeterinaria.TipoConsulta.EMERGENCIA);
        consulta1.setStatus(ConsultaVeterinaria.StatusConsulta.CONCLUIDA);
        consulta1.setDiagnostico("Diagnóstico inicial: " + a.getDetalhesSaude());
        consulta1.setTratamento("Medicação e cuidados intensivos");
        consulta1.setMedicamentos("Antibiótico de amplo espectro");
        consulta1.setObservacoes("Animal chegou em condição preocupante");
        consulta1.setAcompanhamentoNecessario(true);
        consulta1.setDataRetorno(LocalDateTime.now().minusDays(3));
        em.persist(consulta1);

        // Consulta de acompanhamento (já realizada)
        ConsultaVeterinaria consulta2 = new ConsultaVeterinaria();
        consulta2.setAnimal(a);
        consulta2.setVeterinario(veterinario);
        consulta2.setDataHora(LocalDateTime.now().minusDays(3));
        consulta2.setTipoConsulta(ConsultaVeterinaria.TipoConsulta.TRATAMENTO);
        consulta2.setStatus(ConsultaVeterinaria.StatusConsulta.CONCLUIDA);
        consulta2.setDiagnostico("Evolução positiva, mas ainda em tratamento");
        consulta2.setTratamento("Continuar medicação e iniciar fisioterapia");
        consulta2.setMedicamentos("Antibiótico tópico e anti-inflamatório");
        consulta2.setObservacoes("Resposta boa ao tratamento inicial");
        consulta2.setAcompanhamentoNecessario(true);
        consulta2.setDataRetorno(LocalDateTime.now().plusDays(7));
        em.persist(consulta2);
      }
    }

    // 2. Consultas de rotina para animais saudáveis (consultas passadas)
    for (int i = 0; i < 3; i++) {
      Animal animal = animais[i];
      if (animal.getStatusSaude() == StatusSaude.SAUDAVEL) {
        ConsultaVeterinaria consulta = new ConsultaVeterinaria();
        consulta.setAnimal(animal);
        consulta.setVeterinario(veterinario);
        consulta.setDataHora(LocalDateTime.now().minusDays(30 + i * 2));
        consulta.setTipoConsulta(ConsultaVeterinaria.TipoConsulta.ROTINA);
        consulta.setStatus(ConsultaVeterinaria.StatusConsulta.CONCLUIDA);
        consulta.setDiagnostico("Animal em boas condições de saúde");
        consulta.setTratamento("Nenhum tratamento necessário");
        consulta.setMedicamentos("Suplementos vitamínicos preventivos");
        consulta.setObservacoes("Exame de rotina sem intercorrências");
        consulta.setAcompanhamentoNecessario(false);
        em.persist(consulta);
      }
    }

    // 3. Consultas agendadas para o futuro (próximos dias)
    for (int i = 3; i < 6; i++) {
      if (i < animais.length) {
        Animal animal = animais[i];
        ConsultaVeterinaria consulta = new ConsultaVeterinaria();
        consulta.setAnimal(animal);
        consulta.setVeterinario(veterinario);
        consulta.setDataHora(LocalDateTime.now().plusDays(i));
        consulta.setTipoConsulta(
            i % 2 == 0 ? ConsultaVeterinaria.TipoConsulta.ROTINA : ConsultaVeterinaria.TipoConsulta.EXAME);
        consulta.setStatus(ConsultaVeterinaria.StatusConsulta.AGENDADA);
        consulta.setDiagnostico("");
        consulta.setTratamento("");
        consulta.setMedicamentos("");
        consulta.setObservacoes("Consulta agendada para check-up regular");
        consulta.setAcompanhamentoNecessario(false);
        em.persist(consulta);
      }
    }

    // 4. Uma consulta em andamento para hoje
    if (animais.length > 6) {
      Animal animal = animais[6];
      ConsultaVeterinaria consulta = new ConsultaVeterinaria();
      consulta.setAnimal(animal);
      consulta.setVeterinario(veterinario);
      consulta.setDataHora(LocalDateTime.now().minusHours(2));
      consulta.setTipoConsulta(ConsultaVeterinaria.TipoConsulta.CIRURGIA);
      consulta.setStatus(ConsultaVeterinaria.StatusConsulta.EM_ANDAMENTO);
      consulta.setDiagnostico("Necessidade de pequena intervenção cirúrgica");
      consulta.setTratamento("Procedimento cirúrgico menor");
      consulta.setMedicamentos("Anestésicos e antibióticos");
      consulta.setObservacoes("Procedimento em andamento");
      consulta.setAcompanhamentoNecessario(true);
      consulta.setDataRetorno(LocalDateTime.now().plusDays(10));
      em.persist(consulta);
    }

    // 5. Uma consulta cancelada
    if (animais.length > 7) {
      Animal animal = animais[7];
      ConsultaVeterinaria consulta = new ConsultaVeterinaria();
      consulta.setAnimal(animal);
      consulta.setVeterinario(veterinario);
      consulta.setDataHora(LocalDateTime.now().minusDays(1));
      consulta.setTipoConsulta(ConsultaVeterinaria.TipoConsulta.ROTINA);
      consulta.setStatus(ConsultaVeterinaria.StatusConsulta.CANCELADA);
      consulta.setDiagnostico("");
      consulta.setTratamento("");
      consulta.setMedicamentos("");
      consulta.setObservacoes("Consulta cancelada devido a emergência com outro animal");
      consulta.setAcompanhamentoNecessario(false);
      em.persist(consulta);
    }

    // Manutenções de Habitat realistas (uma de cada tipo/status/prioridade)
    ManutencaoHabitat.TipoManutencao[] tipos = ManutencaoHabitat.TipoManutencao.values();
    ManutencaoHabitat.StatusManutencao[] statusArr = ManutencaoHabitat.StatusManutencao.values();
    ManutencaoHabitat.PrioridadeManutencao[] prioridades = ManutencaoHabitat.PrioridadeManutencao.values();

    int idx = 0;
    for (Habitat h : habitats) {
      // Garante que teremos pelo menos uma de cada tipo/status/prioridade
      ManutencaoHabitat.TipoManutencao tipo = tipos[idx % tipos.length];
      ManutencaoHabitat.StatusManutencao status = statusArr[idx % statusArr.length];
      ManutencaoHabitat.PrioridadeManutencao prioridade = prioridades[idx % prioridades.length];

      LocalDateTime dataSolicitacao = LocalDateTime.now().minusDays(2 + idx);
      LocalDateTime dataProgramada = LocalDateTime.now().plusDays(2 + idx);
      LocalDateTime dataConclusao = (status == StatusManutencao.CONCLUIDA) ? LocalDateTime.now().minusDays(1) : null;
      Funcionario responsavel = manutencao;

      ManutencaoHabitat m = new ManutencaoHabitat(
          h,
          tipo,
          dataSolicitacao,
          dataProgramada,
          status,
          "Solicitação de " + tipo.name().toLowerCase().replace('_', ' '),
          prioridade,
          manutencao,
          responsavel);
      if (status == StatusManutencao.CONCLUIDA) {
        m.setDataConclusao(dataConclusao);
        m.setObservacaoConclusao("Serviço concluído com sucesso.");
      }
      em.persist(m);
      idx++;
    }

    // Ingressos para o visitante
    for (int i = 0; i < 3; i++) {
      Ingresso ingresso = Ingresso.create(
          TipoIngresso.ADULTO,
          50.0,
          visitante);
      ingresso.setUtilizado(i == 0);
      em.persist(ingresso);
    }

    em.getTransaction().commit();
    em.close();
    emf.close();

    System.out.println("Banco populado com sucesso!");
  }
}