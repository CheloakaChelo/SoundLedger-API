package br.com.SoundLedger_API.api.spotify.service;

import br.com.SoundLedger_API.api.spotify.dto.SpotifySearchResponse;
import br.com.SoundLedger_API.api.spotify.dto.SpotifyTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.time.Instant;
import java.util.Base64;

@Service
public class SpotifyService {

    private final WebClient authWebClient;
    private final WebClient apiWebClient;
    private final String clientId;
    private final String clientSecret;

    private String cachedAccessToken;
    private Instant tokenExpiryTime;

    public SpotifyService(WebClient.Builder webClientBuilder,
                          @Value("${spotify.client-id}") String clientId,
                          @Value("${spotify.client-secret}") String clientSecret){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authWebClient = webClientBuilder.baseUrl("https://accounts.spotify.com").build();
        this.apiWebClient = webClientBuilder.baseUrl("https://api.spotify.com/v1").build();
    }

    private Mono<String> getAccessToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(tokenExpiryTime)){
            return Mono.just(cachedAccessToken);
        }

        String authHeader = "Basic" + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        return authWebClient.post()
                .uri("/api/token")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(SpotifyTokenResponse.class)
                .doOnNext(tokenResponse -> {
                    this.cachedAccessToken = tokenResponse.getAccess_token();
                    this.tokenExpiryTime = Instant.now().plusSeconds(tokenResponse.getExpires_in() - 60);
                    System.out.println("Novo token do Spotify obtido");
                })
                .map(SpotifyTokenResponse::getAccess_token);
    }

    public Mono<String> searchTrakcAndGetIsrc(String artist, String track){
        return getAccessToken().flatMap(token ->
                apiWebClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1/search")
                                .queryParam("q", String.format("artist:%s track:%s", artist, track))
                                .queryParam("type", "track")
                                .queryParam("Limit", 1)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(SpotifySearchResponse.class)
                        .map(response -> {
                            if (response != null && !response.getTracks().getItems().isEmpty()){
                                return response.getTracks().getItems().get(0).getExternalIds().getIsrc();
                            }
                            throw new RuntimeException("ISRC não encontrado para a música.");
                        })
        );
    }


}
