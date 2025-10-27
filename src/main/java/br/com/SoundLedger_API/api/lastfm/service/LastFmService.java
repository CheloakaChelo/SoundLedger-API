package br.com.SoundLedger_API.api.lastfm.service;

import br.com.SoundLedger_API.api.lastfm.dto.LastFmGetInfoResponse;
import br.com.SoundLedger_API.api.lastfm.dto.LastFmSearchResponse;
import br.com.SoundLedger_API.api.lastfm.dto.TrackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class LastFmService {

    private static final Logger logger = LoggerFactory.getLogger(LastFmService.class);

    private final WebClient webClient;
    private final String apiKey;
    private final Random random = new Random();

    private final List<String> searchTerms = List.of("a", "e", "i", "o", "u", "s", "r", "t", "m", "c", "d", "p", "l",
            "f", "g", "n", "b", "h", "k", "w", "y", "z", "j", "x");

    private final int MAX_RESULTS_PER_PAGE = 200;
    private final Duration BLOCK_TIMEOUT = Duration.ofSeconds(10);

    public LastFmService(WebClient.Builder webClientBuilder,
                         @Value("${lastfm.api-key}") String apiKey){
        this.apiKey = apiKey;
        this.webClient = webClientBuilder.baseUrl("http://ws.audioscrobbler.com").build();

    }

    public List<TrackInfo> getDiverseTrackPlays(int totalLimit) {
        String randomTerm = searchTerms.get(random.nextInt(searchTerms.size()));
        logger.info("Buscando {} 'plays' no Last.fm com o termo: '{}'", totalLimit, randomTerm);

        int totalPagesToFetch = (int) Math.ceil((double) totalLimit / MAX_RESULTS_PER_PAGE);
        if (totalPagesToFetch == 0) totalPagesToFetch = 1;

        try {
            Flux<TrackInfo> trackFlux = Flux.range(1, totalPagesToFetch)
                    .delayElements(Duration.ofMillis(300))
                    .flatMap(page -> {
                        logger.debug("   -> Buscando pagina {} de {}...", page);
                        return getDiverseTrackPlaysPage(randomTerm, MAX_RESULTS_PER_PAGE, page);
                    })
                    .take(totalLimit);

            List<TrackInfo> resultList = trackFlux.collectList().block(BLOCK_TIMEOUT);

            return resultList != null ? resultList : Collections.emptyList();

        } catch (Exception e) {
            logger.error("Erro ao buscar plays paginados do Last.fm (sincrono): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private Flux<TrackInfo> getDiverseTrackPlaysPage(String term, int limit, int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/2.0/")
                        .queryParam("method", "track.search")
                        .queryParam("track", term)
                        .queryParam("api_key", this.apiKey)
                        .queryParam("format", "json")
                        .queryParam("limit", String.valueOf(limit))
                        .queryParam("page", String.valueOf(page))
                        .build())
                .retrieve()
                .bodyToMono(LastFmSearchResponse.class)
                .flatMapMany(response -> {
                    if (response != null && response.getResults() != null &&
                            response.getResults().getTrackMatches() != null &&
                            response.getResults().getTrackMatches().getTrackList() != null) {
                        return Flux.fromIterable(response.getResults().getTrackMatches().getTrackList());
                    }
                    return Flux.empty();
                })
                .doOnError(error -> logger.error("Erro ao buscar pagina {} do Last.fm: {}", page, error.getMessage()))
                .onErrorResume(e -> Flux.empty());
    }

    public BigInteger getTrackPlayCount(String artist, String track) {
        logger.info("LASTFM_SERVICE: Buscando playcount para '{}' por '{}'", track, artist);
        try {
            LastFmGetInfoResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/2.0/")
                            .queryParam("method", "track.getInfo")
                            .queryParam("artist", artist)
                            .queryParam("track", track)
                            .queryParam("api_key", this.apiKey)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .bodyToMono(LastFmGetInfoResponse.class)
                    .block(BLOCK_TIMEOUT);

            if (response != null && response.getTrack() != null && response.getTrack().getPlaycount() != null) {
                try {
                    return new BigInteger(response.getTrack().getPlaycount());
                } catch (NumberFormatException e) {
                    logger.error("Erro ao converter playcount '{}' para BigInteger.", response.getTrack().getPlaycount());
                    return BigInteger.ZERO;
                }
            } else {
                logger.warn("Playcount não encontrado no Last.fm para '{}' por '{}'", track, artist);
                return BigInteger.ZERO;
            }
        } catch (Exception e) {
            logger.error("Erro ao chamar Last.fm track.getInfo: {}", e.getMessage());
            return BigInteger.ZERO;
        }
    }
}
