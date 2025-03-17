package br.com.SoundLedger_API.dao;

import br.com.SoundLedger_API.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableMongoRepositories
public interface IUser extends MongoRepository<User, Long> {

}
