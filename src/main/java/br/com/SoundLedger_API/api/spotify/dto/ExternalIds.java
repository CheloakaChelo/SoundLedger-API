package br.com.SoundLedger_API.api.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalIds {

    private String isrc;

}
