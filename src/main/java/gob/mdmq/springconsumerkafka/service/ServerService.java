package gob.mdmq.springconsumerkafka.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gob.mdmq.springconsumerkafka.model.Server;
import gob.mdmq.springconsumerkafka.repository.ServerRepository;


@Service
public class ServerService {

    private final ServerRepository serverRepository;

    @Autowired
    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public void save(Server server) {
        serverRepository.save(server);
    }

    public List<Server> findAll() {
        return serverRepository.findAll();
    }

 
    
}
