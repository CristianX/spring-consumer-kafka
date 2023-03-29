package gob.mdmq.springconsumerkafka.model;

import java.time.LocalDateTime;

public class Contador {
    private String usuario;
    private int cantidadCorreosEnviados;
    private LocalDateTime lastHourSent;

    public Contador(String usuario, int cantidadCorreosEnviados, LocalDateTime lastHourSent) {
        this.usuario = usuario;
        this.cantidadCorreosEnviados = cantidadCorreosEnviados;
        this.lastHourSent = lastHourSent;
    }

    public Contador() {}

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public int getCantidadCorreosEnviados() {
        return cantidadCorreosEnviados;
    }
    public void setCantidadCorreosEnviados(int cantidadCorreosEnviados) {
        this.cantidadCorreosEnviados = cantidadCorreosEnviados;
    }
    public LocalDateTime getLastHourSent() {
        return lastHourSent;
    }
    public void setLastHourSent(LocalDateTime lastHourSent) {
        this.lastHourSent = lastHourSent;
    }
}
