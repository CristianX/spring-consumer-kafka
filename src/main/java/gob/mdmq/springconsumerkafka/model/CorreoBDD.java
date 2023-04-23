package gob.mdmq.springconsumerkafka.model;

import java.util.List;

public class CorreoBDD {
    String id;
    String idSistema;
    String remitente;
    String destinatarios;
    String asunto;
    String mensaje;
    List<String> adjunto;
    Boolean enviado;

    public CorreoBDD() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }

    
}
