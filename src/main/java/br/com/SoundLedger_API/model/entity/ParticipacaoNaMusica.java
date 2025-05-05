package br.com.SoundLedger_API.model.entity;

import lombok.Data;

@Data
public class ParticipacaoNaMusica {

    private String usuarioId;
    private double percentualDireitos;
    private  String tipoParticipacao;
}
