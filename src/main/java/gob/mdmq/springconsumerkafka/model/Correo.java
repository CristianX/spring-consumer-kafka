package gob.mdmq.springconsumerkafka.model;

import java.util.List;

public class Correo {
    private String idSistema;
    private String remitente;
    private String destinatarios;
    private String asunto;
    private String mensaje;
    private List<String> adjunto;

    public Correo() {}

    public String getIdSistema() {
        return idSistema;
    }

    public void setIdSistema(String idSistema) {
        this.idSistema = idSistema;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(String destinatarios) {
        this.destinatarios = destinatarios;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<String> getAdjunto() {
        return adjunto;
    }

    public void setAdjunto(List<String> adjunto) {
        this.adjunto = adjunto;
    }
}
