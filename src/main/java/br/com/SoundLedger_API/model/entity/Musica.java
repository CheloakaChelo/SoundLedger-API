package br.com.SoundLedger_API.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "musica_entity")
@Data
public class Musica {

    @Id
    private String id;

    private String titulo;
    private String artistaPrincipalId;
    private String album;
    private String isrc;
    private Integer duracaoSegundos;

    private String idSpotify;
    private String contratoBlockchain;

    private List<ParticipacaoNaMusica> participacoes;


}
