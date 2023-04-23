package gob.mdmq.springconsumerkafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gob.mdmq.springconsumerkafka.model.CorreoBDD;
import gob.mdmq.springconsumerkafka.repository.CorreoRepository;


@Service
public class EmailService {

    private final CorreoRepository correoRepository;

    @Autowired
    public EmailService(CorreoRepository correoRepository) {
        this.correoRepository = correoRepository;
    }

    public void save(CorreoBDD correo) {
        correoRepository.save(correo);
    }

    public void findById(String id) {
        correoRepository.findById(id);
    }

    
    public void findByIdSistema(String idSistema) {
        correoRepository.findByIdSistema(idSistema);
    }
    
}
