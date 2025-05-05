package br.com.SoundLedger_API.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "musica_entity")
public class Musica {

    @Id
    private Long id;

    private String titulo;
    private String artistaPrincipal;
    private String album;
    private String isrc;
    private Integer duracaoSegundos;

    private String idSpotify;
    private String contratoBlockchain;


}
