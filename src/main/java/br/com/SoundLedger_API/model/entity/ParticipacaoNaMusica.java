package br.com.SoundLedger_API.model.entity;

import br.com.SoundLedger_API.model.num.FuncaoNaMusica;
import lombok.Data;

@Data
public class ParticipacaoNaMusica {

    private String usuarioId;
    private String artistaParticipante;
    private double percentualDireitos;
    private FuncaoNaMusica funcaoNaMusica;
}
