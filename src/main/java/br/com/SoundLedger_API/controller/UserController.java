package br.com.SoundLedger_API.controller;

import br.com.SoundLedger_API.model.entity.User;
import br.com.SoundLedger_API.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public String helloUser(){
        return "olá";
    }

    @GetMapping("/listar")
    public ResponseEntity<List<User>> listarUser(){
        List<User> userList = service.listarUser();

        if (userList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(userList);
        }

    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<?> userById(@PathVariable String id){
        Optional<User> userPorId = service.listarUserPorId(id);

        if (userPorId.isPresent()){
            return ResponseEntity.ok(userPorId);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<User> cadastrarUser(@RequestBody User user) throws Exception{
        User newUser = service.cadastrarUser(user);
        return ResponseEntity.ok(newUser);
    }

    @PutMapping("/editar")
    public ResponseEntity<User> editarUser(@RequestBody User user){
        User editUser = service.editarUser(user);
        return ResponseEntity.ok(editUser);
    }

    @DeleteMapping("/deletar/{id}")
    @Transactional
    public ResponseEntity<Optional<User>> deleteUserById(@PathVariable String id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
