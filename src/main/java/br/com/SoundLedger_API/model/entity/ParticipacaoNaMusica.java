package br.com.SoundLedger_API.model.entity;

import br.com.SoundLedger_API.model.num.FuncaoNaMusica;
import lombok.Data;

import java.math.BigInteger;

@Data
public class ParticipacaoNaMusica {

    private String usuarioId;
    private String nomeArtista;
    private BigInteger split;
    private FuncaoNaMusica funcaoNaMusica;
    private String enderecoCarteira;
}
