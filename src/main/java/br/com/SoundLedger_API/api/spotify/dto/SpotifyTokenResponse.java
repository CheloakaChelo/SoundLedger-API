package br.com.SoundLedger_API.api.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpotifyTokenResponse {

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("expires_in")
    private int expires_in;
}
