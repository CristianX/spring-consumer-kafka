package gob.mdmq.springconsumerkafka.model;

import java.util.List;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class Correo {
    String idSistema;
    String remitente;
    String destinatarios;
    String asunto;
    String mensaje;
    List<String> adjunto;
}
