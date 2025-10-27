package br.com.SoundLedger_API.service; // Use o mesmo pacote da classe que está a testar

// Imports do JUnit 5 e Mockito
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

// Imports do seu projeto (ajuste os pacotes se necessário)
import br.com.SoundLedger_API.dao.IUser;
import br.com.SoundLedger_API.model.entity.User;

import java.util.Collections;
import java.util.List;

/**
 * Testes unitários para a classe UserService, focando na interação com IUser DAO.
 */
@ExtendWith(MockitoExtension.class) // Habilita as anotações do Mockito
class UserServiceTest {

    // --- Mock da Dependência ---
    // Cria uma instância "falsa" (mock) do DAO IUser.
    // O teste controlará o que este mock retorna.
    @Mock
    private IUser userDao;

    // --- Instância da Classe Sob Teste ---
    // Cria uma instância real do UserService e injeta
    // automaticamente o mock 'userDao' no seu campo correspondente.
    @InjectMocks
    private UserService userService;

    // --- Dados de Teste ---
    private User userA;
    private User userB;
    private List<String> nomesInput;

    @BeforeEach // Método executado antes de CADA teste (@Test)
    void setUp() {
        // Prepara objetos User simulados que o DAO poderia retornar
        userA = new User();
        // Assumindo que você tem setters no seu User.java. Se não, use o construtor.
        // Assumindo também que você tem um getter getEnderecoCarteira().
        // Ajuste os nomes dos métodos conforme seu modelo User.
        // userA.setId("userA1"); // O ID não é usado diretamente no método testado
        userA.setNome("Compositor A");
        userA.setEnderecoCarteira("0xWalletA"); // Use o setter correto

        userB = new User();
        // userB.setId("userB1");
        userB.setNome("Compositor B");
        userB.setEnderecoCarteira("0xWalletB"); // Use o setter correto

        nomesInput = List.of("Compositor A", "Compositor B");
    }

    // --- Teste do Caminho Feliz ---
    @Test
    void findWalletsByNames_deveRetornarListaDeCarteiras_quandoTodosUsuariosSaoEncontrados() {
        // --- Arrange (Given) ---
        // Configura o mock do DAO: QUANDO o método findAllByNomeIn for chamado
        // COM a lista 'nomesInput', ENTÃO ele deve RETORNAR uma lista contendo userA e userB.
        when(userDao.findAllByNomeIn(nomesInput)).thenReturn(List.of(userA, userB));

        // --- Act (When) ---
        // Chama o método do UserService que estamos a testar
        List<String> actualWallets = userService.findWalletsByNames(nomesInput);

        // --- Assert (Then) ---
        // Verifica se o resultado está correto
        assertNotNull(actualWallets, "A lista de carteiras não deve ser nula.");
        assertEquals(2, actualWallets.size(), "A lista deve conter 2 carteiras.");
        assertTrue(actualWallets.contains("0xWalletA"), "Deve conter a carteira do User A.");
        assertTrue(actualWallets.contains("0xWalletB"), "Deve conter a carteira do User B.");

        // Verifica se o método do DAO foi chamado exatamente uma vez com os argumentos corretos
        verify(userDao, times(1)).findAllByNomeIn(nomesInput);
        // Garante que nenhuma outra interação ocorreu com este mock
        verifyNoMoreInteractions(userDao);
    }

    // --- Teste de Falha: Utilizador Não Encontrado ---
    @Test
    void findWalletsByNames_deveLancarRuntimeException_quandoUmUsuarioNaoEEncontrado() {
        // --- Arrange ---
        // Prepara uma lista de nomes onde um deles não corresponderá a um User retornado
        List<String> nomesComFaltante = List.of("Compositor A", "Compositor C"); // "C" não será encontrado

        // Configura o mock do DAO para retornar apenas o userA quando buscar pela lista 'nomesComFaltante'
        when(userDao.findAllByNomeIn(nomesComFaltante)).thenReturn(List.of(userA)); // Retorna lista incompleta

        // --- Act & Assert ---
        // Verifica se a chamada ao método lança a exceção esperada (RuntimeException)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findWalletsByNames(nomesComFaltante);
        });

        // Verifica se a mensagem da exceção está correta
        assertTrue(exception.getMessage().contains("compositores nao foram encontrados"),
                "A mensagem de erro deve indicar que utilizadores não foram encontrados.");

        // Verifica se o método do DAO foi chamado
        verify(userDao, times(1)).findAllByNomeIn(nomesComFaltante);
        verifyNoMoreInteractions(userDao);
    }

    // --- Teste de Borda: Lista de Nomes Vazia ---
    @Test
    void findWalletsByNames_deveRetornarListaVazia_quandoInputEstaVazio() {
        // --- Arrange ---
        List<String> nomesVazio = Collections.emptyList();

        // Configura o DAO para retornar uma lista vazia quando a busca for vazia
        when(userDao.findAllByNomeIn(nomesVazio)).thenReturn(Collections.emptyList());

        // --- Act ---
        List<String> actualWallets = userService.findWalletsByNames(nomesVazio);

        // --- Assert ---
        assertNotNull(actualWallets, "A lista de carteiras não deve ser nula.");
        assertTrue(actualWallets.isEmpty(), "A lista de carteiras deve estar vazia.");

        // Verifica se o método do DAO foi chamado
        verify(userDao, times(1)).findAllByNomeIn(nomesVazio);
        verifyNoMoreInteractions(userDao);
    }
}