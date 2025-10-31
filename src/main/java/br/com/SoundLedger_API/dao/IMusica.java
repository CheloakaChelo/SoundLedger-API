package br.com.SoundLedger_API.dao;

import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface IMusica extends MongoRepository<Musica, String> {

    List<Musica> findAllByArtistaPrincipalId(String artistaPrincipalId);

    Optional<Musica> findByTituloAndArtista(String titulo, String artista);

    List<Musica> findByParticipacoesUsuarioId(String participacoesUsuarioId);

    String id(String id);
}
