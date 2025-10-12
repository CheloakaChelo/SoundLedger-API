package br.com.SoundLedger_API.api.lastfm.service;

import br.com.SoundLedger_API.api.lastfm.dto.LastFmSearchResponse;
import br.com.SoundLedger_API.api.lastfm.dto.TrackInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.View;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Random;

@Service
public class LastFmService {

    private final WebClient webClient;
    private final String apiKey;
    private final Random random = new Random();

    private final List<String> searchItems = List.of("a", "e", "i", "o", "u", "s", "r", "t", "m", "c", "d", "p", "l",
            "f", "g", "n", "b", "h", "k", "w", "y", "z", "j", "x");

    public LastFmService(WebClient.Builder webClientBuilder,
                         @Value("${lastfm.api-key}") String apiKey, View error){
        this.apiKey = apiKey;
        this.webClient = webClientBuilder.baseUrl("http://ws.audioscrobbler.com").build();

    }

    public Flux<TrackInfo> getDiverseTrackPlays(int limit) {
        String randomTerm = searchItems.get(random.nextInt(searchItems.size()));
        System.out.printf("Buscando 'plays' no Last.fm com o termo aleatório: '%s'\n", randomTerm);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/2.0/")
                        .queryParam("method", "track.search")
                        .queryParam("track", randomTerm)
                        .queryParam("api_key", this.apiKey)
                        .queryParam("format", "json")
                        .queryParam("limit", String.valueOf(limit))
                        .build())
                .retrieve()
                .bodyToMono(LastFmSearchResponse.class)
                .flatMapMany(response -> {
                    if (response != null && response.getResults() != null && response.getResults().getTrackMatches() != null
                            && response.getResults().getTrackMatches().getTrackList() != null){
                        return Flux.fromIterable(response.getResults().getTrackMatches().getTrackList());
                    }
                    return Flux.empty();
                })
                .doOnError(error -> System.err.println("Erro ao chamar a API do Last.fm: " + error.getMessage()))
                .onErrorResume(e -> Flux.empty());
    }
}
