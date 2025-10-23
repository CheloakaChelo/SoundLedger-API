package br.com.SoundLedger_API.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MusicDetailsDTO {

    private String isrc;
    private String titulo;
    private String artistaPrincipal;
    private List<String> compositores;
    private List<String> produtores;
    

}
