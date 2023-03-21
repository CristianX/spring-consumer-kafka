package gob.mdmq.springconsumerkafka.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.core.io.FileSystemResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import gob.mdmq.springconsumerkafka.model.Correo;
import gob.mdmq.springconsumerkafka.model.Server;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Consumer {

    public List<Server> ListaServidores = Arrays.asList(
            new Server("smtp.gmail.com", 587, "david1", "david", 0, null),
            new Server("smtp.gmail.com", 587, "david2", "david", 0, null),
            new Server("smtp.gmail.com", 587, "david3", "david", 0, null),
            new Server("smtp.gmail.com", 587, "david4", "david", 0, null));

    public List<JavaMailSender> mailSenders;

    private final AtomicInteger correoCount = new AtomicInteger(0);

    private final int maxCorreosPorHoraPorServidor = 82;

    private static final String orderTopic = "smsr";

    public int contadorServidores = 0;

    public void CorreoConsumer(List<JavaMailSender> mailSenders) {
        this.mailSenders = mailSenders;
    }

    @KafkaListener(topics = { "smsr1", "smsr2", "smsr3" })
    public void consumeMessage(String message) throws JsonProcessingException {

        try {
            ObjectMapper mapper = new ObjectMapper();
            // Transfomamos el mensaje que llega del broker en un objeto de tipo Correo
            Correo correo = mapper.readValue(message, Correo.class);
            log.info("***************DESTINATARIO CONSUMIDO***************** {}", correo.getDestinatarios());

            // Calcular el número de correos enviados en la última hora
            int currentCorreoCount = correoCount.incrementAndGet();
            if (currentCorreoCount % maxCorreosPorHoraPorServidor == 0) {
                // Esperar un minuto antes de continuar enviando correos
                try {
                    log.info(String.format("Limite de correos por hora alcanzado. Esperando 1 minuto..."));
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    log.info("Error al esperar antes de enviar el siguiente correo", e);
                }
                // Reiniciar el contador de correos enviados
                correoCount.set(0);
            }
            // Obtener el siguiente servidor disponible según el balanceo Round-Robin
            Integer serverIndex = getNextServerIndex();
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

            mailSender.setHost(ListaServidores.get(serverIndex).getSmtp());
            mailSender.setPort(ListaServidores.get(serverIndex).getPuerto());
            mailSender.setUsername(ListaServidores.get(serverIndex).getUsuario());
            mailSender.setPassword(ListaServidores.get(serverIndex).getPassword());
            
            //JavaMailSender mailSender = mailSenders.get(getNextServerIndex());

            // Enviar el correo
            try {
                sendEmail(correo, mailSender);
            } catch (MessagingException e) {
                log.info(String.format("Error al enviar el correo: %s", correo.toString()), e);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void sendEmail(Correo correo, JavaMailSender mailSender) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(correo.getRemitente());
        helper.setTo(correo.getDestinatarios());
        helper.setSubject(correo.getAsunto());
        helper.setText(correo.getMensaje());

        List<String> adjuntos = correo.getAdjunto();
        if (adjuntos != null) {
            for (String adjunto : adjuntos) {
                // FileSystemResource file = new FileSystemResource(adjunto);
                // helper.addAttachment(file.getFilename(), file);
            }
        }

        mailSender.send(message);
    }

    public int getNextServerIndex() {
        // Implementar balanceo Round-Robin

        // Obtener la lista de servidores disponibles
        List<Server> servers = getAvailableServers();

        // Si no hay servidores disponibles, retornar -1
        if (servers.isEmpty()) {
            return -1;
        }

        // Obtener el contador de servidores
        int index = getNextServerCounter();

        // Incrementar el contador
        setNextServerCounter(index + 1);

        // Calcular el índice del servidor a utilizar
        int serverIndex = index % servers.size();

        // Obtener el servidor correspondiente
        Server server = servers.get(serverIndex);

        // Verificar si el servidor ha alcanzado el límite de envío por hora
        LocalDateTime now = LocalDateTime.now();

        if (server.getLastHourSent() != null && server.getLastHourSent().isAfter(now.minusHours(1))) {
            if (server.getCantidadCorreosEnviados() >= 82) {
                // Si ha alcanzado el límite, se descarta y se pasa al siguiente
                serverIndex = getNextServerIndex();
            }
        } else {
            // Si ya pasó una hora, se reinicia el contador
            server.setLastHourSent(now);
            server.setCantidadCorreosEnviados(0);
        }

        // Actualizar el número de correos enviados por el servidor
        server.setCantidadCorreosEnviados(server.getCantidadCorreosEnviados() + 1);

        // Retornar el índice del servidor a utilizar
        return serverIndex;
    }

    private List<Server> getAvailableServers() {
        // Obtener la lista de servidores disponibles
        // De la listaServidores, obtener los servidores que no hayan alcanzado el
        // límite
        ListaServidores.forEach(server -> {
            if (server.getLastHourSent() != null
                    && server.getLastHourSent().isAfter(LocalDateTime.now().minusHours(1))) {
                if (server.getCantidadCorreosEnviados() >= 82) {
                    // Si ha alcanzado el límite, se descarta y se pasa al siguiente
                    ListaServidores.remove(server);
                }
            } else {
                // Si ya pasó una hora, se reinicia el contador lastHourSent
                server.setLastHourSent(LocalDateTime.now());
                server.setCantidadCorreosEnviados(0);
            }
        });
        return ListaServidores;
    }

    private int getNextServerCounter() {
        // Obtener el contador de servidores
        // Integer index = counterRepository.getNextServerIndex();
        Integer contador = contadorServidores;
        return contador;
    }

    private void setNextServerCounter(int index) {
        // Guardar el contador de servidores
        // counterRepository.setNextServerIndex(index);
        contadorServidores = index;
    }

}
