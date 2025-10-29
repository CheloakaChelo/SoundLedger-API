package br.com.SoundLedger_API.service;

import br.com.SoundLedger_API.dao.IUser;
import br.com.SoundLedger_API.model.entity.User;
import br.com.SoundLedger_API.model.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private IUser dao;

    @Autowired
    private CarteiraService carteiraService;


    @Autowired
    public UserService(IUser dao, CarteiraService carteiraService) {
        this.dao = dao;
        this.carteiraService = carteiraService;
    }

    public List<User> listarUser(){
        return (List<User>) dao.findAll();
    }

    public Optional<User> listarUserPorId(@PathVariable String id){
        return dao.findById(id);
    }

    public User cadastrarUser(User user) throws Exception {
        user.setSenha(new BCryptPasswordEncoder().encode(user.getSenha()));

        String enderecoCarteira = carteiraService.gerarEnderecoCarteira();
        user.setEnderecoCarteira(enderecoCarteira);

        return dao.save(user);
    }

    public User editarUser(@RequestBody User user){
        return dao.save(user);
    }


    public Optional<User> deleteById(@PathVariable String id){
        Optional<User> userById = dao.findById(id);
        dao.deleteById(id);
        return userById;
    }

    public List<String> findWalletsByNames(List<String> nomes) {

        List<User> users = dao.findAllByNomeIn(nomes);

        if (users.size() != nomes.size()) {
            throw new RuntimeException("Erro: Um ou mais compositores nao foram encontrados no banco de dados.");
        }

        return users.stream()
                .map(User::getEnderecoCarteira)
                .collect(Collectors.toList());
    }

    private void tratarPerfisConformeRoles (User user){
        List<Role> roles = user.getRoles();

        if (roles.contains(Role.ARTISTA)){
            if (user.getPerfilArtista() == null) {
                throw new IllegalArgumentException("Perfil do artista deve ser preenchido ao ser cadastrado como ARTISTA");
            }
            user.getPerfilArtista().setUserId(user.getId());
        } else {
            if (!roles.contains(Role.ARTISTA) && user.getPerfilArtista() != null){
                throw new IllegalArgumentException("Cadastro ARTISTA não atribuído");
            }
            user.setPerfilArtista(null);
        }

        if (roles.contains(Role.COMPOSITOR)){
            if (user.getPerfilCompositor() == null) {
                throw new IllegalArgumentException("Perfil do compositor deve ser preenchido ao ser cadastrado como COMPOSITOR");
            }
            user.getPerfilCompositor().setUserId(user.getId());
        } else {
            if (!roles.contains(Role.COMPOSITOR) && user.getPerfilCompositor() != null){
                throw new IllegalArgumentException("Cadastro COMPOSITOR não atribuído");
            }
            user.setPerfilCompositor(null);
        }

        if (roles.contains(Role.PRODUTOR)) {
            if (user.getPerfilProdutor() == null) {
                throw new IllegalArgumentException("Perfil do produtor deve ser preenchido ao ser cadastrado como PRODUTOR");
            }
            user.getPerfilProdutor().setUserId(user.getId());
        } else {
            if (!roles.contains(Role.PRODUTOR) && user.getPerfilProdutor() != null){
                throw new IllegalArgumentException("Cadastro PRODUTOR não atribuído");
            }
            user.setPerfilProdutor(null);
        }

        if (roles.contains(Role.GRAVADORA)) {
            if (user.getPerfilGravadora() == null) {
                throw new IllegalArgumentException("Perfil da gravadora deve ser preenchido ao ser cadastrado como GRAVADORA");
            }
            user.getPerfilGravadora().setUserId(user.getId());
        } else {
            if (!roles.contains(Role.GRAVADORA) && user.getPerfilGravadora() != null) {
                throw new IllegalArgumentException("Cadastro GRAVADORA não atribuído");
            }
            user.setPerfilGravadora(null);
        }

    }

}
