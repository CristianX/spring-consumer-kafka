package gob.mdmq.springconsumerkafka.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gob.mdmq.springconsumerkafka.model.CorreoBDD;

@Repository
public interface CorreoRepository extends MongoRepository<CorreoBDD, String>{

    public CorreoBDD findByIdSistema(String idSistema);
    
}
