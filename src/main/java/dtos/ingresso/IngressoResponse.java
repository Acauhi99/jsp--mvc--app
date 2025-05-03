package dtos.ingresso;

import java.util.Date;
import models.Ingresso;

public class IngressoResponse {
    private final String tipo;
    private final Double valor;
    private final boolean utilizado;
    private final Date dataCompra;

    public IngressoResponse(Ingresso ingresso) {
        this.tipo = ingresso.getTipo().name();
        this.valor = ingresso.getValor();
        this.utilizado = ingresso.isUtilizado();
        this.dataCompra = ingresso.getDataCompra() == null ? null :
            java.util.Date.from(ingresso.getDataCompra().atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    public String getTipo() { return tipo; }
    public Double getValor() { return valor; }
    public boolean isUtilizado() { return utilizado; }
    public Date getDataCompra() { return dataCompra; }
}