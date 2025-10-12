package br.com.SoundLedger_API.dao;

import br.com.SoundLedger_API.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface IUser extends MongoRepository<User, String> {

    String id(String id);

    UserDetails findByEmail(String email);
}
