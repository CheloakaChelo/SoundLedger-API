package br.com.SoundLedger_API.api.lastfm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResults {

    @JsonProperty("trackmatches")
    private TrackMatches trackMatches;
}
