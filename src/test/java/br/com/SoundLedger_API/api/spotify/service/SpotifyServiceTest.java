package br.com.SoundLedger_API.api.spotify.service; // Use o seu pacote + Test

// Imports do JUnit 5 e Mockito
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

// Imports do Spring e WebClient
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

// Imports dos seus DTOs
import br.com.SoundLedger_API.api.spotify.dto.*;

import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Testes unitários para a classe SpotifyService.
 * Foca em verificar a lógica de negócio, mockando as chamadas de API externas.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SpotifyServiceTest {

    // --- Mocks das Dependências ---
    // Precisamos de mocks para toda a cadeia fluente do WebClient
    @Mock private WebClient.Builder mockWebClientBuilder;
    @Mock private WebClient mockWebClient; // Um único mock para ambos (auth e api) para simplificar
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    // --- Instância da Classe Sob Teste ---
    // Cria uma instância real do SpotifyService e injeta os mocks acima.
    // @InjectMocks
    private SpotifyService spotifyService;

    // --- Dados de Teste ---
    private final String FAKE_CLIENT_ID = "fake-client-id";
    private final String FAKE_CLIENT_SECRET = "fake-client-secret";
    private final String FAKE_TOKEN = "BQD...fake_token...123";
    private final String TEST_ARTIST = "Queen";
    private final String TEST_TRACK = "Bohemian Rhapsody";
    private final String EXPECTED_ISRC = "GBAAA7500003";

    @BeforeEach
    void setUp() {
        // --- CONFIGURAÇÃO DOS MOCKS PRIMEIRO ---
        // Garante que as chamadas encadeadas retornem o próprio builder
        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        // Garante que .build() retorna o WebClient mockado
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // --- CRIAÇÃO MANUAL DA INSTÂNCIA DO SERVIÇO ---
        // Crie o serviço AGORA, passando o builder JÁ configurado
        spotifyService = new SpotifyService(mockWebClientBuilder, FAKE_CLIENT_ID, FAKE_CLIENT_SECRET);

        // --- CONFIGURAÇÃO DO RESTO DA CADEIA (como antes) ---
        // (Isso configura o que acontece QUANDO o serviço USA o mockWebClient)
        when(mockWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpec);
    }

    @Test
    void searchTrackAndGetIsrc_deveRetornarIsrcCorretamente_quandoFluxoCompletoFunciona() {
        // --- Arrange (Given) ---
        // 1. Configura o mock da resposta do TOKEN
        SpotifyTokenResponse mockTokenResponse = new SpotifyTokenResponse();
        mockTokenResponse.setAccess_token(FAKE_TOKEN);
        mockTokenResponse.setExpires_in(3600);
        when(responseSpec.bodyToMono(eq(SpotifyTokenResponse.class))).thenReturn(Mono.just(mockTokenResponse));

        // 2. Configura o mock da resposta da BUSCA
        SpotifySearchResponse mockSearchResponse = new SpotifySearchResponse();
        Tracks tracks = new Tracks();
        TrackItem item = new TrackItem();
        ExternalIds externalIds = new ExternalIds();
        externalIds.setIsrc(EXPECTED_ISRC);
        item.setExternalIds(externalIds);
        tracks.setItems(List.of(item));
        mockSearchResponse.setTracks(tracks);
        // Precisamos reconfigurar o bodyToMono para o tipo correto para esta chamada específica
        when(requestHeadersSpec.retrieve().bodyToMono(eq(SpotifySearchResponse.class)))
                .thenReturn(Mono.just(mockSearchResponse));


        // --- Act (When) ---
        // Chama o método que estamos a testar
        String actualIsrc = spotifyService.searchTrackAndGetIsrc(TEST_ARTIST, TEST_TRACK);


        // --- Assert (Then) ---
        // 3. Verifica se o resultado está correto
        assertNotNull(actualIsrc);
        assertEquals(EXPECTED_ISRC, actualIsrc);

        // 4. Verifica se as chamadas de API (POST e GET) foram feitas
        verify(mockWebClient, times(1)).post(); // Garante que a busca por token foi feita
        verify(mockWebClient, times(1)).get();  // Garante que a busca pela faixa foi feita
    }

    @Test
    void searchTrackAndGetIsrc_deveLancarExcecao_quandoMusicaNaoEncontrada() {
        // --- Arrange ---
        // 1. Configura o mock da resposta do TOKEN (ainda é necessário)
        SpotifyTokenResponse mockTokenResponse = new SpotifyTokenResponse();
        mockTokenResponse.setAccess_token(FAKE_TOKEN);
        mockTokenResponse.setExpires_in(3600);
        when(responseSpec.bodyToMono(eq(SpotifyTokenResponse.class))).thenReturn(Mono.just(mockTokenResponse));

        // 2. Configura o mock da resposta da BUSCA com uma lista VAZIA
        SpotifySearchResponse mockEmptyResponse = new SpotifySearchResponse();
        Tracks emptyTracks = new Tracks();
        emptyTracks.setItems(Collections.emptyList());
        mockEmptyResponse.setTracks(emptyTracks);
        when(requestHeadersSpec.retrieve().bodyToMono(eq(SpotifySearchResponse.class)))
                .thenReturn(Mono.just(mockEmptyResponse));

        // --- Act & Assert ---
        // Verifica se a exceção correta é lançada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spotifyService.searchTrackAndGetIsrc(TEST_ARTIST, "Musica Inexistente");
        });

        // Verifica a mensagem da exceção
        assertTrue(exception.getMessage().contains("ISRC não encontrado"));
    }

    @Test
    void searchTrackAndGetIsrc_deveFalhar_quandoObtencaoDeTokenFalha() {
        // --- Arrange ---
        // 1. Configura o mock da resposta do TOKEN para retornar um erro HTTP
        when(responseSpec.bodyToMono(eq(SpotifyTokenResponse.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(401, "Unauthorized", null, null, null)));

        // --- Act & Assert ---
        // A exceção da API deve ser propagada pelo .block()
        assertThrows(WebClientResponseException.class, () -> {
            spotifyService.searchTrackAndGetIsrc(TEST_ARTIST, TEST_TRACK);
        });

        // Garante que a chamada GET para a busca NUNCA foi feita
        verify(mockWebClient, never()).get();
    }
}