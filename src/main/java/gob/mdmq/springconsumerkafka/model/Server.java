package gob.mdmq.springconsumerkafka.model;

import java.time.LocalDateTime;

public class Server {
    private String smtp;
    private int puerto;
    private String usuario;
    private String password;
    private int cantidadCorreosEnviados;
    private LocalDateTime lastHourSent;
    
    public Server(String smtp, int puerto, String usuario, String password, int cantidadCorreosEnviados, LocalDateTime lastHourSent) {
        this.smtp = smtp;
        this.puerto = puerto;
        this.usuario = usuario;
        this.password = password;
        this.cantidadCorreosEnviados = cantidadCorreosEnviados;
        this.lastHourSent = lastHourSent;
    }
    
    public Server() {}

    public String getSmtp() {
        return smtp;
    }
    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }
    public int getPuerto() {
        return puerto;
    }
    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
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
