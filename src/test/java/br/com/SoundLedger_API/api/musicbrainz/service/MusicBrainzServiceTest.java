package br.com.SoundLedger_API.api.musicbrainz.service; // Use o mesmo pacote + Test

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

// Imports do Spring, WebClient e Reactor
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier; // Import para testar Mono/Flux

// Imports dos seus DTOs
import br.com.SoundLedger_API.api.musicbrainz.dto.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Testes unitários para a classe MusicBrainzService.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Evita erros de "Unnecessary stubbings"
class MusicBrainzServiceTest {

    // --- Mocks das Dependências ---
    @Mock private WebClient.Builder mockWebClientBuilder;
    @Mock private WebClient mockWebClient;
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    // --- Instância da Classe Sob Teste ---
    private MusicBrainzService musicBrainzService;

    // --- Dados de Teste ---
    private final String TEST_ISRC = "TESTISRC123";
    private final String TEST_ARTIST = "Artista Teste";
    private final String TEST_TRACK = "Musica Teste";
    private final String EXPECTED_RECORDING_ID = "rec-id-123";
    private final String COMPOSER_NAME = "Compositor A";

    @BeforeEach
    void setUp() {
        // Configura o Builder mockado para retornar o WebClient mockado
        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeader(eq(HttpHeaders.USER_AGENT), anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Instancia o serviço manualmente, passando o builder já configurado
        musicBrainzService = new MusicBrainzService(mockWebClientBuilder);

        // Configuração genérica da cadeia de mocks do WebClient para chamadas GET
        when(mockWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    // ===================================================================
    // TESTES PARA O MÉTODO SÍNCRONO: findComposersByIsrc
    // ===================================================================

    @Test
    void findComposersByIsrc_deveRetornarNomes_quandoEncontradoComRelacoes() {
        // --- Arrange (Given) ---
        // Cria a resposta DTO simulada que a API retornaria
        MusicBrainzSearchResponse mockResponse = new MusicBrainzSearchResponse();
        Recording recording = new Recording();
        Artist artist = new Artist(); artist.setName(COMPOSER_NAME);
        Relation composerRelation = new Relation(); composerRelation.setType("composer"); composerRelation.setArtist(artist);
        Relation lyricistRelation = new Relation(); lyricistRelation.setType("lyricist"); lyricistRelation.setArtist(artist);
        recording.setRelations(List.of(composerRelation, lyricistRelation));
        mockResponse.setRecordings(List.of(recording));

        // Configura o WebClient mock para retornar a resposta simulada
        when(responseSpec.bodyToMono(eq(MusicBrainzSearchResponse.class))).thenReturn(Mono.just(mockResponse));

        // --- Act (When) ---
        // Chama o método síncrono que estamos a testar
        List<String> actualComposers = musicBrainzService.findComposersByIsrc(TEST_ISRC);

        // --- Assert (Then) ---
        // Verifica se o resultado está correto
        assertNotNull(actualComposers);
        assertEquals(1, actualComposers.size()); // Apenas 1 devido ao distinct()
        assertEquals(COMPOSER_NAME, actualComposers.get(0));
    }

    @Test
    void findComposersByIsrc_deveLancarExcecao_quandoGravacaoNaoEncontrada() {
        // --- Arrange ---
        MusicBrainzSearchResponse mockEmptyResponse = new MusicBrainzSearchResponse();
        mockEmptyResponse.setRecordings(Collections.emptyList());

        when(responseSpec.bodyToMono(eq(MusicBrainzSearchResponse.class))).thenReturn(Mono.just(mockEmptyResponse));

        // --- Act & Assert ---
        // Verifica se a exceção esperada é lançada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            musicBrainzService.findComposersByIsrc(TEST_ISRC);
        });

        assertTrue(exception.getMessage().contains("Nenhuma gravacao encontrada"));
    }

    @Test
    void findComposersByIsrc_deveLancarExcecao_quandoApiRetornaErro() {
        // --- Arrange ---
        // Simula um erro HTTP 500 da API
        when(responseSpec.bodyToMono(eq(MusicBrainzSearchResponse.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(500, "Server Error", null, null, null)));

        // --- Act & Assert ---
        // O .block() dentro do método deve propagar esta exceção
        assertThrows(WebClientResponseException.class, () -> {
            musicBrainzService.findComposersByIsrc(TEST_ISRC);
        });
    }

    // ===================================================================
    // TESTES PARA O MÉTODO REATIVO: findRecordingId
    // ===================================================================

    @Test
    void findRecordingId_deveRetornarId_quandoEncontrado() {
        // --- Arrange ---
        MusicBrainzSearchResponse mockResponse = new MusicBrainzSearchResponse();
        Recording recording = new Recording();
        recording.setId(EXPECTED_RECORDING_ID);
        mockResponse.setRecordings(List.of(recording));

        when(responseSpec.bodyToMono(eq(MusicBrainzSearchResponse.class))).thenReturn(Mono.just(mockResponse));

        // --- Act ---
        // Chama o método reativo, que retorna um Mono
        Mono<String> resultMono = musicBrainzService.findRecordingId(TEST_ARTIST, TEST_TRACK);

        // --- Assert ---
        // Usa o StepVerifier para testar o fluxo do Mono
        StepVerifier.create(resultMono)
                .expectNext(EXPECTED_RECORDING_ID) // Espera que o próximo evento seja o ID correto
                .verifyComplete(); // Verifica se o Mono completa com sucesso
    }

    @Test
    void findRecordingId_deveEmitirErro_quandoNaoEncontrado() {
        // --- Arrange ---
        MusicBrainzSearchResponse mockEmptyResponse = new MusicBrainzSearchResponse();
        mockEmptyResponse.setRecordings(Collections.emptyList());

        when(responseSpec.bodyToMono(eq(MusicBrainzSearchResponse.class))).thenReturn(Mono.just(mockEmptyResponse));

        // --- Act ---
        Mono<String> resultMono = musicBrainzService.findRecordingId(TEST_ARTIST, TEST_TRACK);

        // --- Assert ---
        // Usa o StepVerifier para verificar se o Mono emite o erro esperado
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Nenhuma gravação encontrada")
                )
                .verify(); // Inicia a verificação
    }
}