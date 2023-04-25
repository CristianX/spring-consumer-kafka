package gob.mdmq.springconsumerkafka.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import gob.mdmq.springconsumerkafka.model.CorreoBDD;
import gob.mdmq.springconsumerkafka.model.Server;
import gob.mdmq.springconsumerkafka.repository.ServerRepository;
import gob.mdmq.springconsumerkafka.service.EmailService;
import gob.mdmq.springconsumerkafka.service.ServerService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@Component
public class Consumer {


    

    @Autowired
    private MongoTemplate mongoTemplate;
    
    
    public List<Server> ListaServidores;


    @PostConstruct
    public void init() {
        // Aquí se inicializa la lista global con los datos de MongoDB
        ListaServidores = mongoTemplate.findAll(Server.class);
    }

    

    /* public List<Server> ListaServidores = Arrays.asList(
            new Server("bh8954.banahosting.com", 587, "david@nebulartech.com", "A0H1wky^pW7R", 0, null),
            new Server("bh8954.banahosting.com", 587, "cristian@nebulartech.com", "Qc+#MvMTX.F{", 0, null),
            new Server("bh8954.banahosting.com", 587, "estefy@nebulartech.com", "er#z[k7AE.N.", 0, null));

    
 */
    public List<JavaMailSender> mailSenders;

    public int contadorServidores = 0;

    @Autowired
    public EmailService emailService;

    @Autowired
    public ServerService serverService;

    /*
     * @Autowired
     * private KafkaTemplate<String, Object> kafkaTemplate1;
     */

    @KafkaListener(topics = { "smsr1", "smsr2", "smsr3" })
    public void consumeMessage(String message, Acknowledgment acknowledgment) throws JsonProcessingException {

        try {
            
            ObjectMapper mapper = new ObjectMapper();
            // Transfomamos el mensaje que llega del broker en un objeto de tipo Correo
            CorreoBDD correo = mapper.readValue(message, CorreoBDD.class);
            CorreoBDD correoBDD = mapper.readValue(message, CorreoBDD.class);
            log.info("***************DESTINATARIO CONSUMIDO***************** {}", correo.getDestinatarios());

            // Obtener el siguiente servidor disponible según el balanceo Round-Robin
            Integer serverIndex = getNextServerIndex(); // Si no hay servidores disponibles, retornar -1

            while (serverIndex == -1) {
                log.info(String.format("Limite de correos por hora alcanzado. Esperando 2 minuto..."));
                // Thread.sleep(120 * 1000);
                serverIndex = getNextServerIndex();
            }

            acknowledgment.acknowledge();

            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(ListaServidores.get(serverIndex).getSmtp());
            mailSender.setPort(ListaServidores.get(serverIndex).getPuerto());
            mailSender.setUsername(ListaServidores.get(serverIndex).getUsuario());
            mailSender.setPassword(ListaServidores.get(serverIndex).getPassword());
            try {
                sendEmail(correo, mailSender, ListaServidores.get(serverIndex).getUsuario(), serverIndex);
                correoBDD.setEnviado(true);
                emailService.save(correoBDD);
                // Confirma que el mensaje fue procesado correctamente
                /*
                 * TopicPartition partition = new TopicPartition("smsr3", 0);
                 * OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(offset + 1);
                 * Map<TopicPartition, OffsetAndMetadata> offsetsMap =
                 * Collections.singletonMap(partition, offsetAndMetadata);
                 * ConsumerGroupMetadata groupMetadata = new
                 * ConsumerGroupMetadata("nombre_del_grupo_de_consumidores");
                 * kafkaTemplate1.sendOffsetsToTransaction(offsetsMap, groupMetadata);
                 */

            } catch (MessagingException e) {
                correoBDD.setEnviado(false);
                emailService.save(correoBDD);
                log.info(String.format("Error al enviar el correo: %s", correo.toString()), e);
            }

        } catch (Exception e) {
            System.out.println("Error en el consumo del mensaje: " + e);
            acknowledgment.nack(Duration.ofSeconds(1000));
        }

    }

    public void sendEmail(CorreoBDD correo, JavaMailSender mailSender, String origen, Integer serverIndex)
            throws MessagingException {
        if (serverIndex == -1) {
            log.info(String.format("No se envia debido a que no hay servidores disponibles"));
            return;
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(origen);
        helper.setTo(correo.getDestinatarios());
        helper.setSubject(correo.getAsunto() + " Numero De servidor: " + serverIndex);
        helper.setText(correo.getMensaje());

        List<String> adjuntos = correo.getAdjunto();
        if (adjuntos != null) {
            for (String adjunto : adjuntos) {

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
        int index = getNextServerCounter(servers.size());

        // Incrementar el contador
        // setNextServerCounter(index + 1);

        // Calcular el índice del servidor a utilizar
        int serverIndex = index % servers.size();

        // Obtener el servidor correspondiente
        Server server = servers.get(index);
        setNextServerCounter(index + 1);
        // Verificar si el servidor ha alcanzado el límite de envío por hora
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowServer = now.minusMinutes(1);

        if (server.getLastHourSent() != null && server.getLastHourSent().isAfter(nowServer)) {
            // Aun no pasa una hora
            if (server.getCantidadCorreosEnviados() >= 2) {
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
        try {
            // ObjectMapper mapper1 = new ObjectMapper();
            // Server[] ListaServidores1 = mapper1.readValue("misObjetos", Server[].class);
            // List<Server> servidores = Arrays.asList(ListaServidores1);
            List<Server> servidores = ListaServidores;
            List<Server> availableServers = new ArrayList<>();
            servidores.forEach(server -> {
                if (server.getLastHourSent() != null
                        && server.getLastHourSent().isAfter(LocalDateTime.now().minusMinutes(1))) {
                    if (server.getCantidadCorreosEnviados() < 2) {
                        // Si no ha alcanzado el límite, se agrega a la lista de servidores disponibles
                        availableServers.add(server);
                    }
                } else {
                    // Si ya pasó una hora, se reinicia el contador lastHourSent
                    server.setLastHourSent(LocalDateTime.now());
                    server.setCantidadCorreosEnviados(0);
                    availableServers.add(server); // Se agrega a la lista de servidores disponibles
                }
            });
            return availableServers;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    private int getNextServerCounter(int max) {
        // Obtener el contador de servidores
        // Integer index = counterRepository.getNextServerIndex();
        // maximo numero
        // int max = ListaServidores.size();
        // si el contador es mayor al maximo numero de servidores
        if (contadorServidores >= max) {

            contadorServidores = 0;
        }
        Integer contador = contadorServidores;
        return contador;
    }

    private void setNextServerCounter(int index) {
        // Guardar el contador de servidores
        // counterRepository.setNextServerIndex(index);
        contadorServidores = index;
    }

}
