package br.com.SoundLedger_API.service;


import br.com.SoundLedger_API.dao.IMusica;
import br.com.SoundLedger_API.dao.IUser;
import br.com.SoundLedger_API.model.dto.MusicRoyaltyInfoDTO;
import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.ParticipacaoNaMusica;
import br.com.SoundLedger_API.model.entity.User;

import br.com.SoundLedger_API.api.blockchain.service.BlockchainService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.web3j.utils.Convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private final IMusica musicaDao;
    private final IUser userDao;
    private final BlockchainService blockchainService;

    @Autowired
    public DashboardService(IMusica musicaDao, IUser userDao, BlockchainService blockchainService) {
        this.musicaDao = musicaDao;
        this.userDao = userDao;
        this.blockchainService = blockchainService;
    }

    public List<MusicRoyaltyInfoDTO> getInformacoesRoyaltiesUsuario(String usuarioId) {
        logger.info("DASHBOARD: Iniciando busca de royalties para usuarioId: {}", usuarioId);
        List<MusicRoyaltyInfoDTO> resultados = new ArrayList<>();

        Optional<User> userOpt = userDao.findById(usuarioId);
        if (userOpt.isEmpty()) {
            logger.warn("DASHBOARD: Utilizador {} NAO ENCONTRADO.", usuarioId);
            return Collections.emptyList();
        }
        User utilizador = userOpt.get();
        String carteiraUtilizador = utilizador.getEnderecoCarteira();

        if (carteiraUtilizador == null || carteiraUtilizador.trim().isEmpty()) {
            logger.warn("DASHBOARD: Utilizador {} (Nome: {}) NAO TEM CARTEIRA no DB.",
                    usuarioId, utilizador.getNome());
            return Collections.emptyList();
        }

        logger.info("DASHBOARD: Carteira do utilizador {} encontrada: {}", usuarioId, carteiraUtilizador);


        List<Musica> musicasDoUsuario = musicaDao.findByParticipacoesUsuarioId(usuarioId);
        logger.info("DASHBOARD: Encontradas {} musicas para o utilizador {}", musicasDoUsuario.size(), usuarioId);

        for (Musica musica : musicasDoUsuario) {
            String musicaId = musica.getId();
            String contractAddress = musica.getContratoBlockchain();

            if (contractAddress == null || contractAddress.isEmpty()) {
                logger.warn("DASHBOARD: Musica ID {} sem endereco de contrato.", musicaId);
                continue;
            }
            logger.debug("DASHBOARD: Processando musica ID: {}, Contrato: {}", musicaId, contractAddress);

            try {
                logger.info("DASHBOARD: Chamando getReleasableRoyalties (Contrato: {}, Carteira: {})", contractAddress, carteiraUtilizador);
                BigInteger saldoWei = blockchainService.getReleasableRoyalties(contractAddress, carteiraUtilizador);
                logger.info("DASHBOARD: Saldo retornado (Wei): {}", saldoWei); // ✅ LOG ESSENCIAL: VEJA O VALOR AQUI

                BigInteger totalPlays = blockchainService.getTotalPlays(contractAddress);

                ParticipacaoNaMusica participacao = musica.getParticipacoes().stream()
                        .filter(p -> usuarioId.equals(p.getUsuarioId()))
                        .findFirst()
                        .orElse(null);

                if (participacao == null) {
                    logger.error("DASHBOARD: INCONSISTÊNCIA GRAVE - Participacao nao encontrada para user {} na musica {}", usuarioId, musicaId);
                    continue;
                }
                BigInteger splitUsuario = participacao.getSplit();

                String nomeArtistaPrincipal = userDao.findById(musica.getArtistaPrincipalId())
                        .map(User::getNome)
                        .orElse("Artista Desconhecido");

                BigDecimal saldoEth = Convert.fromWei(new BigDecimal(saldoWei), Convert.Unit.ETHER);
                String saldoEthFormatado = saldoEth.setScale(18, RoundingMode.HALF_UP).toPlainString() + " ETH"; // 18 casas

                MusicRoyaltyInfoDTO dto = new MusicRoyaltyInfoDTO();
                dto.setMusicaId(musicaId);
                dto.setTitulo(musica.getTitulo());
                dto.setArtistaPrincipalNome(nomeArtistaPrincipal);
                dto.setContractAddress(contractAddress);
                dto.setTotalPlaysDaMusica(totalPlays);
                dto.setSplitPercentualDoUsuario(splitUsuario);
                dto.setSaldoDoUsuarioEth(saldoEthFormatado);

                resultados.add(dto);

            } catch (Exception e) {
                logger.error("DASHBOARD: Falha ao buscar dados on-chain para música ID {} (Contrato {}): {}",
                        musicaId, contractAddress, e.getMessage());
            }
        }

        logger.info("DASHBOARD: Retornando {} DTOs para o utilizador {}", resultados.size(), usuarioId);
        return resultados;
    }
}