package br.com.SoundLedger_API.api.musicbrainz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recording {

    private String id;
    private String title;

    @JsonProperty("artist-credit")
    private List<Artist> artists;

    @JsonProperty("relations")
    private List<Relation> relations;

}
