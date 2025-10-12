package br.com.SoundLedger_API.api.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackItem {

    private String nome;

    @JsonProperty("external_ids")
    private ExternalIds externalIds;

}
