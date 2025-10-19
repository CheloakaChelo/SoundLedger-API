package br.com.SoundLedger_API.service;

import br.com.SoundLedger_API.api.blockchain.service.BlockchainService;
import br.com.SoundLedger_API.api.musicbrainz.service.MusicBrainzService;
import br.com.SoundLedger_API.api.spotify.service.SpotifyService;
import br.com.SoundLedger_API.dao.IMusica;
import br.com.SoundLedger_API.dao.IUser;
import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.ParticipacaoNaMusica;
import br.com.SoundLedger_API.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicaOrquestradorService {

    private final SpotifyService spotifyService;
    private final MusicBrainzService musicBrainzService;
    private final UserService userService;
    private final BlockchainService blockchainService;
    private final IMusica dao;
    private final IUser userDao;

    public MusicaOrquestradorService(SpotifyService spotifyService, MusicBrainzService musicBrainzService,
                                     UserService userService, BlockchainService blockchainService, IMusica dao, IUser userDao){
        this.spotifyService = spotifyService;
        this.musicBrainzService = musicBrainzService;
        this.userService = userService;
        this.blockchainService = blockchainService;
        this.dao = dao;
        this.userDao = userDao;
    }

    public Musica cadastrarNovaMusica(String artistaInputNome, String titulo) throws Exception {

        System.out.println("PASSO 1/7: Buscando ISRC no Spotify...");
        String isrc = spotifyService.searchTrackAndGetIsrc(artistaInputNome, titulo);
        System.out.println("ISRC encontrado: " + isrc);

        System.out.println("PASSO 2/7: Buscando compositores no MusicBrainz...");
        List<String> nomesCompositoresMusicBrainz = musicBrainzService.findComposersByIsrc(isrc);
        System.out.println("Compositores (MusicBrainz): " + nomesCompositoresMusicBrainz);

        // --- PASSO 3: ASSOCIAÇÃO COM UTILIZADORES LOCAIS ---
        System.out.println("PASSO 3/7: Buscando utilizadores correspondentes no DB local...");
        // Usamos o método do DAO que criámos
        List<User> usuariosEncontrados = userDao.findAllByNomeIn(nomesCompositoresMusicBrainz);

        // Validação CRUCIAL: Encontrámos todos os compositores como utilizadores registados?
        if (usuariosEncontrados.size() != nomesCompositoresMusicBrainz.size()) {
            // Descobre quais nomes não foram encontrados
            List<String> nomesEncontrados = usuariosEncontrados.stream().map(User::getNome).collect(Collectors.toList());
            List<String> nomesFaltantes = nomesCompositoresMusicBrainz.stream()
                    .filter(nome -> !nomesEncontrados.contains(nome))
                    .collect(Collectors.toList());
            throw new RuntimeException("Erro: Os seguintes compositores/letristas do MusicBrainz nao sao utilizadores registados na aplicacao: " + nomesFaltantes);
        }
        System.out.println("Utilizadores encontrados: " + usuariosEncontrados.stream().map(User::getNome).collect(Collectors.toList()));
        // --- FIM DA ASSOCIAÇÃO ---

        System.out.println("PASSO 4/7: Extraindo carteiras...");
        List<String> wallets = usuariosEncontrados.stream()
                .map(User::getEnderecoCarteira) // Use o getter correto
                .collect(Collectors.toList());
        System.out.println("Carteiras: " + wallets);

        System.out.println("PASSO 5/7: Calculando splits...");
        List<BigInteger> splits = calcularSplits(wallets.size());
        System.out.println("Splits: " + splits);

        // --- PASSO 5.5: CRIAR LISTA DE PARTICIPAÇÕES PARA O MONGODB ---
        List<ParticipacaoNaMusica> participacoes = new ArrayList<>();
        for (int i = 0; i < usuariosEncontrados.size(); i++) {
            User user = usuariosEncontrados.get(i);
            BigInteger split = splits.get(i);

            ParticipacaoNaMusica p = new ParticipacaoNaMusica();
            p.setUsuarioId(user.getId()); // Use o getter correto para o ID
            p.setNomeArtista(user.getNome()); // Use o getter correto para o Nome
            p.setEnderecoCarteira(user.getEnderecoCarteira()); // Use o getter correto
            p.setSplit(split); // Use o setter correto

            participacoes.add(p);
        }
        // --- FIM DA CRIAÇÃO DAS PARTICIPAÇÕES ---


        System.out.println("PASSO 6/7: Enviando transacao de deploy para a blockchain...");
        String contractAddress = blockchainService.deployNewRoyaltyContract(
                titulo, isrc, wallets, splits // Passa as carteiras e splits para o contrato
        );
        System.out.println("Contrato criado em: " + contractAddress);

        System.out.println("PASSO 7/7: Salvando musica no MongoDB...");
        Musica novaMusica = new Musica();
        novaMusica.setTitulo(titulo);
        // Associe o artista principal corretamente (pode precisar buscar o User dele também)
        // novaMusica.setArtistaPrincipalId( ... id do artistaInputNome ... );
        novaMusica.setIsrc(isrc);
        novaMusica.setContratoBlockchain(contractAddress);
        novaMusica.setParticipacoes(participacoes); // ✅ Salva a lista de participantes

        Musica musicaSalva = dao.save(novaMusica);
        System.out.println("✅ SUCESSO! Musica cadastrada no MongoDB com ID: " + musicaSalva.getId());

        return musicaSalva;
    }

    private List<BigInteger> calcularSplits(int numeroDeDetentores) {
        if (numeroDeDetentores == 0) {
            throw new RuntimeException("Não é possível calcular splits para zero detentores");
        }
        BigInteger cem = BigInteger.valueOf(100);
        BigInteger n = BigInteger.valueOf(numeroDeDetentores);
        BigInteger splitBase = cem.divide(n);
        BigInteger resto = cem.remainder(n);

        List<BigInteger> initialSplits = java.util.stream.Stream.generate(() -> splitBase)
                .limit(numeroDeDetentores)
                .collect(Collectors.toList());

        List<BigInteger> splits = new ArrayList<>(initialSplits);



        if (!splits.isEmpty() && resto.compareTo(BigInteger.ZERO) > 0) {
            splits.set(0, splits.get(0).add(resto));
        }
        return splits;
    }
}
