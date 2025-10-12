package br.com.SoundLedger_API.api.musicbrainz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MusicBrainzSearchResponse {

    private List<Recording> recordings;

}
