# Sistema de Gerenciamento de Zoológico

Este projeto é uma aplicação web para gerenciamento de zoológico construída usando Java Server Pages (JSP), Servlets e JPA/Hibernate, seguindo a arquitetura Model-View-Controller (MVC). A aplicação permite o gerenciamento de animais, habitats, consultas veterinárias, tarefas de manutenção e mais, com dados armazenados em um banco de dados SQLite.

## Funcionalidades

- Gerenciamento de animais: acompanhamento de animais, suas espécies, estado de saúde e habitat
- Gerenciamento de habitats: diferentes ambientes com registros de manutenção
- Cuidados veterinários: agendamento e registro de consultas e tratamentos
- Gerenciamento de funcionários: diferentes funções (Veterinário, Manutenção, Administrador)
- Ingressos para visitantes: gerenciamento de ingressos e visitas
- Banco de dados SQLite para fácil portabilidade e configuração

## Começando

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior

### Instalação

1. Clone o repositório:

   ```
   git clone <url-do-repositório>
   cd jsp--mvc--app
   ```

2. Construa o projeto com Maven:
   ```
   mvn clean package
   ```

### Executando a Aplicação

1. Inicie a aplicação usando Jetty:

   ```
   mvn jetty:run
   ```

2. Acesse a aplicação no seu navegador:
   ```
   http://localhost:8080
   ```

### Inicialização do Banco de Dados

A aplicação usa SQLite que é incorporado e não requer instalação separada:

- O arquivo do banco de dados é criado automaticamente em `src/main/data/zoologico.db`
- Para popular o banco de dados com dados iniciais de teste, execute a classe Seeder:
  ```
  mvn exec:java -Dexec.mainClass="config.Seeder"
  ```

## Usuários Padrão

Após executar o Seeder, os seguintes usuários estarão disponíveis:

- Visitante: Email: `visitante@visitante.com`, Senha: `visitante`
- Administrador: Email: `admin@admin.com`, Senha: `admin`
- Veterinário: Email: `veterinario@veterinario.com`, Senha: `veterinario`
- Manutenção: Email: `manutencao@manutencao.com`, Senha: `manutencao`

## Tecnologias Utilizadas

- Java 17
- Jakarta EE 10 (Servlets, JSP)
- JPA/Hibernate ORM
- Banco de Dados SQLite
- BCrypt para criptografia de senhas
- Maven para gerenciamento de dependências
- Jetty como servidor de desenvolvimento

## Licença

Este projeto está licenciado sob a Licença MIT.
