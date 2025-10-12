package br.com.SoundLedger_API.api.musicbrainz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recording {

    private String id;
    private String title;

}
