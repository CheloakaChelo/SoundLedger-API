package br.com.SoundLedger_API.api.musicbrainz.service;

import br.com.SoundLedger_API.api.musicbrainz.dto.MusicBrainzSearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

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
}
