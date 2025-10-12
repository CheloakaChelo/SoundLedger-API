package br.com.SoundLedger_API.api.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tracks {

    @JsonProperty("items")
    private List<TrackItem> items;

}
