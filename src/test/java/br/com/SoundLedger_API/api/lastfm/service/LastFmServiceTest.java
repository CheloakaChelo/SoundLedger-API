package br.com.SoundLedger_API.api.lastfm.service; // Use o mesmo pacote + Test

// Imports do JUnit 5 e Mockito
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

// Imports do Spring e WebClient
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Imports dos seus DTOs
import br.com.SoundLedger_API.api.lastfm.dto.*;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Testes unitários para a classe LastFmService.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LastFmServiceTest {

    // --- Mocks das Dependências ---
    @Mock private WebClient.Builder mockWebClientBuilder;
    @Mock private WebClient mockWebClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    // --- Instância da Classe Sob Teste ---
    private LastFmService lastFmService;

    // --- Dados de Teste ---
    private final String FAKE_API_KEY = "fake-api-key";
    private final String TEST_ARTIST = "Artista Teste";
    private final String TEST_TRACK = "Musica Teste";
    private final String EXPECTED_PLAYCOUNT_STR = "987654";
    private final BigInteger EXPECTED_PLAYCOUNT_BIGINT = new BigInteger(EXPECTED_PLAYCOUNT_STR);
    private final Duration TIMEOUT = Duration.ofSeconds(10); // O mesmo timeout do seu serviço

    @BeforeEach
    void setUp() {
        // Configura o Builder para retornar o WebClient mockado
        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Instancia o serviço manualmente, passando o builder JÁ configurado
        lastFmService = new LastFmService(mockWebClientBuilder, FAKE_API_KEY);

        // Configuração genérica da cadeia de mocks do WebClient para chamadas GET
        when(mockWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    // ===================================================================
    // TESTES PARA O MÉTODO: getTrackPlayCount
    // ===================================================================

    @Test
    void getTrackPlayCount_deveRetornarContagem_quandoEncontrado() {
        // --- Arrange (Given) ---
        // Cria a resposta DTO simulada que a API retornaria
        LastFmGetInfoResponse mockResponse = new LastFmGetInfoResponse();
        TrackDetails trackDetails = new TrackDetails();
        trackDetails.setPlaycount(EXPECTED_PLAYCOUNT_STR);
        mockResponse.setTrack(trackDetails);

        // Configura o WebClient mock para retornar a resposta simulada
        when(responseSpec.bodyToMono(eq(LastFmGetInfoResponse.class))).thenReturn(Mono.just(mockResponse));

        // --- Act (When) ---
        // Chama o método síncrono que estamos a testar
        BigInteger actualPlayCount = lastFmService.getTrackPlayCount(TEST_ARTIST, TEST_TRACK);

        // --- Assert (Then) ---
        // Verifica se o resultado está correto
        assertEquals(EXPECTED_PLAYCOUNT_BIGINT, actualPlayCount);
    }

    @Test
    void getTrackPlayCount_deveRetornarZero_quandoApiRetornaErro() {
        // --- Arrange ---
        // Simula um erro HTTP 404 (Not Found)
        when(responseSpec.bodyToMono(eq(LastFmGetInfoResponse.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

        // --- Act ---
        BigInteger actualPlayCount = lastFmService.getTrackPlayCount(TEST_ARTIST, TEST_TRACK);

        // --- Assert ---
        // O método deve capturar a exceção e retornar BigInteger.ZERO
        assertEquals(BigInteger.ZERO, actualPlayCount);
    }

    @Test
    void getTrackPlayCount_deveRetornarZero_quandoPlaycountNaoENumerico() {
        // --- Arrange ---
        LastFmGetInfoResponse mockResponse = new LastFmGetInfoResponse();
        TrackDetails trackDetails = new TrackDetails();
        trackDetails.setPlaycount("texto-invalido"); // Valor não numérico
        mockResponse.setTrack(trackDetails);
        when(responseSpec.bodyToMono(eq(LastFmGetInfoResponse.class))).thenReturn(Mono.just(mockResponse));

        // --- Act ---
        BigInteger actualPlayCount = lastFmService.getTrackPlayCount(TEST_ARTIST, TEST_TRACK);

        // --- Assert ---
        assertEquals(BigInteger.ZERO, actualPlayCount);
    }


    // ===================================================================
    // TESTES PARA O MÉTODO: getDiverseTrackPlays
    // ===================================================================

    @Test
    void getDiverseTrackPlays_deveRetornarListaDeMusicas_quandoEncontrado() {
        // --- Arrange ---
        // Cria a resposta DTO simulada para o track.search
        LastFmSearchResponse mockResponse = new LastFmSearchResponse();
        SearchResults results = new SearchResults();
        TrackMatches trackMatches = new TrackMatches();
        TrackInfo track1 = new TrackInfo(); track1.setName("Musica A");
        TrackInfo track2 = new TrackInfo(); track2.setName("Musica B");
        trackMatches.setTrackList(List.of(track1, track2));
        results.setTrackMatches(trackMatches);
        mockResponse.setResults(results);

        // Configura o mock para esta chamada específica
        when(responseSpec.bodyToMono(eq(LastFmSearchResponse.class))).thenReturn(Mono.just(mockResponse));

        // --- Act ---
        // Vamos testar pedindo 2 músicas (o que deve resultar em 1 página de busca)
        List<TrackInfo> actualTracks = lastFmService.getDiverseTrackPlays(2);

        // --- Assert ---
        assertNotNull(actualTracks);
        assertEquals(2, actualTracks.size());
        assertEquals("Musica A", actualTracks.get(0).getName());
        assertEquals("Musica B", actualTracks.get(1).getName());

        // Verifica se o método get do WebClient foi chamado uma vez (para uma página)
        verify(mockWebClient, times(1)).get();
    }

    @Test
    void getDiverseTrackPlays_deveRetornarListaVazia_quandoApiRetornaErro() {
        // --- Arrange ---
        // Simula um erro HTTP 503 (Service Unavailable)
        when(responseSpec.bodyToMono(eq(LastFmSearchResponse.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(503, "Service Unavailable", null, null, null)));

        // --- Act ---
        List<TrackInfo> actualTracks = lastFmService.getDiverseTrackPlays(10);

        // --- Assert ---
        // O método deve capturar a exceção e retornar uma lista vazia
        assertNotNull(actualTracks);
        assertTrue(actualTracks.isEmpty());
    }
}