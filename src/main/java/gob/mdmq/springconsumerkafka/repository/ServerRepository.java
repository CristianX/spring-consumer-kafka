package gob.mdmq.springconsumerkafka.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gob.mdmq.springconsumerkafka.model.Server;

@Repository
public interface ServerRepository extends MongoRepository<Server, String>{
    
}
