package br.com.SoundLedger_API.service;

import br.com.SoundLedger_API.api.blockchain.service.BlockchainService;
import br.com.SoundLedger_API.api.musicbrainz.dto.Recording;
import br.com.SoundLedger_API.api.musicbrainz.service.MusicBrainzService;
import br.com.SoundLedger_API.api.spotify.dto.TrackItem;
import br.com.SoundLedger_API.api.spotify.service.SpotifyService;
import br.com.SoundLedger_API.controller.MusicaController;
import br.com.SoundLedger_API.dao.IMusica;
import br.com.SoundLedger_API.dao.IUser;
import br.com.SoundLedger_API.model.dto.CadastroViaISRCRequestDTO;
import br.com.SoundLedger_API.model.dto.MusicDetailsDTO;
import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.ParticipacaoNaMusica;
import br.com.SoundLedger_API.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
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

    private static final Logger logger = LoggerFactory.getLogger(MusicaOrquestradorService.class);

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

        System.out.println("PASSO 3/7: Buscando utilizadores correspondentes no DB local...");
        List<User> usuariosEncontrados = userDao.findAllByNomeIn(nomesCompositoresMusicBrainz);

        if (usuariosEncontrados.size() != nomesCompositoresMusicBrainz.size()) {
            List<String> nomesEncontrados = usuariosEncontrados.stream().map(User::getNome).collect(Collectors.toList());
            List<String> nomesFaltantes = nomesCompositoresMusicBrainz.stream()
                    .filter(nome -> !nomesEncontrados.contains(nome))
                    .collect(Collectors.toList());
            throw new RuntimeException("Erro: Os seguintes compositores/letristas do MusicBrainz nao sao utilizadores registados na aplicacao: " + nomesFaltantes);
        }
        System.out.println("Utilizadores encontrados: " + usuariosEncontrados.stream().map(User::getNome).collect(Collectors.toList()));

        System.out.println("PASSO 4/7: Extraindo carteiras...");
        List<String> wallets = usuariosEncontrados.stream()
                .map(User::getEnderecoCarteira)
                .collect(Collectors.toList());
        System.out.println("Carteiras: " + wallets);

        System.out.println("PASSO 5/7: Calculando splits...");
        List<BigInteger> splits = calcularSplits(wallets.size());
        System.out.println("Splits: " + splits);

        List<ParticipacaoNaMusica> participacoes = new ArrayList<>();
        for (int i = 0; i < usuariosEncontrados.size(); i++) {
            User user = usuariosEncontrados.get(i);
            BigInteger split = splits.get(i);

            ParticipacaoNaMusica p = new ParticipacaoNaMusica();
            p.setUsuarioId(user.getId());
            p.setNomeArtista(user.getNome());
            p.setEnderecoCarteira(user.getEnderecoCarteira());
            p.setSplit(split);

            participacoes.add(p);
        }


        System.out.println("PASSO 6/7: Enviando transacao de deploy para a blockchain...");
        String contractAddress = blockchainService.deployNewRoyaltyContract(
                titulo, isrc, wallets, splits
        );
        System.out.println("Contrato criado em: " + contractAddress);

        System.out.println("PASSO 7/7: Salvando musica no MongoDB...");
        Musica novaMusica = new Musica();
        novaMusica.setTitulo(titulo);
        // Associe o artista principal corretamente (pode precisar buscar o User dele também)
        // novaMusica.setArtistaPrincipalId( ... id do artistaInputNome ... );
        novaMusica.setIsrc(isrc);
        novaMusica.setContratoBlockchain(contractAddress);
        novaMusica.setParticipacoes(participacoes);

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

    public MusicDetailsDTO getMusicDetailsByIsrc(String isrc) {
        logger.info("Orquestrando busca de detalhes para ISRC: {}", isrc);
        MusicDetailsDTO details = new MusicDetailsDTO();
        details.setIsrc(isrc);

        try {
            Recording recording = musicBrainzService.findRecordingByIsrc(isrc);

            details.setTitulo(recording.getTitle());
            logger.info("Título (MusicBrainz): {}", recording.getTitle());

            if (recording.getArtists() != null && !recording.getArtists().isEmpty()) {
                details.setArtistaPrincipal(recording.getArtists().get(0).getName());
                logger.info("Artista Principal (MusicBrainz): {}", recording.getArtists().get(0).getName());
            } else {
                logger.warn("Artista principal não encontrado nos créditos do MusicBrainz para ISRC: {}", isrc);
            }

            if (recording.getRelations() != null) {
                List<String> compositores = recording.getRelations().stream()
                        .filter(rel -> "composer".equals(rel.getType()) || "lyricist".equals(rel.getType()))
                        .map(rel -> rel.getArtist().getName())
                        .distinct()
                        .collect(Collectors.toList());
                details.setCompositores(compositores);
                logger.info("Compositores/Letristas (MusicBrainz): {}", compositores);

                List<String> produtores = recording.getRelations().stream()
                        .filter(rel -> "producer".equals(rel.getType()))
                        .map(rel -> rel.getArtist().getName())
                        .distinct()
                        .collect(Collectors.toList());
                details.setProdutores(produtores);
                logger.info("Produtores (MusicBrainz): {}", produtores);

            } else {
                logger.warn("Relações de artista não encontradas no MusicBrainz para ISRC: {}", isrc);
                details.setCompositores(Collections.emptyList());
                details.setProdutores(Collections.emptyList());
            }

        } catch (Exception e) {
            logger.error("Erro CRÍTICO ao buscar no MusicBrainz para ISRC {}: {}", isrc, e.getMessage());
            throw new RuntimeException("Falha ao obter dados essenciais do MusicBrainz para o ISRC: " + isrc + ". Causa: " + e.getMessage(), e);
        }

        try {
            TrackItem spotifyTrack = spotifyService.findSpotifyTrackByIsrc(isrc);
            if (spotifyTrack != null) {
                logger.info("Dados adicionais do Spotify encontrados para ISRC {}", isrc);
            }
        } catch (Exception e) {
            logger.warn("Nao foi possivel obter dados do Spotify para ISRC {} (opcional): {}", isrc, e.getMessage());
        }

        logger.info("Busca de detalhes concluída para ISRC: {}", isrc);
        logger.debug("Detalhes retornados (incluindo produtores): {}", details);
        return details;
    }

    public Musica cadastrarMusicaComDadosConfirmados(CadastroViaISRCRequestDTO request) throws Exception {
        logger.info("Iniciando cadastro final (com splits definidos) para ISRC: {}", request.getIsrc());

        String isrc = request.getIsrc();
        String tituloConfirmado = request.getTitulo();
        String artistaPrincipalId = request.getArtistaPrincipalId();

        List<CadastroViaISRCRequestDTO.DetentorInfo> detentoresInput = request.getDetentoresDireitos();

        if (isrc == null || isrc.isEmpty() || tituloConfirmado == null || tituloConfirmado.isEmpty() ||
                artistaPrincipalId == null || artistaPrincipalId.isEmpty() ||
                detentoresInput == null || detentoresInput.isEmpty()) {
            throw new IllegalArgumentException("Dados incompletos para o cadastro final da musica.");
        }

        BigInteger somaSplits = detentoresInput.stream()
                .map(CadastroViaISRCRequestDTO.DetentorInfo::getSplit)
                .reduce(BigInteger.ZERO, BigInteger::add);
        if (somaSplits.compareTo(BigInteger.valueOf(100)) != 0) {
            throw new IllegalArgumentException("A soma das percentagens dos detentores deve ser exatamente 100.");
        }
        logger.info("Dados recebidos validados. Soma dos splits: {}", somaSplits);

        User artistaPrincipal = userDao.findById(artistaPrincipalId).orElseThrow();

        List<String> idsDetentores = detentoresInput.stream()
                .map(CadastroViaISRCRequestDTO.DetentorInfo::getUsuarioId)
                .collect(Collectors.toList());
        logger.info("PASSO 2/5: Buscando e validando utilizadores detentores (IDs: {})...", idsDetentores);
        List<User> usuariosDetentores = (List<User>) userDao.findAllById(idsDetentores);

        if (usuariosDetentores.size() != idsDetentores.size()) {
            throw new RuntimeException("Erro: Um ou mais IDs de utilizadores detentores não foram encontrados.");
        }
        logger.info("Utilizadores detentores validados.");

        List<String> wallets = new ArrayList<>();
        List<BigInteger> splits = new ArrayList<>();
        java.util.Map<String, User> userMap = usuariosDetentores.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        for (CadastroViaISRCRequestDTO.DetentorInfo input : detentoresInput) {
            User user = userMap.get(input.getUsuarioId());
            if (user == null) { throw new RuntimeException(); }
            wallets.add(user.getEnderecoCarteira());
            splits.add(input.getSplit());
        }
        logger.info("PASSO 3/5: Carteiras extraídas: {}", wallets);
        logger.info("PASSO 4/5: Splits confirmados: {}", splits);

        logger.info("PASSO 5/5: Enviando transacao de deploy para a blockchain...");
        String contractAddress = blockchainService.deployNewRoyaltyContract(
                tituloConfirmado, isrc, wallets, splits
        );
        logger.info("Contrato criado em: {}", contractAddress);

        logger.info("PASSO 6/5: Salvando musica no MongoDB...");
        Musica novaMusica = new Musica();
        novaMusica.setTitulo(tituloConfirmado);
        novaMusica.setArtistaPrincipalId(artistaPrincipalId);
        novaMusica.setIsrc(isrc);
        novaMusica.setContratoBlockchain(contractAddress);
        novaMusica.setArtista(artistaPrincipal.getNome());


        List<ParticipacaoNaMusica> participacoes = new ArrayList<>();
        for (CadastroViaISRCRequestDTO.DetentorInfo input : detentoresInput) {
            User user = userMap.get(input.getUsuarioId());
            ParticipacaoNaMusica p = new ParticipacaoNaMusica();
            p.setUsuarioId(user.getId());
            p.setNomeArtista(user.getNome());
            p.setEnderecoCarteira(user.getEnderecoCarteira());
            p.setSplit(input.getSplit());
            participacoes.add(p);
        }
        novaMusica.setParticipacoes(participacoes);

        Musica musicaSalva = dao.save(novaMusica);
        logger.info("✅ SUCESSO! Musica cadastrada (com splits definidos) no MongoDB com ID: {}", musicaSalva.getId());

        return musicaSalva;
    }
}
