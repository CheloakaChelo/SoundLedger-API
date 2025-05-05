package br.com.SoundLedger_API.service;

import br.com.SoundLedger_API.dao.IUser;
import br.com.SoundLedger_API.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private IUser dao;

    public List<User> listarUser(){
        return (List<User>) dao.findAll();
    }

    public Optional<User> listarUserPorId(@PathVariable Long id){
        return dao.findById(id);
    }

    public User cadastrarUser(@RequestBody User user){
        return dao.save(user);
    }

    public User editarUser(@RequestBody User user){
        return dao.save(user);
    }


    public Optional<User> deleteById(@PathVariable Long id){
        Optional<User> userById = dao.findById(id);
        dao.deleteById(id);
        return userById;
    }

}
