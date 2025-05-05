package br.com.SoundLedger_API.dao;

import br.com.SoundLedger_API.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUser extends MongoRepository<User, Long> {

}
