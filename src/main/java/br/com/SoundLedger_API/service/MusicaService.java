package br.com.SoundLedger_API.service;

import br.com.SoundLedger_API.dao.IMusica;
import br.com.SoundLedger_API.dao.IUser;
import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.ParticipacaoNaMusica;
import br.com.SoundLedger_API.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class MusicaService {

    @Autowired
    private IMusica dao;

    @Autowired
    private IUser userDao;

    public List<Musica> listarMusica() {
        return (List<Musica>) dao.findAll();
    }

    public Optional<Musica> listarPorId(@PathVariable String id){
        return (Optional<Musica>) dao.findById(id);
    }

    public List<Musica> listarPorArtista(@PathVariable String artistaPrincipalId){
        return (List<Musica>) dao.findAllByArtistaPrincipalId(artistaPrincipalId);
    }

    public Musica cadastrarMusica(@RequestBody Musica musica){
        for (ParticipacaoNaMusica p : musica.getParticipacoes()){
            User user = userDao.findById(p.getUsuarioId()).orElseThrow(() -> new RuntimeException("Usuario não encontrado: " + p.getUsuarioId()));
            p.setNomeArtista(user.getNome());
        }
        return dao.save(musica);
    }

    public Musica editarMusica(@RequestBody Musica musica){
        return dao.save(musica);
    }

    public Optional<Musica> deleteMusica(@PathVariable String id){
        Optional<Musica> musicaById = dao.findById(id);
        dao.deleteById(id);
        return musicaById;
    }

}
