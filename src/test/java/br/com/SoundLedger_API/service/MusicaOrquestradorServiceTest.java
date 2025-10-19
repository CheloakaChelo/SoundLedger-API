package br.com.SoundLedger_API.service;

// Imports necessários do JUnit 5 e Mockito
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*; // Para when, verify, any, etc.
import static org.junit.jupiter.api.Assertions.*; // Para assertEquals, assertNotNull, etc.

// Imports dos seus serviços, DAOs e modelos
import br.com.SoundLedger_API.api.blockchain.service.BlockchainService;
import br.com.SoundLedger_API.api.musicbrainz.service.MusicBrainzService;
import br.com.SoundLedger_API.api.spotify.service.SpotifyService;
import br.com.SoundLedger_API.dao.IMusica;
import br.com.SoundLedger_API.dao.IUser; // Usado para buscar usuários
import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.User;
import br.com.SoundLedger_API.model.entity.ParticipacaoNaMusica;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Testes unitários para a classe MusicaOrquestradorService.
 * Foca em verificar a lógica de orquestração, mockando as dependências externas.
 */
@ExtendWith(MockitoExtension.class) // Habilita as anotações do Mockito
class MusicaOrquestradorServiceTest {

    // --- Mocks das Dependências ---
    // Cria instâncias "falsas" (mocks) de cada dependência que o orquestrador usa.
    @Mock
    private SpotifyService spotifyService;

    @Mock
    private MusicBrainzService musicBrainzService;

    @Mock
    private IUser userDao; // Mockamos o DAO diretamente, como na sua implementação

    @Mock
    private BlockchainService blockchainService;

    @Mock
    private IMusica musicaDao;

    // --- Instância da Classe Sob Teste ---
    // Cria uma instância real do MusicaOrquestradorService e injeta
    // automaticamente os mocks criados acima nos seus campos correspondentes.
    @InjectMocks
    private MusicaOrquestradorService musicaOrquestradorService;

    // --- Dados de Teste Comuns ---
    private String artistaInputNome;
    private String tituloInput;
    private String expectedIsrc;
    private List<String> expectedNomesCompositores;
    private List<User> foundUsers;
    private List<String> expectedWallets;
    private List<BigInteger> expectedSplits;
    private String expectedContractAddress;
    private Musica musicaSalvaMock;

    @BeforeEach // Método executado antes de CADA teste (@Test)
    void setUp() {
        // Define dados de teste reutilizáveis
        artistaInputNome = "Artista Teste";
        tituloInput = "Musica Teste";
        expectedIsrc = "TESTISRC123";
        expectedNomesCompositores = List.of("Compositor A", "Compositor B");

        // Simula os utilizadores encontrados no banco de dados
        User userA = new User(); userA.setId("userA1"); userA.setNome("Compositor A"); userA.setEnderecoCarteira("0xWalletA");
        User userB = new User(); userB.setId("userB1"); userB.setNome("Compositor B"); userB.setEnderecoCarteira("0xWalletB");
        foundUsers = List.of(userA, userB);

        expectedWallets = List.of("0xWalletA", "0xWalletB");
        // O método calcularSplits é privado, não o mockamos. Testamos o resultado dele.
        // Para 2 detentores, a lógica calcula [50, 50]
        expectedSplits = List.of(BigInteger.valueOf(50), BigInteger.valueOf(50));
        expectedContractAddress = "0xContratoTeste123";

        // Cria um mock do objeto Musica que esperamos que seja salvo
        musicaSalvaMock = new Musica();
        musicaSalvaMock.setId("musica123"); // Simula que o save retornou um ID
        musicaSalvaMock.setTitulo(tituloInput);
        musicaSalvaMock.setIsrc(expectedIsrc);
        musicaSalvaMock.setContratoBlockchain(expectedContractAddress);
        // Adiciona as participações esperadas
        List<ParticipacaoNaMusica> participacoes = new ArrayList<>();
        ParticipacaoNaMusica pA = new ParticipacaoNaMusica(); pA.setUsuarioId("userA1"); pA.setNomeArtista("Compositor A"); pA.setEnderecoCarteira("0xWalletA"); pA.setSplit(expectedSplits.get(0));
        ParticipacaoNaMusica pB = new ParticipacaoNaMusica(); pB.setUsuarioId("userB1"); pB.setNomeArtista("Compositor B"); pB.setEnderecoCarteira("0xWalletB"); pB.setSplit(expectedSplits.get(1));
        participacoes.add(pA);
        participacoes.add(pB);
        musicaSalvaMock.setParticipacoes(participacoes);
    }

    // --- O Teste Principal (Caminho Feliz) ---
    @Test
    void cadastrarNovaMusica_deveExecutarFluxoCompleto_quandoDadosSaoValidos() throws Exception {
        // --- Arrange (Given) ---
        // Configura o comportamento esperado dos mocks quando forem chamados.

        // 1. Spotify deve retornar o ISRC esperado
        when(spotifyService.searchTrackAndGetIsrc(artistaInputNome, tituloInput))
                .thenReturn(expectedIsrc);

        // 2. MusicBrainz deve retornar a lista de nomes esperada
        when(musicBrainzService.findComposersByIsrc(expectedIsrc))
                .thenReturn(expectedNomesCompositores);

        // 3. UserDao deve retornar os utilizadores correspondentes
        when(userDao.findAllByNomeIn(expectedNomesCompositores))
                .thenReturn(foundUsers);

        // 4. BlockchainService deve retornar o endereço do contrato esperado
        //    Usamos 'any()' para os argumentos de lista porque a comparação exata pode ser complexa.
        //    Poderíamos usar ArgumentCaptor para verificar os valores exatos se necessário.
        when(blockchainService.deployNewRoyaltyContract(
                eq(tituloInput),        // Título exato
                eq(expectedIsrc),       // ISRC exato
                eq(expectedWallets),    // Lista de carteiras exata
                eq(expectedSplits)))    // Lista de splits exata (calculada implicitamente)
                .thenReturn(expectedContractAddress);

        // 5. MusicaDao deve retornar a música salva mockada quando 'save' for chamado com qualquer Musica
        when(musicaDao.save(any(Musica.class)))
                .thenReturn(musicaSalvaMock);


        // --- Act (When) ---
        // Executa o método que estamos a testar
        Musica resultado = musicaOrquestradorService.cadastrarNovaMusica(artistaInputNome, tituloInput);


        // --- Assert (Then) ---
        // Verifica se o resultado e as interações com os mocks foram como esperado.

        // Verifica o objeto Musica retornado
        assertNotNull(resultado, "O resultado não deve ser nulo");
        assertEquals(musicaSalvaMock.getId(), resultado.getId(), "O ID da música salva deve ser o esperado");
        assertEquals(tituloInput, resultado.getTitulo(), "O título deve ser o esperado");
        assertEquals(expectedIsrc, resultado.getIsrc(), "O ISRC deve ser o esperado");
        assertEquals(expectedContractAddress, resultado.getContratoBlockchain(), "O endereço do contrato deve ser o esperado");
        assertNotNull(resultado.getParticipacoes(), "A lista de participações não deve ser nula");
        assertEquals(2, resultado.getParticipacoes().size(), "Deve haver 2 participações");
        // Verifica detalhes da primeira participação
        assertEquals("userA1", resultado.getParticipacoes().get(0).getUsuarioId());
        assertEquals(BigInteger.valueOf(50), resultado.getParticipacoes().get(0).getSplit());

        // Verifica se os mocks foram chamados na ordem correta e com os argumentos certos
        verify(spotifyService, times(1)).searchTrackAndGetIsrc(artistaInputNome, tituloInput);
        verify(musicBrainzService, times(1)).findComposersByIsrc(expectedIsrc);
        verify(userDao, times(1)).findAllByNomeIn(expectedNomesCompositores);
        verify(blockchainService, times(1)).deployNewRoyaltyContract(tituloInput, expectedIsrc, expectedWallets, expectedSplits);
        verify(musicaDao, times(1)).save(any(Musica.class)); // Verifica se o save foi chamado uma vez

        // Garante que nenhuma outra interação inesperada ocorreu com os mocks
        verifyNoMoreInteractions(spotifyService, musicBrainzService, userDao, blockchainService, musicaDao);
    }

    // --- Outros Testes (Exemplos de Caminhos de Falha) ---

    @Test
    void cadastrarNovaMusica_deveLancarExcecao_quandoUsuarioNaoEncontrado() throws Exception {
        // Arrange
        when(spotifyService.searchTrackAndGetIsrc(anyString(), anyString())).thenReturn(expectedIsrc);
        when(musicBrainzService.findComposersByIsrc(anyString())).thenReturn(expectedNomesCompositores);
        // Simula que o DAO retorna uma lista vazia ou incompleta
        when(userDao.findAllByNomeIn(expectedNomesCompositores)).thenReturn(Collections.emptyList());

        // Act & Assert
        // Verifica se a exceção esperada (RuntimeException) é lançada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            musicaOrquestradorService.cadastrarNovaMusica(artistaInputNome, tituloInput);
        });

        // Verifica a mensagem da exceção
        assertTrue(exception.getMessage().contains("nao sao utilizadores registados"), "Mensagem de erro esperada não encontrada");

        // Garante que o deploy e o save NUNCA foram chamados
        verify(blockchainService, never()).deployNewRoyaltyContract(any(), any(), any(), any());
        verify(musicaDao, never()).save(any());
    }

    @Test
    void cadastrarNovaMusica_deveLancarExcecao_quandoDeployFalha() throws Exception {
        // Arrange
        when(spotifyService.searchTrackAndGetIsrc(anyString(), anyString())).thenReturn(expectedIsrc);
        when(musicBrainzService.findComposersByIsrc(anyString())).thenReturn(expectedNomesCompositores);
        when(userDao.findAllByNomeIn(expectedNomesCompositores)).thenReturn(foundUsers);
        // Simula uma falha no deploy da blockchain
        when(blockchainService.deployNewRoyaltyContract(anyString(), anyString(), anyList(), anyList()))
                .thenThrow(new Exception("Falha ao conectar com Infura"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            musicaOrquestradorService.cadastrarNovaMusica(artistaInputNome, tituloInput);
        });

        assertEquals("Falha ao conectar com Infura", exception.getMessage());

        // Garante que o save NUNCA foi chamado
        verify(musicaDao, never()).save(any());
    }

    // Você pode adicionar mais testes para outros cenários de falha
    // (Spotify falha, MusicBrainz falha, calcularSplits com 0 detentores, etc.)
}