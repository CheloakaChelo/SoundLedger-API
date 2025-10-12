package br.com.SoundLedger_API.api.lastfm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackMatches {

    @JsonProperty("tracks")
    private List<TrackInfo> trackList;

}
