package br.com.SoundLedger_API.api.lastfm.dto; // Use o seu pacote correto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackDetails {


    private String name;


    private String playcount;


    private ArtistInfo artist;


}