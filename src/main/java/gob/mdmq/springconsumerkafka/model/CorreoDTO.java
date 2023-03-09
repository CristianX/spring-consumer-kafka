package gob.mdmq.springconsumerkafka.model;

import java.util.List;

public class CorreoDTO {
    String idSistema;
    String remitente;
    String destinatarios;
    String asunto;
    String mensaje;
    List<String> adjunto;
}
