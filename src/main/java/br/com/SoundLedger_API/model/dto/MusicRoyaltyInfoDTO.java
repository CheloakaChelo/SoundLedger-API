package br.com.SoundLedger_API.model.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class MusicRoyaltyInfoDTO {

    private String musicaId;
    private String titulo;
    private String artistaPrincipalNome;
    private String contractAddress;
    private BigInteger totalPlaysDaMusica;
    private BigInteger splitPercentualDoUsuario;
    private String saldoDoUsuarioEth;

    @Override
    public String toString() {
        return "UserMusicRoyaltyInfoDTO{" +
                "musicaId='" + musicaId + '\'' +
                ", titulo='" + titulo + '\'' +
                ", artistaPrincipalNome='" + artistaPrincipalNome + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", totalPlaysDaMusica=" + totalPlaysDaMusica +
                ", splitPercentualDoUsuario=" + splitPercentualDoUsuario +
                ", saldoSacavelDoUsuarioEth='" + saldoDoUsuarioEth + '\'' +
                '}';
    }
}
