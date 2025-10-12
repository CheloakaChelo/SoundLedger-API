package br.com.SoundLedger_API.api.lastfm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackInfo {

    private String name;

    private String artist;
}
