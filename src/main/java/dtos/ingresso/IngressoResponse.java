package dtos.ingresso;

import java.util.Date;
import java.util.UUID;
import models.Ingresso;

public class IngressoResponse {
    private final String tipo;
    private final Double valor;
    private final boolean utilizado;
    private final Date dataCompra;
    private final CustomerDTO comprador;
    private final UUID id;

    public IngressoResponse(Ingresso ingresso) {
        this.tipo = ingresso.getTipo().name();
        this.valor = ingresso.getValor();
        this.utilizado = ingresso.isUtilizado();

        this.dataCompra = ingresso.getDataCompra() == null ? null
                : java.util.Date.from(ingresso.getDataCompra().atZone(java.time.ZoneId.systemDefault()).toInstant());

        this.id = ingresso.getId();
        this.comprador = ingresso.getComprador() != null ? new CustomerDTO(ingresso.getComprador().getId(),
                ingresso.getComprador().getNome(),
                ingresso.getComprador().getEmail()) : null;
    }

    public String getTipo() {
        return tipo;
    }

    public Double getValor() {
        return valor;
    }

    public boolean isUtilizado() {
        return utilizado;
    }

    public Date getDataCompra() {
        return dataCompra;
    }

    public UUID getId() {
        return id;
    }

    public CustomerDTO getComprador() {
        return comprador;
    }

    // Classe interna para dados do cliente
    public static class CustomerDTO {
        private final UUID id;
        private final String nome;
        private final String email;

        public CustomerDTO(UUID id, String nome, String email) {
            this.id = id;
            this.nome = nome;
            this.email = email;
        }

        public UUID getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public String getEmail() {
            return email;
        }
    }
}