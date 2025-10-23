package br.com.SoundLedger_API.model.dto;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class CadastroViaISRCRequestDTO {

    private String isrc;
    private String titulo;
    private String artistaPrincipalId;
    private List<DetentorInfo> detentoresDireitos;

    public static class DetentorInfo {
        private String usuarioId;
        private BigInteger split;

        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

        public BigInteger getSplit() { return split; }
        public void setSplit(BigInteger split) { this.split = split; }
    }

}


