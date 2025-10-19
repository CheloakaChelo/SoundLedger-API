package br.com.SoundLedger_API.api.musicbrainz.service;

import br.com.SoundLedger_API.api.musicbrainz.dto.MusicBrainzSearchResponse;
import br.com.SoundLedger_API.api.musicbrainz.dto.Recording;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicBrainzService {

    private final WebClient webClient;

    public MusicBrainzService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder
                .baseUrl("https://musicbrainz.org")
                .defaultHeader(HttpHeaders.USER_AGENT, "SoundLedger/1.0")
                .build();
    }

    public Mono<String> findRecordingId(String artist, String track){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ws/2/recording")
                        .queryParam("query", String.format("artist:\"%s\" AND recording:\"%s\"", artist, track))
                        .queryParam("fmt", "json")
                        .build())
                .retrieve()
                .bodyToMono(MusicBrainzSearchResponse.class)
                .map(response -> {
                    if (response.getRecordings() != null && !response.getRecordings().isEmpty()) {
                        return response.getRecordings().get(0).getId();
                    }
                    throw new RuntimeException("Nenhuma gravação encontrada");
                });
    }

    public List<String> findComposersByIsrc(String isrc) {
        System.out.println("MUSICBRAINZ_SERVICE: Buscando por ISRC: " + isrc);

        MusicBrainzSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ws/2/recording")
                        .queryParam("isrc", isrc)
                        .queryParam("inc", "artist-rels")
                        .queryParam("fmt", "json")
                        .build())
                .retrieve()
                .bodyToMono(MusicBrainzSearchResponse.class)
                .block();

        if (response.getRecordings() == null || response.getRecordings().isEmpty()) {
            throw new RuntimeException("Nenhuma gravacao encontrada no MusicBrainz para o ISRC: " + isrc);
        }

        Recording recording = response.getRecordings().get(0);

        if (recording.getRelations() == null || recording.getRelations().isEmpty()) {
            throw new RuntimeException("Gravacao encontrada, mas sem relacoes de artista (compositores) no MusicBrainz.");
        }

        return recording.getRelations().stream()
                .filter(rel -> "composer".equals(rel.getType()) || "lyricist".equals(rel.getType()))
                .map(rel -> rel.getArtist().getName())
                .distinct()
                .collect(Collectors.toList());
    }
}
